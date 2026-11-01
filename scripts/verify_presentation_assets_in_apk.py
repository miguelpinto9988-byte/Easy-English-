#!/usr/bin/env python3
"""Confirm PNG integration in a compiled Android APK.

Returns nonzero if source assets are present but APK omits or changes any.
When source assets have not been committed, emits an explicit warning rather
than describing a successful import. --require additionally blocks release.
"""
from pathlib import Path
from zipfile import ZipFile
import argparse
import hashlib
import os
import struct
import sys

parser=argparse.ArgumentParser()
parser.add_argument("apk",type=Path)
parser.add_argument("--require",action="store_true")
args=parser.parse_args()

root=Path(__file__).resolve().parents[1]
folder=root/"app/src/main/assets/presentations"
index=folder/"index.json"
if not index.exists():
    message="PENDING: 56 PNG todavía no están en el repositorio; la APK de CI no los incluye."
    print(message)
    print("::warning::"+message)
    if "GITHUB_STEP_SUMMARY" in os.environ:
        with open(os.environ["GITHUB_STEP_SUMMARY"],"a") as f:
            f.write("\n### Presentaciones horizontales: PENDIENTES\n\n"
                    "La APK compilada desde este commit no contiene los 56 PNG. "
                    "No distribuir como versión final de presentaciones.\n")
    sys.exit(2 if args.require else 0)

sources=sorted(folder.glob("a0/a0-*/slide_*.png"))
if len(sources)!=56:
    raise SystemExit(f"ERROR: faltan PNG en fuentes: {len(sources)}/56")
with ZipFile(args.apk) as apk:
    names=set(apk.namelist())
    for source in sources:
        rel="assets/presentations/"+source.relative_to(folder).as_posix()
        if rel not in names:
            raise SystemExit("ERROR: falta en APK: "+rel)
        content=apk.read(rel)
        if hashlib.sha256(content).digest()!=hashlib.sha256(source.read_bytes()).digest():
            raise SystemExit("ERROR: bytes distintos: "+rel)
        if content[:8]!=b"\x89PNG\r\n\x1a\n" or struct.unpack_from(">II",content,16)!=(1920,1080):
            raise SystemExit("ERROR: formato incorrecto: "+rel)
    if "assets/presentations/index.json" not in names:
        raise SystemExit("ERROR: falta índice de presentaciones en APK")
    if apk.testzip() is not None:
        raise SystemExit("ERROR: CRC de APK no válido")
print("OK: 56 PNG Full HD comprobados byte a byte dentro de "+str(args.apk))
