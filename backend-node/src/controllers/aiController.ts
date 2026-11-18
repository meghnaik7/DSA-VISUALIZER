import { Request, Response, NextFunction } from "express";
import { AIGenerationRequestSchema } from "../schemas/aiRequestSchema";
import { getAIResponse } from "../services/ai/aiService";
import { logger } from "../config/logger";
import { generateRequestHash } from "../utils/hashGenerator";

/**
 * POST /api/v1/ai/generate
 * Validates the request, runs the full AI generation pipeline,
 * and returns a structured artifact with generation metadata.
 */
export async function generateArtifact(
  req: Request,
  res: Response,
  next: NextFunction
): Promise<void> {
  const startMs = Date.now();

  // 1. Parse & validate request
  const parseResult = AIGenerationRequestSchema.safeParse(req.body);
  if (!parseResult.success) {
    const errors = parseResult.error.errors
      .map((e) => `${e.path.join(".")}: ${e.message}`)
      .join("; ");
    res.status(400).json({
      success: false,
      requestId: req.body?.requestId ?? "unknown",
      error: { code: "VALIDATION_ERROR", message: errors },
    });
    return;
  }

  const validRequest = parseResult.data;
  const requestId =
    validRequest.requestId ??
    generateRequestHash(`${validRequest.query}|${Date.now()}`);

  logger.info({
    requestId,
    requestType: validRequest.requestType,
    msg: "AI generation request received",
  });

  // 2. Run the full generation pipeline
  try {
    const result = await getAIResponse(validRequest, requestId);

    const durationMs = Date.now() - startMs;
    logger.info({ requestId, durationMs, msg: "AI generation succeeded" });

    res.status(200).json({
      success: true,
      requestId,
      artifact: result.artifact,
      metadata: {
        provider: result.provider,
        model: result.model,
        promptVersion: result.promptVersion,
        generatedAt: result.generatedAt,
      },
    });
  } catch (err) {
    next(err);
  }
}

// ─── Legacy handlers kept for backward compatibility ─────────────────────────

export async function getResponseBasedonRequriments(
  req: Request,
  res: Response,
  next: NextFunction
): Promise<void> {
  return generateArtifact(req, res, next);
}
