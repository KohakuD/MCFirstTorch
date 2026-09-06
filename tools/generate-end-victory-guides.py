"""Generate post-Dragon guides from exact Minecraft 26.1.2 assets."""

from io import BytesIO
from pathlib import Path
from zipfile import ZipFile

from PIL import Image, ImageDraw


JAR = Path(r"D:\Minecraft\curseforge\minecraft\Install\versions\26.1.2\26.1.2.jar")
OUT = Path(__file__).resolve().parents[1] / "overrides/resourcepacks/first_torch_guides/assets/firsttorch/textures/questpics"
W, H = 1672, 941
ORANGE = (255, 137, 0, 255)


def asset(archive, path):
    return Image.open(BytesIO(archive.read(path))).convert("RGBA")


def background():
    image = Image.new("RGBA", (W, H), (14, 13, 18, 255))
    pixels = image.load()
    for y in range(6, H - 6):
        shade = int(35 - 10 * y / H)
        for x in range(6, W - 6):
            vignette = int(7 * abs(x - W / 2) / (W / 2))
            value = max(15, shade - vignette)
            pixels[x, y] = (value, value - 2, value + 5, 255)
    draw = ImageDraw.Draw(image)
    draw.rectangle((2, 2, W - 3, H - 3), outline=(5, 5, 8, 255), width=6)
    draw.line((8, 8, W - 9, 8), fill=(103, 100, 111, 255), width=3)
    draw.line((8, 9, 8, H - 9), fill=(72, 70, 79, 255), width=3)
    return image


def paste_nearest(image, texture, box):
    x0, y0, x1, y1 = box
    image.alpha_composite(texture.resize((x1 - x0, y1 - y0), Image.Resampling.NEAREST), (x0, y0))


def arrow(draw, start, end, colour=ORANGE, width=16):
    draw.line((start, end), fill=colour, width=width)
    dx, dy = end[0] - start[0], end[1] - start[1]
    length = max(1, (dx * dx + dy * dy) ** 0.5)
    ux, uy = dx / length, dy / length
    px, py = -uy, ux
    base = (end[0] - ux * 48, end[1] - uy * 48)
    draw.polygon((end, (base[0] + px * 27, base[1] + py * 27), (base[0] - px * 27, base[1] - py * 27)), fill=colour)


def panel(draw, left, right, number):
    draw.rounded_rectangle((left, 55, right, 886), radius=12, fill=(35, 35, 40, 255), outline=(111, 109, 119, 255), width=6)
    draw.ellipse((left + 18, 73, left + 82, 137), fill=ORANGE)
    if number == 1:
        draw.line((left + 50, 89, left + 50, 121), fill=(255, 255, 255, 255), width=9)
    elif number == 2:
        draw.line((left + 36, 92, left + 51, 85, left + 64, 94, left + 37, 121, left + 66, 121), fill=(255, 255, 255, 255), width=8, joint="curve")
    else:
        draw.line((left + 37, 89, left + 64, 89, left + 49, 104, left + 64, 118, left + 37, 122), fill=(255, 255, 255, 255), width=8, joint="curve")


def dragon_egg_front(image, texture, centre, size):
    """Render the front silhouette defined by the target-version Dragon Egg block model."""
    cx, bottom = centre
    scale = size / 16
    rows = [(6, 10, 0, 1), (5, 11, 1, 2), (4, 12, 2, 3), (3, 13, 3, 8), (1, 15, 8, 13), (2, 14, 13, 15), (3, 13, 15, 16)]
    for x0, x1, y0, y1 in rows:
        source = texture.crop((x0, y0, x1, y1))
        left = int(cx - size / 2 + x0 * scale)
        top = int(bottom - size + y0 * scale)
        right = int(cx - size / 2 + x1 * scale)
        lower = int(bottom - size + y1 * scale)
        paste_nearest(image, source, (left, top, right, lower))


def end_stone_block(image, texture, box):
    paste_nearest(image, texture, box)
    ImageDraw.Draw(image).rectangle(box, outline=(90, 92, 61, 255), width=4)


def create_exit_guide(bedrock, portal, end_stone, egg):
    image = background()
    draw = ImageDraw.Draw(image, "RGBA")
    cell = 120
    left, top = 476, 90
    # Top-down view is intentional here: the dark portal surface inside the Bedrock ring is the destination.
    for row in range(6):
        for col in range(6):
            x, y = left + col * cell, top + row * cell
            if row in (0, 5) or col in (0, 5):
                paste_nearest(image, bedrock, (x, y, x + cell, y + cell))
            else:
                paste_nearest(image, portal, (x, y, x + cell, y + cell))
            draw.rectangle((x, y, x + cell, y + cell), outline=(26, 26, 30, 255), width=4)
    dragon_egg_front(image, egg, (W // 2, 252), 150)
    arrow(draw, (W // 2, 820), (W // 2, 600), width=22)
    # A small strip of exact End Stone separates the portal plan from the outer island.
    for col in range(10):
        end_stone_block(image, end_stone, (236 + col * cell, 810, 356 + col * cell, 900))
    return image


def create_egg_guide(egg, end_stone, torch):
    image = background()
    draw = ImageDraw.Draw(image, "RGBA")
    margin, gap = 36, 24
    width = (W - 2 * margin - 2 * gap) // 3
    panels = []
    for number in range(1, 4):
        left = margin + (number - 1) * (width + gap)
        right = left + width
        panel(draw, left, right, number)
        panels.append((left, right))

    # 1: touch the Egg once and follow its teleport particles to the new position.
    left, right = panels[0]
    end_stone_block(image, end_stone, (left + 170, 660, left + 350, 840))
    dragon_egg_front(image, egg, (left + 260, 660), 210)
    dragon_egg_front(image, egg, (left + 410, 390), 150)
    arrow(draw, (left + 320, 500), (left + 390, 430), width=14)

    # 2: leave one support under the Egg and place a Torch one block below it.
    left, right = panels[1]
    end_stone_block(image, end_stone, (left + 178, 500, left + 358, 680))
    dragon_egg_front(image, egg, (left + 268, 500), 210)
    paste_nearest(image, torch, (left + 198, 690, left + 338, 830))
    draw.rectangle((left + 160, 482, left + 376, 848), outline=(245, 245, 245, 255), width=7)

    # 3: break only the supporting block; the Egg falls onto the Torch and becomes an item.
    left, right = panels[2]
    paste_nearest(image, torch, (left + 198, 690, left + 338, 830))
    dragon_egg_front(image, egg, (left + 268, 625), 165)
    draw.rectangle((left + 178, 500, left + 358, 680), outline=ORANGE, width=10)
    draw.line((left + 190, 512, left + 346, 668), fill=ORANGE, width=12)
    draw.line((left + 346, 512, left + 190, 668), fill=ORANGE, width=12)
    arrow(draw, (left + 268, 455), (left + 268, 600), width=15)
    return image


OUT.mkdir(parents=True, exist_ok=True)
with ZipFile(JAR) as archive:
    bedrock = asset(archive, "assets/minecraft/textures/block/bedrock.png")
    portal = asset(archive, "assets/minecraft/textures/entity/end_portal/end_portal.png")
    end_stone = asset(archive, "assets/minecraft/textures/block/end_stone.png")
    egg = asset(archive, "assets/minecraft/textures/block/dragon_egg.png")
    torch = asset(archive, "assets/minecraft/textures/block/torch.png")
    create_egg_guide(egg, end_stone, torch).save(OUT / "dragon_egg_retrieval.png", optimize=True)

print(OUT)
