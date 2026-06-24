from __future__ import annotations

import json
import os
import urllib.error
import urllib.parse
import urllib.request
from typing import Protocol

from ptaf.ai.config.ai_assistant_properties import AiAssistantProperties
from ptaf.ai.telemetry.telemetry import log_gemini_response

_DEFAULT_BASE = "https://generativelanguage.googleapis.com"


class AiModelClient(Protocol):
    def generate(self, system_prompt: str, user_prompt: str, props: AiAssistantProperties) -> str:
        ...


class GeminiClient:
    def generate_content(
        self,
        system_prompt: str,
        user_prompt: str,
        props: AiAssistantProperties,
    ) -> str:
        return self.generate_for_operation("generate", system_prompt, user_prompt, props)

    def generate_raw(
        self,
        system_prompt: str,
        user_prompt: str,
        props: AiAssistantProperties,
    ) -> str:
        return self.generate_for_operation("triage", system_prompt, user_prompt, props)

    def generate_for_operation(
        self,
        operation: str,
        system_prompt: str,
        user_prompt: str,
        props: AiAssistantProperties,
    ) -> str:
        api_key = props.api_key()
        if not api_key:
            raise RuntimeError(f"Set {props.gemini_api_key_env_name()} (Google AI Studio API key)")
        model = props.model()
        if not model:
            raise RuntimeError("Set model in ai_assistant.yml or GEMINI_MODEL")

        base = os.environ.get("GEMINI_API_BASE", _DEFAULT_BASE).rstrip("/")
        url = (
            f"{base}/v1beta/models/{urllib.parse.quote(model, safe='')}:generateContent"
            f"?key={urllib.parse.quote(api_key, safe='')}"
        )
        body = {
            "systemInstruction": {"parts": [{"text": system_prompt}]},
            "contents": [{"role": "user", "parts": [{"text": user_prompt}]}],
            "generationConfig": {
                "temperature": props.temperature(),
                "maxOutputTokens": props.max_output_tokens(),
            },
        }
        payload = json.dumps(body).encode("utf-8")
        request = urllib.request.Request(
            url,
            data=payload,
            headers={"Content-Type": "application/json"},
            method="POST",
        )
        try:
            with urllib.request.urlopen(request, timeout=120) as response:
                response_body = response.read().decode("utf-8")
        except urllib.error.HTTPError as exc:
            error_body = exc.read().decode("utf-8", errors="replace")
            raise RuntimeError(f"Gemini HTTP {exc.code}: {error_body}") from exc

        log_gemini_response(operation or "unknown", model, response_body)
        parsed = json.loads(response_body)
        text = parsed.get("candidates", [{}])[0].get("content", {}).get("parts", [{}])[0].get("text", "")
        if not text or not str(text).strip():
            raise RuntimeError(f"Unexpected Gemini response (no text): {response_body}")
        return str(text)


class GeminiModelClient:
    def __init__(self, delegate: GeminiClient | None = None) -> None:
        self._delegate = delegate or GeminiClient()

    def generate(self, system_prompt: str, user_prompt: str, props: AiAssistantProperties) -> str:
        return self._delegate.generate_content(system_prompt, user_prompt, props)
