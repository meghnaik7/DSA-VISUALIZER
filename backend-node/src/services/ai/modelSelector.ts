import { AIProviderConfig } from "../../config/aiConfig";
import axios, { AxiosError } from "axios";
import { RetryableError, NonRetryableError } from "../../utils/retryHandler";

/**
 * Calls the configured AI provider and returns the raw text content.
 * Translates provider errors into typed errors for the retry handler.
 */
export async function callAIModel(
  prompt: string,
  config: AIProviderConfig
): Promise<string> {
  if (config.provider === "gemini") {
    return callGemini(prompt, config);
  }
  return callOpenAI(prompt, config);
}

// ─── OpenAI ──────────────────────────────────────────────────────────────────

async function callOpenAI(prompt: string, config: AIProviderConfig): Promise<string> {
  try {
    const res = await axios.post(
      "https://api.openai.com/v1/chat/completions",
      {
        model: config.model,
        messages: [{ role: "user", content: prompt }],
        temperature: 0.2,
        response_format: { type: "json_object" },
      },
      {
        headers: { Authorization: `Bearer ${config.apiKey}` },
        timeout: config.timeoutMs,
      }
    );

    const content: string | undefined =
      res.data?.choices?.[0]?.message?.content;
    if (!content) throw new NonRetryableError("OpenAI returned empty content", "EMPTY_RESPONSE");
    return content;
  } catch (err) {
    throw translateAxiosError(err, "OpenAI");
  }
}

// ─── Gemini ──────────────────────────────────────────────────────────────────

async function callGemini(prompt: string, config: AIProviderConfig): Promise<string> {
  const url = `https://generativelanguage.googleapis.com/v1beta/models/${config.model}:generateContent?key=${config.apiKey}`;
  try {
    const res = await axios.post(
      url,
      { contents: [{ parts: [{ text: prompt }] }] },
      { timeout: config.timeoutMs }
    );

    const content: string | undefined =
      res.data?.candidates?.[0]?.content?.parts?.[0]?.text;
    if (!content) throw new NonRetryableError("Gemini returned empty content", "EMPTY_RESPONSE");
    return content;
  } catch (err) {
    throw translateAxiosError(err, "Gemini");
  }
}

// ─── Error translation ───────────────────────────────────────────────────────

function translateAxiosError(err: unknown, providerName: string): Error {
  if (axios.isAxiosError(err)) {
    const axErr = err as AxiosError;
    const status = axErr.response?.status;

    if (axErr.code === "ECONNABORTED" || axErr.code === "ETIMEDOUT") {
      return new RetryableError(`${providerName} request timed out`, 504);
    }
    if (status === 429) {
      return new RetryableError(`${providerName} rate limit exceeded`, 429);
    }
    if (status === 401 || status === 403) {
      return new NonRetryableError(
        `${providerName} authentication failed — check your API key`,
        "AUTH_FAILED"
      );
    }
    if (status && status >= 500) {
      return new RetryableError(`${providerName} server error (${status})`, status);
    }
  }
  if (err instanceof Error) return err;
  return new Error(String(err));
}
