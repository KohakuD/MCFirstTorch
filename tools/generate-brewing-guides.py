"""Generate brewing quest guides from exact Minecraft 26.1.2 assets."""

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
    image = Image.new("RGBA", (W, H), (31, 32, 33, 255))
    pixels = image.load()
    for y in range(6, H - 6):
        shade = int(47 - 12 * y / H)
        for x in range(6, W - 6):
            vignette = int(5 * abs(x - W / 2) / (W / 2))
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


def sprite(texture, size=92):
    result = texture.resize((size, size), Image.Resampling.NEAREST)
    return result


def paste_center(image, icon, x, y, size=124):
    image.alpha_composite(icon, (x + (size - icon.width) // 2, y + (size - icon.height) // 2))


def arrow(draw, left, top, width=155, height=96):
    points = [(left, top + height // 3), (left + width * 3 // 5, top + height // 3),
              (left + width * 3 // 5, top), (left + width, top + height // 2),
              (left + width * 3 // 5, top + height), (left + width * 3 // 5, top + height * 2 // 3),
              (left, top + height * 2 // 3)]
    draw.polygon(points, fill=(194, 195, 196, 255))


def textured_quad(texture, points, shade=1.0):
    left = int(min(p[0] for p in points))
    top = int(min(p[1] for p in points))
    right = int(max(p[0] for p in points)) + 1
    bottom = int(max(p[1] for p in points)) + 1
    face = Image.new("RGBA", (right - left, bottom - top), (0, 0, 0, 0))
    pix = face.load()
    p0, p1, _, p3 = points
    ax, ay = p1[0] - p0[0], p1[1] - p0[1]
    bx, by = p3[0] - p0[0], p3[1] - p0[1]
    det = ax * by - ay * bx
    src = texture.load()
    for yy in range(face.height):
        sy = top + yy + 0.5 - p0[1]
        for xx in range(face.width):
            sx = left + xx + 0.5 - p0[0]
            u = (sx * by - sy * bx) / det
            v = (ax * sy - ay * sx) / det
            if 0 <= u < 1 and 0 <= v < 1:
                r, g, b, a = src[min(texture.width - 1, int(u * texture.width)), min(texture.height - 1, int(v * texture.height))]
                pix[xx, yy] = (int(r * shade), int(g * shade), int(b * shade), a)
    return face, (left, top)


def model_icon(elements, textures, size=108):
    canvas = Image.new("RGBA", (240, 240), (0, 0, 0, 0))
    scale = 7.3
    ox, oy = 120, 158

    def project(x, y, z):
        return (ox + (x - z) * scale * 0.72, oy + (x + z) * scale * 0.36 - y * scale)

    for element in sorted(elements, key=lambda e: (sum(e["from"][i] + e["to"][i] for i in (0, 2)), e["from"][1])):
        x0, y0, z0 = element["from"]
        x1, y1, z1 = element["to"]
        texture = textures[element.get("texture", "all")]
        faces = [
            ([project(x0, y1, z0), project(x1, y1, z0), project(x1, y1, z1), project(x0, y1, z1)], 1.0),
            ([project(x1, y0, z0), project(x1, y0, z1), project(x1, y1, z1), project(x1, y1, z0)], 0.72),
            ([project(x0, y0, z1), project(x1, y0, z1), project(x1, y1, z1), project(x0, y1, z1)], 0.84),
        ]
        for points, shade in faces:
            face, xy = textured_quad(texture, points, shade)
            canvas.alpha_composite(face, xy)
    box = canvas.getbbox()
    cropped = canvas.crop(box)
    cropped.thumbnail((size, size), Image.Resampling.NEAREST)
    return cropped


def recipe_grid(image, ingredients, output):
    draw = ImageDraw.Draw(image)
    left, top, cell = 330, 260, 126
    for row in range(3):
        for col in range(3):
            x, y = left + col * cell, top + row * cell
            slot(draw, x, y, cell)
            icon = ingredients.get((row, col))
            if icon:
                paste_center(image, icon, x, y, cell)
    arrow(draw, 785, 402)
    out_x, out_y, out_size = 1050, 355, 205
    slot(draw, out_x, out_y, out_size)
    paste_center(image, output, out_x, out_y, out_size)


def potion_icon(base, overlay, colour, size=58):
    tinted = Image.new("RGBA", overlay.size, (*colour, 0))
    tinted.putalpha(overlay.getchannel("A"))
    icon = Image.alpha_composite(tinted, base)
    return icon.resize((size, size), Image.Resampling.NEAREST)


def brewing_guide(gui, fuel_length, ingredient, bottle):
    image = background()
    # Only the functional top of the exact interface is needed; inventory rows add no new lesson.
    panel = gui.crop((0, 0, 176, 83)).resize((880, 415), Image.Resampling.NEAREST)
    image.alpha_composite(panel, (396, 255))
    scale = 5
    # A loaded fuel item is consumed from the upper-left slot. The stored charge is
    # represented by Minecraft's own yellow horizontal fuel sprite at (60, 44).
    fuel_bar = fuel_length.resize((18 * scale, 4 * scale), Image.Resampling.NEAREST)
    image.alpha_composite(fuel_bar, (396 + 60 * scale, 255 + 44 * scale))
    positions = [(79, 17, ingredient), (56, 51, bottle), (79, 58, bottle), (102, 51, bottle)]
    for x, y, icon in positions:
        scaled = icon.resize((16 * scale, 16 * scale), Image.Resampling.NEAREST)
        image.alpha_composite(scaled, (396 + x * scale, 255 + y * scale))
    return image


OUT.mkdir(parents=True, exist_ok=True)
with ZipFile(JAR) as archive:
    blaze_rod = asset(archive, "assets/minecraft/textures/item/blaze_rod.png")
    blaze_powder = asset(archive, "assets/minecraft/textures/item/blaze_powder.png")
    glass_bottle = asset(archive, "assets/minecraft/textures/item/glass_bottle.png")
    glass = asset(archive, "assets/minecraft/textures/block/glass.png")
    cobblestone = asset(archive, "assets/minecraft/textures/block/cobblestone.png")
    stand = asset(archive, "assets/minecraft/textures/block/brewing_stand.png")
    stand_base = asset(archive, "assets/minecraft/textures/block/brewing_stand_base.png")
    nether_wart = asset(archive, "assets/minecraft/textures/item/nether_wart.png")
    potion = asset(archive, "assets/minecraft/textures/item/potion.png")
    potion_overlay = asset(archive, "assets/minecraft/textures/item/potion_overlay.png")
    gui = asset(archive, "assets/minecraft/textures/gui/container/brewing_stand.png")
    fuel_length = asset(archive, "assets/minecraft/textures/gui/sprites/container/brewing_stand/fuel_length.png")

    cube = [{"from": [0, 0, 0], "to": [16, 16, 16], "texture": "all"}]
    cobble_icon = model_icon(cube, {"all": cobblestone})
    glass_icon = model_icon(cube, {"all": glass})
    stand_elements = [
        {"from": [7, 0, 7], "to": [9, 14, 9], "texture": "stand"},
        {"from": [9, 0, 5], "to": [15, 2, 11], "texture": "base"},
        {"from": [1, 0, 1], "to": [7, 2, 7], "texture": "base"},
        {"from": [1, 0, 9], "to": [7, 2, 15], "texture": "base"},
    ]
    stand_icon = model_icon(stand_elements, {"stand": stand, "base": stand_base}, 155)

    image = background()
    recipe_grid(image, {(1, 1): sprite(blaze_rod)}, sprite(blaze_powder, 118))
    image.save(OUT / "blaze_powder_recipe.png", optimize=True)

    image = background()
    recipe_grid(image, {(1, 1): sprite(blaze_rod), (2, 0): cobble_icon, (2, 1): cobble_icon, (2, 2): cobble_icon}, stand_icon)
    image.save(OUT / "brewing_stand_recipe.png", optimize=True)

    image = background()
    recipe_grid(image, {(1, 0): glass_icon, (1, 2): glass_icon, (2, 1): glass_icon}, sprite(glass_bottle, 118))
    image.save(OUT / "glass_bottle_recipe.png", optimize=True)

    water = potion_icon(potion, potion_overlay, (56, 93, 198), 16)
    awkward = potion_icon(potion, potion_overlay, (56, 93, 198), 16)
    strength = potion_icon(potion, potion_overlay, (147, 36, 35), 16)
    brewing_guide(gui, fuel_length, sprite(nether_wart, 16), water).save(
        OUT / "awkward_potion_brewing.png", optimize=True
    )
    brewing_guide(gui, fuel_length, sprite(blaze_powder, 16), awkward).save(
        OUT / "strength_potion_brewing.png", optimize=True
    )

print(OUT)
