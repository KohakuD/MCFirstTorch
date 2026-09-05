"""Install the approved Village illustration derived from a 26.1.2 screenshot."""

from pathlib import Path

from PIL import Image


ROOT = Path(__file__).resolve().parents[1]
REFERENCE = ROOT / "tools/artwork/village_overview_reference.png"
SOURCE = ROOT / "tools/artwork/village_overview_generated.png"
OUTPUT = ROOT / "overrides/resourcepacks/first_torch_guides/assets/firsttorch/textures/questpics/village_overview.png"
OUTPUT_SIZE = (1672, 941)


def create_guide(source):
    """Verify and copy the approved illustration at the established guide size."""
    image = Image.open(source).convert("RGB")
    if image.size != OUTPUT_SIZE:
        raise ValueError(f"Expected {OUTPUT_SIZE}, got {image.size}")
    if not REFERENCE.is_file():
        raise FileNotFoundError("The user-provided Minecraft screenshot reference is missing")
    return image


OUTPUT.parent.mkdir(parents=True, exist_ok=True)
create_guide(SOURCE).save(OUTPUT, optimize=True)
print(OUTPUT)
