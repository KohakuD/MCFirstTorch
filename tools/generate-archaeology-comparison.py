"""Compare exact unbrushed Minecraft cube_all blocks using the established renderer."""
import argparse
import importlib.util
import json
from pathlib import Path
from zipfile import ZipFile
from PIL import Image, ImageDraw, ImageFont

ROOT = Path(__file__).resolve().parents[1]
spec = importlib.util.spec_from_file_location("shulker_guides", ROOT / "tools/generate-shulker-box-guide.py")
renderer = importlib.util.module_from_spec(spec)
spec.loader.exec_module(renderer)


def cube_icon(texture):
    # Same straight cube projection as the established Stronghold guides.
    canvas = Image.new("RGBA", (320, 320))
    faces = (
        ([(160, 35), (285, 97), (160, 160), (35, 97)], 1.0),
        ([(285, 97), (160, 160), (160, 285), (285, 222)], 0.70),
        ([(160, 160), (35, 97), (35, 222), (160, 285)], 0.83),
    )
    for points, shade in faces:
        face, position = renderer.textured_quad(texture, points, shade)
        canvas.alpha_composite(face, position)
    return canvas.crop(canvas.getbbox()).resize((330, 330), Image.Resampling.NEAREST)


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--minecraft-jar", required=True, type=Path)
    parser.add_argument("--font", default="C:/Windows/Fonts/arialbd.ttf")
    args = parser.parse_args()
    image = renderer.background()
    draw = ImageDraw.Draw(image)
    font = ImageFont.truetype(args.font, 52)
    blocks = ("sand", "suspicious_sand_0", "gravel", "suspicious_gravel_0")
    with ZipFile(args.minecraft_jar) as archive:
        for index, name in enumerate(blocks):
            model = json.loads(archive.read(f"assets/minecraft/models/block/{name}.json"))
            if model["parent"] != "minecraft:block/cube_all":
                raise ValueError(f"Unexpected block model: {name}")
            texture_path = model["textures"]["all"].replace("minecraft:", "assets/minecraft/textures/") + ".png"
            cube = cube_icon(renderer.asset(archive, texture_path))
            left, top = 70 + (index % 2) * 780, 35 + (index // 2) * 435
            draw.rounded_rectangle((left, top, left + 720, top + 405), radius=8,
                                   fill=(27, 29, 30), outline=(98, 100, 98), width=3)
            image.alpha_composite(cube, (left + (720 - cube.width) // 2, top + 36))
            draw.text((left + 28, top + 18), str(index + 1), font=font, fill=(241, 187, 82))
    output = ROOT / "src/main/resources/assets/firsttorch/textures/questpics/archaeology_comparison.png"
    output.parent.mkdir(parents=True, exist_ok=True)
    image.save(output, optimize=True)
    print(output)


if __name__ == "__main__":
    main()
