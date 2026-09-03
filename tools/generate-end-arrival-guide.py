"""Generate the End arrival guide from exact Minecraft 26.1.2 block textures."""

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
    image = Image.new("RGBA", (W, H), (9, 8, 13, 255))
    pixels = image.load()
    for y in range(6, H - 6):
        for x in range(6, W - 6):
            distance = abs(x - W / 2) / (W / 2)
            value = max(6, int(18 - 6 * distance - 3 * y / H))
            pixels[x, y] = (value, value - 2, value + 4, 255)
    draw = ImageDraw.Draw(image)
    draw.rectangle((2, 2, W - 3, H - 3), outline=(4, 4, 6, 255), width=6)
    draw.line((8, 8, W - 9, 8), fill=(92, 89, 99, 255), width=3)
    draw.line((8, 9, 8, H - 9), fill=(65, 63, 70, 255), width=3)
    return image


def tile(image, texture, box):
    x0, y0, x1, y1 = box
    image.alpha_composite(texture.resize((x1 - x0, y1 - y0), Image.Resampling.NEAREST), (x0, y0))


def create_guide(obsidian, end_stone, cobblestone):
    image = background()
    draw = ImageDraw.Draw(image)
    cell = 104
    platform_left, platform_top = 185, 210

    # Exact five-by-five arrival platform seen from above.
    for row in range(5):
        for col in range(5):
            x = platform_left + col * cell
            y = platform_top + row * cell
            tile(image, obsidian, (x, y, x + cell, y + cell))
            draw.rectangle((x, y, x + cell, y + cell), outline=(12, 10, 18, 255), width=4)

    # A variable Void gap; the guide deliberately shows a safer two-wide bridge.
    bridge_left = platform_left + 5 * cell
    for col in range(4):
        for row in range(2):
            x = bridge_left + col * cell
            y = platform_top + (2 + row) * cell
            tile(image, cobblestone, (x, y, x + cell, y + cell))
            draw.rectangle((x, y, x + cell, y + cell), outline=(24, 24, 25, 255), width=4)

    # Irregular End-stone shoreline rather than a promised fixed platform distance.
    island_left = bridge_left + 4 * cell
    shoreline = [1, 0, 0, 1, 0, 0, 1]
    for row in range(7):
        for col in range(4):
            if col == 0 and shoreline[row]:
                continue
            x = island_left + col * cell
            y = 105 + row * cell
            tile(image, end_stone, (x, y, x + cell, y + cell))
            draw.rectangle((x, y, x + cell, y + cell), outline=(80, 82, 55, 255), width=4)

    # Orange overlays show the crouched route without redrawing any game asset.
    route_y = platform_top + 2.5 * cell
    draw.line((platform_left + 2.5 * cell, route_y, island_left + 1.2 * cell, route_y),
              fill=(255, 137, 0, 255), width=22)
    draw.polygon(((island_left + 1.2 * cell, route_y),
                  (island_left + 0.72 * cell, route_y - 44),
                  (island_left + 0.72 * cell, route_y + 44)),
                 fill=(255, 137, 0, 255))
    draw.rectangle((platform_left + 2 * cell + 10, platform_top + 2 * cell + 10,
                    platform_left + 3 * cell - 10, platform_top + 3 * cell - 10),
                   outline=(255, 137, 0, 255), width=10)
    return image


OUT.mkdir(parents=True, exist_ok=True)
with ZipFile(JAR) as archive:
    obsidian = asset(archive, "assets/minecraft/textures/block/obsidian.png")
    end_stone = asset(archive, "assets/minecraft/textures/block/end_stone.png")
    cobblestone = asset(archive, "assets/minecraft/textures/block/cobblestone.png")
    create_guide(obsidian, end_stone, cobblestone).save(OUT / "end_arrival_platform.png", optimize=True)

print(OUT)
