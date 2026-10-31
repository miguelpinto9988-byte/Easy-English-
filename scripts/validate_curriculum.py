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
expanded_ids=set()
stats=Counter()


for level, quantity in expected.items():
    path=folder/(level.lower()+".json")
    package=json.loads(path.read_text(encoding="utf-8"))
    if package.get("schema") != 5 or package.get("level") != level:
        errors.append(f"{level}: esquema 5 / nivel incorrecto")
    lessons=package.get("lessons",[])
    level_pairs = [
        (example.get("en", "").strip(), example.get("es", "").strip())
        for lesson in lessons for example in lesson.get("examples", [])
    ]
    level_pairs = list(dict.fromkeys(level_pairs))
    for direction, index in (("inglés", 0), ("español", 1)):
        if len({pair[index] for pair in level_pairs if pair[index]}) < 4:
            errors.append(f"{level}: menos de cuatro alternativas distintas en {direction}")
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
        local = list(dict.fromkeys(
            (e.get("en","").strip(),e.get("es","").strip())
            for e in lesson.get("examples",[])
        ))
        local_english = {en for en,_ in local}
        review = [pair for pair in level_pairs if pair[0] not in local_english]
        if not local or not review:
            errors.append(f"{key}: sin suficientes modelos para el repaso")
        for session_index, session in enumerate(sessions):
            tasks=session.get("tasks",[])
            if len(tasks)!=8:
                errors.append(f"{key}/{session['id']}: requiere ocho actividades")
            # Los ejercicios 9-16 se generan en Android a partir de pares bilingües.
            # Validamos de antemano sus ocho identificadores, fuentes y distractores.
            if local and review:
                for i in range(8):
                    pair = (local[(session_index+i) % len(local)] if i<4 else
                            review[(lesson["number"]*4+session_index*4+i-4) % len(review)])
                    to_spanish = (session_index+i) % 2 == 0
                    answer = pair[1 if to_spanish else 0]
                    other_answers = {
                        v[1 if to_spanish else 0] for v in level_pairs
                        if v[1 if to_spanish else 0] and v[1 if to_spanish else 0] != answer
                    }
                    extra_id = f"{key}-s{session_index+1}-extra{i+1}"
                    if extra_id in expanded_ids or extra_id in task_ids:
                        errors.append(f"{extra_id}: identificador generado duplicado")
                    expanded_ids.add(extra_id)
                    if not answer or len(other_answers)<3 or not all(pair):
                        errors.append(f"{extra_id}: ejercicio generado sin solución o distractores")
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
if len(task_ids)!=9984 or stats["units"]!=156 or len(expanded_ids)!=9984:
    errors.append(
        f"Total incorrecto: {stats['units']} unidades, "
        f"{len(task_ids)} tareas base y {len(expanded_ids)} de refuerzo"
    )
if errors:
    raise SystemExit("\n".join(errors[:80]))
print("OK - Banco intensivo validado estructuralmente")
print("Unidades:",stats["units"],"sesiones:",stats["sessions"],
      "ejercicios base:",len(task_ids),"ejercicios de repaso:",len(expanded_ids),
      "total disponible:",len(task_ids)+len(expanded_ids),
      "ejercicios por sesión:",16,"ejemplos bilingües:",stats["examples"])
print("Por modalidad:",", ".join(k+"="+str(v) for k,v in stats.items()
                               if k not in {"units","sessions","examples"}))
print("ADVERTENCIA: no demuestra validez pedagógica, corrección semántica ni variedad lingüística completa.")
