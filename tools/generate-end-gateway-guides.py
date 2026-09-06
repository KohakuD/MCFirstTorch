"""Generate End Gateway route guides from exact Minecraft 26.1.2 assets."""

from io import BytesIO
from pathlib import Path
from zipfile import ZipFile

from PIL import Image, ImageDraw


JAR = Path(r"D:\Minecraft\curseforge\minecraft\Install\versions\26.1.2\26.1.2.jar")
OUT = Path(__file__).resolve().parents[1] / "overrides/resourcepacks/first_torch_guides/assets/firsttorch/textures/questpics"
W, H = 1672, 941
ORANGE = (255, 137, 0, 255)


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


def paste_nearest(image, texture, box):
    x0, y0, x1, y1 = box
    image.alpha_composite(texture.resize((x1 - x0, y1 - y0), Image.Resampling.NEAREST), (x0, y0))


def block(image, texture, box, outline=(30, 30, 34, 255)):
    paste_nearest(image, texture, box)
    ImageDraw.Draw(image).rectangle(box, outline=outline, width=4)


def arrow(draw, start, end, colour=ORANGE, width=16):
    draw.line((start, end), fill=colour, width=width)
    dx, dy = end[0] - start[0], end[1] - start[1]
    length = max(1, (dx * dx + dy * dy) ** 0.5)
    ux, uy = dx / length, dy / length
    px, py = -uy, ux
    base = (end[0] - ux * 48, end[1] - uy * 48)
    draw.polygon((end, (base[0] + px * 27, base[1] + py * 27), (base[0] - px * 27, base[1] - py * 27)), fill=colour)


def gateway_front(image, bedrock, portal, centre, cell=105):
    cx, cy = centre
    # Exact front projection of the five-block-high generated gateway centre.
    for offset in (-2, -1, 1, 2):
        box = (cx - cell // 2, cy + offset * cell - cell // 2, cx + cell // 2, cy + offset * cell + cell // 2)
        block(image, bedrock, box)
    paste_nearest(image, portal, (cx - cell // 2, cy - cell // 2, cx + cell // 2, cy + cell // 2))
    ImageDraw.Draw(image).rectangle((cx - cell // 2, cy - cell // 2, cx + cell // 2, cy + cell // 2), outline=(112, 48, 148, 255), width=6)
    # Top and bottom cross-arms belong to the generated three-dimensional Bedrock frame.
    for y in (cy - 2 * cell, cy + 2 * cell):
        for x in (cx - cell, cx + cell):
            block(image, bedrock, (x - cell // 2, y - cell // 2, x + cell // 2, y + cell // 2))


def create_access_guide(end_stone, bedrock, portal, pearl):
    image = background()
    draw = ImageDraw.Draw(image, "RGBA")
    floor_y, cell = 790, 100
    for col in range(9):
        block(image, end_stone, (35 + col * cell, floor_y, 35 + (col + 1) * cell, 890), (91, 93, 61, 255))

    # The single approach block makes the first rise climbable without jumping
    # two blocks. Each following stage is a grid-aligned pair: Sneak outward with
    # the lower block, then place the next block on top.
    block(image, end_stone, (535, 690, 635, 790), (91, 93, 61, 255))
    for step in range(3):
        x = 635 + step * 100
        lower_y = floor_y - (step + 1) * 100
        block(image, end_stone, (x, lower_y, x + 100, lower_y + 100), (91, 93, 61, 255))
        block(image, end_stone, (x, lower_y - 100, x + 100, lower_y), (91, 93, 61, 255))
    # The standing platform stops before the Bedrock frame instead of passing
    # behind the portal in this side-view diagram.
    for col in range(3):
        x = 935 + col * 100
        block(image, end_stone, (x, 390, x + 100, 490), (91, 93, 61, 255))

    gateway_front(image, bedrock, portal, (1335, 365), 92)
    paste_nearest(image, pearl, (700, 230, 840, 370))
    arrow(draw, (830, 310), (1272, 365), width=20)
    return image


def create_arrival_guide(end_stone, bedrock, portal, cobblestone, torch):
    image = background()
    draw = ImageDraw.Draw(image, "RGBA")
    cell, floor_y = 105, 620
    for row in range(3):
        for col in range(13):
            x = 150 + col * cell
            y = floor_y + row * cell
            block(image, end_stone, (x, y, x + cell, y + 105), (91, 93, 61, 255))

    gateway_front(image, bedrock, portal, (390, 395), 90)
    draw.ellipse((240, 135, 540, 655), outline=ORANGE, width=14)

    # A conspicuous exact-texture marker remains beside the return gateway.
    block(image, cobblestone, (680, 505, 795, 620), (44, 44, 44, 255))
    block(image, cobblestone, (680, 390, 795, 505), (44, 44, 44, 255))
    paste_nearest(image, torch, (705, 265, 770, 390))
    arrow(draw, (570, 570), (660, 570), width=18)
    arrow(draw, (820, 570), (1320, 570), width=18)
    return image


OUT.mkdir(parents=True, exist_ok=True)
with ZipFile(JAR) as archive:
    end_stone = asset(archive, "assets/minecraft/textures/block/end_stone.png")
    bedrock = asset(archive, "assets/minecraft/textures/block/bedrock.png")
    portal = asset(archive, "assets/minecraft/textures/entity/end_portal/end_portal.png")
    pearl = asset(archive, "assets/minecraft/textures/item/ender_pearl.png")
    cobblestone = asset(archive, "assets/minecraft/textures/block/cobblestone.png")
    torch = asset(archive, "assets/minecraft/textures/block/torch.png")
    create_arrival_guide(end_stone, bedrock, portal, cobblestone, torch).save(OUT / "outer_end_arrival.png", optimize=True)

print(OUT)
