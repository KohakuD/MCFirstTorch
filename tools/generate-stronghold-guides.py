"""Generate Stronghold guides from exact Minecraft 26.1.2 textures."""

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


def tile(image, texture, box, rotation=0):
    x0, y0, x1, y1 = box
    source = texture.rotate(rotation, expand=False) if rotation else texture
    image.alpha_composite(source.resize((x1 - x0, y1 - y0), Image.Resampling.NEAREST), (x0, y0))


def textured_quad(texture, points, shade=1.0):
    left = int(min(p[0] for p in points))
    top = int(min(p[1] for p in points))
    right = int(max(p[0] for p in points)) + 1
    bottom = int(max(p[1] for p in points)) + 1
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
                r, g, b, a = source[min(texture.width - 1, int(u * texture.width)), min(texture.height - 1, int(v * texture.height))]
                pixels[xx, yy] = (int(r * shade), int(g * shade), int(b * shade), a)
    return face, (left, top)


def cube_icon(texture, size=230):
    canvas = Image.new("RGBA", (320, 320), (0, 0, 0, 0))
    top = [(160, 35), (285, 97), (160, 160), (35, 97)]
    right = [(285, 97), (160, 160), (160, 285), (285, 222)]
    left = [(160, 160), (35, 97), (35, 222), (160, 285)]
    for points, shade in ((top, 1.0), (right, 0.70), (left, 0.83)):
        face, position = textured_quad(texture, points, shade)
        canvas.alpha_composite(face, position)
    crop = canvas.crop(canvas.getbbox())
    crop.thumbnail((size, size), Image.Resampling.NEAREST)
    return crop


def button_icon(texture, size=150):
    canvas = Image.new("RGBA", (260, 210), (0, 0, 0, 0))
    top_texture = texture.crop((5, 6, 11, 10))
    front_texture = texture.crop((5, 12, 11, 16))
    side_texture = texture.crop((6, 12, 10, 16))
    faces = (
        (top_texture, [(130, 30), (220, 75), (130, 120), (40, 75)], 1.0),
        (side_texture, [(220, 75), (130, 120), (130, 165), (220, 120)], 0.70),
        (front_texture, [(130, 120), (40, 75), (40, 120), (130, 165)], 0.83),
    )
    for face_texture, points, shade in faces:
        face, position = textured_quad(face_texture, points, shade)
        canvas.alpha_composite(face, position)
    crop = canvas.crop(canvas.getbbox())
    crop.thumbnail((size, size), Image.Resampling.NEAREST)
    return crop


def slot(draw, x, y, size=124):
    draw.rectangle((x, y, x + size, y + size), fill=(29, 31, 32, 255), outline=(5, 6, 7, 255), width=8)
    draw.line((x + 8, y + 8, x + size - 8, y + 8), fill=(153, 156, 158, 255), width=4)
    draw.line((x + 8, y + 8, x + 8, y + size - 8), fill=(115, 118, 120, 255), width=4)


