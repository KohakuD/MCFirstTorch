"""Generate Nether Fortress quest guides from exact Minecraft 26.1.2 assets."""

from io import BytesIO
from pathlib import Path
from zipfile import ZipFile

from PIL import Image, ImageDraw


JAR = Path(r"D:\Minecraft\curseforge\minecraft\Install\versions\26.1.2\26.1.2.jar")
OUT = Path(__file__).resolve().parents[1] / "overrides/resourcepacks/first_torch_guides/assets/firsttorch/textures/questpics"
W, H = 1672, 941


def asset(archive, path):
    return Image.open(BytesIO(archive.read(path))).convert("RGBA")


def crop_scaled(texture, box, size):
    return texture.crop(box).resize(size, Image.Resampling.NEAREST)


def framed_background():
    image = Image.new("RGBA", (W, H), (27, 28, 29, 255))
    pixels = image.load()
    for y in range(6, H - 6):
        shade = int(49 - 15 * y / H)
        for x in range(6, W - 6):
            vignette = int(7 * abs(x - W / 2) / (W / 2))
            value = max(24, shade - vignette)
            pixels[x, y] = (value, value, value, 255)
    draw = ImageDraw.Draw(image)
    draw.rectangle((2, 2, W - 3, H - 3), outline=(7, 8, 9, 255), width=6)
    draw.line((8, 8, W - 9, 8), fill=(126, 129, 130, 255), width=3)
    draw.line((8, 9, 8, H - 9), fill=(91, 94, 96, 255), width=3)
    return image


def textured_parallelogram(texture, p0, p1, p3, shade=1.0):
    """Map a texture onto p0 + u(p1-p0) + v(p3-p0), using nearest pixels."""
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
    def __init__(self, image, texture, center=(836, 185), tile=(92, 46), block_height=82):
        self.image = image
        self.texture = texture
        self.cx, self.base = center
        self.tw, self.th = tile
        self.bh = block_height
        self.blocks = []

    def add_block(self, x, y, z, texture=None):
        self.blocks.append((x, y, z, texture or self.texture))

    def block_faces(self, x, y, z, texture):
        cx = self.cx + (x - z) * self.tw / 2
        cy = self.base + (x + z) * self.th / 2 - (y + 1) * self.bh
        top = (cx, cy - self.th / 2)
        right = (cx + self.tw / 2, cy)
        bottom = (cx, cy + self.th / 2)
        left = (cx - self.tw / 2, cy)
        down = (0, self.bh)
        return (
            textured_parallelogram(texture, top, right, left, 1.0),
            textured_parallelogram(texture, right, bottom, (right[0] + down[0], right[1] + down[1]), 0.68),
            textured_parallelogram(texture, bottom, left, (bottom[0] + down[0], bottom[1] + down[1]), 0.82),
        )

    def render(self):
        for x, y, z, texture in sorted(self.blocks, key=lambda b: (b[0] + b[2], b[1], b[0])):
            for face, xy in self.block_faces(x, y, z, texture):
                self.image.alpha_composite(face, xy)


def add_fortress_scene(image, nether_bricks):
    draw = ImageDraw.Draw(image, "RGBA")
    draw.ellipse((265, 760, 1407, 875), fill=(8, 8, 9, 105))
    scene = IsoScene(image, nether_bricks, center=(836, 690), tile=(102, 51), block_height=84)
    # Long three-block-wide bridge.
    for x in range(-6, 7):
        for z in range(-1, 2):
            scene.add_block(x, 4, z)
    # Massive characteristic supports below the bridge.
    for x in (-5, 0, 5):
        for y in range(0, 4):
            for z in (-1, 0, 1):
                scene.add_block(x, y, z)
    # A small raised junction at the far end makes the segment read as a structure.
    for x in range(4, 7):
        for z in range(-3, 4):
            scene.add_block(x, 4, z)
    for z in range(-3, 4):
        scene.add_block(6, 5, z)
    scene.render()


