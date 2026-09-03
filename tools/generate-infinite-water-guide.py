"""Generate infinite-water layouts from exact Minecraft 26.1.2 textures."""

from io import BytesIO
from pathlib import Path
from zipfile import ZipFile

from PIL import Image, ImageDraw, ImageEnhance, ImageChops


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


def vanilla_water_tint(texture):
    # Vanilla applies biome colour to the exact animated Water texture at render time.
    grey = ImageEnhance.Contrast(texture.convert("L")).enhance(1.15).convert("RGBA")
    tint = Image.new("RGBA", grey.size, (63, 118, 228, 255))
    coloured = ImageChops.multiply(grey, tint)
    coloured.putalpha(texture.getchannel("A"))
    return coloured


def tile(image, texture, x, y, size):
    image.alpha_composite(texture.resize((size, size), Image.Resampling.NEAREST), (x, y))


def down_arrow(draw, x, top, bottom):
    draw.line((x, top, x, bottom - 34), fill=(255, 137, 0, 255), width=18)
    draw.polygon(((x, bottom), (x - 34, bottom - 42), (x + 34, bottom - 42)), fill=(255, 137, 0, 255))


def up_arrow(draw, x, top, bottom):
    draw.line((x, bottom, x, top + 34), fill=(255, 137, 0, 255), width=18)
    draw.polygon(((x, top), (x - 34, top + 42), (x + 34, top + 42)), fill=(255, 137, 0, 255))


def item(image, texture, centre_x, top, size=104):
    icon = texture.resize((size, size), Image.Resampling.NEAREST)
    image.alpha_composite(icon, (centre_x - size // 2, top))


def create_guide(water, water_bucket, empty_bucket):
    image = background()
    draw = ImageDraw.Draw(image)
    draw.line((836, 90, 836, 851), fill=(91, 94, 96, 255), width=5)
    cell = 190

    # 2 x 2: place the two source buckets diagonally; all four settle as sources.
    left, top = 228, 390
    for row in range(2):
        for col in range(2):
            x, y = left + col * cell, top + row * cell
            tile(image, water, x, y, cell)
            draw.rectangle((x, y, x + cell, y + cell), outline=(8, 21, 48, 255), width=7)
    diagonal = ((left, top), (left + cell, top + cell))
    for x, y in diagonal:
        draw.rectangle((x + 8, y + 8, x + cell - 8, y + cell - 8), outline=(255, 137, 0, 255), width=11)
        centre = x + cell // 2
        item(image, water_bucket, centre, 160 if y == top else 820, 96)
        if y == top:
            down_arrow(draw, centre, 268, y - 12)
        else:
            up_arrow(draw, centre, y + cell + 5, 812)

    # 1 x 3: source buckets at both ends; repeatedly collect only the centre.
    right, row_y = 946, 440
    for col in range(3):
        x = right + col * cell
        tile(image, water, x, row_y, cell)
        draw.rectangle((x, row_y, x + cell, row_y + cell), outline=(8, 21, 48, 255), width=7)
    for col in (0, 2):
        x = right + col * cell
        draw.rectangle((x + 8, row_y + 8, x + cell - 8, row_y + cell - 8), outline=(255, 137, 0, 255), width=11)
        centre = x + cell // 2
        item(image, water_bucket, centre, 180, 96)
        down_arrow(draw, centre, 286, row_y - 12)
    middle_x = right + cell
    draw.rectangle((middle_x + 16, row_y + 16, middle_x + cell - 16, row_y + cell - 16),
                   outline=(255, 255, 255, 255), width=10)
    centre = middle_x + cell // 2
    up_arrow(draw, centre, row_y + cell + 20, 748)
    item(image, empty_bucket, centre, 758, 96)
    return image


OUT.mkdir(parents=True, exist_ok=True)
with ZipFile(JAR) as archive:
    water = vanilla_water_tint(asset(archive, "assets/minecraft/textures/block/water_still.png"))
    water_bucket = asset(archive, "assets/minecraft/textures/item/water_bucket.png")
    empty_bucket = asset(archive, "assets/minecraft/textures/item/bucket.png")
    create_guide(water, water_bucket, empty_bucket).save(OUT / "infinite_water_sources.png", optimize=True)

print(OUT)
