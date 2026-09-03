"""Generate the optional Enderman shelter guide from exact Minecraft 26.1.2 assets."""

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


def tile(image, texture, box):
    x0, y0, x1, y1 = box
    image.alpha_composite(texture.resize((x1 - x0, y1 - y0), Image.Resampling.NEAREST), (x0, y0))


def crop_scaled(texture, box, size):
    return texture.crop(box).resize(size, Image.Resampling.NEAREST)


def front_model(texture, kind):
    canvas = Image.new("RGBA", (260, 520), (0, 0, 0, 0))
    if kind == "player":
        canvas.alpha_composite(crop_scaled(texture, (8, 8, 16, 16), (96, 96)), (82, 12))
        canvas.alpha_composite(crop_scaled(texture, (20, 20, 28, 32), (88, 168)), (86, 108))
        arm = crop_scaled(texture, (44, 20, 48, 32), (44, 168))
        leg = crop_scaled(texture, (4, 20, 8, 32), (44, 168))
        canvas.alpha_composite(arm, (42, 108))
        canvas.alpha_composite(arm.transpose(Image.Transpose.FLIP_LEFT_RIGHT), (174, 108))
        canvas.alpha_composite(leg, (86, 276))
        canvas.alpha_composite(leg.transpose(Image.Transpose.FLIP_LEFT_RIGHT), (130, 276))
    else:
        canvas.alpha_composite(crop_scaled(texture, (8, 8, 16, 16), (104, 104)), (78, 0))
        canvas.alpha_composite(crop_scaled(texture, (20, 20, 28, 32), (78, 190)), (91, 104))
        arm = crop_scaled(texture, (44, 20, 46, 32), (26, 250))
        leg = crop_scaled(texture, (4, 20, 6, 32), (28, 220))
        canvas.alpha_composite(arm, (65, 104))
        canvas.alpha_composite(arm.transpose(Image.Transpose.FLIP_LEFT_RIGHT), (169, 104))
        canvas.alpha_composite(leg, (98, 294))
        canvas.alpha_composite(leg.transpose(Image.Transpose.FLIP_LEFT_RIGHT), (134, 294))
    return canvas


def panel(draw, box):
    draw.rounded_rectangle(box, radius=12, fill=(35, 35, 40, 255), outline=(111, 109, 119, 255), width=6)


def create_guide(end_stone, obsidian, player, enderman):
    image = background()
    draw = ImageDraw.Draw(image, "RGBA")
    panel(draw, (42, 55, 806, 886))
    panel(draw, (838, 55, 1630, 886))

    # Left: exact top view of the minimum three-by-three roof beside an Obsidian pillar.
    cell = 154
    roof_left, roof_top = 162, 225
    for row in range(3):
        for col in range(3):
            x = roof_left + col * cell
            y = roof_top + row * cell
            tile(image, end_stone, (x, y, x + cell, y + cell))
            draw.rectangle((x, y, x + cell, y + cell), outline=(84, 86, 57, 255), width=5)
    # The centre is where the player stands beneath the roof.
    draw.rectangle((roof_left + cell + 13, roof_top + cell + 13, roof_left + 2 * cell - 13, roof_top + 2 * cell - 13), outline=(255, 137, 0, 255), width=13)
    # A pillar edge makes the intended landmark/location recognisable.
    for row in range(3):
        tile(image, obsidian, (roof_left + 3 * cell + 20, roof_top + row * cell, roof_left + 3 * cell + 112, roof_top + (row + 1) * cell))

    # Right: side cutaway with exactly two clear blocks below the roof.
    floor_y, block = 790, 150
    for col in range(5):
        tile(image, end_stone, (875 + col * block, floor_y, 875 + (col + 1) * block, floor_y + 78))
    for col in range(3):
        tile(image, end_stone, (900 + col * block, floor_y - 3 * block, 900 + (col + 1) * block, floor_y - 2 * block))
        draw.rectangle((900 + col * block, floor_y - 3 * block, 900 + (col + 1) * block, floor_y - 2 * block), outline=(84, 86, 57, 255), width=5)
    # Obsidian pillar as the rear wall/landmark.
    for level in range(5):
        tile(image, obsidian, (875, floor_y - (level + 1) * block, 950, floor_y - level * block))

    steve = front_model(player, "player")
    steve.thumbnail((150, 300), Image.Resampling.NEAREST)
    image.alpha_composite(steve, (1082, floor_y - steve.height))
    tall = front_model(enderman, "enderman")
    tall.thumbnail((185, 470), Image.Resampling.NEAREST)
    image.alpha_composite(tall, (1420, floor_y - tall.height))

    # White two-block height marker; red cross shows the Enderman cannot enter.
    draw.line((1015, floor_y, 1015, floor_y - 2 * block), fill=(245, 245, 245, 255), width=10)
    draw.line((995, floor_y, 1035, floor_y), fill=(245, 245, 245, 255), width=10)
    draw.line((995, floor_y - 2 * block, 1035, floor_y - 2 * block), fill=(245, 245, 245, 255), width=10)
    draw.line((1430, 322, 1590, 482), fill=(226, 47, 47, 220), width=22)
    draw.line((1590, 322, 1430, 482), fill=(226, 47, 47, 220), width=22)
    return image


OUT.mkdir(parents=True, exist_ok=True)
with ZipFile(JAR) as archive:
    end_stone = asset(archive, "assets/minecraft/textures/block/end_stone.png")
    obsidian = asset(archive, "assets/minecraft/textures/block/obsidian.png")
    player = asset(archive, "assets/minecraft/textures/entity/player/wide/steve.png")
    enderman = asset(archive, "assets/minecraft/textures/entity/enderman/enderman.png")
    create_guide(end_stone, obsidian, player, enderman).save(OUT / "enderman_roof_shelter.png", optimize=True)

print(OUT)
