import express from "express";
import cors from "cors";
import { env } from "./config/env";
import { serviceAuth } from "./middleware/serviceAuthentication";
import { errorHandler } from "./middleware/errorHandler";
import aiRoutes from "./routes/aiRoutes";
import visualizationRoutes from "./routes/visualizationRoutes";

export function createApp(): express.Application {
  const app = express();

  // ── Core middleware ─────────────────────────────────────────────────────────
  app.use(cors());
  app.use(
    express.json({ limit: `${env.MAX_PAYLOAD_BYTES}b` })
  );

  // ── Service authentication ──────────────────────────────────────────────────
  app.use("/api", serviceAuth);

  // ── Routes ──────────────────────────────────────────────────────────────────
  app.use("/api/v1/ai", aiRoutes);
  app.use("/api/v1/visualizations", visualizationRoutes);

  // ── Health check ────────────────────────────────────────────────────────────
  app.get("/health", (_req, res) => {
    res.json({ status: "ok", ts: new Date().toISOString() });
  });

  // ── Global error handler (must be last) ────────────────────────────────────
  app.use(errorHandler);

  return app;
}
