from __future__ import annotations

import json
from datetime import datetime, timezone
from pathlib import Path

from ptaf.ai.build_info import VERSION


def log_gemini_response(operation: str, model: str, response_body: str) -> None:
    try:
        parsed = json.loads(response_body)
        line = {
            "ts": datetime.now(timezone.utc).isoformat(),
            "moduleVersion": VERSION,
            "operation": operation or "unknown",
            "model": model,
            "usageMetadata": parsed.get("usageMetadata"),
        }
        target = Path("target")
        target.mkdir(parents=True, exist_ok=True)
        log_path = target / "ai-telemetry.jsonl"
        with log_path.open("a", encoding="utf-8") as handle:
            handle.write(json.dumps(line) + "\n")
    except Exception:
        pass
