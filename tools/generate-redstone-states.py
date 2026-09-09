"""Generate schematic Redstone state diagrams from Minecraft 26.1.2 assets.

Original component icons identify inputs. Arrows and comparison lettering
are neutral lesson notation explained by the quests.
"""
import argparse
import importlib.util
from pathlib import Path
from zipfile import ZipFile

from PIL import Image, ImageDraw


ROOT = Path(__file__).resolve().parents[1]
SPEC = importlib.util.spec_from_file_location("redstone_followup", ROOT / "tools/generate-redstone-followup.py")
FOLLOWUP = importlib.util.module_from_spec(SPEC)
SPEC.loader.exec_module(FOLLOWUP)
PLANS = FOLLOWUP.PLANS


def lamp_cell(image, lamp, x, y, size):
    """Place the unmodified vanilla lamp texture in a consistently framed cell."""
    image.alpha_composite(lamp.resize((size, size), Image.Resampling.NEAREST), (x, y))
    ImageDraw.Draw(image).rectangle((x, y, x + size, y + size), outline=(12, 13, 14, 255), width=4)


def state_row(archive, image, marker, lamps, row_label, top, size=132):
    """Render one neutral input marker followed by an off/on state sequence."""
    draw = ImageDraw.Draw(image)
    left = 265
    PLANS.label(draw, 150, top + 38, row_label)
    PLANS.component_cell(archive, image, left, top, size, marker)
    positions = [left + 300, left + 610, left + 920]
    for index, (lamp, x) in enumerate(zip(lamps, positions, strict=True)):
        lamp_cell(image, lamp, x, top, size)
        if index < len(positions) - 1:
            PLANS.arrow(draw, x + size + 36, top + size // 2, 140)


def save_inputs(archive, lamp_off, lamp_on):
    image = PLANS.background()
    state_row(archive, image, "L", (lamp_off, lamp_on, lamp_off), "A", 130)
    state_row(archive, image, "B", (lamp_off, lamp_on, lamp_off), "B", 405)
    state_row(archive, image, "P", (lamp_off, lamp_on, lamp_off), "C", 680)
    image.save(PLANS.OUT / "redstone_inputs.png", optimize=True)


def save_comparator(lamp_off, lamp_on, cobblestone):
    image = PLANS.background()
    draw = ImageDraw.Draw(image)
    rows = (("A", "0", lamp_off), ("B", "64", lamp_off), ("C", "128", lamp_on))
    size = 150
    for row_label, count, lamp, top in ((a, b, c, 128 + i * 275) for i, (a, b, c) in enumerate(rows)):
        PLANS.label(draw, 150, top + 45, row_label)
        swatch_x = 350
        lamp_cell(image, cobblestone, swatch_x, top, size)
        # Keep quantities off the busy original texture for half-pane reading.
        draw.rounded_rectangle((swatch_x + 165, top + 43, swatch_x + 295, top + 108),
                               radius=6, fill=(15, 16, 17), outline=PLANS.LINE, width=2)
        draw.text((swatch_x + 230, top + 75), count, font=PLANS.font(48), anchor="mm", fill=PLANS.GOLD)
        PLANS.arrow(draw, swatch_x + 320, top + size // 2, 190)
        lamp_cell(image, lamp, 885, top, size)
    image.save(PLANS.OUT / "comparator_states.png", optimize=True)


def save_observer(lamp_off, lamp_on):
    image = PLANS.background()
    draw = ImageDraw.Draw(image)
    size = 220
    top = 340
    left = 350
    for index, (label, lamp) in enumerate(zip(("A", "B", "C"), (lamp_off, lamp_on, lamp_off), strict=True)):
        x = left + index * 360
        PLANS.label(draw, x + size // 2, top - 72, label)
        lamp_cell(image, lamp, x, top, size)
        if index < 2:
            PLANS.arrow(draw, x + size + 35, top + size // 2, 90)
    image.save(PLANS.OUT / "observer_pulse.png", optimize=True)


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--minecraft-jar", type=Path, required=True)
    args = parser.parse_args()
    PLANS.OUT.mkdir(parents=True, exist_ok=True)
    with ZipFile(args.minecraft_jar) as archive:
        lamp_off = PLANS.asset(archive, "assets/minecraft/textures/block/redstone_lamp.png")
        lamp_on = PLANS.asset(archive, "assets/minecraft/textures/block/redstone_lamp_on.png")
        cobblestone = PLANS.asset(archive, "assets/minecraft/textures/block/cobblestone.png")
        save_inputs(archive, lamp_off, lamp_on)
        save_comparator(lamp_off, lamp_on, cobblestone)
        save_observer(lamp_off, lamp_on)
    for name in ("redstone_inputs.png", "comparator_states.png", "observer_pulse.png"):
        print(PLANS.OUT / name)


if __name__ == "__main__":
    main()
