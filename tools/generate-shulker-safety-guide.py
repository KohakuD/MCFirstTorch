"""Generate a language-neutral Shulker and Levitation guide from Minecraft 26.1.2 assets."""

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


def paste_nearest(image, texture, box):
    x0, y0, x1, y1 = box
    image.alpha_composite(texture.resize((x1 - x0, y1 - y0), Image.Resampling.NEAREST), (x0, y0))


def arrow(draw, start, end, colour=ORANGE, width=15):
    draw.line((start, end), fill=colour, width=width)
    dx, dy = end[0] - start[0], end[1] - start[1]
    length = max(1, (dx * dx + dy * dy) ** 0.5)
    ux, uy = dx / length, dy / length
    px, py = -uy, ux
    base = (end[0] - ux * 42, end[1] - uy * 42)
    draw.polygon((end, (base[0] + px * 24, base[1] + py * 24), (base[0] - px * 24, base[1] - py * 24)), fill=colour)


def panel(draw, box):
    draw.rounded_rectangle(box, radius=20, fill=(34, 33, 39, 255), outline=(112, 109, 120, 255), width=6)


def shulker_front(image, texture, centre_x, floor_y, open_shell):
    # ShulkerModel.createBodyLayer uses a 64 x 64 texture with exact model boxes:
    # lid 16 x 12 x 16 at UV 0,0; base 16 x 8 x 16 at UV 0,28;
    # head 6 x 6 x 6 at UV 0,52. These are their front orthographic faces.
    lid = texture.crop((16, 16, 32, 28))
    base = texture.crop((16, 44, 32, 52))
    head = texture.crop((6, 58, 12, 64))
    scale = 15
    left = centre_x - 8 * scale
    paste_nearest(image, base, (left, floor_y - 8 * scale, left + 16 * scale, floor_y))
    lift = 125 if open_shell else 0
    paste_nearest(image, lid, (left, floor_y - 20 * scale - lift, left + 16 * scale, floor_y - 8 * scale - lift))
    if open_shell:
        head_size = 6 * scale
        paste_nearest(image, head, (centre_x - head_size // 2, floor_y - 16 * scale, centre_x + head_size // 2, floor_y - 10 * scale))


def main():
    image = Image.new("RGBA", (W, H), (18, 16, 23, 255))
    draw = ImageDraw.Draw(image, "RGBA")
    draw.rectangle((3, 3, W - 4, H - 4), outline=(6, 6, 9, 255), width=6)
    draw.line((10, 10, W - 11, 10), fill=(108, 105, 116, 255), width=3)
    panel(draw, (35, 55, 745, 885))
    panel(draw, (775, 55, 1637, 885))

    with ZipFile(JAR) as archive:
        shulker = asset(archive, "assets/minecraft/textures/entity/shulker/shulker.png")
        bullet_sheet = asset(archive, "assets/minecraft/textures/entity/shulker/spark.png")
        levitation = asset(archive, "assets/minecraft/textures/mob_effect/levitation.png")
        milk = asset(archive, "assets/minecraft/textures/item/milk_bucket.png")
        water = asset(archive, "assets/minecraft/textures/item/water_bucket.png")

    shulker_front(image, shulker, 235, 690, False)
    shulker_front(image, shulker, 555, 690, True)
    arrow(draw, (325, 360), (445, 360), width=17)

    # The projectile uses ShulkerBulletModel's exact 8 x 8 front face at UV 0,0.
    bullet = bullet_sheet.crop((2, 2, 10, 10))
    paste_nearest(image, bullet, (875, 245, 1035, 405))
    draw.line((1045, 325, 1180, 325, 1180, 505), fill=ORANGE, width=16, joint="curve")
    arrow(draw, (1180, 505), (1280, 505), width=16)
    draw.rectangle((1280, 380, 1340, 650), fill=(112, 109, 120, 255))
    for end in ((1235, 465), (1235, 545), (1260, 425), (1260, 585)):
        draw.line((1280, 505, end[0], end[1]), fill=ORANGE, width=10)

    paste_nearest(image, levitation, (1425, 175, 1545, 295))
    arrow(draw, (1485, 330), (1485, 455), width=16)
    paste_nearest(image, milk, (1370, 485, 1465, 580))
    paste_nearest(image, water, (1510, 485, 1605, 580))
    draw.rectangle((1360, 465, 1615, 600), outline=(112, 109, 120, 255), width=5)

    OUT.mkdir(parents=True, exist_ok=True)
    image.save(OUT / "shulker_levitation.png", optimize=True)
    print(OUT / "shulker_levitation.png")


if __name__ == "__main__":
    main()
