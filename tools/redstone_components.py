"""Original Minecraft 26.1.2 component icons for the Redstone diagrams.

Block JSON supplies geometry, UVs and element rotations. Chest geometry is the
closed ChestModel.createSingleBodyLayer from the target client (64x64 entity
atlas); its three cuboids are not a substitute cube or a redraw. Projection and
directional lighting are explanatory presentation, not a game screenshot.
"""

from io import BytesIO
from functools import lru_cache
import json
import math

from PIL import Image


def _asset(archive, name):
    return Image.open(BytesIO(archive.read('assets/minecraft/textures/' + name + '.png'))).convert('RGBA')


def _model(archive, name):
    data = json.loads(archive.read('assets/minecraft/models/' + name.removeprefix('minecraft:') + '.json'))
    base = _model(archive, data['parent']) if 'parent' in data else {}
    return {**base, **data, 'textures': {**base.get('textures', {}), **data.get('textures', {})}}


def _texture(archive, textures, name):
    while isinstance(name, str) and name.startswith('#'):
        name = textures[name[1:]]
    if isinstance(name, dict):
        name = name['sprite']
    return _asset(archive, name.removeprefix('minecraft:'))


def _corners(element, side):
    x, y, z = element['from']
    X, Y, Z = element['to']
    return {
        'up': [(x,Y,z),(X,Y,z),(X,Y,Z),(x,Y,Z)],
        'down': [(x,y,Z),(X,y,Z),(X,y,z),(x,y,z)],
        'north': [(X,Y,z),(x,Y,z),(x,y,z),(X,y,z)],
        'south': [(x,Y,Z),(X,Y,Z),(X,y,Z),(x,y,Z)],
        'west': [(x,Y,z),(x,Y,Z),(x,y,Z),(x,y,z)],
        'east': [(X,Y,Z),(X,Y,z),(X,y,z),(X,y,Z)],
    }[side]


def _rotate(point, rotation):
    if not rotation:
        return point
    if rotation.get('rescale'):
        raise ValueError('Rescaled elements are not supported by this bounded renderer')
    origin = rotation['origin']
    p = [point[i] - origin[i] for i in range(3)]
    axis = 'xyz'.index(rotation['axis'])
    i, j = (axis + 1) % 3, (axis + 2) % 3
    c, s = math.cos(math.radians(rotation['angle'])), math.sin(math.radians(rotation['angle']))
    p[i], p[j] = c * p[i] - s * p[j], s * p[i] + c * p[j]
    return tuple(p[k] + origin[k] for k in range(3))


