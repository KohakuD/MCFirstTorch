"""Generate Boat recipe and controls guides from exact Minecraft 26.1.2 assets."""

from io import BytesIO
import os
from pathlib import Path
from zipfile import ZipFile

from PIL import Image, ImageDraw, ImageFont


JAR = Path(os.environ.get(
    "FIRST_TORCH_MINECRAFT_JAR",
    r"C:\Games\CurseForge\Minecraft\Install\versions\26.1.2\26.1.2.jar",
))
OUT = Path(__file__).resolve().parents[1] / "overrides/resourcepacks/first_torch_guides/assets/firsttorch/textures/questpics"
W, H = 1672, 941
ORANGE = (255, 137, 0, 255)
try:
    KEY_FONT = ImageFont.truetype("arialbd.ttf", 42)
except OSError:
    KEY_FONT = ImageFont.load_default()


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


def cube_icon(top_texture, side_texture=None, size=130):
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


def arrow(draw, left, top, width=170, height=100, colour=(194, 195, 196, 255)):
    points = [(left, top + height // 3), (left + width * 3 // 5, top + height // 3),
              (left + width * 3 // 5, top), (left + width, top + height // 2),
              (left + width * 3 // 5, top + height), (left + width * 3 // 5, top + height * 2 // 3),
              (left, top + height * 2 // 3)]
    draw.polygon(points, fill=colour)


def create_recipe(planks, boat):
    image = background()
    draw = ImageDraw.Draw(image)
    left, top, cell = 225, 225, 160
    for row in range(3):
        for col in range(3):
            slot(draw, left + col * cell, top + row * cell, cell)
    block = cube_icon(planks, size=124)
    for col, row in ((0, 1), (2, 1), (0, 2), (1, 2), (2, 2)):
        image.alpha_composite(block, (left + col * cell + (cell - block.width) // 2,
                                      top + row * cell + (cell - block.height) // 2))
    arrow(draw, 800, 410)
    output_x, output_y, output_size = 1080, 335, 250
    slot(draw, output_x, output_y, output_size)
    boat_icon = boat.resize((190, 190), Image.Resampling.NEAREST)
    image.alpha_composite(boat_icon, (output_x + 30, output_y + 30))
    return image


def tinted_tile(texture, colour):
    tile = texture.crop((0, 0, 16, 16)).convert("RGBA")
    pixels = tile.load()
    for y in range(tile.height):
        for x in range(tile.width):
            r, g, b, a = pixels[x, y]
            grey = (r + g + b) / (3 * 255)
            pixels[x, y] = (int(colour[0] * grey), int(colour[1] * grey), int(colour[2] * grey), a)
    return tile


def fill_tiled(image, box, texture, tile_size=64):
    tile = texture.resize((tile_size, tile_size), Image.Resampling.NEAREST)
    for y in range(box[1], box[3], tile_size):
        for x in range(box[0], box[2], tile_size):
            image.alpha_composite(tile, (x, y))


def mouse(draw, x, y, right=False, left=False):
    draw.rounded_rectangle((x, y, x + 82, y + 122), radius=38, fill=(196, 198, 199, 255), outline=(21, 22, 23, 255), width=6)
    draw.line((x + 41, y + 4, x + 41, y + 58), fill=(21, 22, 23, 255), width=5)
    if right:
        draw.pieslice((x + 4, y + 4, x + 78, y + 78), 270, 360, fill=ORANGE)
    if left:
        draw.pieslice((x + 4, y + 4, x + 78, y + 78), 180, 270, fill=ORANGE)


def key(draw, x, y, letter=None, arrow_up=False, active=False):
    size = 72
    colour = (255, 151, 20, 255) if active else (85, 88, 90, 255)
    draw.rounded_rectangle((x, y, x + size, y + size), radius=9, fill=(38, 40, 41, 255), outline=colour, width=6)
    if letter:
        draw.text((x + 36, y + 36), letter, fill=(235, 236, 237, 255), font=KEY_FONT, anchor="mm")
    if arrow_up:
        draw.polygon(((x + 36, y + 15), (x + 55, y + 39), (x + 44, y + 39),
                      (x + 44, y + 58), (x + 28, y + 58), (x + 28, y + 39), (x + 17, y + 39)), fill=ORANGE)


def create_controls(water, sand, boat, steve):
    image = background()
    draw = ImageDraw.Draw(image, "RGBA")
    margin, gap = 45, 30
    panel_width = (W - 2 * margin - 2 * gap) // 3
    panels = []
    for index in range(3):
        left = margin + index * (panel_width + gap)
        box = (left, 65, left + panel_width, 875)
        panels.append(box)
        draw.rounded_rectangle(box, radius=12, fill=(38, 40, 41, 255), outline=(117, 119, 121, 255), width=7)

    water_tile = tinted_tile(water, (70, 130, 218))
    sand_tile = sand.crop((0, 0, 16, 16))
    boat_icon = boat.resize((230, 230), Image.Resampling.NEAREST)

    # Place and board with right click.
    box = panels[0]
    fill_tiled(image, (box[0] + 12, box[1] + 12, box[2] - 12, box[3] - 12), water_tile)
    image.alpha_composite(boat_icon, (box[0] + 125, 300))
    mouse(draw, box[0] + 205, 650, right=True)
    draw.line((box[0] + 246, 640, box[0] + 246, 565), fill=ORANGE, width=14)
    draw.polygon(((box[0] + 246, 545), (box[0] + 222, 578), (box[0] + 270, 578)), fill=ORANGE)

    # WASD steering on open water.
    box = panels[1]
    fill_tiled(image, (box[0] + 12, box[1] + 12, box[2] - 12, box[3] - 12), water_tile)
    image.alpha_composite(boat_icon, (box[0] + 135, 250))
    draw.arc((box[0] + 85, 170, box[0] + 420, 545), 205, 340, fill=ORANGE, width=15)
    key(draw, box[0] + 223, 590, "W", active=True)
    key(draw, box[0] + 145, 668, "A", active=True)
    key(draw, box[0] + 223, 668, "S")
    key(draw, box[0] + 301, 668, "D", active=True)

    # Shift to a safe shore, then left click and collect the Boat.
    box = panels[2]
    water_right = box[0] + 315
    fill_tiled(image, (box[0] + 12, box[1] + 12, water_right, box[3] - 12), water_tile)
    fill_tiled(image, (water_right, box[1] + 12, box[2] - 12, box[3] - 12), sand_tile)
    image.alpha_composite(boat_icon.resize((180, 180), Image.Resampling.NEAREST), (box[0] + 165, 315))
    head = steve.crop((8, 8, 16, 16)).resize((115, 115), Image.Resampling.NEAREST)
    image.alpha_composite(head, (box[0] + 355, 350))
    key(draw, box[0] + 365, 535, arrow_up=True, active=True)
    mouse(draw, box[0] + 85, 620, left=True)
    draw.line((box[0] + 126, 610, box[0] + 205, 505), fill=ORANGE, width=14)
    draw.polygon(((box[0] + 216, 490), (box[0] + 179, 507), (box[0] + 218, 535)), fill=ORANGE)
    return image


OUT.mkdir(parents=True, exist_ok=True)
with ZipFile(JAR) as archive:
    planks = asset(archive, "assets/minecraft/textures/block/oak_planks.png")
    boat = asset(archive, "assets/minecraft/textures/item/oak_boat.png")
    water = asset(archive, "assets/minecraft/textures/block/water_still.png")
    sand = asset(archive, "assets/minecraft/textures/block/sand.png")
    steve = asset(archive, "assets/minecraft/textures/entity/player/wide/steve.png")
    create_recipe(planks, boat).save(OUT / "boat_recipe.png", optimize=True)
    create_controls(water, sand, boat, steve).save(OUT / "boat_controls.png", optimize=True)

print(OUT)
