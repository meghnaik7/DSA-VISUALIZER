// ─── Request ────────────────────────────────────────────────────────────────

export type RequestType = "ALGORITHM" | "PROBLEM";

export interface RequirementFlags {
  approach: boolean;
  pseudocode: boolean;
  sourceCode: boolean;
  timeComplexity: boolean;
  spaceComplexity: boolean;
  visualizationScript: boolean;
}

export interface AIGenerationRequest {
  requestId?: string;
  requestType: RequestType;
  query: string;
  requirements: RequirementFlags;
  visualizationLibrary: string;
  outputLanguage: string;
}

// ─── Artifact sections ───────────────────────────────────────────────────────

export interface ApproachSection {
  title: string;
  explanation: string;
}

export interface SourceCodeSection {
  language: string;
  code: string;
}

export interface ComplexitySection {
  value: string;
  explanation: string;
}

export interface VisualizationScriptSection {
  library: string;
  language: string;
  code: string;
}

export interface GeneratedArtifact {
  approach?: ApproachSection;
  pseudocode?: string;
  sourceCode?: SourceCodeSection;
  timeComplexity?: ComplexitySection;
  spaceComplexity?: ComplexitySection;
  visualizationScript?: VisualizationScriptSection;
}

// ─── Response ────────────────────────────────────────────────────────────────

export interface GenerationMetadata {
  provider: string;
  model: string;
  promptVersion: string;
  generatedAt: string;
}

export interface AIGenerationResponse {
  success: true;
  requestId: string;
  artifact: GeneratedArtifact;
  metadata: GenerationMetadata;
}

export interface AIErrorResponse {
  success: false;
  requestId: string;
  error: {
    code: string;
    message: string;
  };
}
