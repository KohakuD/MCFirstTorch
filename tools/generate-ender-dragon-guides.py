"""Generate Ender Dragon fight guides from exact Minecraft 26.1.2 assets."""

from io import BytesIO
from pathlib import Path
from zipfile import ZipFile

from PIL import Image, ImageDraw


JAR = Path(r"D:\Minecraft\curseforge\minecraft\Install\versions\26.1.2\26.1.2.jar")
OUT = Path(__file__).resolve().parents[1] / "overrides/resourcepacks/first_torch_guides/assets/firsttorch/textures/questpics"
W, H = 1672, 941
ORANGE = (255, 137, 0, 255)
RED = (225, 48, 48, 255)


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


def paste_nearest(image, texture, box, mirror=False):
    x0, y0, x1, y1 = box
    source = texture.transpose(Image.Transpose.FLIP_LEFT_RIGHT) if mirror else texture
    image.alpha_composite(source.resize((x1 - x0, y1 - y0), Image.Resampling.NEAREST), (x0, y0))


def arrow(draw, start, end, colour=ORANGE, width=16):
    draw.line((start, end), fill=colour, width=width)
    dx, dy = end[0] - start[0], end[1] - start[1]
    length = max(1, (dx * dx + dy * dy) ** 0.5)
    ux, uy = dx / length, dy / length
    px, py = -uy, ux
    base = (end[0] - ux * 48, end[1] - uy * 48)
    draw.polygon((end, (base[0] + px * 27, base[1] + py * 27), (base[0] - px * 27, base[1] - py * 27)), fill=colour)


def cross(draw, box):
    x0, y0, x1, y1 = box
    draw.line((x0, y0, x1, y1), fill=RED, width=24)
    draw.line((x1, y0, x0, y1), fill=RED, width=24)


def end_floor(image, texture):
    draw = ImageDraw.Draw(image)
    size = 94
    top = 744
    for y in range(top, H - 24, size):
        for x in range(25, W - 25, size):
            paste_nearest(image, texture, (x, y, min(x + size, W - 25), min(y + size, H - 25)))
    draw.line((25, top, W - 25, top), fill=(111, 111, 69, 255), width=5)


def dragon_front(image, dragon, centre, scale=1.0):
    """Build a clear front view from the exact face and wing regions of dragon.png."""
    cx, cy = centre
    wing = dragon.crop((0, 96, 56, 144))
    face = dragon.crop((128, 48, 144, 64))
    wing_w, wing_h = int(455 * scale), int(360 * scale)
    face_size = int(245 * scale)
    paste_nearest(image, wing, (cx - face_size // 2 - wing_w + 32, cy - 105, cx - face_size // 2 + 46, cy - 105 + wing_h), mirror=True)
    paste_nearest(image, wing, (cx + face_size // 2 - 46, cy - 105, cx + face_size // 2 + wing_w - 32, cy - 105 + wing_h))
    paste_nearest(image, face, (cx - face_size // 2, cy - face_size // 2, cx + face_size // 2, cy + face_size // 2))


def create_flight_guide(dragon, fireball, bow, end_stone):
    image = background()
    draw = ImageDraw.Draw(image, "RGBA")
    end_floor(image, end_stone)
    dragon_front(image, dragon, (1090, 290), 1.08)

    paste_nearest(image, bow, (200, 520, 380, 700))
    arrow(draw, (390, 585), (885, 325), width=20)

    # Exact Dragon fireball plus a schematic of its lingering purple hazard.
    paste_nearest(image, fireball, (650, 480, 780, 610))
    draw.ellipse((565, 694, 925, 825), fill=(108, 13, 149, 155), outline=(222, 89, 255, 255), width=9)
    draw.ellipse((625, 720, 860, 800), fill=(169, 33, 211, 120))
    arrow(draw, (1070, 845), (1370, 845), width=20)
    arrow(draw, (1070, 845), (770, 845), width=20)
    return image


def bedrock_fountain(image, bedrock):
    draw = ImageDraw.Draw(image)
    block = 92
    centre = W // 2
    for row, count in enumerate((7, 5, 3)):
        y = 744 - row * block
        left = centre - count * block // 2
        for index in range(count):
            box = (left + index * block, y, left + (index + 1) * block, y + block)
            paste_nearest(image, bedrock, box)
            draw.rectangle(box, outline=(31, 31, 31, 255), width=4)
    for row in range(2):
        box = (centre - block // 2, 468 - row * block, centre + block // 2, 560 - row * block)
        paste_nearest(image, bedrock, box)
        draw.rectangle(box, outline=(31, 31, 31, 255), width=4)


def create_perched_guide(dragon, bow, sword, bedrock, end_stone):
    image = background()
    draw = ImageDraw.Draw(image, "RGBA")
    end_floor(image, end_stone)
    bedrock_fountain(image, bedrock)
    dragon_front(image, dragon, (W // 2, 250), 0.88)

    paste_nearest(image, bow, (150, 190, 325, 365))
    cross(draw, (135, 175, 340, 380))

    paste_nearest(image, sword, (1270, 455, 1475, 660))
    arrow(draw, (1260, 565), (1000, 330), width=20)
    arrow(draw, (1430, 820), (1190, 820), width=20)
    return image


OUT.mkdir(parents=True, exist_ok=True)
with ZipFile(JAR) as archive:
    dragon = asset(archive, "assets/minecraft/textures/entity/enderdragon/dragon.png")
    fireball = asset(archive, "assets/minecraft/textures/entity/enderdragon/dragon_fireball.png")
    bow = asset(archive, "assets/minecraft/textures/item/bow_pulling_2.png")
    sword = asset(archive, "assets/minecraft/textures/item/iron_sword.png")
    bedrock = asset(archive, "assets/minecraft/textures/block/bedrock.png")
    end_stone = asset(archive, "assets/minecraft/textures/block/end_stone.png")
    create_flight_guide(dragon, fireball, bow, end_stone).save(OUT / "ender_dragon_flight.png", optimize=True)
    create_perched_guide(dragon, bow, sword, bedrock, end_stone).save(OUT / "ender_dragon_perched.png", optimize=True)

print(OUT)
