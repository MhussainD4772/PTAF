"""Register all Gherkin feature files with pytest-bdd."""

from __future__ import annotations

from pathlib import Path

from pytest_bdd import scenarios

_STEPS_DIR = Path(__file__).resolve().parent
_FEATURES_DIR = _STEPS_DIR.parent / "features"

for _feature_path in sorted(_FEATURES_DIR.rglob("*.feature")):
    _relative = Path("..") / "features" / _feature_path.relative_to(_FEATURES_DIR)
    scenarios(str(_relative))
