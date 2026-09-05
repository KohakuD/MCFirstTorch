"""Generate outer-End travel guides from exact Minecraft 26.1.2 textures."""

from io import BytesIO
import os
from pathlib import Path
from zipfile import ZipFile

from PIL import Image, ImageDraw


JAR = Path(os.environ.get(
    "FIRST_TORCH_MINECRAFT_JAR",
    r"C:\Games\CurseForge\Minecraft\Install\versions\26.1.2\26.1.2.jar",
))
OUT = Path(__file__).resolve().parents[1] / "overrides/resourcepacks/first_torch_guides/assets/firsttorch/textures/questpics"
W, H = 1672, 941
ORANGE = (255, 145, 0, 255)
RED = (214, 42, 52, 235)


def asset(archive, path):
    return Image.open(BytesIO(archive.read(path))).convert("RGBA")


def background():
    image = Image.new("RGBA", (W, H), (18, 16, 23, 255))
    draw = ImageDraw.Draw(image)
    draw.rectangle((3, 3, W - 4, H - 4), outline=(6, 6, 9, 255), width=6)
    draw.line((10, 10, W - 11, 10), fill=(108, 105, 116, 255), width=3)
    return image


def paste_nearest(image, texture, box):
    x0, y0, x1, y1 = box
    image.alpha_composite(texture.resize((x1 - x0, y1 - y0), Image.Resampling.NEAREST), (x0, y0))


def arrow(draw, start, end, colour=ORANGE, width=15):
    draw.line((start, end), fill=colour, width=width)
    dx, dy = end[0] - start[0], end[1] - start[1]
    length = max(1, (dx * dx + dy * dy) ** 0.5)
    ux, uy = dx / length, dy / length
    px, py = -uy, ux
    base = (end[0] - ux * 42, end[1] - uy * 42)
    draw.polygon((end, (base[0] + px * 24, base[1] + py * 24), (base[0] - px * 24, base[1] - py * 24)), fill=colour)


def top_tile(image, texture, x, y, size=72, outline=(74, 76, 49, 255)):
    paste_nearest(image, texture, (x, y, x + size, y + size))
    ImageDraw.Draw(image).rectangle((x, y, x + size, y + size), outline=outline, width=3)


def block_face(image, texture, x, y, w, h=None, outline=(37, 34, 45, 255)):
    h = h or w
    paste_nearest(image, texture, (x, y, x + w, y + h))
    ImageDraw.Draw(image).rectangle((x, y, x + w, y + h), outline=outline, width=3)


def bridge_guide(end_stone, cobblestone, torch, pearl, chorus_fruit, steve):
    image = background()
    draw = ImageDraw.Draw(image, "RGBA")
    draw.line((836, 28, 836, H - 28), fill=(84, 81, 91, 255), width=4)

    # Safe plan: a two-wide bridge between fully visible shores, built while sneaking.
    tile = 70
    for row in range(6):
        for col in range(5):
            top_tile(image, end_stone, 55 + col * tile, 255 + row * tile, tile)
            top_tile(image, end_stone, 1235 + col * tile, 255 + row * tile, tile)
    for row in range(2):
        for col in range(12):
            top_tile(image, end_stone, 405 + col * tile, 395 + row * tile, tile)
    head = steve.crop((8, 8, 16, 16)).resize((92, 92), Image.Resampling.NEAREST)
    image.alpha_composite(head, (470, 380))
    arrow(draw, (570, 430), (1120, 430), width=18)

    # A conspicuous marker points back towards the secured return route.
    top_tile(image, cobblestone, 195, 325, tile, (45, 45, 45, 255))
    top_tile(image, cobblestone, 195, 255, tile, (45, 45, 45, 255))
    paste_nearest(image, torch, (215, 200, 245, 270))
    arrow(draw, (340, 310), (270, 310), width=13)

    # Unsafe shortcuts: neither a blind Pearl nor Chorus Fruit gives a controlled landing.
    for texture, y in ((pearl, 675), (chorus_fruit, 775)):
        paste_nearest(image, texture, (925, y, 1025, y + 100))
        draw.line((900, y - 5, 1050, y + 115), fill=RED, width=18)
        draw.line((1050, y - 5, 900, y + 115), fill=RED, width=18)
    return image


def city_guide(end_stone, purpur, purpur_pillar, end_bricks, end_rod):
    image = background()
    draw = ImageDraw.Draw(image, "RGBA")

    # Broad End Stone foreground keeps the observation point visibly away from edges.
    for row in range(3):
        for col in range(18):
            block_face(image, end_stone, 20 + col * 92, 650 + row * 92, 92, outline=(77, 78, 51, 255))

    # Recognisable End City silhouette: Purpur towers, End Stone Brick floors and End Rod lights.
    def tower(x, base_y, width, levels):
        for level in range(levels):
            y = base_y - (level + 1) * 105
            for col in range(width):
                tex = purpur_pillar if col in (0, width - 1) else purpur
                block_face(image, tex, x + col * 88, y, 88, 105)
            block_face(image, end_bricks, x - 25, y - 25, width * 88 + 50, 25)
        block_face(image, purpur, x - 35, base_y - levels * 105 - 80, width * 88 + 70, 55)

    tower(980, 650, 3, 4)
    tower(560, 650, 2, 2)
    # Bridge and narrow branching tower communicate the generated city's characteristic shape.
    for col in range(4):
        block_face(image, end_bricks, 735 + col * 70, 420, 70, 50)
    tower(1260, 650, 2, 3)
    for x in (585, 1035, 1305):
        paste_nearest(image, end_rod, (x, 120, x + 30, 100 + 95))

    # Orange sight line stops at the safe observation point rather than entering the city.
    arrow(draw, (230, 570), (520, 520), width=18)
    draw.ellipse((155, 495, 245, 585), outline=ORANGE, width=12)
    return image


OUT.mkdir(parents=True, exist_ok=True)
with ZipFile(JAR) as archive:
    end_stone = asset(archive, "assets/minecraft/textures/block/end_stone.png")
    cobblestone = asset(archive, "assets/minecraft/textures/block/cobblestone.png")
    torch = asset(archive, "assets/minecraft/textures/block/torch.png")
    pearl = asset(archive, "assets/minecraft/textures/item/ender_pearl.png")
    chorus_fruit = asset(archive, "assets/minecraft/textures/item/chorus_fruit.png")
    steve = asset(archive, "assets/minecraft/textures/entity/player/wide/steve.png")
    purpur = asset(archive, "assets/minecraft/textures/block/purpur_block.png")
    purpur_pillar = asset(archive, "assets/minecraft/textures/block/purpur_pillar.png")
    end_bricks = asset(archive, "assets/minecraft/textures/block/end_stone_bricks.png")
    end_rod = asset(archive, "assets/minecraft/textures/block/end_rod.png")
    bridge_guide(end_stone, cobblestone, torch, pearl, chorus_fruit, steve).save(OUT / "end_island_crossing.png", optimize=True)
    city_guide(end_stone, purpur, purpur_pillar, end_bricks, end_rod).save(OUT / "end_city_search.png", optimize=True)

print(OUT)
