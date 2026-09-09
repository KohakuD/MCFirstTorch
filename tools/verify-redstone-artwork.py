"""Bounded original-component and continuous-wire regression checks."""
import argparse
from pathlib import Path
from zipfile import ZipFile
import redstone_components as components


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--minecraft-jar", required=True, type=Path)
    args = parser.parse_args()
    with ZipFile(args.minecraft_jar) as archive:
        for kind in ("lever", "chest", "stone_button", "stone_pressure_plate", "iron_door"):
            icon = components.component(archive, kind, size=96)
            assert icon.mode == "RGBA" and icon.size == (96, 96)
            assert icon.getbbox() is not None, kind
            original = icon.tobytes()
            icon.paste((0, 0, 0, 0), (0, 0, 96, 96))
            assert components.component(archive, kind, size=96).tobytes() == original
        previous_red = -1
        for power in range(16):
            wire = components.wire(archive, power=power, size=16)
            alpha = wire.getchannel("A")
            assert alpha.crop((0, 0, 1, 16)).getbbox(), power
            assert alpha.crop((15, 0, 16, 16)).getbbox(), power
            assert not alpha.crop((0, 0, 16, 1)).getbbox(), power
            assert not alpha.crop((0, 15, 16, 16)).getbbox(), power
            red = sum(pixel[0] for pixel in wire.getdata())
            assert red > previous_red, power
            previous_red = red
        for invalid in (-1, 16):
            try:
                components.wire(archive, power=invalid)
            except ValueError:
                pass
            else:
                raise AssertionError("Invalid wire power accepted")
    print("Verified five original component icons and all sixteen east/west wire strengths.")


if __name__ == "__main__":
    main()
