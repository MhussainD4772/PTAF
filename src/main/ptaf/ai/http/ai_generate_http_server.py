from __future__ import annotations

import json
import threading
from dataclasses import asdict
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer
from pathlib import Path
from typing import Any
from urllib.parse import urlparse

from ptaf.ai.audit import generation_audit_support as audit_support
from ptaf.ai.config.ai_assistant_properties import AiAssistantProperties
from ptaf.ai.feature_generator_service import FeatureGeneratorService
from ptaf.ai.model.ai_generation_mode import AiGenerationMode
from ptaf.ai.validation.generation_mode_evaluator import GenerationModeEvaluator

_UI_HTML = """<!doctype html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>PTAF AI Feature Generator</title>
  <style>
    body { font-family: Arial, sans-serif; margin: 20px; background: #f7f9fc; }
    .card { max-width: 980px; background: #fff; border-radius: 10px; padding: 16px; box-shadow: 0 2px 10px rgba(0,0,0,0.08); }
    textarea, input, select { width: 100%; margin: 6px 0 12px; padding: 8px; }
    button { padding: 10px 14px; margin-right: 8px; }
    pre { background: #0f172a; color: #e2e8f0; padding: 12px; border-radius: 8px; overflow: auto; white-space: pre-wrap; }
    .row { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }
    .muted { color: #475569; font-size: 13px; }
  </style>
</head>
<body>
  <div class="card">
    <h2>PTAF AI Feature Generator</h2>
    <p class="muted">Write your prompt here, choose mode, and generate a feature file without CLI flags.</p>
    <label>Requirement Prompt</label>
    <textarea id="requirement" rows="8" placeholder="Describe the feature to generate..."></textarea>
    <div class="row">
      <div>
        <label>Mode</label>
        <select id="mode">
          <option value="preview">preview</option>
          <option value="write" selected>write</option>
          <option value="strict">strict</option>
        </select>
      </div>
      <div>
        <label>Output Path (relative to project root)</label>
        <input id="output" value="target/ai-proposals/generated.feature" />
      </div>
    </div>
    <label><input id="overwrite" type="checkbox" checked /> Overwrite if file exists</label>
    <div style="margin-top: 12px;">
      <button onclick="generate()">Generate</button>
    </div>
    <h3>Result</h3>
    <pre id="result">No result yet.</pre>
  </div>
  <script>
    async function generate() {
      const payload = {
        requirement: document.getElementById("requirement").value,
        mode: document.getElementById("mode").value,
        output: document.getElementById("output").value,
        overwrite: document.getElementById("overwrite").checked
      };
      const box = document.getElementById("result");
      box.textContent = "Generating...";
      try {
        const res = await fetch("/generate-write", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(payload)
        });
        const data = await res.json();
        box.textContent = JSON.stringify(data, null, 2);
      } catch (e) {
        box.textContent = "Request failed: " + e;
      }
    }
  </script>
</body>
</html>
"""


def _structured_to_dict(response: Any) -> dict[str, Any]:
    if response is None:
        return {}
    return {
        "featureFile": response.feature_file,
        "reusedSteps": response.reused_steps,
        "newStepsNeeded": response.new_steps_needed,
        "yamlKeysUsed": response.yaml_keys_used,
        "missingYamlKeys": response.missing_yaml_keys,
        "warnings": response.warnings,
        "parseSuccessful": response.parse_successful,
        "parseErrors": response.parse_errors,
    }


