from __future__ import annotations

import html
import json
from pathlib import Path


def write_html(target_dir: Path) -> Path:
    target_dir.mkdir(parents=True, exist_ok=True)
    jsonl = target_dir / "ai-telemetry.jsonl"
    out = target_dir / "ai-telemetry-summary.html"
    lines = jsonl.read_text(encoding="utf-8").splitlines() if jsonl.is_file() else []

    rows: list[str] = []
    count = 0
    for line in lines:
        if not line.strip():
            continue
        obj = json.loads(line)
        count += 1
        usage = obj.get("usageMetadata")
        usage_text = json.dumps(usage, indent=2) if usage is not None else ""
        rows.append(
            "<tr>"
            f"<td>{html.escape(str(obj.get('ts', '')))}</td>"
            f"<td>{html.escape(str(obj.get('operation', '')))}</td>"
            f"<td>{html.escape(str(obj.get('model', '')))}</td>"
            f"<td><pre>{html.escape(usage_text)}</pre></td>"
            "</tr>"
        )

    content = f"""<!DOCTYPE html><html><head><meta charset="utf-8"><title>PTAF AI telemetry</title>
<style>body{{font-family:sans-serif;margin:1rem;}} table{{border-collapse:collapse;}}
td,th{{border:1px solid #ccc;padding:6px;vertical-align:top;}} pre{{white-space:pre-wrap;margin:0;max-width:480px;}}</style>
</head><body><h1>Gemini telemetry (local)</h1>
<p>Rows: {count} — source: ai-telemetry.jsonl</p>
<table><thead><tr><th>Time</th><th>Operation</th><th>Model</th><th>usageMetadata</th></tr></thead><tbody>
{''.join(rows)}
</tbody></table></body></html>"""
    out.write_text(content, encoding="utf-8")
    return out
