import request from "supertest";
import { createApp } from "../../src/app";
import axios from "axios";

// ─── Helpers ─────────────────────────────────────────────────────────────────

const app = createApp();

const mockedAxios = axios as jest.Mocked<typeof axios>;

const validArtifact = {
  approach: { title: "DFS", explanation: "Depth-first traversal..." },
  pseudocode: "1. Mark node visited\n2. Recurse neighbours",
  sourceCode: { language: "java", code: "void dfs(int v) {}" },
  timeComplexity: { value: "O(V + E)", explanation: "Visit every vertex and edge once" },
  spaceComplexity: { value: "O(V)", explanation: "Recursion stack depth" },
  visualizationScript: {
    library: "react-flow",
    language: "javascript",
    code: "const nodes = []; export default nodes;",
  },
};

const allFlagsRequest = {
  requestId: "req-test-001",
  requestType: "ALGORITHM",
  query: "Explain DFS traversal",
  requirements: {
    approach: true,
    pseudocode: true,
    sourceCode: true,
    timeComplexity: true,
    spaceComplexity: true,
    visualizationScript: true,
  },
  visualizationLibrary: "react-flow",
  outputLanguage: "java",
};

function mockAISuccess(artifact: object = validArtifact): void {
  mockedAxios.post.mockResolvedValueOnce({
    data: {
      choices: [{ message: { content: JSON.stringify(artifact) } }],
    },
  });
}

// ─── Tests ───────────────────────────────────────────────────────────────────

beforeEach(() => {
  jest.clearAllMocks();
  // Provide a fake API key so env doesn't throw
  process.env["OPENAI_API_KEY"] = "sk-test-key";
  process.env["AI_PROVIDER"] = "openai";
  process.env["AI_MODEL"] = "gpt-4o-mini";
  process.env["AI_MAX_RETRIES"] = "0";
});

