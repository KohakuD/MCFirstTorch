"""Generate delay, condensed range and two-sided doorway lesson diagrams."""
import argparse
import importlib.util
from pathlib import Path
from zipfile import ZipFile
from PIL import Image, ImageDraw

ROOT = Path(__file__).resolve().parents[1]
SPEC = importlib.util.spec_from_file_location("plans", ROOT / "tools/generate-redstone-plans.py")
PLANS = importlib.util.module_from_spec(SPEC)
SPEC.loader.exec_module(PLANS)


def row(image, cells, labels, top, size, show_arrow=True, *, archive):
    draw = ImageDraw.Draw(image)
    left = (PLANS.W - len(cells) * size) // 2
    for index, (cell, label) in enumerate(zip(cells, labels, strict=True)):
        x = left + index * size
        PLANS.label(draw, x + size // 2, top - 64, label)
        if isinstance(cell, str):
            PLANS.component_cell(archive, image, x, top, size, cell)
        else:
            image.alpha_composite(cell.resize((size, size), Image.Resampling.NEAREST), (x, top))
            draw.rectangle((x, top, x + size, top + size), outline=(12, 13, 14), width=4)
    if show_arrow:
        PLANS.arrow(draw, left + size // 3, top + size + 48, (len(cells) - 1) * size)


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--minecraft-jar", required=True, type=Path)
    args = parser.parse_args()
    with ZipFile(args.minecraft_jar) as archive:
        short = PLANS.model_top(archive, "repeater_1tick").transpose(Image.Transpose.ROTATE_270)
        long = PLANS.model_top(archive, "repeater_4tick").transpose(Image.Transpose.ROTATE_270)
        lamp = PLANS.asset(archive, "assets/minecraft/textures/block/redstone_lamp.png")
        image = PLANS.background()
        row(image, ["L", short, short, lamp], ["0", "1", "2", "3"], 135, 180, archive=archive)
        row(image, ["L", long, long, lamp], ["0", "1", "2", "3"], 565, 180, archive=archive)
        draw = ImageDraw.Draw(image)
        draw.text((180, 205), "A", font=PLANS.font(56), fill=PLANS.GOLD)
        draw.text((180, 635), "B", font=PLANS.font(56), fill=PLANS.GOLD)
        image.save(PLANS.OUT / "repeater_delay.png", optimize=True)

        image = PLANS.background()
        # D groups are explicitly condensed, not single world positions.
        row(image, ["L", "D x15", short, "D x3", lamp],
            ["0", "1-15", "16", "17-19", "20"], 285, 220, archive=archive)
        image.save(PLANS.OUT / "repeater_range.png", optimize=True)

        image = PLANS.background()
        # Original icons identify the three floor positions, not perspective.
        row(image, ["P", "T", "P"], ["0", "1", "2"], 290, 240, archive=archive)
        draw = ImageDraw.Draw(image)
        # A second mirrored arrow indicates the return walk, not wiring.
        arrow_layer = Image.new("RGBA", image.size)
        PLANS.arrow(ImageDraw.Draw(arrow_layer), 556, 650, 560)
        image.alpha_composite(arrow_layer.transpose(Image.Transpose.FLIP_LEFT_RIGHT))
        image.save(PLANS.OUT / "iron_door_plan.png", optimize=True)
    for name in ("repeater_delay", "repeater_range", "iron_door_plan"):
        print(PLANS.OUT / (name + ".png"))


if __name__ == "__main__":
    main()
