# Диаграмма Ганта — MindFlow

## Календарный план разработки (18 недель)

```plantuml
@startgantt
!theme plain
title Диаграмма Ганта - Курсовой проект "MindFlow" (18 недель)

Project starts 2026-02-02
saturday are closed
sunday are closed

-- Этап 0: Инициация (Недели 1-2) --
[Анализ предметной области] starts 2026-02-02 and lasts 5 days
[Паспорт проекта] starts 2026-02-09 and lasts 3 days
[IDEF0 / BUC диаграммы] starts 2026-02-12 and lasts 3 days
[SWOT, стейкхолдеры, CJM] starts 2026-02-17 and lasts 3 days
[Бизнес-глоссарий] starts 2026-02-20 and lasts 2 days

-- Этап 1: Требования (Недели 3-4) --
[Use Case диаграмма] starts 2026-02-23 and lasts 3 days
[Domain Model] starts 2026-02-26 and lasts 3 days
[Спецификации прецедентов] starts 2026-03-02 and lasts 3 days
[Матрица трассировки] starts 2026-03-05 and lasts 2 days

-- Этап 2: Архитектура (Недели 5-6) --
[Диаграмма пакетов PCMEF] starts 2026-03-09 and lasts 3 days
[Спецификация интерфейсов] starts 2026-03-12 and lasts 3 days
[ADR (9 решений)] starts 2026-03-16 and lasts 4 days

-- Этап 3: Проектирование БД (Недели 7-8) --
[ER-диаграмма] starts 2026-03-23 and lasts 3 days
[DDL-скрипты] starts 2026-03-26 and lasts 2 days
[Стратегия ORM] starts 2026-03-30 and lasts 2 days
[Тестовые данные] starts 2026-04-01 and lasts 2 days

-- Этап 4: Детальное проектирование (Недели 9-10) --
[Диаграммы последовательности] starts 2026-04-06 and lasts 3 days
[Диаграммы классов] starts 2026-04-09 and lasts 4 days
[Спецификации методов] starts 2026-04-15 and lasts 2 days

-- Этап 5: Реализация ядра (Недели 11-13) --
[Entity + Foundation (backend)] starts 2026-04-20 and lasts 4 days
[Mediator (Services)] starts 2026-04-24 and lasts 3 days
[Control (Controllers)] starts 2026-04-29 and lasts 3 days
[JWT + Security] starts 2026-05-04 and lasts 3 days
[Android Foundation (Room + Retrofit)] starts 2026-04-27 and lasts 5 days
[Android Mediator (Repositories)] starts 2026-05-04 and lasts 3 days

-- Этап 6: Рефакторинг и тесты (Недели 14-15) --
[Модульные тесты backend (36)] starts 2026-05-11 and lasts 5 days
[Модульные тесты Android (253)] starts 2026-05-18 and lasts 5 days
[Статический анализ + рефакторинг] starts 2026-05-25 and lasts 4 days

-- Этап 7: Интерфейс (Недели 15-16) --
[Android Control (ViewModels)] starts 2026-05-18 and lasts 4 days
[Android Presentation (12 экранов)] starts 2026-05-22 and lasts 8 days
[Навигация и темизация] starts 2026-06-02 and lasts 3 days

-- Этап 8: Завершение (Недели 17-18) --
[Техническое задание] starts 2026-06-08 and lasts 2 days
[Пояснительная записка] starts 2026-06-10 and lasts 4 days
[Руководства (user + admin)] starts 2026-06-16 and lasts 2 days
[Docker + развёртывание] starts 2026-06-18 and lasts 2 days
[WBS + Ганта + COCOMO] starts 2026-06-22 and lasts 2 days
[Презентация] starts 2026-06-24 and lasts 2 days

@endgantt
```

## Сводная таблица этапов

| Этап | Период | Недели | Ключевые результаты | % веса |
|------|--------|--------|---------------------|--------|
| 0: Инициация | 02.02–13.02 | 1–2 | Паспорт, IDEF0, BUC, глоссарий | 5% |
| 1: Требования | 16.02–27.02 | 3–4 | Use Case, Domain Model, трассировка | 10% |
| 2: Архитектура | 02.03–13.03 | 5–6 | PCMEF, интерфейсы, ADR | 10% |
| 3: БД | 16.03–27.03 | 7–8 | ER, DDL, ORM-стратегия | 10% |
| 4: Детальное проектирование | 30.03–10.04 | 9–10 | Sequence, Class diagrams | 10% |
| 5: Реализация ядра | 13.04–01.05 | 11–13 | Backend + Android core | 15% |
| 6: Рефакторинг и тесты | 04.05–15.05 | 14–15 | Тесты, покрытие >40%, паттерны | 10% |
| 7: Интерфейс | 11.05–22.05 | 15–16 | 12 экранов, Material Design 3 | 15% |
| 8: Завершение | 25.05–19.06 | 17–18 | Документация, Docker, презентация | 15% |
| **ИТОГО** | 02.02–19.06 | **18 нед.** | | **100%** |

