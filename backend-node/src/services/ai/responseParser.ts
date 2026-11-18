import { GeneratedArtifact } from "../../types/ai";

/**
 * Parse raw AI text output into a GeneratedArtifact object.
 * Strips markdown fences the model may have added despite instructions.
 */
export function parseAIResponse(raw: string): GeneratedArtifact {
  const cleaned = stripMarkdownFences(raw);

  let parsed: unknown;
  try {
    parsed = JSON.parse(cleaned);
  } catch {
    throw new Error("AI response is not valid JSON");
  }

  if (typeof parsed !== "object" || parsed === null || Array.isArray(parsed)) {
    throw new Error("AI response JSON is not an object");
  }

  return parsed as GeneratedArtifact;
}

function stripMarkdownFences(text: string): string {
  // Remove ```json ... ``` or ``` ... ``` wrappers
  return text
    .trim()
    .replace(/^```(?:json)?\s*/i, "")
    .replace(/\s*```$/, "")
    .trim();
}
