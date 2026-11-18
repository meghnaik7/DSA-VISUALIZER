import { env } from "./env";

export interface AIProviderConfig {
  provider: string;
  model: string;
  apiKey: string;
  timeoutMs: number;
  maxRetries: number;
}

export function getAIConfig(): AIProviderConfig {
  const provider = env.AI_PROVIDER;

  if (provider === "gemini") {
    return {
      provider: "gemini",
      model: env.AI_MODEL,
      apiKey: env.GEMINI_API_KEY,
      timeoutMs: env.AI_TIMEOUT_MS,
      maxRetries: env.AI_MAX_RETRIES,
    };
  }

  // default: openai
  return {
    provider: "openai",
    model: env.AI_MODEL,
    apiKey: env.OPENAI_API_KEY,
    timeoutMs: env.AI_TIMEOUT_MS,
    maxRetries: env.AI_MAX_RETRIES,
  };
}
