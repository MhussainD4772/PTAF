from __future__ import annotations

import hashlib
import json
from dataclasses import asdict, dataclass
from pathlib import Path

from ptaf.ai.audit.ai_generation_audit_record import AiGenerationAuditRecord


@dataclass(frozen=True)
class WriteResult:
    written: bool
    output_path: str
    warning_message: str | None


class AiGenerationAuditLogger:
    def append(
        self,
        project_root: Path,
        enabled: bool,
        output_path: str,
        record: AiGenerationAuditRecord,
    ) -> WriteResult:
        if not enabled:
            return WriteResult(False, output_path, None)
        try:
            file_path = (project_root / output_path).resolve()
            file_path.parent.mkdir(parents=True, exist_ok=True)
            with file_path.open("a", encoding="utf-8") as handle:
                handle.write(json.dumps(asdict(record)) + "\n")
            return WriteResult(True, str(file_path), None)
        except Exception:
            return WriteResult(False, output_path, "Warning: audit log could not be written")

    @staticmethod
    def sha256(text: str | None) -> str:
        if text is None:
            return ""
        return hashlib.sha256(text.encode("utf-8")).hexdigest()
