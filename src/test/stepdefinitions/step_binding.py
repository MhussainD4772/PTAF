"""Keyword-agnostic step registration (Cucumber-JVM parity).

pytest-bdd ``step`` matches Given / When / Then / And interchangeably::

    from pytest_bdd import parsers
    from stepdefinitions.step_binding import keyword_step

    @keyword_step(parsers.parse("we click on page {element} locator {locator}"))
    def we_click_action_on_page(page, element, locator): ...
"""

from pytest_bdd.steps import step as keyword_step

__all__ = ["keyword_step"]
