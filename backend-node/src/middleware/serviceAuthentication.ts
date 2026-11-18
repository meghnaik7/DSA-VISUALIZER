import { Request, Response, NextFunction } from "express";
import { env } from "../config/env";

/**
 * Optional service-to-service authentication.
 * When SERVICE_SECRET is set, every request must carry:
 *   Authorization: Bearer <SERVICE_SECRET>
 * Skip by leaving SERVICE_SECRET empty in development.
 */
export function serviceAuth(req: Request, res: Response, next: NextFunction): void {
  if (!env.SERVICE_SECRET) {
    next();
    return;
  }

  const authHeader = req.headers["authorization"] ?? "";
  const token = authHeader.startsWith("Bearer ") ? authHeader.slice(7) : "";

  if (token !== env.SERVICE_SECRET) {
    res.status(401).json({
      success: false,
      requestId: "unknown",
      error: { code: "UNAUTHORIZED", message: "Invalid or missing service secret" },
    });
    return;
  }

  next();
}
