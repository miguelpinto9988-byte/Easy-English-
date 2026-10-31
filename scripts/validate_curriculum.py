#!/usr/bin/env python3
"""Validación estructural del banco intensivo; no demuestra calidad lingüística externa."""
import json
from collections import Counter
from pathlib import Path

root = Path(__file__).resolve().parents[1]
folder = root / "app/src/main/assets/curriculum"
expected = {"A0":12, "A1":24, "A2":24, "B1":24, "B2":24, "C1":24, "C2":24}
errors=[]
lesson_ids=set()
task_ids=set()
stats=Counter()

for level, quantity in expected.items():
    path=folder/(level.lower()+".json")
    package=json.loads(path.read_text(encoding="utf-8"))
    if package.get("schema") != 5 or package.get("level") != level:
        errors.append(f"{level}: esquema 5 / nivel incorrecto")
    lessons=package.get("lessons",[])
    if len(lessons)!=quantity:
        errors.append(f"{level}: cantidad de unidades incorrecta")
    if sorted(l["number"] for l in lessons)!=list(range(1,quantity+1)):
        errors.append(f"{level}: numeración inconsistente")
    for lesson in lessons:
        key=lesson["id"]
        if key in lesson_ids:errors.append(f"{key}: identificador duplicado")
        lesson_ids.add(key)
        if lesson.get("reviewStatus")!="draft_unreviewed":
            errors.append(f"{key}: no se debe afirmar revisión externa")
        if len(lesson.get("specificRule",""))<50:
            errors.append(f"{key}: explicación de tema breve")
        if len(lesson.get("examples",[]))<4:
            errors.append(f"{key}: faltan modelos bilingües")
        if len(lesson.get("dialogue",[]))!=2:
            errors.append(f"{key}: diálogo inválido")
        if len(lesson.get("learningOutcomes",[]))<3:
            errors.append(f"{key}: objetivos insuficientes")
        if len(lesson.get("rubric",[]))!=3:
            errors.append(f"{key}: rúbrica ausente")
        if len(lesson.get("readingChecks",[]))<2:
            errors.append(f"{key}: comprensión incompleta")
        c=lesson.get("cloze",{})
        if len(c.get("options",[]))!=4 or c.get("options",[]).count(c.get("answer"))!=1:
            errors.append(f"{key}: cloze incorrecto")
        sessions=lesson.get("intensiveSessions",[])
        if len(sessions)!=8:
            errors.append(f"{key}: requiere ocho sesiones")
        if len(set(s["id"] for s in sessions))!=len(sessions):
            errors.append(f"{key}: sesiones duplicadas")
        for session in sessions:
            tasks=session.get("tasks",[])
            if len(tasks)!=8:
                errors.append(f"{key}/{session['id']}: requiere ocho actividades")
            for task in tasks:
                tid=task["id"]
                if tid in task_ids:errors.append(f"{tid}: identificador repetido")
                task_ids.add(tid)
                kind=task.get("kind")
                if kind not in {"choice","listen","read","arrange","write","speak","observe","reflect"}:
                    errors.append(f"{tid}: modalidad desconocida")
                if not task.get("answer") or not task.get("prompt"):
                    errors.append(f"{tid}: objetivo o solución vacío")
                if kind in {"choice","listen","read"}:
                    opts=task.get("options",[])
                    if len(opts)!=4 or len(set(opts))!=4 or opts.count(task.get("answer"))!=1:
                        errors.append(f"{tid}: opción múltiple mal formada")
                if kind=="listen" and not task.get("audioText"):
                    errors.append(f"{tid}: falta el guion de audio")
                if kind=="arrange" and len(task.get("answer","").split())<2:
                    errors.append(f"{tid}: frase no ordenable")
                stats[kind]+=1
        stats["units"]+=1
        stats["sessions"]+=len(sessions)
        stats["examples"]+=len(lesson.get("examples",[]))
if len(task_ids)!=9984 or stats["units"]!=156:
    errors.append(f"Total incorrecto: {stats['units']} unidades, {len(task_ids)} tareas")
if errors:
    raise SystemExit("\n".join(errors[:80]))
print("OK - Banco intensivo validado estructuralmente")
print("Unidades:",stats["units"],"sesiones:",stats["sessions"],
      "actividades:",len(task_ids),"ejemplos bilingües:",stats["examples"])
print("Por modalidad:",", ".join(k+"="+str(v) for k,v in stats.items()
                               if k not in {"units","sessions","examples"}))
print("ADVERTENCIA: no demuestra validez pedagógica, corrección semántica ni variedad lingüística completa.")
