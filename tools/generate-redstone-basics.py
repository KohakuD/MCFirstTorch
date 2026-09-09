"""Illustrate the short wire, wire-length limit and settled piston comparison."""
import argparse
import importlib.util
from pathlib import Path
from zipfile import ZipFile
from PIL import Image, ImageDraw

ROOT = Path(__file__).resolve().parents[1]
SPEC = importlib.util.spec_from_file_location("followup", ROOT / "tools/generate-redstone-followup.py")
FOLLOWUP = importlib.util.module_from_spec(SPEC)
SPEC.loader.exec_module(FOLLOWUP)
PLANS = FOLLOWUP.PLANS


def letter(image, y, text):
    ImageDraw.Draw(image).text((150, y), text, font=PLANS.font(56), fill=PLANS.GOLD)


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--minecraft-jar", required=True, type=Path)
    args = parser.parse_args()
    with ZipFile(args.minecraft_jar) as archive:
        off = PLANS.asset(archive, "assets/minecraft/textures/block/redstone_lamp.png")
        on = PLANS.asset(archive, "assets/minecraft/textures/block/redstone_lamp_on.png")
        stone = PLANS.asset(archive, "assets/minecraft/textures/block/cobblestone.png")
        normal = PLANS.model_top(archive, "piston").transpose(Image.Transpose.ROTATE_270)
        sticky = PLANS.model_top(archive, "sticky_piston").transpose(Image.Transpose.ROTATE_270)
        image = PLANS.background()
        FOLLOWUP.row(image, ["L", "D", "D", "D", off], ["0", "1", "2", "3", "4"], 145, 180)
        FOLLOWUP.row(image, ["L", "D", "D", "D", on], ["0", "1", "2", "3", "4"], 565, 180)
        letter(image, 205, "A")
        letter(image, 625, "B")
        image.save(PLANS.OUT / "redstone_short_line.png", optimize=True)

        image = PLANS.background()
        FOLLOWUP.row(image, ["L", "D x15", "D", off], ["0", "1-15", "16", "17"], 145, 200)
        FOLLOWUP.row(image, ["L", "D x15", on, ""], ["0", "1-15", "16", "17"], 565, 200)
        letter(image, 205, "A")
        letter(image, 625, "B")
        image.save(PLANS.OUT / "redstone_dust_limit.png", optimize=True)

        # Both rows show the final, unpowered/retracted state after one slow
        # on/off operation, never an approximation of the extended model.
        image = PLANS.background()
        FOLLOWUP.row(image, [normal, "", stone], ["0", "1", "2"], 145, 180, show_arrow=False)
        FOLLOWUP.row(image, [sticky, stone, ""], ["0", "1", "2"], 565, 180, show_arrow=False)
        letter(image, 205, "A")
        letter(image, 625, "B")
        # The matching front faces identify A/B; horizontal top views alone
        # cannot distinguish normal and sticky piston bases.
        for y, texture in ((145, "piston_top"), (565, "piston_top_sticky")):
            face = PLANS.asset(archive, "assets/minecraft/textures/block/" + texture + ".png")
            image.alpha_composite(face.resize((120, 120), Image.Resampling.NEAREST), (1300, y + 25))
        image.save(PLANS.OUT / "piston_return.png", optimize=True)
    for name in ("redstone_short_line", "redstone_dust_limit", "piston_return"):
        print(PLANS.OUT / (name + ".png"))


if __name__ == "__main__":
    main()
