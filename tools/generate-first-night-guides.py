"""Generate early Shovel and first-night shelter guides from Minecraft 26.1.2 assets."""

from io import BytesIO
import os
from pathlib import Path
from zipfile import ZipFile

from PIL import Image, ImageDraw


JAR = Path(os.environ.get(
    "FIRST_TORCH_MINECRAFT_JAR",
    r"D:\Minecraft\curseforge\minecraft\Install\versions\26.1.2\26.1.2.jar",
))
OUT = Path(__file__).resolve().parents[1] / "overrides/resourcepacks/first_torch_guides/assets/firsttorch/textures/questpics"
W, H = 1672, 941
ORANGE = (255, 137, 0, 255)


def asset(archive, path):
    return Image.open(BytesIO(archive.read(path))).convert("RGBA")


def background():
    image = Image.new("RGBA", (W, H), (29, 30, 31, 255))
    pixels = image.load()
    for y in range(6, H - 6):
        shade = int(48 - 13 * y / H)
        for x in range(6, W - 6):
            vignette = int(6 * abs(x - W / 2) / (W / 2))
            value = max(25, shade - vignette)
            pixels[x, y] = (value, value, value, 255)
    draw = ImageDraw.Draw(image)
    draw.rectangle((2, 2, W - 3, H - 3), outline=(7, 8, 9, 255), width=6)
    draw.line((8, 8, W - 9, 8), fill=(126, 129, 130, 255), width=3)
    draw.line((8, 9, 8, H - 9), fill=(91, 94, 96, 255), width=3)
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
                r, g, b, a = source[min(texture.width - 1, int(u * texture.width)),
                                     min(texture.height - 1, int(v * texture.height))]
                pixels[xx, yy] = (int(r * shade), int(g * shade), int(b * shade), a)
    return face, (left, top)


def cube_icon(top_texture, side_texture=None, size=150):
    side_texture = side_texture or top_texture
    canvas = Image.new("RGBA", (320, 320), (0, 0, 0, 0))
    top = [(160, 35), (285, 97), (160, 160), (35, 97)]
    right = [(285, 97), (160, 160), (160, 285), (285, 222)]
    left = [(160, 160), (35, 97), (35, 222), (160, 285)]
    for texture, points, shade in ((top_texture, top, 1.0), (side_texture, right, 0.70),
                                   (side_texture, left, 0.83)):
        face, position = textured_quad(texture, points, shade)
        canvas.alpha_composite(face, position)
    crop = canvas.crop(canvas.getbbox())
    crop.thumbnail((size, size), Image.Resampling.NEAREST)
    return crop


def slot(draw, x, y, size=150):
    draw.rectangle((x, y, x + size, y + size), fill=(29, 31, 32, 255), outline=(5, 6, 7, 255), width=8)
    draw.line((x + 8, y + 8, x + size - 8, y + 8), fill=(153, 156, 158, 255), width=4)
    draw.line((x + 8, y + 8, x + 8, y + size - 8), fill=(115, 118, 120, 255), width=4)


def arrow(draw, left, top, width=170, height=100):
    points = [(left, top + height // 3), (left + width * 3 // 5, top + height // 3),
              (left + width * 3 // 5, top), (left + width, top + height // 2),
              (left + width * 3 // 5, top + height), (left + width * 3 // 5, top + height * 2 // 3),
              (left, top + height * 2 // 3)]
    draw.polygon(points, fill=(194, 195, 196, 255))


def create_shovel_recipe(planks, stick, shovel):
    image = background()
    draw = ImageDraw.Draw(image)
    left, top, cell = 225, 225, 160
    for row in range(3):
        for col in range(3):
            slot(draw, left + col * cell, top + row * cell, cell)

    plank = cube_icon(planks, size=122)
    image.alpha_composite(plank, (left + cell + (cell - plank.width) // 2,
                                  top + (cell - plank.height) // 2))
    stick_icon = stick.resize((105, 105), Image.Resampling.NEAREST)
    for row in (1, 2):
        image.alpha_composite(stick_icon, (left + cell + (cell - stick_icon.width) // 2,
                                           top + row * cell + (cell - stick_icon.height) // 2))

    arrow(draw, 800, 410)
    output_x, output_y, output_size = 1080, 335, 250
    slot(draw, output_x, output_y, output_size)
    shovel_icon = shovel.resize((190, 190), Image.Resampling.NEAREST)
    image.alpha_composite(shovel_icon, (output_x + 30, output_y + 30))
    return image


def create_shelter_guide(dirt, grass_side):
    image = background()
    draw = ImageDraw.Draw(image)
    left, top, cell = 430, 170, 120

    def block(texture, col, row):
        x, y = left + col * cell, top + row * cell
        image.alpha_composite(texture.resize((cell, cell), Image.Resampling.NEAREST), (x, y))
        draw.rectangle((x, y, x + cell, y + cell), outline=(58, 47, 35, 255), width=5)

    # A side cutaway makes the two-block interior and complete roof unambiguous.
    for col in range(7):
        block(grass_side, col, 0)
        block(dirt, col, 1)
        block(dirt, col, 4)
    for row in (2, 3):
        block(dirt, 0, row)
        block(dirt, 5, row)
        block(dirt, 6, row)

    # The dark cutaway is four blocks long and exactly two blocks high.
    interior = (left + cell, top + 2 * cell, left + 5 * cell, top + 4 * cell)
    draw.rectangle(interior, fill=(13, 14, 15, 255), outline=(130, 132, 133, 255), width=7)

    # The two highlighted blocks completely seal the only two-block-high entrance.
    for row in (2, 3):
        x, y = left, top + row * cell
        draw.rectangle((x + 7, y + 7, x + cell - 7, y + cell - 7), outline=ORANGE, width=10)
    draw.line((235, 530, left - 15, 530), fill=ORANGE, width=18)
    draw.polygon(((left, 530), (left - 55, 495), (left - 55, 565)), fill=ORANGE)
    return image


OUT.mkdir(parents=True, exist_ok=True)
with ZipFile(JAR) as archive:
    planks = asset(archive, "assets/minecraft/textures/block/oak_planks.png")
    stick = asset(archive, "assets/minecraft/textures/item/stick.png")
    shovel = asset(archive, "assets/minecraft/textures/item/wooden_shovel.png")
    dirt = asset(archive, "assets/minecraft/textures/block/dirt.png")
    grass_side = asset(archive, "assets/minecraft/textures/block/grass_block_side.png")
    create_shovel_recipe(planks, stick, shovel).save(OUT / "wooden_shovel.png", optimize=True)
    create_shelter_guide(dirt, grass_side).save(OUT / "first_night_shelter.png", optimize=True)

print(OUT)
