/**
 * Sanitize a user-supplied query before embedding it in a prompt.
 * Strips prompt-injection attempts (lines that try to override instructions).
 */
export function normalizeQuery(raw: string): string {
  return raw
    .trim()
    .replace(/\r\n/g, "\n")
    .split("\n")
    .filter((line) => {
      const lower = line.toLowerCase();
      // Drop lines trying to hijack the system prompt
      return !(
        lower.includes("ignore previous") ||
        lower.includes("disregard") ||
        lower.includes("system prompt") ||
        lower.includes("you are now")
      );
    })
    .join("\n")
    .slice(0, 2000); // hard cap matching schema
}