def add_front_model(canvas, texture, kind):
    if kind == "blaze":
        # Exact head and rod faces from the Blaze texture, arranged to the model's three rings.
        head = crop_scaled(texture, (8, 8, 16, 16), (150, 150))
        rod = crop_scaled(texture, (2, 18, 4, 26), (34, 136))
        positions = [(46, 70), (145, 30), (245, 78), (291, 173),
                     (22, 235), (112, 205), (218, 225), (300, 278),
                     (58, 392), (151, 430), (246, 390), (286, 490)]
        for x, y in positions:
            canvas.alpha_composite(rod, (x, y))
        canvas.alpha_composite(head, (111, 180))
    elif kind == "wither":
        # Exact standard Skeleton-model front faces at Wither Skeleton proportions.
        head = crop_scaled(texture, (8, 8, 16, 16), (132, 132))
        body = crop_scaled(texture, (20, 20, 28, 32), (112, 216))
        arm = crop_scaled(texture, (44, 20, 46, 32), (34, 216))
        leg = crop_scaled(texture, (4, 20, 6, 32), (38, 216))
        canvas.alpha_composite(head, (120, 30))
        canvas.alpha_composite(body, (130, 162))
        canvas.alpha_composite(arm, (88, 162))
        canvas.alpha_composite(arm.transpose(Image.Transpose.FLIP_LEFT_RIGHT), (250, 162))
        canvas.alpha_composite(leg, (139, 378))
        canvas.alpha_composite(leg.transpose(Image.Transpose.FLIP_LEFT_RIGHT), (205, 378))
    elif kind == "magma":
        # Exact front faces from all eight one-block-high Magma Cube model segments.
        segment_face = Image.new("RGBA", (8, 8), (0, 0, 0, 0))
        for row in range(8):
            u = 0 if row < 4 else 32
            v = 0 if row in (0, 4) else 9 * (row if row < 4 else row - 4)
            segment_face.paste(texture.crop((u + 8, v + 8, u + 16, v + 9)), (0, row))
        face = segment_face.resize((292, 292), Image.Resampling.NEAREST)
        canvas.alpha_composite(face, (40, 235))


def create_hazards(blaze, wither, magma):
    image = framed_background()
    draw = ImageDraw.Draw(image)
    margin, gap = 55, 36
    panel_width = (W - 2 * margin - 2 * gap) // 3
    panel_top, panel_bottom = 70, 870
    textures = ((blaze, "blaze"), (wither, "wither"), (magma, "magma"))
    for index, (texture, kind) in enumerate(textures):
        left = margin + index * (panel_width + gap)
        right = left + panel_width
        draw.rounded_rectangle((left, panel_top, right, panel_bottom), radius=12,
                               fill=(38, 40, 41, 255), outline=(117, 119, 121, 255), width=7)
        draw.ellipse((left + 70, 770, right - 70, 830), fill=(13, 14, 14, 145))
        model = Image.new("RGBA", (372, 650), (0, 0, 0, 0))
        add_front_model(model, texture, kind)
        image.alpha_composite(model, (left + (panel_width - model.width) // 2, 125))
    return image


def create_spawner_scene(nether_bricks, spawner, blaze):
    image = framed_background()
    draw = ImageDraw.Draw(image, "RGBA")
    draw.ellipse((260, 735, 1390, 855), fill=(8, 8, 9, 105))
    scene = IsoScene(image, nether_bricks, center=(720, 680), tile=(100, 50), block_height=82)
    for x in range(-5, 6):
        for z in range(-2, 3):
            scene.add_block(x, 2, z)
    # Raised end platform typical of an exposed Fortress Blaze Spawner.
    for x in range(2, 6):
        for z in range(-3, 4):
            scene.add_block(x, 3, z)
    scene.render()

    # Put the exact Blaze faces inside the cage, then render the translucent cage above them.
    mini = Image.new("RGBA", (105, 125), (0, 0, 0, 0))
    head = crop_scaled(blaze, (8, 8, 16, 16), (42, 42))
    rod = crop_scaled(blaze, (2, 18, 4, 26), (10, 42))
    for pos in ((14, 8), (45, 2), (76, 12), (5, 50), (84, 52), (25, 82), (66, 80)):
        mini.alpha_composite(rod, pos)
    mini.alpha_composite(head, (32, 38))
    image.alpha_composite(mini, (870, 306))
    cage = IsoScene(image, nether_bricks, center=(720, 680), tile=(100, 50), block_height=82)
    cage.add_block(4, 4, 0, spawner)
    cage.render()
    return image


OUT.mkdir(parents=True, exist_ok=True)
with ZipFile(JAR) as archive:
    nether_bricks = asset(archive, "assets/minecraft/textures/block/nether_bricks.png")
    spawner = asset(archive, "assets/minecraft/textures/block/spawner.png")
    blaze = asset(archive, "assets/minecraft/textures/entity/blaze/blaze.png")
    wither = asset(archive, "assets/minecraft/textures/entity/skeleton/wither_skeleton.png")
    magma = asset(archive, "assets/minecraft/textures/entity/slime/magmacube.png")

    fortress = framed_background()
    add_fortress_scene(fortress, nether_bricks)
    fortress.save(OUT / "nether_fortress.png", optimize=True)
    create_hazards(blaze, wither, magma).save(OUT / "fortress_hazards.png", optimize=True)
    create_spawner_scene(nether_bricks, spawner, blaze).save(OUT / "blaze_spawner.png", optimize=True)

print(OUT)
