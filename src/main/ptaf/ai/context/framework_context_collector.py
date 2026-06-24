from __future__ import annotations

from pathlib import Path

from ptaf.ai.config.ai_assistant_properties import AiAssistantProperties
from ptaf.ai.context.framework_generation_context import FrameworkGenerationContext
from ptaf.ai.index.step_definition_index import StepDefinitionIndex
from ptaf.ai.index.yaml_key_index import YamlKeyIndex


class FrameworkContextCollector:
    def __init__(self, properties: AiAssistantProperties) -> None:
        self._properties = properties

    def collect(self, project_root: Path) -> FrameworkGenerationContext:
        step_index = StepDefinitionIndex.build(project_root, self._properties.context_step_definition_paths())
        yaml_index = YamlKeyIndex.build(project_root, self._properties.context_yaml_paths())

        feature_snippets = self._collect_feature_snippets(project_root)
        step_definitions = step_index.known_steps()
        yaml_keys = sorted(yaml_index.normalized_keys())

        ui_keys = [key for key in yaml_keys if key.startswith("elements.")]
        api_keys = [key for key in yaml_keys if key.startswith("api_requests.")]
        db_keys = [key for key in yaml_keys if key.startswith("queries.")]

        return FrameworkGenerationContext(
            existing_feature_snippets=feature_snippets,
            existing_step_definitions=step_definitions,
            existing_yaml_keys=yaml_keys,
            ui_element_keys=ui_keys,
            api_request_keys=api_keys,
            db_query_keys=db_keys,
        )

    def _collect_feature_snippets(self, project_root: Path) -> list[str]:
        feature_files: list[Path] = []
        for relative_path in self._properties.context_feature_paths():
            if not relative_path or not relative_path.strip():
                continue
            root = (project_root / relative_path).resolve()
            if not root.is_dir():
                continue
            feature_files.extend(
                sorted(
                    path
                    for path in root.rglob("*.feature")
                    if path.is_file()
                )
            )

        limit = max(0, self._properties.context_max_feature_snippets())
        snippets: list[str] = []
        for file_path in feature_files:
            if len(snippets) >= limit:
                break
            content = file_path.read_text(encoding="utf-8")
            snippet = _build_feature_snippet(content)
            if snippet.strip():
                snippets.append(snippet)
        return snippets


def _build_feature_snippet(content: str | None) -> str:
    if not content or not content.strip():
        return ""
    lines: list[str] = []
    for raw_line in content.splitlines():
        line = raw_line.strip()
        if not line or line.startswith("#"):
            continue
        lower = line.lower()
        if (
            lower.startswith("feature:")
            or lower.startswith("scenario:")
            or lower.startswith("scenario outline:")
            or lower.startswith("given ")
            or lower.startswith("when ")
            or lower.startswith("then ")
            or lower.startswith("and ")
            or lower.startswith("but ")
            or lower.startswith("@")
        ):
            lines.append(line)
        if len(lines) >= 12:
            break
    return "\n".join(lines)
