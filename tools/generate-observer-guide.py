"""Top-down Observer plan and face legend using exact target block-model textures."""
import argparse
import importlib.util
import json
from pathlib import Path
from zipfile import ZipFile
from PIL import Image, ImageDraw, ImageFont

ROOT = Path(__file__).resolve().parents[1]
SPEC = importlib.util.spec_from_file_location("guide_renderer", ROOT / "tools/generate-shulker-box-guide.py")
RENDERER = importlib.util.module_from_spec(SPEC)
SPEC.loader.exec_module(RENDERER)


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--minecraft-jar", type=Path, required=True)
    args = parser.parse_args()
    image = RENDERER.background()
    draw = ImageDraw.Draw(image)
    font = ImageFont.truetype("C:/Windows/Fonts/arialbd.ttf", 48)
    with ZipFile(args.minecraft_jar) as archive:
        model = json.loads(archive.read("assets/minecraft/models/block/observer.json"))
        element, = model["elements"]
        assert element["from"] == [0, 0, 0] and element["to"] == [16, 16, 16]

        def face(name):
            definition = element["faces"][name]
            texture = model["textures"][definition["texture"].removeprefix("#")]
            path = texture.removeprefix("minecraft:")
            pixels = RENDERER.asset(archive, "assets/minecraft/textures/" + path + ".png")
            uv = definition["uv"]
            assert uv in ([0, 0, 16, 16], [0, 16, 16, 0])
            return pixels.transpose(Image.Transpose.FLIP_TOP_BOTTOM) if uv[1] > uv[3] else pixels

        # Observer default watches north (top of a top-down plan). Rotate the
        # entire plan texture counterclockwise to watch west, towards block 0.
        observer_top = face("up").transpose(Image.Transpose.ROTATE_90)
        textures = (
            RENDERER.asset(archive, "assets/minecraft/textures/block/cobblestone.png"),
            observer_top,
            RENDERER.asset(archive, "assets/minecraft/textures/block/redstone_lamp.png"),
        )
        side, left, top = 250, 460, 170
        for index, texture in enumerate(textures):
            x = left + index * side
            image.alpha_composite(texture.resize((side, side), Image.Resampling.NEAREST), (x, top))
            draw.rectangle((x, top, x + side, top + side), outline=(12, 13, 14), width=4)
            draw.text((x + side // 2, top - 64), str(index), font=font, anchor="mt", fill=(245, 191, 83))
        # The plan is contiguous: arrows below it are explanatory, not gaps/wire.
        for start in (left + 70, left + side + 70):
            draw.line((start, 468, start + side - 40, 468), fill=(245, 191, 83), width=9)
            draw.polygon(((start + side - 40, 468), (start + side - 64, 449),
                          (start + side - 64, 487)), fill=(245, 191, 83))
        # A/B are original north/south faces, not schematic approximations.
        for label, name, x in (("A", "north", 480), ("B", "south", 930)):
            image.alpha_composite(face(name).resize((210, 210), Image.Resampling.NEAREST), (x, 640))
            draw.text((x + 105, 570), label, font=font, anchor="mt", fill=(245, 191, 83))
        draw.line((200, 530, 1472, 530), fill=(100, 100, 96), width=3)
    output = ROOT / "archive/native-illustrations/questpics/observer_orientation.png"
    image.save(output, optimize=True)
    print(output)


if __name__ == "__main__":
    main()
