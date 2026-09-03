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
    for col in range(10):
        block(image, end_stone, (35 + col * cell, floor_y, 35 + (col + 1) * cell, 890), (91, 93, 61, 255))

    # A crouched staircase and small working platform reach the elevated gateway without an open jump.
    for step in range(7):
        x = 620 + step * 75
        y = floor_y - (step + 1) * 68
        block(image, end_stone, (x, y, x + 105, y + 105), (91, 93, 61, 255))
    for col in range(5):
        x = 1080 + col * 105
        block(image, end_stone, (x, 520, x + 105, 625), (91, 93, 61, 255))
    # Raised rear edge communicates a guarded standing platform.
    for col in range(4):
        x = 1185 + col * 105
        block(image, end_stone, (x, 415, x + 105, 520), (91, 93, 61, 255))

    gateway_front(image, bedrock, portal, (1335, 285), 92)
    paste_nearest(image, pearl, (865, 300, 1015, 450))
    arrow(draw, (1000, 355), (1270, 285), width=20)
    return image


def create_arrival_guide(end_stone, bedrock, portal, cobblestone, torch, player):
    image = background()
    draw = ImageDraw.Draw(image, "RGBA")
    cell, floor_y = 105, 760
    for row in range(3):
        for col in range(13):
            x = 150 + col * cell + row * 35
            y = floor_y - row * 70
            block(image, end_stone, (x, y, x + cell, y + 105), (91, 93, 61, 255))

    gateway_front(image, bedrock, portal, (390, 340), 88)
    draw.ellipse((245, 115, 535, 600), outline=ORANGE, width=14)

    # A conspicuous exact-texture marker remains beside the return gateway.
    block(image, cobblestone, (625, 550, 740, 665), (44, 44, 44, 255))
    block(image, cobblestone, (625, 435, 740, 550), (44, 44, 44, 255))
    paste_nearest(image, torch, (650, 310, 715, 435))

    steve = player.crop((8, 8, 16, 16)).resize((115, 115), Image.Resampling.NEAREST)
    image.alpha_composite(steve, (1010, 505))
    arrow(draw, (945, 590), (760, 535), width=18)
    arrow(draw, (1135, 590), (1370, 590), width=18)
    return image


OUT.mkdir(parents=True, exist_ok=True)
with ZipFile(JAR) as archive:
    end_stone = asset(archive, "assets/minecraft/textures/block/end_stone.png")
    bedrock = asset(archive, "assets/minecraft/textures/block/bedrock.png")
    portal = asset(archive, "assets/minecraft/textures/entity/end_portal/end_portal.png")
    pearl = asset(archive, "assets/minecraft/textures/item/ender_pearl.png")
    cobblestone = asset(archive, "assets/minecraft/textures/block/cobblestone.png")
    torch = asset(archive, "assets/minecraft/textures/block/torch.png")
    player = asset(archive, "assets/minecraft/textures/entity/player/wide/steve.png")
    create_access_guide(end_stone, bedrock, portal, pearl).save(OUT / "end_gateway_access.png", optimize=True)
    create_arrival_guide(end_stone, bedrock, portal, cobblestone, torch, player).save(OUT / "outer_end_arrival.png", optimize=True)

print(OUT)
