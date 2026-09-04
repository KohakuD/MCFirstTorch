"""Generate the common-hostile-mob guide from exact Minecraft 26.1.2 assets."""

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


def asset(archive, path):
    return Image.open(BytesIO(archive.read(path))).convert("RGBA")


def crop_scaled(texture, box, size):
    return texture.crop(box).resize(size, Image.Resampling.NEAREST)


def background():
    image = Image.new("RGBA", (W, H), (28, 29, 30, 255))
    pixels = image.load()
    for y in range(6, H - 6):
        shade = int(49 - 14 * y / H)
        for x in range(6, W - 6):
            vignette = int(6 * abs(x - W / 2) / (W / 2))
            value = max(24, shade - vignette)
            pixels[x, y] = (value, value, value, 255)
    draw = ImageDraw.Draw(image)
    draw.rectangle((2, 2, W - 3, H - 3), outline=(7, 8, 9, 255), width=6)
    draw.line((8, 8, W - 9, 8), fill=(126, 129, 130, 255), width=3)
    draw.line((8, 9, 8, H - 9), fill=(91, 94, 96, 255), width=3)
    return image


def biped(texture, slender=False):
    canvas = Image.new("RGBA", (320, 620), (0, 0, 0, 0))
    if slender:
        head_size, body_width, limb_width = 126, 76, 28
    else:
        head_size, body_width, limb_width = 132, 112, 50
    body_height, limb_height = 205, 205
    head = crop_scaled(texture, (8, 8, 16, 16), (head_size, head_size))
    body = crop_scaled(texture, (20, 20, 28, 32), (body_width, body_height))
    arm = crop_scaled(texture, (44, 20, 48, 32), (limb_width, limb_height))
    leg = crop_scaled(texture, (4, 20, 8, 32), (limb_width, limb_height))
    canvas.alpha_composite(head, ((320 - head_size) // 2, 12))
    body_x, body_y = (320 - body_width) // 2, 144
    canvas.alpha_composite(body, (body_x, body_y))
    canvas.alpha_composite(arm, (body_x - limb_width - 5, body_y))
    canvas.alpha_composite(arm.transpose(Image.Transpose.FLIP_LEFT_RIGHT), (body_x + body_width + 5, body_y))
    gap = 7
    canvas.alpha_composite(leg, (160 - gap - limb_width, body_y + body_height))
    canvas.alpha_composite(leg.transpose(Image.Transpose.FLIP_LEFT_RIGHT), (160 + gap, body_y + body_height))
    return canvas


def creeper_model(texture):
    canvas = Image.new("RGBA", (320, 620), (0, 0, 0, 0))
    head = crop_scaled(texture, (8, 8, 16, 16), (154, 154))
    body = crop_scaled(texture, (20, 20, 28, 32), (118, 210))
    leg = crop_scaled(texture, (4, 20, 8, 26), (54, 135))
    canvas.alpha_composite(head, (83, 55))
    canvas.alpha_composite(body, (101, 209))
    for x, flip in ((72, False), (132, False), (188, True), (128, True)):
        current = leg.transpose(Image.Transpose.FLIP_LEFT_RIGHT) if flip else leg
        canvas.alpha_composite(current, (x, 419 if x in (72, 188) else 402))
    return canvas


def spider_model(texture):
    canvas = Image.new("RGBA", (350, 620), (0, 0, 0, 0))
    abdomen = crop_scaled(texture, (12, 24, 22, 32), (188, 150))
    body = crop_scaled(texture, (6, 6, 12, 12), (115, 115))
    head = crop_scaled(texture, (40, 12, 48, 20), (155, 155))
    leg_texture = crop_scaled(texture, (18, 0, 34, 2), (190, 24))
    canvas.alpha_composite(abdomen, (81, 185))
    canvas.alpha_composite(body, (118, 284))
    draw = ImageDraw.Draw(canvas)
    for index, y in enumerate((265, 310, 355, 400)):
        offset = index * 12
        left_leg = leg_texture.rotate(18 + index * 7, resample=Image.Resampling.NEAREST, expand=True)
        right_leg = left_leg.transpose(Image.Transpose.FLIP_LEFT_RIGHT)
        canvas.alpha_composite(left_leg, (-18 - offset, y))
        canvas.alpha_composite(right_leg, (350 - right_leg.width + 18 + offset, y))
    canvas.alpha_composite(head, (98, 350))
    # Reassert the exact head silhouette over the leg roots.
    draw.rectangle((98, 350, 252, 504), outline=(10, 10, 10, 190), width=3)
    return canvas


def add_drop(image, item, panel_left, panel_width):
    draw = ImageDraw.Draw(image)
    size = 108
    x = panel_left + (panel_width - size) // 2
    y = 748
    draw.rectangle((x, y, x + size, y + size), fill=(29, 31, 32, 255), outline=(5, 6, 7, 255), width=7)
    draw.line((x + 7, y + 7, x + size - 7, y + 7), fill=(153, 156, 158, 255), width=4)
    draw.line((x + 7, y + 7, x + 7, y + size - 7), fill=(115, 118, 120, 255), width=4)
    icon = item.resize((78, 78), Image.Resampling.NEAREST)
    image.alpha_composite(icon, (x + 15, y + 15))


def create_guide(zombie, skeleton, spider, creeper, drops):
    image = background()
    draw = ImageDraw.Draw(image, "RGBA")
    margin, gap = 38, 24
    panel_width = (W - 2 * margin - 3 * gap) // 4
    models = (biped(zombie), biped(skeleton, slender=True), spider_model(spider), creeper_model(creeper))
    for index, (model, drop) in enumerate(zip(models, drops)):
        left = margin + index * (panel_width + gap)
        right = left + panel_width
        draw.rounded_rectangle((left, 55, right, 880), radius=12,
                               fill=(38, 40, 41, 255), outline=(117, 119, 121, 255), width=7)
        draw.ellipse((left + 55, 690, right - 55, 745), fill=(11, 12, 12, 150))
        image.alpha_composite(model, (left + (panel_width - model.width) // 2, 75))
        add_drop(image, drop, left, panel_width)
    return image


OUT.mkdir(parents=True, exist_ok=True)
with ZipFile(JAR) as archive:
    zombie = asset(archive, "assets/minecraft/textures/entity/zombie/zombie.png")
    skeleton = asset(archive, "assets/minecraft/textures/entity/skeleton/skeleton.png")
    spider = asset(archive, "assets/minecraft/textures/entity/spider/spider.png")
    creeper = asset(archive, "assets/minecraft/textures/entity/creeper/creeper.png")
    drops = (
        asset(archive, "assets/minecraft/textures/item/rotten_flesh.png"),
        asset(archive, "assets/minecraft/textures/item/bone.png"),
        asset(archive, "assets/minecraft/textures/item/string.png"),
        asset(archive, "assets/minecraft/textures/item/gunpowder.png"),
    )
    create_guide(zombie, skeleton, spider, creeper, drops).save(OUT / "hostile_mob_overview.png", optimize=True)

print(OUT)
