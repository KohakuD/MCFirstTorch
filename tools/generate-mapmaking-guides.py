"""Generate map and Cartography Table guides from exact Minecraft 26.1.2 assets."""

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
    image = Image.new("RGBA", (W, H), (28, 29, 31, 255))
    draw = ImageDraw.Draw(image)
    draw.rectangle((3, 3, W - 4, H - 4), outline=(8, 9, 10, 255), width=6)
    draw.line((10, 10, W - 11, 10), fill=(125, 128, 130, 255), width=3)
    draw.line((10, 11, 10, H - 11), fill=(91, 94, 96, 255), width=3)
    return image


def panel(draw, box, number):
    draw.rounded_rectangle(box, radius=12, fill=(42, 43, 45, 255), outline=(113, 116, 118, 255), width=6)
    x, y = box[0] + 28, box[1] + 28
    draw.ellipse((x, y, x + 64, y + 64), fill=ORANGE)
    draw.text((x + 32, y + 31), str(number), anchor="mm", fill=(255, 255, 255, 255), stroke_width=1)


def slot(draw, x, y, size):
    draw.rectangle((x, y, x + size, y + size), fill=(30, 31, 33, 255), outline=(5, 6, 7, 255), width=7)
    draw.line((x + 7, y + 7, x + size - 7, y + 7), fill=(154, 156, 158, 255), width=4)
    draw.line((x + 7, y + 7, x + 7, y + size - 7), fill=(115, 118, 120, 255), width=4)


def arrow(draw, x, y, width=115, height=72):
    draw.polygon(((x, y + height // 3), (x + width * 3 // 5, y + height // 3),
                  (x + width * 3 // 5, y), (x + width, y + height // 2),
                  (x + width * 3 // 5, y + height), (x + width * 3 // 5, y + height * 2 // 3),
                  (x, y + height * 2 // 3)), fill=(196, 198, 199, 255))


def plus(draw, x, y, size=50):
    width = size // 4
    draw.rectangle((x + size // 2 - width // 2, y, x + size // 2 + width // 2, y + size), fill=(196, 198, 199, 255))
    draw.rectangle((x, y + size // 2 - width // 2, x + size, y + size // 2 + width // 2), fill=(196, 198, 199, 255))


def item(image, texture, x, y, size):
    icon = texture.resize((size, size), Image.Resampling.NEAREST)
    image.alpha_composite(icon, (x, y))


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


def table_cube(top_texture, left_texture, right_texture, size=190):
    canvas = Image.new("RGBA", (320, 320), (0, 0, 0, 0))
    faces = (
        (top_texture, [(160, 35), (285, 97), (160, 160), (35, 97)], 1.0),
        (right_texture, [(285, 97), (160, 160), (160, 285), (285, 222)], 0.72),
        (left_texture, [(160, 160), (35, 97), (35, 222), (160, 285)], 0.84),
    )
    for texture, points, shade in faces:
        face, position = textured_quad(texture, points, shade)
        canvas.alpha_composite(face, position)
    crop = canvas.crop(canvas.getbbox())
    crop.thumbnail((size, size), Image.Resampling.NEAREST)
    return crop


def recipe_grid(image, draw, x, y, cell, ingredients):
    for row in range(3):
        for column in range(3):
            slot(draw, x + column * cell, y + row * cell, cell)
    for column, row, texture in ingredients:
        item(image, texture, x + column * cell + 15, y + row * cell + 15, cell - 30)


def map_recipes(sugar_cane, paper, compass, empty_map):
    image = background()
    draw = ImageDraw.Draw(image)
    panels = ((32, 55, 812, 886), (860, 55, 1640, 886))
    for index, box in enumerate(panels, 1):
        panel(draw, box, index)

    cell = 128
    x, y = 92, 260
    recipe_grid(image, draw, x, y, cell, [(0, 1, sugar_cane), (1, 1, sugar_cane), (2, 1, sugar_cane)])
    arrow(draw, 500, 414)
    slot(draw, 640, 350, 140)
    item(image, paper, 665, 375, 90)

    x, y = 900, 260
    ingredients = [(column, row, paper) for row in range(3) for column in range(3) if (column, row) != (1, 1)]
    ingredients.append((1, 1, compass))
    recipe_grid(image, draw, x, y, cell, ingredients)
    arrow(draw, 1308, 414)
    slot(draw, 1448, 350, 140)
    item(image, empty_map, 1473, 375, 90)
    return image


def cartography_guide(paper, empty_map, filled_map, planks, table_top, table_left, table_right, glass, previews):
    image = background()
    draw = ImageDraw.Draw(image)
    panel(draw, (32, 55, 790, 886), 1)
    panel(draw, (825, 55, 1640, 886), 2)

    cell = 122
    x, y = 90, 270
    ingredients = [(0, 0, paper), (1, 0, paper), (0, 1, planks), (1, 1, planks), (0, 2, planks), (1, 2, planks)]
    recipe_grid(image, draw, x, y, cell, ingredients)
    arrow(draw, 485, 418)
    slot(draw, 620, 350, 145)
    cube = table_cube(table_top, table_left, table_right)
    image.alpha_composite(cube, (598, 325))

    operations = ((paper, previews[0]), (empty_map, previews[1]), (glass, previews[2]))
    for row, (material, preview) in enumerate(operations):
        top = 190 + row * 220
        slot(draw, 880, top, 122)
        item(image, filled_map, 900, top + 20, 82)
        plus(draw, 1030, top + 36)
        slot(draw, 1105, top, 122)
        item(image, material, 1125, top + 20, 82)
        arrow(draw, 1260, top + 25, 105, 70)
        slot(draw, 1395, top, 150)
        item(image, filled_map, 1423, top + 28, 94)
        marker = preview.copy()
        marker.thumbnail((80, 80), Image.Resampling.NEAREST)
        image.alpha_composite(marker, (1430, top + 35))
    return image


OUT.mkdir(parents=True, exist_ok=True)
with ZipFile(JAR) as archive:
    sugar_cane = asset(archive, "assets/minecraft/textures/item/sugar_cane.png")
    paper = asset(archive, "assets/minecraft/textures/item/paper.png")
    compass = asset(archive, "assets/minecraft/textures/item/compass_16.png")
    empty_map = asset(archive, "assets/minecraft/textures/item/map.png")
    filled_map = asset(archive, "assets/minecraft/textures/item/filled_map.png")
    planks = asset(archive, "assets/minecraft/textures/block/oak_planks.png")
    table_top = asset(archive, "assets/minecraft/textures/block/cartography_table_top.png")
    table_left = asset(archive, "assets/minecraft/textures/block/cartography_table_side1.png")
    table_right = asset(archive, "assets/minecraft/textures/block/cartography_table_side3.png")
    glass = asset(archive, "assets/minecraft/textures/block/glass.png")
    previews = [asset(archive, f"assets/minecraft/textures/gui/sprites/container/cartography_table/{name}.png")
                for name in ("scaled_map", "duplicated_map", "locked")]
    map_recipes(sugar_cane, paper, compass, empty_map).save(OUT / "map_recipe.png", optimize=True)
    cartography_guide(paper, empty_map, filled_map, planks, table_top, table_left, table_right, glass, previews).save(
        OUT / "cartography_table_guide.png", optimize=True
    )

print(OUT)
