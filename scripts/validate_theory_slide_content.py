#!/usr/bin/env python3
"""Estructura mínima para generar las ocho diapositivas PNG de todas las unidades.

No certifica traducciones ni comprensión pedagógica: comprueba que el renderizador
recibirá los campos que utiliza, incluidos los modelos bilingües y el diálogo.
"""
import json
from pathlib import Path

levels = ("a0", "a1", "a2", "b1", "b2", "c1", "c2")
count = 0
errors = []
for level in levels:
    source = Path("app/src/main/assets/curriculum") / (level + ".json")
    data = json.loads(source.read_text(encoding="utf-8"))
    for lesson in data.get("lessons", []):
        unit = lesson.get("id", "?")
        for field in (
            "title", "objective", "specificRule", "rule", "attention",
            "contextualText", "writing", "speaking"
        ):
            if not isinstance(lesson.get(field), str) or not lesson[field].strip():
                errors.append(unit + ": falta " + field)
        examples = lesson.get("examples", [])
        if len(examples) < 4 or not all(
            item.get("en", "").strip() and item.get("es", "").strip()
            for item in examples[:4]
        ):
            errors.append(unit + ": cuatro ejemplos bilingües requeridos")
        if len(lesson.get("dialogue", [])) != 2:
            errors.append(unit + ": diálogo inválido")
        if len(lesson.get("learningOutcomes", [])) < 3:
            errors.append(unit + ": faltan tres objetivos")
        count += 1

photo = Path("app/src/main/res/drawable-nodpi/ee_theory_photo_sheet.jpg")
raw = photo.read_bytes()
if not (raw.startswith(b"\xff\xd8\xff") and raw.endswith(b"\xff\xd9")):
    errors.append("El mosaico de fotografías no tiene formato JPEG válido")
if count != 156:
    errors.append(f"Unidades encontradas: {count}, se esperaban 156")
if errors:
    raise SystemExit("\n".join(errors[:35]))
print(f"Contenido preparado: {count} unidades x 8 = {count*8} diapositivas PNG posibles.")
