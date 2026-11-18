import { logger } from "../config/logger";

export class RetryableError extends Error {
  constructor(message: string, public readonly statusCode?: number) {
    super(message);
    this.name = "RetryableError";
  }
}

export class NonRetryableError extends Error {
  constructor(message: string, public readonly code: string) {
    super(message);
    this.name = "NonRetryableError";
  }
}

/**
 * Retry an async operation with bounded attempts.
 * Retries only on RetryableError; bails immediately on NonRetryableError.
 */
export async function withRetry<T>(
  fn: () => Promise<T>,
  maxRetries: number,
  requestId: string
): Promise<T> {
  let lastError: unknown;

  for (let attempt = 1; attempt <= maxRetries + 1; attempt++) {
    try {
      return await fn();
    } catch (err) {
      lastError = err;

      if (err instanceof NonRetryableError) {
        throw err;
      }

      if (attempt <= maxRetries) {
        const delay = 500 * attempt; // 500ms, 1000ms
        logger.warn({ requestId, attempt, delay, msg: "Retrying AI call" });
        await new Promise((r) => setTimeout(r, delay));
      }
    }
  }

  throw lastError;
}
