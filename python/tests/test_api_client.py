"""Unit and integration tests for the API layer."""

from __future__ import annotations

import json
from unittest.mock import MagicMock

import pytest

from ptaf.api.api_client import (
    ApiActionImpl,
    ApiActionPerformer,
    ApiRequestHandler,
    ApiResponseWrapper,
)
from ptaf.api.api_common import ApiCommonMethods


class TestApiActionPerformer:
    def test_replace_path_parameters(self) -> None:
        endpoint = ApiActionPerformer._replace_path_parameters(
            "/posts/{postId}",
            {"postId": "101"},
        )
        assert endpoint == "/posts/101"

    def test_send_get_request(self) -> None:
        context = MagicMock()
        response = MagicMock()
        response.status = 200
        response.text.return_value = '{"id": 101}'
        response.headers = {"content-type": "application/json"}
        context.get.return_value = response

        wrapper = ApiActionPerformer().send_request(
            context,
            "GET",
            "/posts/{postId}",
            None,
            None,
            {"postId": "101"},
            None,
        )

        context.get.assert_called_once_with("/posts/101")
        assert wrapper.status_code == 200
        assert wrapper.body == '{"id": 101}'


class TestApiActionImpl:
    def test_json_path_extraction(self) -> None:
        action = ApiActionImpl()
        action._performer = MagicMock()
        action._performer.send_request.return_value = ApiResponseWrapper(
            status_code=200,
            body='{"title": "My Awesome Post", "id": 101}',
            headers={},
        )

        action.send_request("jsonplaceholder", "jsonplaceholder_requests.get_single_post")
        assert action.get_value_from_response("$.title") == "My Awesome Post"
        assert action.get_value_from_response("$.id") == 101


@pytest.mark.api
def test_jsonplaceholder_get_all_posts_integration() -> None:
    api = ApiCommonMethods()
    api.send_request("jsonplaceholder", "jsonplaceholder_requests.get_all_posts")
    api.verify_response_status_code(200)
    body = api._api_action.get_response_body()
    posts = json.loads(body)
    assert isinstance(posts, list)
    assert posts
    ApiRequestHandler.dispose_context()
