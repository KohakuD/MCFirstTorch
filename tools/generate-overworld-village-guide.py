"""Generate a Village-recognition scene from exact Minecraft 26.1.2 assets."""

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


def tint(texture, colour):
    """Apply Minecraft-style biome colouring while preserving exact source pixels."""
    red, green, blue = colour
    result = texture.copy()
    pixels = result.load()
    for y in range(result.height):
        for x in range(result.width):
            r, g, b, a = pixels[x, y]
            pixels[x, y] = (r * red // 255, g * green // 255, b * blue // 255, a)
    return result


def background():
    image = Image.new("RGBA", (W, H), (117, 173, 241, 255))
    draw = ImageDraw.Draw(image, "RGBA")
    draw.rectangle((0, 0, W, 300), fill=(123, 180, 247, 255))
    draw.rectangle((0, 275, W, H), fill=(43, 72, 36, 255))
    draw.polygon(((0, 310), (330, 150), (650, 310)), fill=(79, 109, 73, 255))
    draw.polygon(((1030, 315), (1370, 125), (W, 315)), fill=(72, 102, 69, 255))
    draw.rectangle((2, 2, W - 3, H - 3), outline=(7, 8, 9, 255), width=6)
    return image


def textured_parallelogram(texture, p0, p1, p3, shade=1.0):
    xs = (p0[0], p1[0], p3[0], p1[0] + p3[0] - p0[0])
    ys = (p0[1], p1[1], p3[1], p1[1] + p3[1] - p0[1])
    left, top = int(min(xs)), int(min(ys))
    right, bottom = int(max(xs)) + 1, int(max(ys)) + 1
    face = Image.new("RGBA", (right - left, bottom - top), (0, 0, 0, 0))
    pix = face.load()
    ax, ay = p1[0] - p0[0], p1[1] - p0[1]
    bx, by = p3[0] - p0[0], p3[1] - p0[1]
    det = ax * by - ay * bx
    source = texture.load()
    for dy in range(face.height):
        sy = top + dy + 0.5 - p0[1]
        for dx in range(face.width):
            sx = left + dx + 0.5 - p0[0]
            u = (sx * by - sy * bx) / det
            v = (ax * sy - ay * sx) / det
            if 0 <= u < 1 and 0 <= v < 1:
                r, g, b, a = source[min(texture.width - 1, int(u * texture.width)), min(texture.height - 1, int(v * texture.height))]
                pix[dx, dy] = (int(r * shade), int(g * shade), int(b * shade), a)
    return face, (left, top)


class IsoScene:
    def __init__(self, image, center=(820, 700), tile=(92, 46), block_height=64):
        self.image = image
        self.cx, self.base = center
        self.tw, self.th = tile
        self.bh = block_height
        self.blocks = []

    def add_block(self, x, y, z, top, side=None):
        self.blocks.append((x, y, z, top, side or top))

    def render(self):
        for x, y, z, top_texture, side_texture in sorted(self.blocks, key=lambda block: (block[0] + block[2], block[1], block[0])):
            cx = self.cx + (x - z) * self.tw / 2
            cy = self.base + (x + z) * self.th / 2 - (y + 1) * self.bh
            top = (cx, cy - self.th / 2)
            right = (cx + self.tw / 2, cy)
            bottom = (cx, cy + self.th / 2)
            left = (cx - self.tw / 2, cy)
            down = (0, self.bh)
            faces = (
                textured_parallelogram(top_texture, top, right, left, 1.0),
                textured_parallelogram(side_texture, right, bottom, (right[0] + down[0], right[1] + down[1]), 0.68),
                textured_parallelogram(side_texture, bottom, left, (bottom[0] + down[0], bottom[1] + down[1]), 0.82),
            )
            for face, position in faces:
                self.image.alpha_composite(face, position)


def villager_model(texture):
    canvas = Image.new("RGBA", (180, 350), (0, 0, 0, 0))
    head = crop_scaled(texture, (8, 8, 16, 18), (100, 125))
    body = crop_scaled(texture, (22, 26, 30, 38), (86, 175))
    leg = crop_scaled(texture, (4, 26, 8, 38), (38, 50))
    nose = crop_scaled(texture, (26, 2, 28, 6), (24, 48))
    canvas.alpha_composite(head, (40, 0))
    canvas.alpha_composite(nose, (78, 62))
    canvas.alpha_composite(body, (47, 125))
    canvas.alpha_composite(leg, (49, 300))
    canvas.alpha_composite(leg.transpose(Image.Transpose.FLIP_LEFT_RIGHT), (93, 300))
    return canvas


def golem_model(texture):
    canvas = Image.new("RGBA", (270, 470), (0, 0, 0, 0))
    head = crop_scaled(texture, (8, 8, 16, 18), (115, 140))
    body = crop_scaled(texture, (11, 51, 29, 63), (155, 150))
    arm = crop_scaled(texture, (66, 27, 70, 57), (42, 225))
    leg = crop_scaled(texture, (42, 5, 48, 21), (58, 145))
    canvas.alpha_composite(head, (78, 0))
    canvas.alpha_composite(body, (58, 140))
    canvas.alpha_composite(arm, (8, 135))
    canvas.alpha_composite(arm.transpose(Image.Transpose.FLIP_LEFT_RIGHT), (220, 135))
    canvas.alpha_composite(leg, (70, 290))
    canvas.alpha_composite(leg.transpose(Image.Transpose.FLIP_LEFT_RIGHT), (142, 290))
    return canvas


def create_scene(textures):
    image = background()
    scene = IsoScene(image)
    grass_top, grass_side = textures["grass_top"], textures["grass_side"]
    path_top, path_side = textures["path_top"], textures["path_side"]

    # Shared ground and the characteristic path linking both houses.
    for x in range(-7, 8):
        for z in range(-4, 5):
            if z == 0 or (x in (-4, 4) and -2 <= z <= 2):
                scene.add_block(x, 0, z, path_top, path_side)
            else:
                scene.add_block(x, 0, z, grass_top, grass_side)

    # Two compact village houses made solely from exact block textures.
    for x0, z0, width, depth in ((-6, -3, 4, 3), (2, 1, 4, 3)):
        for x in range(x0, x0 + width):
            for z in range(z0, z0 + depth):
                scene.add_block(x, 1, z, textures["cobblestone"])
                edge = x in (x0, x0 + width - 1) or z in (z0, z0 + depth - 1)
                doorway = x == x0 + 1 and z == z0 + depth - 1
                if edge and not doorway:
                    scene.add_block(x, 2, z, textures["planks"])
                    scene.add_block(x, 3, z, textures["planks"])
                scene.add_block(x, 4, z, textures["planks"])

    # A small farm beside the path: moist Farmland, one Water source, and mature Wheat.
    for x in range(2, 7):
        for z in range(-4, -1):
            if x == 4 and z == -3:
                scene.add_block(x, 1, z, textures["water"])
            else:
                scene.add_block(x, 1, z, textures["farmland"])
    scene.render()

    # Exact mature-Wheat sprites mark the planted farm without inventing a new crop texture.
    wheat = textures["wheat"].resize((58, 58), Image.Resampling.NEAREST)
    for x, y in ((1085, 558), (1140, 585), (1195, 610), (1032, 582), (1090, 612), (1147, 638)):
        image.alpha_composite(wheat, (x, y))

    # Exact target-version entity surfaces make the inhabitants unmistakable.
    image.alpha_composite(villager_model(textures["villager"]), (690, 475))
    image.alpha_composite(golem_model(textures["golem"]), (1220, 355))

    # The Bell is shown as its exact item texture beside the central path.
    bell = textures["bell"].resize((92, 92), Image.Resampling.NEAREST)
    image.alpha_composite(bell, (860, 540))
    return image


OUT.mkdir(parents=True, exist_ok=True)
with ZipFile(JAR) as archive:
    grass_top = tint(asset(archive, "assets/minecraft/textures/block/grass_block_top.png"), (124, 189, 72))
    grass_side = asset(archive, "assets/minecraft/textures/block/grass_block_side.png")
    grass_overlay = tint(asset(archive, "assets/minecraft/textures/block/grass_block_side_overlay.png"), (124, 189, 72))
    grass_side.alpha_composite(grass_overlay)
    villager = asset(archive, "assets/minecraft/textures/entity/villager/villager.png")
    villager.alpha_composite(asset(archive, "assets/minecraft/textures/entity/villager/type/plains.png"))
    villager.alpha_composite(asset(archive, "assets/minecraft/textures/entity/villager/profession/farmer.png"))
    textures = {
        "grass_top": grass_top,
        "grass_side": grass_side,
        "path_top": asset(archive, "assets/minecraft/textures/block/dirt_path_top.png"),
        "path_side": asset(archive, "assets/minecraft/textures/block/dirt_path_side.png"),
        "cobblestone": asset(archive, "assets/minecraft/textures/block/cobblestone.png"),
        "planks": asset(archive, "assets/minecraft/textures/block/oak_planks.png"),
        "farmland": asset(archive, "assets/minecraft/textures/block/farmland_moist.png"),
        "water": tint(asset(archive, "assets/minecraft/textures/block/water_still.png").crop((0, 0, 16, 16)), (63, 118, 228)),
        "wheat": asset(archive, "assets/minecraft/textures/block/wheat_stage7.png"),
        "villager": villager,
        "golem": asset(archive, "assets/minecraft/textures/entity/iron_golem/iron_golem.png"),
        "bell": asset(archive, "assets/minecraft/textures/item/bell.png"),
    }
    create_scene(textures).save(OUT / "village_overview.png", optimize=True)

print(OUT)
