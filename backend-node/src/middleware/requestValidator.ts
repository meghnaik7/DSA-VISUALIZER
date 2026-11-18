import { Request, Response, NextFunction } from "express";
import { env } from "../config/env";

/**
 * Rejects requests whose body exceeds MAX_PAYLOAD_BYTES.
 * Must be registered before body parsers or used with express.json({ limit }).
 */
export function payloadSizeGuard(req: Request, res: Response, next: NextFunction): void {
  const contentLength = parseInt(req.headers["content-length"] ?? "0", 10);
  if (contentLength > env.MAX_PAYLOAD_BYTES) {
    res.status(413).json({
      success: false,
      requestId: req.body?.requestId ?? "unknown",
      error: {
        code: "PAYLOAD_TOO_LARGE",
        message: `Request body exceeds the ${env.MAX_PAYLOAD_BYTES} byte limit`,
      },
    });
    return;
  }
  next();
}
