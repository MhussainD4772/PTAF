from __future__ import annotations

from pathlib import Path

from ptaf.ai.browser.browser_page_context import BrowserPageContext
from ptaf.ai.config.ai_assistant_properties import AiAssistantProperties
from ptaf.ai.context.framework_context_collector import FrameworkContextCollector
from ptaf.ai.context.similar_feature_retriever import SimilarFeatureRetriever
from ptaf.ai.gemini_client import AiModelClient, GeminiModelClient
from ptaf.ai.index.step_definition_index import StepDefinitionIndex
from ptaf.ai.index.yaml_key_index import YamlKeyIndex
from ptaf.ai.model.generation_result import GenerationResult
from ptaf.ai.parser.structured_ai_response_parser import parse
from ptaf.ai.policy.ai_policy import AiPolicy
from ptaf.ai.prompt_builder import PromptBuilder
from ptaf.ai.validation.allowed_yaml_guard import AllowedYamlGuard
from ptaf.ai.validation.missing_yaml_patch_suggester import MissingYamlPatchSuggester
from ptaf.ai.validation.page_frame_context_guard import PageFrameContextGuard
from ptaf.ai.validation.runnable_feature_gate import RunnableFeatureGate
from ptaf.ai.validation.step_reuse_validator import StepReuseValidator
from ptaf.ai.validation.yaml_key_validator import YamlKeyValidator


class FeatureGeneratorService:
    def __init__(
        self,
        properties: AiAssistantProperties,
        policy: AiPolicy | None = None,
        model_client: AiModelClient | None = None,
    ) -> None:
        self._properties = properties
        self._policy = policy or AiPolicy()
        self._model_client = model_client or GeminiModelClient()
        self._framework_context_collector = FrameworkContextCollector(properties)
        self._similar_feature_retriever = SimilarFeatureRetriever(properties)
        self._prompt_builder = PromptBuilder(properties)

    def generate(
        self,
        project_root: Path,
        requirement: str,
        *,
        browser_context: BrowserPageContext | None = None,
    ) -> GenerationResult:
        blocked = self._policy.validate_requirement(requirement)
        if blocked:
            raise RuntimeError(f"Policy rejected requirement: {blocked}")

        framework_context = self._framework_context_collector.collect(project_root)
        similar = self._similar_feature_retriever.retrieve(requirement, framework_context)
        system = self._prompt_builder.system_prompt()
        user = self._prompt_builder.user_prompt(
            requirement,
            framework_context,
            similar,
            browser_context=browser_context,
        )
        raw = self._model_client.generate(system, user, self._properties)
        structured = parse(raw)
        step_index = StepDefinitionIndex.build(project_root, self._properties.step_definition_paths())
        step_reuse_validation = StepReuseValidator().validate(structured, step_index)
        yaml_index = YamlKeyIndex.build(project_root, self._properties.yaml_paths())
        yaml_validation = YamlKeyValidator().validate(structured, yaml_index)
        allowed_yaml_guard_result = AllowedYamlGuard().validate(structured, yaml_index)
        page_frame_context_guard_result = PageFrameContextGuard().validate(
            structured,
            self._properties.default_ui_context(),
            self._properties.frame_allowed_pages(),
            self._properties.frame_allowed_locators(),
        )
        runnable_feature_result = RunnableFeatureGate().evaluate(
            structured,
            step_reuse_validation,
            yaml_validation,
            allowed_yaml_guard_result,
            page_frame_context_guard_result,
        )
        missing_yaml_patch_suggestions = MissingYamlPatchSuggester().suggest(
            yaml_validation,
            allowed_yaml_guard_result,
        )
        return GenerationResult(
            feature_gherkin=structured.feature_file,
            suggested_reusable_steps=structured.reused_steps,
            raw_model_response=raw,
            reuse_trace=[],
            structured_response=structured,
            step_reuse_validation_result=step_reuse_validation,
            yaml_key_validation_result=yaml_validation,
            allowed_yaml_guard_result=allowed_yaml_guard_result,
            page_frame_context_guard_result=page_frame_context_guard_result,
            runnable_feature_result=runnable_feature_result,
            missing_yaml_patch_suggestions=missing_yaml_patch_suggestions,
        )

    def write_feature_file(self, output_file: Path, result: GenerationResult, overwrite: bool) -> Path:
        if output_file.exists() and not overwrite:
            raise RuntimeError(
                f"Output file already exists: {output_file} (use --overwrite to replace)"
            )
        output_file.parent.mkdir(parents=True, exist_ok=True)
        content = result.feature_gherkin.strip() + "\n"
        output_file.write_text(content, encoding="utf-8")
        return output_file