def arrow(draw, left, top, width=130, height=82):
    points = [(left, top + height // 3), (left + width * 3 // 5, top + height // 3),
              (left + width * 3 // 5, top), (left + width, top + height // 2),
              (left + width * 3 // 5, top + height), (left + width * 3 // 5, top + height * 2 // 3),
              (left, top + height * 2 // 3)]
    draw.polygon(points, fill=(194, 195, 196, 255))


def silverfish_top(texture):
    # Exact cuboid sizes and UV origins used by Minecraft's seven-part Silverfish model.
    sizes = [(3, 2, 2), (4, 3, 2), (6, 4, 3), (3, 3, 3), (2, 2, 3), (2, 1, 2), (1, 1, 1)]
    origins = [(0, 0), (0, 4), (0, 9), (0, 16), (0, 22), (11, 0), (13, 4)]
    canvas = Image.new("RGBA", (420, 650), (0, 0, 0, 0))
    y = 32
    scale = 25
    for (dx, _dy, dz), (u, v) in zip(sizes, origins):
        top_face = texture.crop((u + dz, v, u + dz + dx, v + dz))
        width, height = dx * scale, max(28, dz * scale)
        segment = top_face.resize((width, height), Image.Resampling.NEAREST)
        canvas.alpha_composite(segment, ((canvas.width - width) // 2, y))
        y += height - 5
    crop = canvas.crop(canvas.getbbox())
    crop.thumbnail((255, 430), Image.Resampling.NEAREST)
    return crop


def create_silverfish_guide(stone_bricks, mossy, cracked, silverfish):
    image = background()
    draw = ImageDraw.Draw(image)
    block_textures = (stone_bricks, mossy, cracked)
    for index, texture in enumerate(block_textures):
        icon = cube_icon(texture)
        image.alpha_composite(icon, (90 + index * 290, 330))
    draw.line((965, 470, 1115, 470), fill=(255, 137, 0, 255), width=28)
    draw.polygon(((1115, 410), (1210, 470), (1115, 530)), fill=(255, 137, 0, 255))
    model = silverfish_top(silverfish)
    image.alpha_composite(model, (1300 - model.width // 2, 255))
    draw.rectangle((1160, 165, 1450, 760), outline=(191, 54, 48, 255), width=12)
    return image


def create_portal_room(frame_top, lava, spawner, stone_bricks):
    image = background()
    draw = ImageDraw.Draw(image)
    cell = 104
    room_left, room_top = 420, 80
    for row in range(7):
        for col in range(8):
            tile(image, stone_bricks, (room_left + col * cell, room_top + row * cell,
                                       room_left + (col + 1) * cell, room_top + (row + 1) * cell))
    cx, cy = room_left + 3 * cell, room_top + 2 * cell
    for row in range(3):
        for col in range(3):
            tile(image, lava, (cx + col * cell, cy + row * cell, cx + (col + 1) * cell, cy + (row + 1) * cell))
    frames = []
    for col in range(3):
        frames.append((cx + col * cell, cy - cell, 180))
        frames.append((cx + col * cell, cy + 3 * cell, 0))
    for row in range(3):
        frames.append((cx - cell, cy + row * cell, 90))
        frames.append((cx + 3 * cell, cy + row * cell, 270))
    for x, y, rotation in frames:
        tile(image, frame_top, (x, y, x + cell, y + cell), rotation)
        draw.rectangle((x, y, x + cell, y + cell), outline=(12, 13, 14, 255), width=5)
    # In the generated room the Silverfish Spawner stands on the approach stairs,
    # centred in front of one side of the portal frame.
    spawner_x, spawner_y = cx + cell, cy + 4 * cell
    tile(image, spawner, (spawner_x, spawner_y, spawner_x + cell, spawner_y + cell))
    draw.rectangle((spawner_x - 8, spawner_y - 8, spawner_x + cell + 8, spawner_y + cell + 8),
                   outline=(255, 137, 0, 255), width=10)
    return image


def create_frame_states(frame_top, frame_eye):
    image = background()
    draw = ImageDraw.Draw(image)
    panels = ((250, 175, 760, 765), (912, 175, 1422, 765))
    for left, top, right, bottom in panels:
        draw.rounded_rectangle((left, top, right, bottom), radius=12, fill=(37, 39, 40, 255),
                               outline=(118, 121, 123, 255), width=7)
        tile(image, frame_top, (left + 115, top + 155, right - 115, bottom - 155))
    # The raised Eye uses the exact top-face UV region from the filled frame model.
    eye_top = frame_eye.crop((4, 4, 12, 12))
    eye_size = 150
    shadow_box = (1092, 360, 1242, 510)
    draw.rectangle((shadow_box[0] + 14, shadow_box[1] + 14, shadow_box[2] + 14, shadow_box[3] + 14),
                   fill=(10, 11, 11, 115))
    tile(image, eye_top, shadow_box)
    return image


def create_iron_door_guide(stone, stone_bricks, iron_door_top, iron_door_bottom):
    image = background()
    draw = ImageDraw.Draw(image)
    draw.line((805, 70, 805, 870), fill=(90, 92, 94, 255), width=5)

    # Left: exact shapeless one-Stone recipe, with placeable Stone in 3D.
    left, top, cell = 170, 310, 130
    for row in range(2):
        for col in range(2):
            slot(draw, left + col * cell, top + row * cell, cell)
    stone_icon = cube_icon(stone, 102)
    image.alpha_composite(stone_icon, (left + (cell - stone_icon.width) // 2, top + (cell - stone_icon.height) // 2))
    arrow(draw, 485, 400)
    output_x, output_y, output_size = 600, 345, 180
    slot(draw, output_x, output_y, output_size)
    result = button_icon(stone, 125)
    image.alpha_composite(result, (output_x + (output_size - result.width) // 2,
                                   output_y + (output_size - result.height) // 2))

    # Right: exact Iron Door faces in a Stone Brick wall; the wall-mounted
    # button is built from the exact Stone texture and highlighted for use.
    wall_left, wall_top, wall_cell = 935, 185, 112
    for row in range(5):
        for col in range(5):
            x, y = wall_left + col * wall_cell, wall_top + row * wall_cell
            if col == 2 and row in (2, 3):
                door_texture = iron_door_top if row == 2 else iron_door_bottom
                tile(image, door_texture, (x, y, x + wall_cell, y + wall_cell))
            else:
                tile(image, stone_bricks, (x, y, x + wall_cell, y + wall_cell))
    button_x = wall_left + 3 * wall_cell + 35
    button_y = wall_top + 3 * wall_cell + 42
    button_face = stone.crop((5, 12, 11, 16))
    tile(image, button_face, (button_x, button_y, button_x + 42, button_y + 28))
    draw.rectangle((button_x - 12, button_y - 12, button_x + 54, button_y + 40),
                   outline=(255, 137, 0, 255), width=9)
    draw.line((1505, 680, button_x + 34, button_y + 25), fill=(255, 137, 0, 255), width=16)
    draw.polygon(((button_x + 34, button_y + 25), (button_x + 82, button_y + 15),
                  (button_x + 57, button_y + 61)), fill=(255, 137, 0, 255))
    return image


OUT.mkdir(parents=True, exist_ok=True)
with ZipFile(JAR) as archive:
    stone_bricks = asset(archive, "assets/minecraft/textures/block/stone_bricks.png")
    mossy = asset(archive, "assets/minecraft/textures/block/mossy_stone_bricks.png")
    cracked = asset(archive, "assets/minecraft/textures/block/cracked_stone_bricks.png")
    silverfish = asset(archive, "assets/minecraft/textures/entity/silverfish/silverfish.png")
    frame_top = asset(archive, "assets/minecraft/textures/block/end_portal_frame_top.png")
    frame_eye = asset(archive, "assets/minecraft/textures/block/end_portal_frame_eye.png")
    lava = asset(archive, "assets/minecraft/textures/block/lava_still.png")
    spawner = asset(archive, "assets/minecraft/textures/block/spawner.png")
    stone = asset(archive, "assets/minecraft/textures/block/stone.png")
    iron_door_top = asset(archive, "assets/minecraft/textures/block/iron_door_top.png")
    iron_door_bottom = asset(archive, "assets/minecraft/textures/block/iron_door_bottom.png")

    create_silverfish_guide(stone_bricks, mossy, cracked, silverfish).save(
        OUT / "stronghold_silverfish.png", optimize=True
    )
    create_portal_room(frame_top, lava, spawner, stone_bricks).save(
        OUT / "end_portal_room.png", optimize=True
    )
    create_frame_states(frame_top, frame_eye).save(
        OUT / "end_portal_frame_states.png", optimize=True
    )
    create_iron_door_guide(stone, stone_bricks, iron_door_top, iron_door_bottom).save(
        OUT / "stronghold_iron_door.png", optimize=True
    )

print(OUT)
