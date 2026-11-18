import { getAIConfig } from "../../config/aiConfig";
import { logger } from "../../config/logger";
import { ValidatedAIRequest } from "../../schemas/aiRequestSchema";
import { GeneratedArtifact } from "../../types/ai";
import { withRetry } from "../../utils/retryHandler";
import { getPrompt, PROMPT_VERSION } from "./promptService";
import { callAIModel } from "./modelSelector";
import { parseAIResponse } from "./responseParser";
import { validateArtifact } from "./responseValidator";
import { NonRetryableError } from "../../utils/retryHandler";

export interface AIServiceResult {
  artifact: GeneratedArtifact;
  provider: string;
  model: string;
  promptVersion: string;
  generatedAt: string;
}

export async function getAIResponse(
  request: ValidatedAIRequest,
  requestId: string
): Promise<AIServiceResult> {
  const config = getAIConfig();

  const prompt = getPrompt(
    request.query,
    request.requestType,
    request.requirements,
    request.visualizationLibrary,
    request.outputLanguage
  );

  const startMs = Date.now();

  const rawText = await withRetry(
    () => callAIModel(prompt, config),
    config.maxRetries,
    requestId
  );

  const durationMs = Date.now() - startMs;
  logger.info({ requestId, durationMs, provider: config.provider, msg: "AI call completed" });

  // Parse
  let parsed: unknown;
  try {
    parsed = parseAIResponse(rawText);
  } catch (err) {
    throw new NonRetryableError(
      `AI response could not be parsed as JSON: ${(err as Error).message}`,
      "PARSE_FAILED"
    );
  }

  const validation = validateArtifact(parsed, request.requirements);
  if (!validation.valid) {
    if (validation.missingSection) {
      throw new NonRetryableError(
        `Generated response is missing required section: "${validation.missingSection}"`,
        "AI_OUTPUT_VALIDATION_FAILED"
      );
    }
    throw new NonRetryableError(
      `Generated response failed schema validation: ${validation.schemaError}`,
      "AI_OUTPUT_VALIDATION_FAILED"
    );
  }

  return {
    artifact: validation.artifact!,
    provider: config.provider,
    model: config.model,
    promptVersion: PROMPT_VERSION,
    generatedAt: new Date().toISOString(),
  };
}
