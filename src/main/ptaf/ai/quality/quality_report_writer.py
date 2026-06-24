from __future__ import annotations

import html
import json
from dataclasses import asdict
from datetime import datetime, timezone
from pathlib import Path

from ptaf.ai.quality.quality_gate_service import QualityReport


def write(target_dir: Path, report: QualityReport) -> None:
    target_dir.mkdir(parents=True, exist_ok=True)
    json_path = target_dir / "ai-quality-report.json"
    html_path = target_dir / "ai-quality-report.html"

    payload = {
        "generatedAt": datetime.now(timezone.utc).isoformat(),
        "warnThreshold": report.warn_threshold,
        "failedStrict": report.failed_strict,
        "syntaxIssues": report.syntax_issues,
        "duplicateStepGroups": [
            {
                "normalized": group.normalized_step,
                "count": group.count,
                "occurrences": [asdict(occ) for occ in group.occurrences],
            }
            for group in report.duplicate_groups
        ],
    }
    json_path.write_text(json.dumps(payload, indent=2), encoding="utf-8")

    lines = [
        "<!DOCTYPE html><html><head><meta charset=\"utf-8\"><title>PTAF AI quality</title></head><body>",
        f"<h1>AI quality report</h1><p>{datetime.now(timezone.utc).isoformat()}</p>",
        "<h2>Syntax / structure</h2><ul>",
    ]
    for issue in report.syntax_issues:
        lines.append(f"<li>{html.escape(issue)}</li>")
    lines.append(f"</ul><h2>Duplicate step groups (≥ {report.warn_threshold})</h2>")
    for group in report.duplicate_groups:
        lines.append(
            f"<h3>{html.escape(group.normalized_step)} ({group.count})</h3><ul>"
        )
        for occurrence in group.occurrences:
            lines.append(
                f"<li>{html.escape(occurrence.relative_path)}:{occurrence.line_number}</li>"
            )
        lines.append("</ul>")
    lines.append("</body></html>")
    html_path.write_text("\n".join(lines), encoding="utf-8")
