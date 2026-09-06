"""Generate the Shulker Box recipe guide from Minecraft 26.1.2 assets."""

from io import BytesIO
import os
from pathlib import Path
from zipfile import ZipFile

from PIL import Image, ImageDraw


JAR = Path(os.environ.get(
    "FIRST_TORCH_MINECRAFT_JAR",
    r"C:\Games\CurseForge\Minecraft\Install\versions\26.1.2\26.1.2.jar",
))
ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / "overrides/resourcepacks/first_torch_guides/assets/firsttorch/textures/questpics"
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


def slot(draw, x, y, size=160):
    draw.rectangle((x, y, x + size, y + size), fill=(29, 31, 32, 255), outline=(5, 6, 7, 255), width=8)
    draw.line((x + 8, y + 8, x + size - 8, y + 8), fill=(153, 156, 158, 255), width=4)
    draw.line((x + 8, y + 8, x + 8, y + size - 8), fill=(115, 118, 120, 255), width=4)


def arrow(draw, left, top, width=170, height=100):
    points = [(left, top + height // 3), (left + width * 3 // 5, top + height // 3),
              (left + width * 3 // 5, top), (left + width, top + height // 2),
              (left + width * 3 // 5, top + height), (left + width * 3 // 5, top + height * 2 // 3),
              (left, top + height * 2 // 3)]
    draw.polygon(points, fill=(194, 195, 196, 255))


def textured_quad(texture, points, shade=1.0):
    left = int(min(point[0] for point in points))
    top = int(min(point[1] for point in points))
    right = int(max(point[0] for point in points)) + 1
    bottom = int(max(point[1] for point in points)) + 1
    face = Image.new("RGBA", (right - left, bottom - top), (0, 0, 0, 0))
    pixels = face.load()
    p0, p1, _, p3 = points
    ax, ay = p1[0] - p0[0], p1[1] - p0[1]
    bx, by = p3[0] - p0[0], p3[1] - p0[1]
    determinant = ax * by - ay * bx
    source = texture.load()
    for yy in range(face.height):
        sy = top + yy + 0.5 - p0[1]
        for xx in range(face.width):
            sx = left + xx + 0.5 - p0[0]
            u = (sx * by - sy * bx) / determinant
            v = (ax * sy - ay * sx) / determinant
            if 0 <= u < 1 and 0 <= v < 1:
                r, g, b, a = source[min(texture.width - 1, int(u * texture.width)),
                                     min(texture.height - 1, int(v * texture.height))]
                pixels[xx, yy] = (int(r * shade), int(g * shade), int(b * shade), a)
    return face, (left, top)


def shulker_box_icon(texture, size=210):
    # ShulkerBoxModel uses a 64 x 64 texture: a 16 x 12 x 16 lid at UV 0,0
    # above a 16 x 8 x 16 base at UV 0,28. Join their exact faces while closed.
    top_texture = texture.crop((16, 0, 32, 16))
    front = Image.new("RGBA", (16, 20))
    front.alpha_composite(texture.crop((16, 16, 32, 28)), (0, 0))
    front.alpha_composite(texture.crop((16, 44, 32, 52)), (0, 12))
    side = Image.new("RGBA", (16, 20))
    side.alpha_composite(texture.crop((0, 16, 16, 28)), (0, 0))
    side.alpha_composite(texture.crop((0, 44, 16, 52)), (0, 12))

    canvas = Image.new("RGBA", (360, 390), (0, 0, 0, 0))
    top_points = [(180, 25), (325, 98), (180, 171), (35, 98)]
    right_points = [(325, 98), (180, 171), (180, 351), (325, 278)]
    left_points = [(180, 171), (35, 98), (35, 278), (180, 351)]
    for face_texture, points, shade in ((top_texture, top_points, 1.0), (side, right_points, 0.70),
                                        (front, left_points, 0.83)):
        face, position = textured_quad(face_texture, points, shade)
        canvas.alpha_composite(face, position)
    crop = canvas.crop(canvas.getbbox())
    crop.thumbnail((size, size), Image.Resampling.NEAREST)
    return crop


def chest_icon():
    # Reuse the exact target-version chest item render already present in the
    # established Chest recipe guide instead of approximating its special model.
    guide = Image.open(OUT / "chest.png").convert("RGBA")
    return guide.crop((1260, 350, 1455, 570)).resize((112, 126), Image.Resampling.LANCZOS)


def main():
    image = background()
    draw = ImageDraw.Draw(image)
    left, top, cell = 225, 225, 160
    for row in range(3):
        for col in range(3):
            slot(draw, left + col * cell, top + row * cell, cell)

    with ZipFile(JAR) as archive:
        shell = asset(archive, "assets/minecraft/textures/item/shulker_shell.png")
        box_texture = asset(archive, "assets/minecraft/textures/entity/shulker/shulker.png")

    shell_icon = shell.resize((108, 108), Image.Resampling.NEAREST)
    for row in (0, 2):
        image.alpha_composite(shell_icon, (left + cell + 26, top + row * cell + 26))

    chest = chest_icon()
    image.alpha_composite(chest, (left + cell + (cell - chest.width) // 2,
                                  top + cell + (cell - chest.height) // 2))

    arrow(draw, 800, 410)
    output_x, output_y, output_size = 1080, 335, 250
    slot(draw, output_x, output_y, output_size)
    box = shulker_box_icon(box_texture)
    image.alpha_composite(box, (output_x + (output_size - box.width) // 2,
                                output_y + (output_size - box.height) // 2))

    OUT.mkdir(parents=True, exist_ok=True)
    output = OUT / "shulker_box_recipe.png"
    image.save(output, optimize=True)
    print(output)


if __name__ == "__main__":
    main()
