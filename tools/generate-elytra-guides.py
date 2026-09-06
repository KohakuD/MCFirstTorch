"""Generate language-neutral Elytra practice and Firework Rocket guides."""

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


def tint(texture, colour):
    tinted = texture.copy()
    pixels = tinted.load()
    for y in range(tinted.height):
        for x in range(tinted.width):
            r, g, b, a = pixels[x, y]
            pixels[x, y] = (r * colour[0] // 255, g * colour[1] // 255,
                            b * colour[2] // 255, a)
    return tinted


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


def panel(draw, box):
    draw.rounded_rectangle(box, radius=20, fill=(34, 33, 39, 255), outline=(112, 109, 120, 255), width=6)


def tile(image, texture, box):
    x0, y0, x1, y1 = box
    image.alpha_composite(texture.resize((x1 - x0, y1 - y0), Image.Resampling.NEAREST), (x0, y0))


def arrow(draw, start, end, width=16):
    draw.line((start, end), fill=ORANGE, width=width)
    dx, dy = end[0] - start[0], end[1] - start[1]
    length = max(1, (dx * dx + dy * dy) ** 0.5)
    ux, uy = dx / length, dy / length
    px, py = -uy, ux
    base = (end[0] - ux * 40, end[1] - uy * 40)
    draw.polygon((end, (base[0] + px * 22, base[1] + py * 22),
                  (base[0] - px * 22, base[1] - py * 22)), fill=ORANGE)


def create_water_course(cobble, water, grass_top, grass_side, ladder, elytra):
    image = background()
    draw = ImageDraw.Draw(image, "RGBA")
    panel(draw, (35, 55, 800, 885))
    panel(draw, (835, 55, 1637, 885))

    # Top-down plan: five-wide launch deck with a two-block opening over a
    # broad water lane. Flat textures are intentional because this is a plan.
    cell = 62
    water_left, water_top = 132, 105
    for row in range(12):
        for col in range(9):
            tile(image, water, (water_left + col * cell, water_top + row * cell,
                                water_left + (col + 1) * cell, water_top + (row + 1) * cell))
    deck_left, deck_top = water_left + 2 * cell, water_top
    for row in range(4):
        for col in range(5):
            tile(image, cobble, (deck_left + col * cell, deck_top + row * cell,
                                 deck_left + (col + 1) * cell, deck_top + (row + 1) * cell))
    # Full-block rails on three sides; the two highlighted centre cells form
    # the only launch opening towards water.
    for col in range(5):
        draw.rectangle((deck_left + col * cell, deck_top,
                        deck_left + (col + 1) * cell, deck_top + cell), outline=(15, 15, 18, 255), width=8)
    for row in range(4):
        for col in (0, 4):
            draw.rectangle((deck_left + col * cell, deck_top + row * cell,
                            deck_left + (col + 1) * cell, deck_top + (row + 1) * cell),
                           outline=(15, 15, 18, 255), width=8)
    opening_y = deck_top + 4 * cell
    draw.rectangle((deck_left + cell, opening_y - cell,
                    deck_left + 2 * cell, opening_y), outline=(15, 15, 18, 255), width=8)
    for col in (2, 3):
        draw.rectangle((deck_left + col * cell + 4, opening_y - cell + 4,
                        deck_left + (col + 1) * cell - 4, opening_y - 4), outline=ORANGE, width=9)
    wing = elytra.resize((82, 82), Image.Resampling.NEAREST)
    image.alpha_composite(wing, (deck_left + 2 * cell + 21, deck_top + 2 * cell + 18))
    arrow(draw, (deck_left + 3 * cell, opening_y - 15),
          (deck_left + 3 * cell, water_top + 10 * cell), width=15)

    # Side profile: the launch floor is four blocks above the water surface,
    # with a rear wall, ladder access, and water beneath the launch opening.
    side_cell = 78
    base_x, water_y = 915, 720
    for col in range(8):
        tile(image, water, (base_x + col * side_cell, water_y,
                            base_x + (col + 1) * side_cell, water_y + side_cell))
    for col in range(2):
        tile(image, grass_top, (base_x + col * side_cell, water_y,
                                base_x + (col + 1) * side_cell, water_y + 20))
        tile(image, grass_side, (base_x + col * side_cell, water_y + 20,
                                 base_x + (col + 1) * side_cell, water_y + side_cell))
    deck_y = water_y - 4 * side_cell
    for col in range(4):
        tile(image, cobble, (base_x + col * side_cell, deck_y,
                             base_x + (col + 1) * side_cell, deck_y + side_cell))
    for row in range(1, 5):
        tile(image, cobble, (base_x + side_cell, water_y - row * side_cell,
                             base_x + 2 * side_cell, water_y - (row - 1) * side_cell))
        ladder_icon = ladder.resize((side_cell - 16, side_cell - 16), Image.Resampling.NEAREST)
        image.alpha_composite(ladder_icon, (base_x + side_cell + 8, water_y - row * side_cell + 8))
    tile(image, cobble, (base_x, deck_y - side_cell, base_x + side_cell, deck_y))
    wing_small = elytra.resize((72, 72), Image.Resampling.NEAREST)
    image.alpha_composite(wing_small, (base_x + 2 * side_cell + 4, deck_y - 10))
    arrow(draw, (base_x + 3 * side_cell, deck_y + 20),
          (base_x + 7 * side_cell, water_y + 18), width=15)
    for index in range(4):
        draw.rectangle((1510, deck_y + index * side_cell + 8,
                        1540, deck_y + (index + 1) * side_cell - 8), fill=ORANGE)
    return image


def slot(draw, x, y, size=160):
    draw.rectangle((x, y, x + size, y + size), fill=(29, 31, 32, 255), outline=(5, 6, 7, 255), width=8)
    draw.line((x + 8, y + 8, x + size - 8, y + 8), fill=(153, 156, 158, 255), width=4)
    draw.line((x + 8, y + 8, x + 8, y + size - 8), fill=(115, 118, 120, 255), width=4)


def recipe_arrow(draw, left, top, width=170, height=100):
    points = [(left, top + height // 3), (left + width * 3 // 5, top + height // 3),
              (left + width * 3 // 5, top), (left + width, top + height // 2),
              (left + width * 3 // 5, top + height), (left + width * 3 // 5, top + height * 2 // 3),
              (left, top + height * 2 // 3)]
    draw.polygon(points, fill=(194, 195, 196, 255))


def create_rocket_recipe(paper, gunpowder, rocket):
    image = background()
    draw = ImageDraw.Draw(image)
    left, top, cell = 225, 225, 160
    for row in range(3):
        for col in range(3):
            slot(draw, left + col * cell, top + row * cell, cell)
    paper_icon = paper.resize((108, 108), Image.Resampling.NEAREST)
    powder_icon = gunpowder.resize((108, 108), Image.Resampling.NEAREST)
    image.alpha_composite(paper_icon, (left + 26, top + 26))
    image.alpha_composite(powder_icon, (left + cell + 26, top + cell + 26))
    recipe_arrow(draw, 800, 410)
    output_x, output_y, output_size = 1080, 335, 250
    slot(draw, output_x, output_y, output_size)
    rocket_icon = rocket.resize((170, 170), Image.Resampling.NEAREST)
    image.alpha_composite(rocket_icon, (output_x + 40, output_y + 40))
    # Three small exact rocket icons make the output quantity language-neutral.
    for index in range(3):
        tiny = rocket.resize((45, 45), Image.Resampling.NEAREST)
        image.alpha_composite(tiny, (1370 + index * 58, 435))
    return image


def main():
    with ZipFile(JAR) as archive:
        cobble = asset(archive, "assets/minecraft/textures/block/cobblestone.png")
        water_sheet = asset(archive, "assets/minecraft/textures/block/water_still.png")
        # Vanilla applies a biome water colour at render time. Plains blue keeps
        # the exact first animation frame recognisable in this neutral guide.
        water = tint(water_sheet.crop((0, 0, 16, 16)), (63, 118, 228))
        grass_top = asset(archive, "assets/minecraft/textures/block/grass_block_top.png")
        grass_side = asset(archive, "assets/minecraft/textures/block/grass_block_side.png")
        ladder = asset(archive, "assets/minecraft/textures/block/ladder.png")
        elytra = asset(archive, "assets/minecraft/textures/item/elytra.png")
        paper = asset(archive, "assets/minecraft/textures/item/paper.png")
        gunpowder = asset(archive, "assets/minecraft/textures/item/gunpowder.png")
        rocket = asset(archive, "assets/minecraft/textures/item/firework_rocket.png")

    OUT.mkdir(parents=True, exist_ok=True)
    create_water_course(cobble, water, grass_top, grass_side, ladder, elytra).save(
        OUT / "elytra_water_course.png", optimize=True)
    create_rocket_recipe(paper, gunpowder, rocket).save(
        OUT / "firework_rocket_recipe.png", optimize=True)
    print(OUT)


if __name__ == "__main__":
    main()
