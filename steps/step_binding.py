"""Keyword-agnostic step registration (Cucumber-JVM parity).

Cucumber-JVM matches steps by text only; Given/When/Then/And are interchangeable.
pytest-bdd's generic ``step`` decorator (``type_=None``) matches all keywords.
"""

from pytest_bdd.steps import step

__all__ = ["step"]
