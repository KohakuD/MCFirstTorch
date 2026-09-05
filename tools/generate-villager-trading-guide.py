"""Generate the Villager trading guide from exact Minecraft 26.1.2 GUI assets."""

from io import BytesIO
import os
from pathlib import Path
from zipfile import ZipFile

from PIL import Image, ImageDraw


JAR = Path(os.environ.get(
    "FIRST_TORCH_MINECRAFT_JAR",
    r"C:\Games\CurseForge\Minecraft\Install\versions\26.1.2\26.1.2.jar",
))
OUT = Path(__file__).resolve().parents[1] / "overrides/resourcepacks/first_torch_guides/assets/firsttorch/textures/questpics/villager_trading.png"
W, H = 1672, 941


def asset(archive, path):
    return Image.open(BytesIO(archive.read(path))).convert("RGBA")


def paste_item(canvas, texture, x, y):
    canvas.alpha_composite(texture.resize((16, 16), Image.Resampling.NEAREST), (x, y))


def pixel_number(draw, value, x, y):
    glyphs = {
        "0": ("111", "101", "101", "101", "111"),
        "2": ("111", "001", "111", "100", "111"),
    }
    cursor = x
    for character in str(value):
        rows = glyphs[character]
        for row, bits in enumerate(rows):
            for column, bit in enumerate(bits):
                if bit == "1":
                    draw.rectangle((cursor + column, y + row, cursor + column, y + row), fill=(255, 255, 255, 255))
        cursor += 4


with ZipFile(JAR) as archive:
    gui = asset(archive, "assets/minecraft/textures/gui/container/villager.png").crop((0, 0, 276, 166))
    draw = ImageDraw.Draw(gui)
    arrow = asset(archive, "assets/minecraft/textures/gui/sprites/container/villager/trade_arrow.png")
    xp_back = asset(archive, "assets/minecraft/textures/gui/sprites/container/villager/experience_bar_background.png")
    xp_fill = asset(archive, "assets/minecraft/textures/gui/sprites/container/villager/experience_bar_current.png")
    wheat = asset(archive, "assets/minecraft/textures/item/wheat.png")
    emerald = asset(archive, "assets/minecraft/textures/item/emerald.png")

    # One clearly selected example offer in the list at left.
    draw.rectangle((5, 18, 88, 38), fill=(116, 116, 116, 255), outline=(255, 255, 255, 255))
    paste_item(gui, wheat, 10, 20)
    gui.alpha_composite(arrow, (42, 23))
    paste_item(gui, emerald, 66, 20)
    pixel_number(draw, 20, 23, 31)

    # The same example in the two input spaces and output space.
    gui.alpha_composite(xp_back, (136, 17))
    gui.alpha_composite(xp_fill.crop((0, 0, 36, xp_fill.height)), (136, 17))
    paste_item(gui, wheat, 136, 36)
    paste_item(gui, emerald, 216, 34)
    pixel_number(draw, 20, 145, 47)

    # Orange teaching overlays frame cost and result without changing the GUI itself.
    draw.rectangle((133, 33, 155, 56), outline=(255, 145, 0, 255), width=2)
    draw.rectangle((213, 31, 241, 59), outline=(255, 145, 0, 255), width=2)

    scale = 4
    enlarged = gui.resize((gui.width * scale, gui.height * scale), Image.Resampling.NEAREST)
    output = Image.new("RGBA", (W, H), (20, 21, 23, 255))
    frame = ImageDraw.Draw(output)
    left = (W - enlarged.width) // 2
    top = (H - enlarged.height) // 2
    frame.rounded_rectangle((left - 28, top - 28, left + enlarged.width + 28, top + enlarged.height + 28), radius=18, fill=(35, 36, 39, 255), outline=(105, 108, 112, 255), width=4)
    output.alpha_composite(enlarged, (left, top))
    output.convert("RGB").save(OUT, optimize=True)

print(OUT)
