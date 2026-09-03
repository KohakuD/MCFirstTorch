"""Generate Ender Eye quest guides from exact Minecraft 26.1.2 assets."""

from io import BytesIO
from pathlib import Path
from zipfile import ZipFile

from PIL import Image, ImageDraw, ImageFont


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


def path_arrow(draw, start, end):
    colour = (255, 137, 0, 255)
    draw.line((start, end), fill=colour, width=18)
    dx, dy = end[0] - start[0], end[1] - start[1]
    length = max(1, (dx * dx + dy * dy) ** 0.5)
    ux, uy = dx / length, dy / length
    px, py = -uy, ux
    tip = end
    base = (end[0] - ux * 46, end[1] - uy * 46)
    draw.polygon(
        [tip, (base[0] + px * 28, base[1] + py * 28), (base[0] - px * 28, base[1] - py * 28)],
        fill=colour,
    )


def tile(image, texture, box):
    x0, y0, x1, y1 = box
    patch = texture.resize((x1 - x0, y1 - y0), Image.Resampling.NEAREST)
    image.alpha_composite(patch, (x0, y0))


OUT.mkdir(parents=True, exist_ok=True)
with ZipFile(JAR) as archive:
    ender_pearl = asset(archive, "assets/minecraft/textures/item/ender_pearl.png")
    blaze_powder = asset(archive, "assets/minecraft/textures/item/blaze_powder.png")
    ender_eye = asset(archive, "assets/minecraft/textures/item/ender_eye.png")
    grass = asset(archive, "assets/minecraft/textures/block/grass_block_side.png")
    dirt = asset(archive, "assets/minecraft/textures/block/dirt.png")
    stone = asset(archive, "assets/minecraft/textures/block/stone.png")
    stone_bricks = asset(archive, "assets/minecraft/textures/block/stone_bricks.png")

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

    # Language-neutral side-view sequence: the Eye first rises and leads forward,
    # then dives when the search reaches the buried Stronghold target area.
    image = background()
    draw = ImageDraw.Draw(image)
    panels = [(55, 85, 525, 855), (601, 85, 1071, 855), (1147, 85, 1617, 855)]
    for index, (x0, y0, x1, y1) in enumerate(panels, start=1):
        draw.rectangle((x0, y0, x1, y1), fill=(35, 36, 37, 255), outline=(112, 115, 117, 255), width=6)
        draw.ellipse((x0 + 18, y0 + 18, x0 + 82, y0 + 82), fill=(255, 137, 0, 255))
        number = str(index)
        font = ImageFont.load_default(size=40)
        draw.text((x0 + 50, y0 + 49), number, font=font, fill=(255, 255, 255, 255), anchor="mm", stroke_width=1)
        ground = y0 + 545
        for xx in range(x0 + 6, x1 - 5, 64):
            tile(image, grass, (xx, ground, min(xx + 64, x1 - 5), ground + 64))
            tile(image, dirt, (xx, ground + 64, min(xx + 64, x1 - 5), ground + 128))
            tile(image, stone, (xx, ground + 128, min(xx + 64, x1 - 5), y1 - 5))

    eye_large = ender_eye.resize((96, 96), Image.Resampling.NEAREST)
    image.alpha_composite(eye_large, (210, 430))
    path_arrow(draw, (250, 440), (385, 280))
    image.alpha_composite(eye_large, (755, 300))
    path_arrow(draw, (790, 360), (970, 360))
    # Exact Stone Brick textures form the buried target in the final cutaway.
    x0, y0, x1, y1 = panels[2]
    for xx in range(x0 + 70, x1 - 68, 64):
        tile(image, stone_bricks, (xx, y0 + 675, xx + 64, y0 + 739))
    image.alpha_composite(eye_large, (1325, 300))
    path_arrow(draw, (1375, 390), (1375, 615))
    image.save(OUT / "ender_eye_search.png", optimize=True)

print(OUT / "ender_eye_recipe.png")
print(OUT / "ender_eye_search.png")
