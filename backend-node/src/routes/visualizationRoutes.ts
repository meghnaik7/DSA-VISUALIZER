import { Router } from "express";

const router = Router();

// TODO: implement visualization routes
router.get("/", (_req, res) => {
  res.json({ message: "Visualization routes — coming soon" });
});

export default router;
