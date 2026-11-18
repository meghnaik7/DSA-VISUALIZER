import { z } from "zod";

export const ApproachSchema = z.object({
  title: z.string().min(1),
  explanation: z.string().min(1),
});

export const SourceCodeSchema = z.object({
  language: z.string().min(1),
  code: z.string().min(1),
});

export const ComplexitySchema = z.object({
  value: z.string().min(1),
  explanation: z.string().min(1),
});

export const VisualizationScriptSchema = z.object({
  library: z.string().min(1),
  language: z.string().min(1),
  code: z.string().min(1).max(50_000), // guard against oversized scripts
});

export const GeneratedArtifactSchema = z.object({
  approach: ApproachSchema.optional(),
  pseudocode: z.string().min(1).optional(),
  sourceCode: SourceCodeSchema.optional(),
  timeComplexity: ComplexitySchema.optional(),
  spaceComplexity: ComplexitySchema.optional(),
  visualizationScript: VisualizationScriptSchema.optional(),
});
