import crypto from "crypto";

/** Generate a short deterministic ID for a request, useful for deduplication logs. */
export function generateRequestHash(input: string): string {
  return crypto.createHash("sha256").update(input).digest("hex").slice(0, 16);
}
