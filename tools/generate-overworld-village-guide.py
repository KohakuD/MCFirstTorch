"""Prepare the real Minecraft 26.1.2 Village screenshot for the quest guide."""

from pathlib import Path

from PIL import Image


ROOT = Path(__file__).resolve().parents[1]
SOURCE = ROOT / "tools/artwork/village_overview_source.png"
OUTPUT = ROOT / "overrides/resourcepacks/first_torch_guides/assets/firsttorch/textures/questpics/village_overview.png"
OUTPUT_SIZE = (1672, 941)


def create_guide(source):
    """Crop the ultrawide screenshot to the established 16:9 in-world guide format."""
    image = Image.open(source).convert("RGB")
    target_ratio = OUTPUT_SIZE[0] / OUTPUT_SIZE[1]

    # Remove the hotbar while retaining the first-person hand and the full Village view.
    bottom = int(image.height * 0.927)
    crop_height = bottom
    crop_width = round(crop_height * target_ratio)
    left = round(image.width * 0.143)
    if left + crop_width > image.width:
        left = image.width - crop_width

    cropped = image.crop((left, 0, left + crop_width, bottom))
    return cropped.resize(OUTPUT_SIZE, Image.Resampling.LANCZOS)


OUTPUT.parent.mkdir(parents=True, exist_ok=True)
create_guide(SOURCE).save(OUTPUT, optimize=True)
print(OUTPUT)
