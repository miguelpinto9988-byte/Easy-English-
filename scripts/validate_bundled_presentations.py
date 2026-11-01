#!/usr/bin/env python3
"""Validate complete 8-slide landscape PNG bundles when imported into Android assets.

Until the binary package has been extracted into app/src/main/assets, this check
reports a pending import. It MUST NOT be mistaken for successful PNG integration.
"""
from pathlib import Path
import json
import struct
import sys
import zlib

ROOT = Path(__file__).resolve().parents[1]
ASSETS = ROOT / "app" / "src" / "main" / "assets"
INDEX = ASSETS / "presentations" / "index.json"
EXPECTED = {f"a0-{i:02}" for i in (1, 2, 3, 4, 5, 6, 8)}

if not INDEX.is_file():
    print("PENDING: No se ha importado el paquete de PNG horizontales.")
    sys.exit(0)

manifest = json.loads(INDEX.read_text(encoding="utf-8"))
bundles = manifest.get("bundles", [])
found = {item.get("lessonId") for item in bundles}
if found != EXPECTED:
    raise SystemExit(f"Paquetes incorrectos: {found} != {EXPECTED}")

count = 0
for record in bundles:
    lesson = record["lessonId"]
    folder = ASSETS / "presentations" / "a0" / lesson
    expected_names = [f"slide_{i:02}.png" for i in range(1, 9)]
    actual_names = sorted(x.name for x in folder.glob("*.png"))
    if actual_names != expected_names:
        raise SystemExit(f"{lesson}: faltan o sobran diapositivas: {actual_names}")
    for filename in expected_names:
        data = (folder / filename).read_bytes()
        if not data.startswith(b"\x89PNG\r\n\x1a\n"):
            raise SystemExit(f"{lesson}/{filename}: no es PNG")
        width, height = struct.unpack(">II", data[16:24])
        if (width, height) != (1920, 1080):
            raise SystemExit(f"{lesson}/{filename}: requiere 1920x1080, no {width}x{height}")
        pos = 8
        seen_iend = False
        while pos + 12 <= len(data):
            length = struct.unpack(">I", data[pos : pos + 4])[0]
            typ = data[pos + 4 : pos + 8]
            end = pos + 12 + length
            if end > len(data):
                raise SystemExit(f"{lesson}/{filename}: PNG truncado")
            payload = data[pos + 8 : pos + 8 + length]
            crc = struct.unpack(">I", data[pos + 8 + length : end])[0]
            if zlib.crc32(typ + payload) != crc:
                raise SystemExit(f"{lesson}/{filename}: CRC incorrecto")
            pos = end
            if typ == b"IEND":
                seen_iend = True
                break
        if not seen_iend:
            raise SystemExit(f"{lesson}/{filename}: no contiene IEND")
        count += 1

if count != 56:
    raise SystemExit(f"Se esperaban 56 imágenes, se encontraron {count}")
print("OK: 56 diapositivas PNG horizontales verificadas en 7 unidades A0.")
