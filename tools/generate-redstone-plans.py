"""Render the three Redstone lesson plans from Minecraft 26.1.2 model assets.

The plans deliberately leave Lever, Chest and Redstone Dust as neutral labelled
cells.  They are lesson positions, not substitute drawings of game assets.
"""

import argparse
import json
from io import BytesIO
from pathlib import Path
from zipfile import ZipFile

from PIL import Image, ImageDraw, ImageFont


ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / "src/main/resources/assets/firsttorch/textures/questpics"
W, H = 1672, 941
GOLD = (245, 191, 83, 255)
LINE = (118, 121, 122, 255)


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


def font(size):
    return ImageFont.truetype("C:/Windows/Fonts/arialbd.ttf", size)


def texture_name(value):
    value = value.removeprefix("minecraft:")
    return value if value.startswith("block/") else "block/" + value


def resolved_model(archive, name):
    """Resolve the small vanilla parent chain, keeping child texture overrides."""
    model = json.loads(archive.read("assets/minecraft/models/block/" + name + ".json"))
    textures = {}
    elements = model.get("elements")
    parent = model.get("parent")
    if parent:
        parent_name = parent.removeprefix("minecraft:").removeprefix("block/")
        parent_model, textures, parent_elements = resolved_model(archive, parent_name)
        elements = elements if elements is not None else parent_elements
    textures.update(model.get("textures", {}))
    return model, textures, elements


def resolve_texture(textures, reference):
    while reference.startswith("#"):
        reference = textures[reference[1:]]
    return texture_name(reference)


def model_top(archive, name):
    """Compose the actual upward model faces onto a 16 by 16 top-down canvas."""
    _, textures, elements = resolved_model(archive, name)
    canvas = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    for element in sorted(elements, key=lambda value: value["to"][1]):
        assert "rotation" not in element, "Rotated elements require a full projection"
        definition = element.get("faces", {}).get("up")
        if definition is None:
            continue
        source = asset(archive, "assets/minecraft/textures/" +
                       resolve_texture(textures, definition["texture"]) + ".png")
        u0, v0, u1, v1 = definition.get("uv", [0, 0, 16, 16])
        face = source.crop((u0, v0, u1, v1))
        rotation = definition.get("rotation", 0)
        if rotation:
            face = face.rotate(-rotation, expand=True)
        x0, z0 = element["from"][0], element["from"][2]
        x1, z1 = element["to"][0], element["to"][2]
        # North is negative Z: small Z belongs at image top before rotation.
        destination = (int(x0), int(z0), int(x1), int(z1))
        face = face.resize((destination[2] - destination[0], destination[3] - destination[1]),
                           Image.Resampling.NEAREST)
        canvas.alpha_composite(face, destination[:2])
    return canvas


def label(draw, x, y, value):
    draw.text((x, y), value, font=font(48), anchor="mt", fill=GOLD)


def arrow(draw, left, y, width):
    draw.line((left, y, left + width - 25, y), fill=GOLD, width=9)
    draw.polygon(((left + width - 25, y), (left + width - 52, y - 21),
                  (left + width - 52, y + 21)), fill=GOLD)


def neutral_cell(image, x, y, size, letter):
    draw = ImageDraw.Draw(image)
    draw.rectangle((x, y, x + size, y + size), fill=(38, 40, 41, 255), outline=LINE, width=5)
    draw.rectangle((x + 13, y + 13, x + size - 13, y + size - 13), outline=(77, 80, 81, 255), width=3)
    draw.ellipse((x + size // 2 - 45, y + size // 2 - 45,
                  x + size // 2 + 45, y + size // 2 + 45), outline=GOLD, width=5)
    draw.text((x + size // 2, y + size // 2 - 4), letter, font=font(50), anchor="mm", fill=GOLD)


def plan(archive, output, cells, arrows=()):
    image = background()
    draw = ImageDraw.Draw(image)
    count = len(cells)
    size = 220 if count <= 3 else 180
    gap = 0
    left = (W - (count * size + (count - 1) * gap)) // 2
    top = 235
    for index, (kind, value) in enumerate(cells):
        x = left + index * (size + gap)
        label(draw, x + size // 2, top - 73, str(index))
        if kind == "model":
            texture = value.resize((size, size), Image.Resampling.NEAREST)
            image.alpha_composite(texture, (x, top))
            draw.rectangle((x, top, x + size, top + size), outline=(12, 13, 14, 255), width=4)
        else:
            neutral_cell(image, x, top, size, value)
    for index in arrows:
        x = left + index * (size + gap) + size // 3
        arrow(draw, x, top + size + 68, size)
    return image, left, top, size, gap


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--minecraft-jar", type=Path, required=True)
    args = parser.parse_args()
    OUT.mkdir(parents=True, exist_ok=True)
    with ZipFile(args.minecraft_jar) as archive:
        # Blockstate defaults point north.  A clockwise plan rotation makes the
        # repeater/comparator output face east while north remains page-top.
        repeater = model_top(archive, "repeater_1tick").transpose(Image.Transpose.ROTATE_270)
        comparator = model_top(archive, "comparator").transpose(Image.Transpose.ROTATE_270)
        lamp = asset(archive, "assets/minecraft/textures/block/redstone_lamp.png")
        piston = model_top(archive, "piston").transpose(Image.Transpose.ROTATE_270)
        cobblestone = asset(archive, "assets/minecraft/textures/block/cobblestone.png")

        image, _, _, _, _ = plan(archive, "repeater_direction.png",
                                 (("neutral", "L"), ("model", repeater), ("model", lamp)), (0, 1))
        image.save(OUT / "repeater_direction.png", optimize=True)

        image, _, _, _, _ = plan(archive, "comparator_read.png",
                                 (("neutral", "C"), ("model", comparator), ("neutral", "D"),
                                  ("neutral", "D"), ("model", lamp)), (0, 1, 2, 3))
        image.save(OUT / "comparator_read.png", optimize=True)

        image, _, top, size, _ = plan(archive, "piston_push.png",
                                      (("model", piston), ("model", cobblestone), ("neutral", "")), (0,))
        # This is the model's north/front platform texture, shown unaltered as
        # a face swatch; it identifies the piston wooden face without redrawing it.
        face = asset(archive, "assets/minecraft/textures/block/piston_top.png").resize((150, 150), Image.Resampling.NEAREST)
        image.alpha_composite(face, (W // 2 - 75, top + size + 210))
        draw = ImageDraw.Draw(image)
        label(draw, W // 2, top + size + 144, "A")
        draw.rectangle((W // 2 - 75, top + size + 210, W // 2 + 75, top + size + 360), outline=(12, 13, 14, 255), width=4)
        image.save(OUT / "piston_push.png", optimize=True)
    for name in ("repeater_direction.png", "comparator_read.png", "piston_push.png"):
        print(OUT / name)


if __name__ == "__main__":
    main()
