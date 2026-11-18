import { z } from "zod";
import { VisualizationScriptSchema } from "./aiResponseSchema";

// Standalone validator for the visualization script section
export const VisualizationScriptValidator = VisualizationScriptSchema.refine(
  (s) => typeof s.code === "string" && s.code.trim().length > 0,
  { message: "visualizationScript.code must not be empty" }
);

export type ValidatedVisualizationScript = z.infer<typeof VisualizationScriptValidator>;
