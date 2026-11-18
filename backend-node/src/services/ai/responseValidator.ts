import { GeneratedArtifact, RequirementFlags } from "../../types/ai";
import { GeneratedArtifactSchema } from "../../schemas/aiResponseSchema";

export interface ValidationResult {
  valid: boolean;
  artifact?: GeneratedArtifact;
  missingSection?: string;
  schemaError?: string;
}

export function validateArtifact(
  parsed: unknown,
  requirements: RequirementFlags
): ValidationResult {
  // 1. Schema validation
  const result = GeneratedArtifactSchema.safeParse(parsed);
  if (!result.success) {
    return {
      valid: false,
      schemaError: result.error.errors.map((e) => e.message).join("; "),
    };
  }

  const artifact = result.data as GeneratedArtifact;

  // 2. Check all requested sections are present
  const sectionMap: [keyof RequirementFlags, keyof GeneratedArtifact][] = [
    ["approach", "approach"],
    ["pseudocode", "pseudocode"],
    ["sourceCode", "sourceCode"],
    ["timeComplexity", "timeComplexity"],
    ["spaceComplexity", "spaceComplexity"],
    ["visualizationScript", "visualizationScript"],
  ];

  for (const [flag, section] of sectionMap) {
    if (requirements[flag] && artifact[section] === undefined) {
      return { valid: false, missingSection: section };
    }
  }

  // 3. Strip unrequested sections from the artifact (do not leak extra data)
  const clean: GeneratedArtifact = {};
  if (requirements.approach) clean.approach = artifact.approach;
  if (requirements.pseudocode) clean.pseudocode = artifact.pseudocode;
  if (requirements.sourceCode) clean.sourceCode = artifact.sourceCode;
  if (requirements.timeComplexity) clean.timeComplexity = artifact.timeComplexity;
  if (requirements.spaceComplexity) clean.spaceComplexity = artifact.spaceComplexity;
  if (requirements.visualizationScript) clean.visualizationScript = artifact.visualizationScript;

  return { valid: true, artifact: clean };
}
