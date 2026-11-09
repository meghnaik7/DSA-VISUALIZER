-- ============================================================================
-- DSA Visualizer Database Schema (PostgreSQL)
-- ============================================================================

-- Extensions (UUID generation, pg_trgm for text search if needed)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ----------------------------------------------------------------------------
-- 1. USERS
-- Note: UNIQUE constraint on email automatically creates a unique B-tree index.
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_user_role CHECK (role IN ('USER', 'ADMIN'))
);

-- ----------------------------------------------------------------------------
-- 2. ALGORITHMS
-- Note: UNIQUE constraint on slug automatically creates a unique B-tree index.
-- Composite index (category, difficulty) covers category filter and category+difficulty.
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS algorithms (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    slug VARCHAR(200) NOT NULL UNIQUE,
    category VARCHAR(30) NOT NULL,
    description TEXT,
    input_type VARCHAR(100),
    output_type VARCHAR(100),
    difficulty VARCHAR(20) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_algo_difficulty CHECK (difficulty IN ('BEGINNER', 'INTERMEDIATE', 'ADVANCED'))
);

CREATE INDEX IF NOT EXISTS idx_algorithm_cat_diff ON algorithms(category, difficulty);

-- ----------------------------------------------------------------------------
-- 3. PROBLEMS
-- Note: UNIQUE constraint on slug automatically creates a unique B-tree index.
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS problems (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    slug VARCHAR(300) NOT NULL UNIQUE,
    statement TEXT NOT NULL,
    input_description TEXT,
    output_description TEXT,
    constraints TEXT,
    difficulty VARCHAR(20) NOT NULL,
    canonical_hash VARCHAR(64),
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_problem_difficulty CHECK (difficulty IN ('BEGINNER', 'INTERMEDIATE', 'ADVANCED'))
);

CREATE INDEX IF NOT EXISTS idx_problem_canonical_hash ON problems(canonical_hash);

