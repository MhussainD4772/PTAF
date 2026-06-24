from __future__ import annotations

import re

from ptaf.ai.config.ai_assistant_properties import AiAssistantProperties
from ptaf.ai.context.framework_generation_context import FrameworkGenerationContext

_NON_WORD = re.compile(r"[^a-z0-9\s]")
_STOP_WORDS = {
    "the", "a", "an", "to", "of", "in", "on", "for", "with",
    "and", "or", "is", "are", "be", "should", "can", "user",
}


class SimilarFeatureRetriever:
    def __init__(
        self,
        properties: AiAssistantProperties | None = None,
        *,
        max_similar_features: int | None = None,
        min_similarity_score: int | None = None,
    ) -> None:
        if properties is not None:
            self._max_similar_features = max(0, properties.context_max_similar_features())
            self._min_similarity_score = max(0, properties.context_min_similarity_score())
        else:
            self._max_similar_features = max(0, max_similar_features or 0)
            self._min_similarity_score = max(0, min_similarity_score or 0)

    def retrieve(self, requirement: str | None, context: FrameworkGenerationContext | None) -> list[str]:
        if not requirement or not requirement.strip():
            return []
        if not context or not context.existing_feature_snippets:
            return []

        req_tokens = _tokenize(requirement)
        if not req_tokens:
            return []

        ranked: list[tuple[str, int]] = []
        for snippet in context.existing_feature_snippets:
            if not snippet or not snippet.strip():
                continue
            score = _overlap_score(req_tokens, _tokenize(snippet))
            if score >= self._min_similarity_score:
                ranked.append((snippet, score))

        ranked.sort(key=lambda item: (-item[1], len(item[0])))
        return [snippet for snippet, _ in ranked[: self._max_similar_features]]


def _overlap_score(req_tokens: set[str], snippet_tokens: set[str]) -> int:
    return sum(1 for token in req_tokens if token in snippet_tokens)


def _tokenize(text: str) -> set[str]:
    normalized = _NON_WORD.sub(" ", text.lower())
    tokens: set[str] = set()
    for token in normalized.split():
        if token and token not in _STOP_WORDS:
            tokens.add(token)
    return tokens
