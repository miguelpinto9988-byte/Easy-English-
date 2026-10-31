#!/usr/bin/env python3
"""Validación automatizada estructural. No sustituye revisión humana."""
import json
import re
from collections import Counter
from pathlib import Path

root=Path(__file__).resolve().parents[1]
directory=root/"app/src/main/assets/curriculum"
levels={"A0":12,"A1":24,"A2":24,"B1":24,"B2":24,"C1":24,"C2":24}
units=[]
errors=[]

for level,count in levels.items():
    f=directory/(level.lower()+".json")
    data=json.loads(f.read_text(encoding="utf-8"))
    if data.get("schema")!=4 or data.get("level")!=level:
        errors.append(f"{f.name}: esquema o nivel incorrecto")
    lessons=data.get("lessons",[])
    if len(lessons)!=count:
        errors.append(f"{f.name}: tiene {len(lessons)} unidades; se esperaban {count}")
    units+=lessons
    numbers=sorted(x["number"] for x in lessons)
    if numbers!=list(range(1,count+1)):
        errors.append(f"{f.name}: números discontinuos")

ids=set()
rules=set()
dialogues=set()
reading_checks=0
examples=0
for l in units:
    lid=l["id"]
    if lid in ids:errors.append(f"{lid}: identificador duplicado")
    ids.add(lid)
    if l.get("reviewStatus")!="draft_unreviewed":
        errors.append(f"{lid}: estado de revisión no verificado")
    rule=l.get("specificRule","")
    if len(rule)<50 or rule in rules:errors.append(f"{lid}: regla particular ausente o duplicada")
    rules.add(rule)
    d=tuple(l.get("dialogue",[]))
    if len(d)!=2 or d in dialogues or any(": " not in turn for turn in d):
        errors.append(f"{lid}: diálogo no válido o repetido")
    dialogues.add(d)
    if len(l.get("learningOutcomes",[]))<3:
        errors.append(f"{lid}: objetivos insuficientes")
    if len(l.get("teachingNotes",[]))<5:
        errors.append(f"{lid}: teoría interactiva insuficiente")
    e=l.get("examples",[])
    examples+=len(e)
    if len(e)<4 or any(not x.get("en") or not x.get("es") for x in e):
        errors.append(f"{lid}: faltan ejemplos bilingües")
    checks=l.get("readingChecks",[])
    reading_checks+=len(checks)
    if len(checks)!=2 or checks[0].get("answer","") not in d[1]:
        errors.append(f"{lid}: lectura o evidencia incoherente")
    if checks[1].get("answer")!=l.get("title"):
        errors.append(f"{lid}: identificación de tema incoherente")
    c=l.get("cloze",{})
    if c.get("prompt","").count("___")!=1 or len(c.get("options",[]))!=4:
        errors.append(f"{lid}: pregunta de completar mal formada")
    if c.get("options",[]).count(c.get("answer"))!=1 or len(set(c.get("options",[])))!=4:
        errors.append(f"{lid}: respuesta ambigua o distractores duplicados")
    if len(l.get("rubric",[]))!=3:
        errors.append(f"{lid}: criterios de autoevaluación insuficientes")
    if l.get("sourceMeta",{}).get("origin")!="original_easy_english":
        errors.append(f"{lid}: fuente editorial no identificada")
    if not l.get("productionTask",{}).get("speaking"):
        errors.append(f"{lid}: actividad oral faltante")
    if len(l.get("contextualText",""))<len("\n".join(d)):
        errors.append(f"{lid}: contexto de lectura insuficiente")

if len(units)!=156:
    errors.append(f"Total incorrecto: {len(units)}")
if errors:
    raise SystemExit("\n".join(errors))
print(f"OK: {len(units)} unidades, {len(rules)} reglas particulares, "
      f"{len(dialogues)} diálogos originales, {reading_checks} ejercicios de lectura "
      f"y {examples} ejemplos bilingües.")
print("IMPORTANTE: este control NO certifica precisión lingüística ni validez pedagógica.")
