"""Generate Chorus guides from exact Minecraft 26.1.2 textures."""

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


def asset(archive, path):
    return Image.open(BytesIO(archive.read(path))).convert("RGBA")


def background():
    image = Image.new("RGBA", (W, H), (23, 20, 28, 255))
    draw = ImageDraw.Draw(image)
    draw.rectangle((3, 3, W - 4, H - 4), outline=(7, 7, 9, 255), width=6)
    draw.line((10, 10, W - 11, 10), fill=(112, 110, 118, 255), width=3)
    return image


def textured_quad(texture, points, shade=1.0):
    left = int(min(point[0] for point in points))
    top = int(min(point[1] for point in points))
    right = int(max(point[0] for point in points)) + 1
    bottom = int(max(point[1] for point in points)) + 1
    face = Image.new("RGBA", (right - left, bottom - top), (0, 0, 0, 0))
    pixels = face.load()
    p0, p1, _, p3 = points
    ax, ay = p1[0] - p0[0], p1[1] - p0[1]
    bx, by = p3[0] - p0[0], p3[1] - p0[1]
    determinant = ax * by - ay * bx
    source = texture.load()
    for yy in range(face.height):
        sy = top + yy + 0.5 - p0[1]
        for xx in range(face.width):
            sx = left + xx + 0.5 - p0[0]
            u = (sx * by - sy * bx) / determinant
            v = (ax * sy - ay * sx) / determinant
            if 0 <= u < 1 and 0 <= v < 1:
                r, g, b, a = source[min(texture.width - 1, int(u * texture.width)), min(texture.height - 1, int(v * texture.height))]
                pixels[xx, yy] = (int(r * shade), int(g * shade), int(b * shade), a)
    return face, (left, top)


def cube(texture, size):
    canvas = Image.new("RGBA", (320, 320), (0, 0, 0, 0))
    faces = (
        ([(160, 35), (285, 97), (160, 160), (35, 97)], 1.0),
        ([(285, 97), (160, 160), (160, 285), (285, 222)], 0.70),
        ([(160, 160), (35, 97), (35, 222), (160, 285)], 0.84),
    )
    for points, shade in faces:
        face, position = textured_quad(texture, points, shade)
        canvas.alpha_composite(face, position)
    result = canvas.crop(canvas.getbbox())
    result.thumbnail((size, size), Image.Resampling.NEAREST)
    return result


def arrow(draw, start, end, width=14):
    draw.line((start, end), fill=ORANGE, width=width)
    x, y = end
    dx, dy = x - start[0], y - start[1]
    length = max(1, (dx * dx + dy * dy) ** 0.5)
    ux, uy = dx / length, dy / length
    px, py = -uy, ux
    back_x, back_y = x - ux * 36, y - uy * 36
    draw.polygon(((x, y), (back_x + px * 22, back_y + py * 22), (back_x - px * 22, back_y - py * 22)), fill=ORANGE)


def harvest_guide(end_stone, plant, flower, fruit):
    image = background()
    draw = ImageDraw.Draw(image)
    draw.line((836, 28, 836, H - 28), fill=(88, 86, 94, 255), width=4)

    ground = cube(end_stone, 190)
    stem = cube(plant, 142)
    blossom = cube(flower, 150)
    image.alpha_composite(ground, (290, 660))
    for y in (530, 410, 290):
        image.alpha_composite(stem, (314, y))
    image.alpha_composite(blossom, (310, 160))
    arrow(draw, (500, 500), (650, 500))
    slot = (665, 420, 805, 560)
    draw.rectangle(slot, fill=(29, 30, 32, 255), outline=(143, 145, 148, 255), width=6)
    image.alpha_composite(fruit.resize((100, 100), Image.Resampling.NEAREST), (685, 440))

    image.alpha_composite(ground, (1090, 660))
    image.alpha_composite(stem, (1114, 530))
    image.alpha_composite(blossom, (1110, 390))
    arrow(draw, (1280, 420), (1430, 300))
    draw.rectangle((1450, 210, 1600, 360), fill=(29, 30, 32, 255), outline=(143, 145, 148, 255), width=6)
    small_flower = cube(flower, 115)
    image.alpha_composite(small_flower, (1468, 225))
    return image


def safety_guide(end_stone, fruit, steve):
    image = background()
    draw = ImageDraw.Draw(image, "RGBA")
    tile_size = 48
    left, top, cells = 428, 64, 17
    tile = end_stone.resize((tile_size, tile_size), Image.Resampling.NEAREST)
    for row in range(cells):
        for column in range(cells):
            image.alpha_composite(tile, (left + column * tile_size, top + row * tile_size))
    draw.rectangle((left, top, left + cells * tile_size, top + cells * tile_size), outline=(137, 132, 143, 255), width=8)
    centre = (left + cells * tile_size // 2, top + cells * tile_size // 2)
    draw.ellipse((centre[0] - 8 * tile_size, centre[1] - 8 * tile_size,
                  centre[0] + 8 * tile_size, centre[1] + 8 * tile_size), outline=ORANGE, width=10)
    head = steve.crop((8, 8, 16, 16)).resize((100, 100), Image.Resampling.NEAREST)
    image.alpha_composite(head, (centre[0] - 50, centre[1] - 50))
    for end in ((centre[0] + 250, centre[1] - 190), (centre[0] - 285, centre[1] + 160),
                (centre[0] + 180, centre[1] + 245)):
        arrow(draw, centre, end, width=12)
    draw.rounded_rectangle((75, 335, 300, 605), radius=18, fill=(35, 34, 39, 245), outline=(105, 103, 111, 255), width=6)
    image.alpha_composite(fruit.resize((160, 160), Image.Resampling.NEAREST), (108, 390))
    arrow(draw, (300, 470), (410, 470), width=14)
    # Red crosses mark void-edge testing as unsafe.
    for x, y in ((350, 120), (1270, 750)):
        draw.line((x - 35, y - 35, x + 35, y + 35), fill=(210, 35, 42, 220), width=16)
        draw.line((x + 35, y - 35, x - 35, y + 35), fill=(210, 35, 42, 220), width=16)
    return image


OUT.mkdir(parents=True, exist_ok=True)
with ZipFile(JAR) as archive:
    end_stone = asset(archive, "assets/minecraft/textures/block/end_stone.png")
    plant = asset(archive, "assets/minecraft/textures/block/chorus_plant.png")
    flower = asset(archive, "assets/minecraft/textures/block/chorus_flower.png")
    fruit = asset(archive, "assets/minecraft/textures/item/chorus_fruit.png")
    steve = asset(archive, "assets/minecraft/textures/entity/player/wide/steve.png")
    harvest_guide(end_stone, plant, flower, fruit).save(OUT / "chorus_harvest.png", optimize=True)
    safety_guide(end_stone, fruit, steve).save(OUT / "chorus_fruit_safety.png", optimize=True)

print(OUT)