def create_and_start(port: int, project_root: Path) -> ThreadingHTTPServer:
    service = FeatureGeneratorService(AiAssistantProperties())
    evaluator = GenerationModeEvaluator()

    class Handler(BaseHTTPRequestHandler):
        def log_message(self, format: str, *args: Any) -> None:
            return

        def _send_json(self, status: int, payload: dict[str, Any]) -> None:
            body = json.dumps(payload).encode("utf-8")
            self.send_response(status)
            self.send_header("Content-Type", "application/json; charset=utf-8")
            self.send_header("Content-Length", str(len(body)))
            self.end_headers()
            self.wfile.write(body)

        def _send_html(self, status: int, html: str) -> None:
            body = html.encode("utf-8")
            self.send_response(status)
            self.send_header("Content-Type", "text/html; charset=utf-8")
            self.send_header("Content-Length", str(len(body)))
            self.end_headers()
            self.wfile.write(body)

        def _read_json(self) -> dict[str, Any]:
            length = int(self.headers.get("Content-Length", "0"))
            raw = self.rfile.read(length).decode("utf-8") if length else "{}"
            parsed = json.loads(raw)
            return parsed if isinstance(parsed, dict) else {}

        def do_GET(self) -> None:
            path = urlparse(self.path).path
            if path == "/health":
                self._send_json(200, {"status": "ok"})
                return
            if path == "/":
                self._send_html(200, _UI_HTML)
                return
            self._send_json(404, {"error": "not found"})

        def do_POST(self) -> None:
            path = urlparse(self.path).path
            try:
                if path == "/generate":
                    payload = self._read_json()
                    requirement = str(payload.get("requirement", "")).strip()
                    if not requirement:
                        self._send_json(400, {"error": "requirement is required"})
                        return
                    result = service.generate(project_root, requirement)
                    audit_support.append(
                        project_root,
                        AiAssistantProperties(),
                        AiGenerationMode.PREVIEW,
                        requirement,
                        project_root / "target/ai-proposals/generated.feature",
                        None,
                        result,
                        [],
                    )
                    out = {
                        "deprecated": True,
                        "deprecationNotice": "Endpoint /generate is deprecated; use /generate-write.",
                        "preferredEndpoint": "/generate-write",
                        "featureGherkin": result.feature_gherkin,
                        "suggestedReusableSteps": result.suggested_reusable_steps,
                        "rawModelResponse": result.raw_model_response,
                        "structuredResponse": _structured_to_dict(result.structured_response),
                        "stepReuseValidation": asdict(result.step_reuse_validation_result) if result.step_reuse_validation_result else None,
                        "yamlKeyValidation": asdict(result.yaml_key_validation_result) if result.yaml_key_validation_result else None,
                        "allowedYamlGuard": asdict(result.allowed_yaml_guard_result) if result.allowed_yaml_guard_result else None,
                        "pageFrameContextGuard": asdict(result.page_frame_context_guard_result) if result.page_frame_context_guard_result else None,
                        "runnableFeature": asdict(result.runnable_feature_result) if result.runnable_feature_result else None,
                        "missingYamlPatchSuggestions": [asdict(item) for item in result.missing_yaml_patch_suggestions],
                        "reuseTrace": [],
                    }
                    self._send_json(200, out)
                    return

                if path == "/generate-write":
                    payload = self._read_json()
                    requirement = str(payload.get("requirement", "")).strip()
                    mode_raw = str(payload.get("mode", "preview"))
                    output_raw = str(payload.get("output", "target/ai-proposals/generated.feature"))
                    overwrite = bool(payload.get("overwrite", True))
                    if not requirement:
                        self._send_json(400, {"error": "requirement is required"})
                        return
                    mode = AiGenerationMode.from_string(mode_raw)
                    output = (project_root / output_raw).resolve()
                    result = service.generate(project_root, requirement)
                    blocking_errors = evaluator.blocking_errors(mode, result)
                    written = None
                    if evaluator.should_write_file(mode, blocking_errors):
                        written = service.write_feature_file(output, result, overwrite)
                    audit_support.append(
                        project_root,
                        AiAssistantProperties(),
                        mode,
                        requirement,
                        output,
                        written,
                        result,
                        blocking_errors,
                    )
                    status = 200 if not blocking_errors or mode == AiGenerationMode.PREVIEW else 422
                    out = {
                        "mode": mode.name,
                        "fileWritten": written is not None,
                        "outputPath": str(written) if written else str(output),
                        "blockingErrors": blocking_errors,
                        "featureGherkin": result.feature_gherkin,
                        "suggestedReusableSteps": result.suggested_reusable_steps,
                        "rawModelResponse": result.raw_model_response,
                        "structuredResponse": _structured_to_dict(result.structured_response),
                        "stepReuseValidation": asdict(result.step_reuse_validation_result) if result.step_reuse_validation_result else None,
                        "yamlKeyValidation": asdict(result.yaml_key_validation_result) if result.yaml_key_validation_result else None,
                        "allowedYamlGuard": asdict(result.allowed_yaml_guard_result) if result.allowed_yaml_guard_result else None,
                        "pageFrameContextGuard": asdict(result.page_frame_context_guard_result) if result.page_frame_context_guard_result else None,
                        "runnableFeature": asdict(result.runnable_feature_result) if result.runnable_feature_result else None,
                        "missingYamlPatchSuggestions": [asdict(item) for item in result.missing_yaml_patch_suggestions],
                    }
                    self._send_json(status, out)
                    return

                self._send_json(404, {"error": "not found"})
            except Exception as exc:
                self._send_json(500, {"error": str(exc) or exc.__class__.__name__})

    server = ThreadingHTTPServer(("127.0.0.1", port), Handler)
    thread = threading.Thread(target=server.serve_forever, daemon=True)
    thread.start()
    return server
