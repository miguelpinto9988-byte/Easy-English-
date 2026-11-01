#!/usr/bin/env python3
"""Ensure every A0-C2 unit can supply 16 complete, curriculum-specific PNG slides.

The Kotlin renderer generates PNG files locally on Android. This preflight checks
its 16 slide data contracts for all 156 records; it does not certify translation
quality or visual accessibility on physical phones.
"""
import json
from collections import Counter
from pathlib import Path

LEVELS = ("a0", "a1", "a2", "b1", "b2", "c1", "c2")
FRAMES_PER_UNIT = 16
required = (
    "title", "objective", "specificRule", "rule", "attention",
    "contextualText", "writing", "speaking"
)
errors = []
ids = set()
counts = Counter()
for level in LEVELS:
    filename = Path("app/src/main/assets/curriculum") / (level + ".json")
    document = json.loads(filename.read_text(encoding="utf-8"))
    lessons = document.get("lessons", [])
    expected = 12 if level == "a0" else 24
    if len(lessons) != expected:
        errors.append(f"{level}: {len(lessons)} units (expected {expected})")
    for number, lesson in enumerate(lessons, start=1):
        ident = lesson.get("id", "?")
        if ident in ids:
            errors.append(f"{ident}: duplicate unit ID")
        ids.add(ident)
        if lesson.get("number") != number:
            errors.append(f"{ident}: inconsistent unit order")
        if lesson.get("level", "").lower() != level:
            errors.append(f"{ident}: wrong level")
        for field in required:
            value = lesson.get(field, "")
            if not isinstance(value, str) or not value.strip():
                errors.append(f"{ident}: missing field {field}")
        examples = lesson.get("examples", [])
        if len(examples) < 4:
            errors.append(f"{ident}: fewer than four models")
        for i, item in enumerate(examples[:4]):
            if not item.get("en", "").strip() or not item.get("es", "").strip():
                errors.append(f"{ident}: model {i+1} incomplete")
        if len(lesson.get("dialogue", [])) != 2 or not all(
            isinstance(line, str) and line.strip()
            for line in lesson.get("dialogue", [])
        ):
            errors.append(f"{ident}: two dialogue lines required")
        checks = lesson.get("readingChecks", [])
        if len(checks) < 2 or not all(
            check.get("prompt", "").strip() for check in checks[:2]
        ):
            errors.append(f"{ident}: two reading-check questions required")
        if len(lesson.get("learningOutcomes", [])) < 3:
            errors.append(f"{ident}: three outcomes required")
        if len(lesson.get("rubric", [])) < 3:
            errors.append(f"{ident}: three writing rubric items required")
        cloze = lesson.get("cloze", {})
        if not cloze.get("prompt", "").strip() or not cloze.get("answer", "").strip():
            errors.append(f"{ident}: missing cloze content")
        if cloze.get("answer") not in cloze.get("options", []):
            errors.append(f"{ident}: cloze answer not included in options")
        if not cloze.get("explanation", "").strip():
            errors.append(f"{ident}: missing cloze explanation")
        counts[level] += 1

photos = Path("app/src/main/res/drawable-nodpi/ee_theory_photo_sheet.jpg")
data = photos.read_bytes()
if len(data) < 1024 or not (data[:3] == b"\xff\xd8\xff" and data[-2:] == b"\xff\xd9"):
    errors.append("Offline photo montage JPEG is missing or invalid")
if len(ids) != 156:
    errors.append(f"Expected 156 unique units, got {len(ids)}")
if errors:
    raise SystemExit("\n".join(errors[:80]))

for level in LEVELS:
    print(f"{level.upper()}: {counts[level]} units × {FRAMES_PER_UNIT} = "
          f"{counts[level] * FRAMES_PER_UNIT} theory PNG slides")
print(f"TOTAL: {len(ids)} units × {FRAMES_PER_UNIT} = "
      f"{len(ids) * FRAMES_PER_UNIT} curriculum-grounded theory PNG slides.")
print("All 16 per-unit data contracts passed. Classroom and device review remains required.")
