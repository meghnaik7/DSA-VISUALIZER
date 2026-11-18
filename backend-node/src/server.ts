import { env } from "./config/env";
import { logger } from "./config/logger";
import { createApp } from "./app";

// Validate env at startup — fails fast if keys are missing
try {
  // validateEnv();  // Uncomment when deploying to production
} catch (err) {
  logger.error({ msg: (err as Error).message });
  process.exit(1);
}

const app = createApp();

app.listen(env.PORT, () => {
  logger.info({ msg: `Node backend listening`, port: env.PORT, env: env.NODE_ENV });
});