describe("POST /api/v1/ai/generate", () => {
  // ── Success ────────────────────────────────────────────────────────────────

  it("returns 200 with full artifact on valid request", async () => {
    mockAISuccess();
    const res = await request(app).post("/api/v1/ai/generate").send(allFlagsRequest);

    expect(res.status).toBe(200);
    expect(res.body.success).toBe(true);
    expect(res.body.requestId).toBe("req-test-001");
    expect(res.body.artifact.approach.title).toBe("DFS");
    expect(res.body.metadata.provider).toBe("openai");
    expect(res.body.metadata.promptVersion).toBe("1");
    expect(res.body.metadata.generatedAt).toBeDefined();
  });

  // ── Partial flags ──────────────────────────────────────────────────────────

  it("returns only requested sections when some flags are false", async () => {
    const partialArtifact = {
      approach: { title: "DFS", explanation: "..." },
      timeComplexity: { value: "O(V+E)", explanation: "..." },
    };
    mockAISuccess(partialArtifact);

    const res = await request(app)
      .post("/api/v1/ai/generate")
      .send({
        ...allFlagsRequest,
        requirements: {
          approach: true,
          pseudocode: false,
          sourceCode: false,
          timeComplexity: true,
          spaceComplexity: false,
          visualizationScript: false,
        },
      });

    expect(res.status).toBe(200);
    expect(res.body.artifact.approach).toBeDefined();
    expect(res.body.artifact.timeComplexity).toBeDefined();
    expect(res.body.artifact.pseudocode).toBeUndefined();
    expect(res.body.artifact.sourceCode).toBeUndefined();
    expect(res.body.artifact.visualizationScript).toBeUndefined();
  });

  // ── All flags disabled ─────────────────────────────────────────────────────

  it("returns 400 when all requirement flags are false", async () => {
    const res = await request(app)
      .post("/api/v1/ai/generate")
      .send({
        ...allFlagsRequest,
        requirements: {
          approach: false,
          pseudocode: false,
          sourceCode: false,
          timeComplexity: false,
          spaceComplexity: false,
          visualizationScript: false,
        },
      });

    expect(res.status).toBe(400);
    expect(res.body.success).toBe(false);
    expect(mockedAxios.post).not.toHaveBeenCalled();
  });

  // ── Invalid fields ─────────────────────────────────────────────────────────

  it("returns 400 for missing query", async () => {
    const { query: _q, ...withoutQuery } = allFlagsRequest;
    const res = await request(app).post("/api/v1/ai/generate").send(withoutQuery);
    expect(res.status).toBe(400);
    expect(res.body.success).toBe(false);
    expect(mockedAxios.post).not.toHaveBeenCalled();
  });

  it("returns 400 for invalid requestType", async () => {
    const res = await request(app)
      .post("/api/v1/ai/generate")
      .send({ ...allFlagsRequest, requestType: "UNKNOWN" });
    expect(res.status).toBe(400);
    expect(mockedAxios.post).not.toHaveBeenCalled();
  });

  it("returns 400 for query that is too short", async () => {
    const res = await request(app)
      .post("/api/v1/ai/generate")
      .send({ ...allFlagsRequest, query: "ab" });
    expect(res.status).toBe(400);
    expect(mockedAxios.post).not.toHaveBeenCalled();
  });

  // ── Malformed AI JSON ──────────────────────────────────────────────────────

  it("returns 502 when AI returns invalid JSON", async () => {
    mockedAxios.post.mockResolvedValueOnce({
      data: { choices: [{ message: { content: "this is not json" } }] },
    });
    const res = await request(app).post("/api/v1/ai/generate").send(allFlagsRequest);
    expect(res.status).toBe(502);
    expect(res.body.error.code).toBe("PARSE_FAILED");
  });

  // ── Missing requested section ──────────────────────────────────────────────

  it("returns 502 when AI omits a required section", async () => {
    const { approach: _a, ...withoutApproach } = validArtifact;
    mockAISuccess(withoutApproach);

    const res = await request(app).post("/api/v1/ai/generate").send(allFlagsRequest);
    expect(res.status).toBe(502);
    expect(res.body.error.code).toBe("AI_OUTPUT_VALIDATION_FAILED");
  });

  // ── Provider timeout ───────────────────────────────────────────────────────

  it("returns 504 on provider timeout", async () => {
    const timeoutErr = Object.assign(new Error("timeout"), {
      code: "ECONNABORTED",
      __isAxios: true,
    });
    mockedAxios.post.mockRejectedValueOnce(timeoutErr);

    const res = await request(app).post("/api/v1/ai/generate").send(allFlagsRequest);
    expect(res.status).toBe(504);
  });

  // ── Provider auth failure ──────────────────────────────────────────────────

  it("returns 502 on provider auth failure", async () => {
    const authErr = Object.assign(new Error("Unauthorized"), {
      response: { status: 401 },
      __isAxios: true,
    });
    mockedAxios.post.mockRejectedValueOnce(authErr);

    const res = await request(app).post("/api/v1/ai/generate").send(allFlagsRequest);
    expect(res.status).toBe(502);
    expect(res.body.error.code).toBe("AUTH_FAILED");
  });

  // ── Visualization script generation ───────────────────────────────────────

  it("includes visualizationScript when flag is true", async () => {
    mockAISuccess();
    const res = await request(app).post("/api/v1/ai/generate").send(allFlagsRequest);
    expect(res.status).toBe(200);
    expect(res.body.artifact.visualizationScript.library).toBe("react-flow");
    expect(res.body.artifact.visualizationScript.code).toBeTruthy();
  });

  it("omits visualizationScript when flag is false", async () => {
    const noViz = { ...validArtifact };
    delete (noViz as Partial<typeof validArtifact>).visualizationScript;
    mockAISuccess(noViz);

    const res = await request(app)
      .post("/api/v1/ai/generate")
      .send({
        ...allFlagsRequest,
        requirements: { ...allFlagsRequest.requirements, visualizationScript: false },
      });

    expect(res.status).toBe(200);
    expect(res.body.artifact.visualizationScript).toBeUndefined();
  });

  // ── requestId passthrough ──────────────────────────────────────────────────

  it("echoes the caller's requestId in the response", async () => {
    mockAISuccess();
    const res = await request(app)
      .post("/api/v1/ai/generate")
      .send({ ...allFlagsRequest, requestId: "my-custom-id" });
    expect(res.body.requestId).toBe("my-custom-id");
  });

  it("generates a requestId when caller does not provide one", async () => {
    mockAISuccess();
    const { requestId: _id, ...noId } = allFlagsRequest;
    const res = await request(app).post("/api/v1/ai/generate").send(noId);
    expect(res.status).toBe(200);
    expect(typeof res.body.requestId).toBe("string");
    expect(res.body.requestId.length).toBeGreaterThan(0);
  });
});
