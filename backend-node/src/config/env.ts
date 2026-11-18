import dotenv from "dotenv";
dotenv.config();

function required(key: string): string {
  const val = process.env[key];
  if (!val) throw new Error(`Missing required environment variable: ${key}`);
  return val;
}

function optional(key: string, fallback: string): string {
  return process.env[key] ?? fallback;
}

export const env = {
  NODE_ENV: optional("NODE_ENV", "development"),
  PORT: parseInt(optional("PORT", "3000"), 10),

  // AI provider
  AI_PROVIDER: optional("AI_PROVIDER", "openai"),          // openai | gemini
  OPENAI_API_KEY: process.env["OPENAI_API_KEY"] ?? "",
  GEMINI_API_KEY: process.env["GEMINI_API_KEY"] ?? "",
  AI_MODEL: optional("AI_MODEL", "gpt-4o-mini"),
  AI_TIMEOUT_MS: parseInt(optional("AI_TIMEOUT_MS", "30000"), 10),
  AI_MAX_RETRIES: parseInt(optional("AI_MAX_RETRIES", "2"), 10),

  // Request limits
  MAX_PAYLOAD_BYTES: parseInt(optional("MAX_PAYLOAD_BYTES", "65536"), 10), // 64 KB

  // Service auth
  SERVICE_SECRET: optional("SERVICE_SECRET", ""),

  // Spring backend
  SPRING_BASE_URL: optional("SPRING_BASE_URL", "http://localhost:8080"),
} as const;

// Eagerly validate that at least one AI key is present at startup
export function validateEnv(): void {
  if (!env.OPENAI_API_KEY && !env.GEMINI_API_KEY) {
    throw new Error(
      "At least one AI provider key must be set: OPENAI_API_KEY or GEMINI_API_KEY"
    );
  }
}
