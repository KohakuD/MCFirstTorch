"""Reproduce non-generative grading of the owner's local Fortress screenshots."""
from pathlib import Path
from PIL import Image

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / "overrides/resourcepacks/first_torch_guides/assets/firsttorch/textures/questpics"

# Source captures are intentionally local, not distributed with the repository.
for capture, target, gamma in [
    ("2026-09-08_16.12.00.png", "nether_wart_garden.png", 0.50),
    ("2026-09-08_16.13.39.png", "blaze_spawner.png", 0.65),
]:
    with Image.open(ROOT / "run/screenshots" / capture) as original:
        source = original.convert("RGB")
        # Monotonic lookup curve lifts dark textures without spatial edits or clipping.
        curve = [round(255 * (value / 255) ** gamma) for value in range(256)]
        graded = source.point(curve * 3)
        graded.save(OUT / target, optimize=True)
        print(f"{target}: {graded.width} x {graded.height}, gamma={gamma}")
