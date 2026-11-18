import { z } from "zod";

export const RequirementFlagsSchema = z.object({
  approach: z.boolean(),
  pseudocode: z.boolean(),
  sourceCode: z.boolean(),
  timeComplexity: z.boolean(),
  spaceComplexity: z.boolean(),
  visualizationScript: z.boolean(),
});

export const AIGenerationRequestSchema = z
  .object({
    requestId: z.string().optional(),
    requestType: z.enum(["ALGORITHM", "PROBLEM"]),
    query: z.string().min(3).max(2000),
    requirements: RequirementFlagsSchema,
    visualizationLibrary: z.string().min(1).max(100),
    outputLanguage: z.string().min(1).max(50),
  })
  .refine(
    (data) => Object.values(data.requirements).some(Boolean),
    { message: "At least one requirement flag must be true", path: ["requirements"] }
  );

export type ValidatedAIRequest = z.infer<typeof AIGenerationRequestSchema>;
