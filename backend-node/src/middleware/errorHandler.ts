import { Request, Response, NextFunction } from "express";
import { ZodError } from "zod";
import { logger } from "../config/logger";
import { NonRetryableError, RetryableError } from "../utils/retryHandler";

interface ErrorResponse {
  success: false;
  requestId: string;
  error: { code: string; message: string };
}

export function errorHandler(
  err: unknown,
  req: Request,
  res: Response,
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  _next: NextFunction
): void {
  const requestId: string =
    (req.body?.requestId as string) ?? (req.headers["x-request-id"] as string) ?? "unknown";

  const body: ErrorResponse = {
    success: false,
    requestId,
    error: { code: "INTERNAL_ERROR", message: "An unexpected error occurred" },
  };

  if (err instanceof ZodError) {
    body.error = {
      code: "VALIDATION_ERROR",
      message: err.errors.map((e) => `${e.path.join(".")}: ${e.message}`).join("; "),
    };
    res.status(400).json(body);
    return;
  }

  if (err instanceof NonRetryableError) {
    const statusMap: Record<string, number> = {
      AUTH_FAILED: 502,
      PARSE_FAILED: 502,
      AI_OUTPUT_VALIDATION_FAILED: 502,
    };
    const status = statusMap[err.code] ?? 500;
    body.error = { code: err.code, message: err.message };
    res.status(status).json(body);
    return;
  }

  if (err instanceof RetryableError) {
    const status = err.statusCode === 504 ? 504 : 503;
    body.error = { code: "AI_SERVICE_UNAVAILABLE", message: err.message };
    res.status(status).json(body);
    return;
  }

  if (err instanceof Error && err.message.includes("too large")) {
    body.error = { code: "PAYLOAD_TOO_LARGE", message: err.message };
    res.status(413).json(body);
    return;
  }

  // Unknown error — log safely without exposing internals
  logger.error({
    requestId,
    msg: "Unhandled error",
    name: err instanceof Error ? err.name : "Unknown",
  });

  res.status(500).json(body);
}
