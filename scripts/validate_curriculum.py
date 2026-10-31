#!/usr/bin/env python3
"""Comprobaciones editoriales y estructurales: no sustituyen revisión docente."""
import json
import re
from collections import Counter
from pathlib import Path

root=Path(__file__).resolve().parents[1]
dataset=json.loads((root/"app/src/main/assets/lessons-v3.json").read_text(encoding="utf-8"))
assert dataset["schema"]==3
lessons=dataset["lessons"]
assert len(lessons)==156, "Se esperan 156 unidades"
counts=Counter(l["level"] for l in lessons)
assert counts=={"A0":12,"A1":24,"A2":24,"B1":24,"B2":24,"C1":24,"C2":24},counts
assert len({l["id"] for l in lessons})==156
assert len({tuple(l["dialogue"]) for l in lessons})==156
errors=[]
for l in lessons:
    lid=l["id"]
    if l["reviewStatus"]!="draft_unreviewed":
        errors.append(f"{lid}: el estado editorial debe reflejar una revisión externa pendiente")
    if len(l["title"])<4 or len(l["specificRule"])<35:
        errors.append(f"{lid}: teoría o título insuficiente")
    if len(l["examples"])<4 or any(not e.get("en") or not e.get("es") for e in l["examples"]):
        errors.append(f"{lid}: mínimo de cuatro ejemplos bilingües")
    if len(l["rubric"])!=3 or len(l["teachingSequence"])<4:
        errors.append(f"{lid}: rúbricas o etapas insuficientes")
    if len(l["dialogue"])!=2 or any(": " not in turn for turn in l["dialogue"]):
        errors.append(f"{lid}: diálogo mal formado")
    if l["level"] in {"B1","B2","C1","C2"} and len(l["readingExtended"])<170:
        errors.append(f"{lid}: lectura avanzada muy corta")
    c=l["cloze"]
    if c["prompt"].count("___")!=1 or len(c["options"])!=4:
        errors.append(f"{lid}: ejercicio de completar no válido")
    if len(set(c["options"]))!=4 or c["options"].count(c["answer"])!=1:
        errors.append(f"{lid}: opciones duplicadas o respuesta no única")
    if not l["objective"] or not l["writing"] or not l["speaking"]:
        errors.append(f"{lid}: objetivo u otra destreza incompleta")
    if not re.fullmatch(r"[a-z][0-2]-[0-9]{2}|[abc][12]-[0-9]{2}",lid):
        errors.append(f"{lid}: identificador inválido")
if errors:
    raise SystemExit("\n".join(errors))
print("VALIDADO (estructura): 156 unidades, 156 diálogos, 39 bancos de cloze,")
print("24 lecturas avanzadas; rúbricas y actividades presentes.")
print("ATENCIÓN: revisión docente, licencias y validez psicométrica NO certificadas.")