-- ----------------------------------------------------------------------------
-- 4. PROBLEM_ALGORITHMS (Many-to-Many Join Table)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS problem_algorithms (
    problem_id BIGINT NOT NULL,
    algorithm_id BIGINT NOT NULL,
    PRIMARY KEY (problem_id, algorithm_id),
    CONSTRAINT fk_pa_problem FOREIGN KEY (problem_id) REFERENCES problems(id) ON DELETE CASCADE,
    CONSTRAINT fk_pa_algorithm FOREIGN KEY (algorithm_id) REFERENCES algorithms(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_pa_problem ON problem_algorithms(problem_id);
CREATE INDEX IF NOT EXISTS idx_pa_algorithm ON problem_algorithms(algorithm_id);

-- ----------------------------------------------------------------------------
-- 5. ALGORITHM_ARTIFACTS (Global Reusable AI Output Cache)
-- Note: UNIQUE constraint on cache_key automatically creates a unique B-tree index.
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS algorithm_artifacts (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    algorithm_id BIGINT,
    problem_id BIGINT,
    artifact_type VARCHAR(30) NOT NULL,
    cache_key VARCHAR(255) NOT NULL UNIQUE,
    approach TEXT,
    pseudocode TEXT,
    time_complexity VARCHAR(50),
    space_complexity VARCHAR(50),
    visualization_spec JSONB,
    source_code TEXT,
    visualization_library VARCHAR(100),
    library_version VARCHAR(30),
    generation_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    content_hash VARCHAR(64),
    prompt_version VARCHAR(50),
    model_name VARCHAR(100),
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_artifact_algorithm FOREIGN KEY (algorithm_id) REFERENCES algorithms(id) ON DELETE SET NULL,
    CONSTRAINT fk_artifact_problem FOREIGN KEY (problem_id) REFERENCES problems(id) ON DELETE SET NULL,
    CONSTRAINT chk_artifact_type CHECK (artifact_type IN ('ALGORITHM', 'PROBLEM_SOLUTION')),
    CONSTRAINT chk_artifact_status CHECK (generation_status IN ('PENDING', 'READY', 'FAILED')),
    CONSTRAINT chk_artifact_owner CHECK (algorithm_id IS NOT NULL OR problem_id IS NOT NULL)
);

CREATE INDEX IF NOT EXISTS idx_artifact_algo_status ON algorithm_artifacts(algorithm_id, generation_status);
CREATE INDEX IF NOT EXISTS idx_artifact_problem ON algorithm_artifacts(problem_id);

-- ----------------------------------------------------------------------------
-- 6. SUBSCRIPTION_PLANS
-- Note: UNIQUE constraints on name and code automatically create unique indexes.
-- Small catalogue table (<10 rows), additional indexes not required.
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS subscription_plans (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    code VARCHAR(30) NOT NULL UNIQUE,
    description TEXT,
    price NUMERIC(10, 2) NOT NULL DEFAULT 0.00,
    currency VARCHAR(3) NOT NULL DEFAULT 'INR',
    billing_interval VARCHAR(20) NOT NULL,
    max_ai_requests INTEGER NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_plan_interval CHECK (billing_interval IN ('MONTHLY', 'YEARLY', 'LIFETIME')),
    CONSTRAINT chk_plan_price CHECK (price >= 0),
    CONSTRAINT chk_plan_requests CHECK (max_ai_requests >= 0)
);

-- ----------------------------------------------------------------------------
-- 7. SUBSCRIPTIONS
-- Note: (user_id, status) covers user-level active subscription queries.
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS subscriptions (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL,
    plan_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    start_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_date TIMESTAMP WITHOUT TIME ZONE,
    auto_renew BOOLEAN NOT NULL DEFAULT FALSE,
    provider_subscription_id VARCHAR(255),
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_sub_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_sub_plan FOREIGN KEY (plan_id) REFERENCES subscription_plans(id) ON DELETE RESTRICT,
    CONSTRAINT chk_sub_status CHECK (status IN ('PENDING', 'ACTIVE', 'CANCELLED', 'EXPIRED'))
);

CREATE INDEX IF NOT EXISTS idx_sub_user_status ON subscriptions(user_id, status);
CREATE INDEX IF NOT EXISTS idx_sub_provider_id ON subscriptions(provider_subscription_id);
CREATE INDEX IF NOT EXISTS idx_sub_plan ON subscriptions(plan_id);

-- ----------------------------------------------------------------------------
-- 8. PAYMENTS
-- Note: UNIQUE constraint on provider_payment_id automatically creates unique index.
-- (user_id, created_at DESC) covers user transaction history.
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS payments (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL,
    subscription_id BIGINT,
    amount NUMERIC(10, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'INR',
    provider VARCHAR(50),
    provider_payment_id VARCHAR(255) UNIQUE,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    payment_method VARCHAR(50),
    paid_at TIMESTAMP WITHOUT TIME ZONE,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_payment_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_payment_subscription FOREIGN KEY (subscription_id) REFERENCES subscriptions(id) ON DELETE SET NULL,
    CONSTRAINT chk_payment_status CHECK (status IN ('PENDING', 'SUCCESS', 'FAILED', 'REFUNDED')),
    CONSTRAINT chk_payment_amount CHECK (amount >= 0)
);

CREATE INDEX IF NOT EXISTS idx_payment_user_created ON payments(user_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_payment_subscription ON payments(subscription_id);

-- ----------------------------------------------------------------------------
-- 9. LEARNING_HISTORIES
-- Note: (user_id, created_at DESC) covers user dashboard and activity history.
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS learning_histories (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL,
    artifact_id BIGINT,
    original_query TEXT,
    request_type VARCHAR(20) NOT NULL,
    input_data JSONB,
    status VARCHAR(20) NOT NULL DEFAULT 'STARTED',
    time_spent_seconds INTEGER DEFAULT 0,
    completed_at TIMESTAMP WITHOUT TIME ZONE,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_lh_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_lh_artifact FOREIGN KEY (artifact_id) REFERENCES algorithm_artifacts(id) ON DELETE SET NULL,
    CONSTRAINT chk_lh_request_type CHECK (request_type IN ('ALGORITHM', 'PROBLEM')),
    CONSTRAINT chk_lh_status CHECK (status IN ('STARTED', 'COMPLETED', 'FAILED')),
    CONSTRAINT chk_lh_time_spent CHECK (time_spent_seconds >= 0)
);

CREATE INDEX IF NOT EXISTS idx_lh_user_created ON learning_histories(user_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_lh_artifact ON learning_histories(artifact_id);
