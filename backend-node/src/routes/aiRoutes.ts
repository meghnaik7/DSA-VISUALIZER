import { Router } from "express";
import { generateArtifact } from "../controllers/aiController";
import { payloadSizeGuard } from "../middleware/requestValidator";

const router = Router();

// POST /api/v1/ai/generate
// Full AI generation: validate → prompt → call → parse → validate → respond
router.post("/generate", payloadSizeGuard, generateArtifact);

// Legacy route kept for backward compatibility
router.post("/generate-algorithm", payloadSizeGuard, generateArtifact);

export default router;
