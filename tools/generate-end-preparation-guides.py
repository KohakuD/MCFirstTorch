"""Generate End-preparation guides from exact Minecraft 26.1.2 textures."""

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


def slot(draw, x, y, size=124):
    draw.rectangle((x, y, x + size, y + size), fill=(29, 31, 32, 255), outline=(5, 6, 7, 255), width=8)
    draw.line((x + 8, y + 8, x + size - 8, y + 8), fill=(153, 156, 158, 255), width=4)
    draw.line((x + 8, y + 8, x + 8, y + size - 8), fill=(115, 118, 120, 255), width=4)


def place_item(image, texture, x, y, size=88):
    icon = texture.resize((size, size), Image.Resampling.NEAREST)
    image.alpha_composite(icon, (x + (124 - size) // 2, y + (124 - size) // 2))


def arrow(draw, left, top, width=130, height=82):
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


def recipe_panel(image, left, textures, output, output_count=1):
    draw = ImageDraw.Draw(image)
    top, cell = 285, 124
    for row in range(3):
        for col in range(3):
            x, y = left + col * cell, top + row * cell
            slot(draw, x, y, cell)
            texture = textures.get((row, col))
            if texture is not None:
                place_item(image, texture, x, y)
    arrow(draw, left + 410, top + 144)
    out_x, out_y = left + 570, top + 96
    slot(draw, out_x, out_y, 180)
    icon = output.resize((118, 118), Image.Resampling.NEAREST)
    image.alpha_composite(icon, (out_x + 31, out_y + 31))
    if output_count > 1:
        # Minecraft-style quantity placement without depending on a font file.
        # Four small white blocks form a language-neutral "4".
        x, y = out_x + 142, out_y + 137
        draw.rectangle((x, y, x + 8, y + 34), fill=(255, 255, 255, 255))
        draw.rectangle((x - 20, y + 17, x + 8, y + 25), fill=(255, 255, 255, 255))
        draw.rectangle((x - 20, y, x - 12, y + 25), fill=(255, 255, 255, 255))


def create_bow_and_arrow(stick, string, flint, feather, bow, arrow_item):
    image = background()
    draw = ImageDraw.Draw(image)
    draw.line((836, 90, 836, 851), fill=(91, 94, 96, 255), width=5)
    bow_recipe = {
        (0, 1): stick,
        (0, 2): string,
        (1, 0): stick,
        (1, 2): string,
        (2, 1): stick,
        (2, 2): string,
    }
    arrow_recipe = {(0, 1): flint, (1, 1): stick, (2, 1): feather}
    recipe_panel(image, 55, bow_recipe, bow)
    recipe_panel(image, 891, arrow_recipe, arrow_item, 4)
    return image


def tile(image, texture, box, rotation=0):
    x0, y0, x1, y1 = box
    source = texture.rotate(rotation, expand=False) if rotation else texture
    image.alpha_composite(source.resize((x1 - x0, y1 - y0), Image.Resampling.NEAREST), (x0, y0))


def create_final_eye(frame_top, frame_eye, lava, stone_bricks, eye_item):
    image = background()
    draw = ImageDraw.Draw(image)
    cell = 142
    left, top = 410, 45
    for row in range(6):
        for col in range(6):
            tile(image, stone_bricks, (left + col * cell, top + row * cell,
                                       left + (col + 1) * cell, top + (row + 1) * cell))

    cx, cy = left + 2 * cell, top + 2 * cell
    for row in range(3):
        for col in range(3):
            tile(image, lava, (cx + col * cell, cy + row * cell,
                               cx + (col + 1) * cell, cy + (row + 1) * cell))

    frames = []
    for col in range(3):
        frames.append((cx + col * cell, cy - cell, 180))
        frames.append((cx + col * cell, cy + 3 * cell, 0))
    for row in range(3):
        frames.append((cx - cell, cy + row * cell, 90))
        frames.append((cx + 3 * cell, cy + row * cell, 270))

    empty_index = 4
    eye_top = frame_eye.crop((4, 4, 12, 12))
    for index, (x, y, rotation) in enumerate(frames):
        tile(image, frame_top, (x, y, x + cell, y + cell), rotation)
        if index != empty_index:
            eye_size = 72
            tile(image, eye_top, (x + 35, y + 35, x + 35 + eye_size, y + 35 + eye_size))
        draw.rectangle((x, y, x + cell, y + cell), outline=(12, 13, 14, 255), width=5)

    target_x, target_y, _ = frames[empty_index]
    draw.rectangle((target_x - 9, target_y - 9, target_x + cell + 9, target_y + cell + 9),
                   outline=(255, 137, 0, 255), width=11)
    safe_x, safe_y = cx - 2 * cell, cy + 2 * cell
    draw.rectangle((safe_x + 12, safe_y + 12, safe_x + cell - 12, safe_y + cell - 12),
                   outline=(255, 137, 0, 255), width=10)

    eye = eye_item.resize((112, 112), Image.Resampling.NEAREST)
    eye_x, eye_y = 1270, 390
    image.alpha_composite(eye, (eye_x, eye_y))
    draw.line((eye_x - 25, eye_y + 56, target_x + cell - 12, target_y + cell // 2),
              fill=(255, 137, 0, 255), width=18)
    draw.polygon(((target_x + cell - 12, target_y + cell // 2),
                  (target_x + cell + 35, target_y + cell // 2 - 30),
                  (target_x + cell + 35, target_y + cell // 2 + 30)),
                 fill=(255, 137, 0, 255))
    return image


OUT.mkdir(parents=True, exist_ok=True)
with ZipFile(JAR) as archive:
    stick = asset(archive, "assets/minecraft/textures/item/stick.png")
    string = asset(archive, "assets/minecraft/textures/item/string.png")
    flint = asset(archive, "assets/minecraft/textures/item/flint.png")
    feather = asset(archive, "assets/minecraft/textures/item/feather.png")
    bow = asset(archive, "assets/minecraft/textures/item/bow.png")
    arrow_item = asset(archive, "assets/minecraft/textures/item/arrow.png")
    frame_top = asset(archive, "assets/minecraft/textures/block/end_portal_frame_top.png")
    frame_eye = asset(archive, "assets/minecraft/textures/block/end_portal_frame_eye.png")
    lava = asset(archive, "assets/minecraft/textures/block/lava_still.png")
    stone_bricks = asset(archive, "assets/minecraft/textures/block/stone_bricks.png")
    eye_item = asset(archive, "assets/minecraft/textures/item/ender_eye.png")

    create_bow_and_arrow(stick, string, flint, feather, bow, arrow_item).save(
        OUT / "bow_and_arrows.png", optimize=True
    )
    create_final_eye(frame_top, frame_eye, lava, stone_bricks, eye_item).save(
        OUT / "end_portal_final_eye.png", optimize=True
    )

print(OUT)
