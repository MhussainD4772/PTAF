"""Element locator parsing and YAML lookup."""

from __future__ import annotations

import logging

from ptaf.utils import yaml_reader

logger = logging.getLogger(__name__)


class ElementLocatorHelper:
    """Retrieve and parse element locators from YAML configuration."""

    def get_element(self, element: str, key: str) -> str:
        """Get the locator value for an element name and key."""
        try:
            value = yaml_reader.get(f"elements.{element}.{key}")
            if value is None:
                raise KeyError(f"elements.{element}.{key}")
            return str(value)
        except Exception:
            logger.error(
                "Failed to retrieve selector for element '%s'",
                element + key,
                exc_info=True,
            )
            raise

    def get_locator_type(self, locator_value: str) -> str:
        """Extract locator type (before first underscore)."""
        first_underscore_index = locator_value.find("_")
        if first_underscore_index != -1:
            return locator_value[:first_underscore_index]
        return ""

    def get_locator(self, locator_value: str) -> str:
        """Extract locator string (after first underscore)."""
        first_underscore_index = locator_value.find("_")
        if (
            first_underscore_index != -1
            and first_underscore_index + 1 < len(locator_value)
        ):
            return locator_value[first_underscore_index + 1 :]
        return ""
