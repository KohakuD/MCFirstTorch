"""Generate the End Crystal removal guide from exact Minecraft 26.1.2 assets."""

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


def slot_item(image, texture, centre, size):
    icon = texture.resize((size, size), Image.Resampling.NEAREST)
    image.alpha_composite(icon, (int(centre[0] - size / 2), int(centre[1] - size / 2)))


def arrow(draw, start, end, colour=(255, 137, 0, 255), width=15):
    draw.line((start, end), fill=colour, width=width)
    dx, dy = end[0] - start[0], end[1] - start[1]
    length = max(1, (dx * dx + dy * dy) ** 0.5)
    ux, uy = dx / length, dy / length
    px, py = -uy, ux
    tip = end
    base = (end[0] - ux * 48, end[1] - uy * 48)
    draw.polygon((tip, (base[0] + px * 28, base[1] + py * 28), (base[0] - px * 28, base[1] - py * 28)), fill=colour)


def panel(draw, left, right, number):
    draw.rounded_rectangle((left, 55, right, 886), radius=12, fill=(35, 35, 40, 255), outline=(111, 109, 119, 255), width=6)
    draw.ellipse((left + 18, 73, left + 82, 137), fill=(255, 137, 0, 255))
    # Language-neutral step number using simple straight strokes.
    if number == 1:
        draw.line((left + 50, 89, left + 50, 121), fill=(255, 255, 255, 255), width=9)
    elif number == 2:
        draw.line((left + 36, 92, left + 51, 85, left + 64, 94, left + 37, 121, left + 66, 121), fill=(255, 255, 255, 255), width=8, joint="curve")
    else:
        draw.line((left + 37, 89, left + 64, 89, left + 49, 104, left + 64, 118, left + 37, 122), fill=(255, 255, 255, 255), width=8, joint="curve")


def floor(image, draw, left, right, end_stone):
    size = 72
    top = 766
    for x in range(left + 18, right - 18, size):
        tile(image, end_stone, (x, top, min(x + size, right - 18), 838))
    draw.rectangle((left + 18, top, right - 18, 838), outline=(92, 94, 62, 255), width=4)


def pillar(image, draw, centre, obsidian, crystal, cage=None, height=5):
    block = 82
    bottom = 766
    left = centre - block // 2
    for level in range(height):
        top = bottom - (level + 1) * block
        tile(image, obsidian, (left, top, left + block, top + block))
        draw.rectangle((left, top, left + block, top + block), outline=(12, 9, 17, 255), width=4)
    crystal_y = bottom - height * block - 78
    slot_item(image, crystal, (centre, crystal_y), 118)
    if cage is not None:
        cage_left, cage_top = centre - 102, crystal_y - 94
        cage_right, cage_bottom = centre + 102, crystal_y + 104
        bar = cage.resize((32, cage_bottom - cage_top), Image.Resampling.NEAREST)
        image.alpha_composite(bar, (cage_left, cage_top))
        image.alpha_composite(bar, (cage_right - 32, cage_top))
        horizontal = cage.rotate(90, expand=True).resize((cage_right - cage_left, 32), Image.Resampling.NEAREST)
        image.alpha_composite(horizontal, (cage_left, cage_top))
        image.alpha_composite(horizontal, (cage_left, cage_bottom - 32))
        return (cage_left, cage_top, cage_right, cage_bottom)
    return None


def create_guide(obsidian, end_stone, iron_bars, crystal, bow, pickaxe):
    image = background()
    draw = ImageDraw.Draw(image, "RGBA")
    margin, gap = 42, 28
    panel_width = (W - 2 * margin - 2 * gap) // 3
    bounds = []
    for index in range(3):
        left = margin + index * (panel_width + gap)
        right = left + panel_width
        panel(draw, left, right, index + 1)
        floor(image, draw, left, right, end_stone)
        bounds.append((left, right))

    # 1: destroy an exposed Crystal with a fully drawn Bow from the ground.
    left, right = bounds[0]
    pillar(image, draw, left + 345, obsidian, crystal, height=5)
    slot_item(image, bow, (left + 105, 686), 110)
    arrow(draw, (left + 154, 652), (left + 314, 275))

    # 2: climb on exact End Stone beside a cage and open only one side.
    left, right = bounds[1]
    cage_box = pillar(image, draw, left + 342, obsidian, crystal, iron_bars, height=5)
    stair_left = left + 92
    block = 72
    for level in range(5):
        x = stair_left + level * 44
        y = 766 - (level + 1) * block
        tile(image, end_stone, (x, y, x + block, y + block))
        draw.rectangle((x, y, x + block, y + block), outline=(91, 93, 62, 255), width=4)
    slot_item(image, pickaxe, (left + 286, 270), 92)
    arrow(draw, (left + 278, 316), (cage_box[0] + 12, (cage_box[1] + cage_box[3]) // 2), width=12)
    # White opening marks the single side bar to remove, never the Crystal.
    draw.rectangle((cage_box[0] - 5, cage_box[1] + 58, cage_box[0] + 39, cage_box[3] - 50), outline=(245, 245, 245, 255), width=8)

    # 3: return to the ground and shoot through the opened side from a distance.
    left, right = bounds[2]
    cage_box = pillar(image, draw, left + 345, obsidian, crystal, iron_bars, height=5)
    draw.rectangle((cage_box[0] - 5, cage_box[1] + 58, cage_box[0] + 39, cage_box[3] - 50), fill=(35, 35, 40, 255), outline=(245, 245, 245, 255), width=7)
    slot_item(image, bow, (left + 100, 686), 110)
    arrow(draw, (left + 151, 650), (cage_box[0] + 28, (cage_box[1] + cage_box[3]) // 2))
    return image


OUT.mkdir(parents=True, exist_ok=True)
with ZipFile(JAR) as archive:
    obsidian = asset(archive, "assets/minecraft/textures/block/obsidian.png")
    end_stone = asset(archive, "assets/minecraft/textures/block/end_stone.png")
    iron_bars = asset(archive, "assets/minecraft/textures/block/iron_bars.png")
    crystal = asset(archive, "assets/minecraft/textures/item/end_crystal.png")
    bow = asset(archive, "assets/minecraft/textures/item/bow_pulling_2.png")
    pickaxe = asset(archive, "assets/minecraft/textures/item/iron_pickaxe.png")
    create_guide(obsidian, end_stone, iron_bars, crystal, bow, pickaxe).save(OUT / "end_crystal_removal.png", optimize=True)

print(OUT)