def _render(archive, model, size):
    # Orthographic view from above/south/east. Depth testing handles the tilted
    # Lever handle and the overlapping Chest lid/body without painter guesses.
    faces = []
    for element in model['elements']:
        for side, face in element['faces'].items():
            if 'uv' not in face:
                raise ValueError('Explicit face UVs required')
            points = [_rotate(p, element.get('rotation')) for p in _corners(element, side)]
            projected = [((x-z)*.8660254, (x+z)*.5-y, x+y+z) for x,y,z in points]
            faces.append((projected, face, _texture(archive, model['textures'], face['texture']),
                          {'up':1., 'down':.5, 'south':.85, 'north':.7, 'east':.7, 'west':.7}[side]))
    flat = [p for points, *_ in faces for p in points]
    left, top = min(p[0] for p in flat), min(p[1] for p in flat)
    width, height = max(p[0] for p in flat)-left, max(p[1] for p in flat)-top
    scale = (size-4) / max(width, height)
    dx, dy = (size-width*scale)/2, (size-height*scale)/2
    out = Image.new('RGBA', (size,size))
    pixels = out.load()
    depth = [-math.inf] * (size*size)
    for points, face, texture, shade in faces:
        p = [((x-left)*scale+dx, (y-top)*scale+dy, z) for x,y,z in points]
        ax, ay = p[1][0]-p[0][0], p[1][1]-p[0][1]
        bx, by = p[3][0]-p[0][0], p[3][1]-p[0][1]
        determinant = ax*by-ay*bx
        if abs(determinant) < 1e-8:
            continue
        u0,v0,u1,v1 = face['uv']
        for yy in range(max(0, int(min(q[1] for q in p))), min(size, math.ceil(max(q[1] for q in p)))):
            for xx in range(max(0, int(min(q[0] for q in p))), min(size, math.ceil(max(q[0] for q in p)))):
                sx, sy = xx+.5-p[0][0], yy+.5-p[0][1]
                u, v = (sx*by-sy*bx)/determinant, (ax*sy-ay*sx)/determinant
                if not (0<=u<1 and 0<=v<1):
                    continue
                z = p[0][2]+u*(p[1][2]-p[0][2])+v*(p[3][2]-p[0][2])
                if z < depth[yy*size+xx]:
                    continue
                for _ in range(face.get('rotation',0)//90):
                    u,v = v,1-u
                tx = min(texture.width-1,max(0,int((u0+(u1-u0)*u)/16*texture.width)))
                ty = min(texture.height-1,max(0,int((v0+(v1-v0)*v)/16*texture.height)))
                r,g,b,a = texture.getpixel((tx,ty))
                if a:
                    pixels[xx,yy] = (int(r*shade),int(g*shade),int(b*shade),a)
                    depth[yy*size+xx] = z
    return out


def _chest():
    elements = []
    # Target ChestModel: bottom (1,0,1)+(14,10,14), lid offset (0,9,1)
    # plus (1,0,0)+(14,5,14), lock offset (0,9,1)+(7,-2,14)+(2,4,1).
    for origin, dims, uv in [((1,0,1),(14,10,14),(0,19)),
                              ((1,9,1),(14,5,14),(0,0)),
                              ((7,7,15),(2,4,1),(0,0))]:
        x,y,z = origin
        w,h,d = dims
        u,v = uv
        # ModelPart.Cube unwrap; entity UV pixel coordinates -> model 0..16.
        rectangles = {'up':(u+d,v,u+d+w,v+d),
                      'down':(u+d+w,v,u+d+2*w,v+d),
                      'west':(u,v+d,u+d,v+d+h),
                      'north':(u+d,v+d,u+d+w,v+d+h),
                      'east':(u+d+w,v+d,u+2*d+w,v+d+h),
                      'south':(u+2*d+w,v+d,u+2*d+2*w,v+d+h)}
        elements.append({'from':origin,'to':(x+w,y+h,z+d), 'faces':{
            side:{'uv':[n/4 for n in rect], 'texture':'#chest'} for side,rect in rectangles.items()}})
    return {'textures':{'chest':'entity/chest/normal'}, 'elements':elements}


def component(archive, kind, size=256):
    """Return a square transparent original-asset icon; unknown kinds fail."""
    return _component_cached(archive, kind, size).copy()


@lru_cache(maxsize=64)
def _component_cached(archive, kind, size):
    if kind == 'iron_door':
        return _asset(archive, 'item/iron_door').resize((size,size), Image.Resampling.NEAREST)
    if kind == 'chest':
        return _render(archive, _chest(), size)
    if kind not in ('lever', 'stone_button', 'stone_pressure_plate'):
        raise ValueError('Unsupported Redstone component: '+kind)
    return _render(archive, _model(archive, 'block/'+kind), size)


def wire(archive, power=0, size=256):
    """One full world cell: east/west connected Dust, exact line1 + overlay.

    Target blockstate selects side1 and side_alt1 with y=270; their two half
    model faces together cover the complete texture. Tint follows vanilla
    RedStoneWireBlock's power-colour formula, not an invented red stroke.
    """
    if not isinstance(power, int) or not 0 <= power <= 15:
        raise ValueError('Wire power must be an integer from 0 through 15')
    level = power/15
    tint = (level*.6+(.4 if power else .3),
            max(0,level*level*.7-.5), max(0,level*level*.6-.7))
    result = _asset(archive, 'block/redstone_dust_line1')
    result.putdata([tuple(int(c*t) for c,t in zip(pixel[:3], tint))+(pixel[3],)
                    for pixel in result.getdata()])
    result.alpha_composite(_asset(archive, 'block/redstone_dust_overlay'))
    return result.transpose(Image.Transpose.ROTATE_90).resize((size,size), Image.Resampling.NEAREST)
