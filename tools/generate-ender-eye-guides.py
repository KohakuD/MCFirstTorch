"""Generate Ender Eye quest guides from exact Minecraft 26.1.2 assets."""

from io import BytesIO
from pathlib import Path
from zipfile import ZipFile

from PIL import Image, ImageDraw


JAR = Path(r"D:\Minecraft\curseforge\minecraft\Install\versions\26.1.2\26.1.2.jar")
OUT = Path(__file__).resolve().parents[1] / "overrides/resourcepacks/first_torch_guides/assets/firsttorch/textures/questpics"
W, H = 1672, 941


def asset(archive, path):
    return Image.open(BytesIO(archive.read(path))).convert("RGBA")


def background():
    image = Image.new("RGBA", (W, H), (31, 32, 33, 255))
    pixels = image.load()
    for y in range(6, H - 6):
        shade = int(47 - 12 * y / H)
        for x in range(6, W - 6):
            vignette = int(5 * abs(x - W / 2) / (W / 2))
            value = max(25, shade - vignette)
            pixels[x, y] = (value, value, value, 255)
    draw = ImageDraw.Draw(image)
    draw.rectangle((2, 2, W - 3, H - 3), outline=(7, 8, 9, 255), width=6)
    draw.line((8, 8, W - 9, 8), fill=(126, 129, 130, 255), width=3)
    draw.line((8, 9, 8, H - 9), fill=(91, 94, 96, 255), width=3)
    return image


def slot(draw, x, y, size=124):
    draw.rectangle((x, y, x + size, y + size), fill=(29, 31, 32, 255), outline=(5, 6, 7, 255), width=8)
    draw.line((x + 8, y + 8, x + size - 8, y + 8), fill=(153, 156, 158, 255), width=4)
    draw.line((x + 8, y + 8, x + 8, y + size - 8), fill=(115, 118, 120, 255), width=4)


def paste_icon(image, texture, x, y, slot_size=124, icon_size=92):
    icon = texture.resize((icon_size, icon_size), Image.Resampling.NEAREST)
    image.alpha_composite(icon, (x + (slot_size - icon_size) // 2, y + (slot_size - icon_size) // 2))


def arrow(draw, left, top, width=155, height=96):
    points = [
        (left, top + height // 3),
        (left + width * 3 // 5, top + height // 3),
        (left + width * 3 // 5, top),
        (left + width, top + height // 2),
        (left + width * 3 // 5, top + height),
        (left + width * 3 // 5, top + height * 2 // 3),
        (left, top + height * 2 // 3),
    ]
    draw.polygon(points, fill=(194, 195, 196, 255))


OUT.mkdir(parents=True, exist_ok=True)
with ZipFile(JAR) as archive:
    ender_pearl = asset(archive, "assets/minecraft/textures/item/ender_pearl.png")
    blaze_powder = asset(archive, "assets/minecraft/textures/item/blaze_powder.png")
    ender_eye = asset(archive, "assets/minecraft/textures/item/ender_eye.png")

    image = background()
    draw = ImageDraw.Draw(image)
    left, top, cell = 360, 345, 126
    for row in range(2):
        for col in range(2):
            slot(draw, left + col * cell, top + row * cell, cell)
    paste_icon(image, ender_pearl, left, top, cell)
    paste_icon(image, blaze_powder, left + cell, top, cell)
    arrow(draw, 720, 422)
    out_x, out_y, out_size = 1010, 345, 205
    slot(draw, out_x, out_y, out_size)
    paste_icon(image, ender_eye, out_x, out_y, out_size, 124)
    image.save(OUT / "ender_eye_recipe.png", optimize=True)

print(OUT / "ender_eye_recipe.png")
