#!/usr/bin/env python3
"""Valida el atlas PNG transparente utilizado por las pantallas Compose."""
from pathlib import Path
import struct
import zlib

PATH = Path("app/src/main/res/drawable-nodpi/ee_icon_atlas.png")
NAMES = (
    "home", "levels", "unit", "intensive", "games", "reading",
    "listening", "writing", "speaking", "progress", "settings", "review",
    "profile", "certificate", "target", "help", "notification", "bookmark",
    "back", "next", "sparkles", "award", "ribbon", "speech_decor",
    "download", "english", "audio", "check", "menu", "globe",
)
data = PATH.read_bytes()
assert data[:8] == b"\x89PNG\r\n\x1a\n", "No es un PNG válido"
offset = 8
chunks = {}
while offset + 12 <= len(data):
    size = struct.unpack(">I", data[offset:offset+4])[0]
    marker = data[offset+4:offset+8]
    payload = data[offset+8:offset+8+size]
    given_crc = struct.unpack(">I", data[offset+8+size:offset+12+size])[0]
    assert zlib.crc32(marker + payload) == given_crc, marker
    chunks.setdefault(marker, []).append(payload)
    offset += size + 12
    if marker == b"IEND":
        break
assert b"IHDR" in chunks and b"IDAT" in chunks and b"tRNS" in chunks
width, height, depth, mode, compression, filtering, interlace = struct.unpack(
    ">IIBBBBB", chunks[b"IHDR"][0]
)
assert (width, height, depth, mode) == (672, 560, 8, 3)
assert (compression, filtering, interlace) == (0, 0, 0)
assert chunks[b"tRNS"][0][0] == 0, "El fondo no es transparente"
assert len(NAMES) == 30
indices = zlib.decompress(b"".join(chunks[b"IDAT"]))
assert len(indices) == (width + 1) * height
assert all(indices[y*(width+1)] == 0 for y in range(height))
for index, name in enumerate(NAMES):
    x0, y0 = (index % 6) * 112, (index // 6) * 112
    area = [
        indices[y*(width+1)+1+x0:y*(width+1)+1+x0+112]
        for y in range(y0,y0+112)
    ]
    assert any(any(pixel != 0 for pixel in row) for row in area), name
    assert area[0][0] == 0, f"{name}: no hay transparencia de margen"
print(f"Atlas validado: {len(NAMES)} iconos, {width}×{height}px, alpha PNG.")
