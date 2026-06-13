# Диаграмма Ганта — MindFlow

## Календарный план разработки (18 недель)

```plantuml
@startgantt
<style>
ganttDiagram {
  task {
    FontName Arial
    FontSize 11
  }
}
</style>

Project starts 2026-01-20
saturday are closed
sunday are closed

-- Этап 0: Инициация (Недели 1-2) --
[Анализ предметной области] lasts 5 days
[Паспорт проекта] lasts 3 days
[IDEF0 / BUC диаграммы] lasts 3 days
[SWOT, стейкхолдеры, CJM] lasts 3 days
[Бизнес-глоссарий] lasts 2 days

-- Этап 1: Требования (Недели 3-4) --
[Use Case диаграмма] lasts 3 days
[Domain Model] lasts 3 days
[Спецификации прецедентов] lasts 3 days
[Матрица трассировки] lasts 2 days

-- Этап 2: Архитектура (Недели 5-6) --
[Диаграмма пакетов PCMEF] lasts 3 days
[Спецификация интерфейсов] lasts 3 days
[ADR (9 решений)] lasts 4 days

-- Этап 3: Проектирование БД (Недели 7-8) --
[ER-диаграмма] lasts 3 days
[DDL-скрипты] lasts 2 days
[Стратегия ORM] lasts 2 days
[Тестовые данные] lasts 2 days

-- Этап 4: Детальное проектирование (Недели 9-10) --
[Диаграммы последовательности] lasts 3 days
[Диаграммы классов] lasts 4 days
[Спецификации методов] lasts 2 days

-- Этап 5: Реализация ядра (Недели 11-12) --
[Entity + Foundation (backend)] lasts 4 days
[Mediator (Services)] lasts 3 days
[Control (Controllers)] lasts 3 days
[JWT + Security] lasts 3 days
[Android Foundation (Room + Retrofit)] lasts 5 days
[Android Mediator (Repositories)] lasts 3 days

-- Этап 6: Рефакторинг (Недели 13-14) --
[Модульные тесты backend (36)] lasts 5 days
[Модульные тесты Android (253)] lasts 5 days
[Статический анализ + рефакторинг] lasts 4 days

-- Этап 7: Интерфейс (Недели 15-16) --
[Android Control (ViewModels)] lasts 4 days
[Android Presentation (12 экранов)] lasts 8 days
[Навигация и темизация] lasts 3 days

-- Этап 8: Завершение (Недели 17-18) --
[Техническое задание] lasts 2 days
[Пояснительная записка] lasts 4 days
[Руководства (user + admin)] lasts 2 days
[Docker + развёртывание] lasts 2 days
[WBS + Ганта + COCOMO] lasts 2 days
[Презентация] lasts 2 days

@endgantt
```

## Сводная таблица этапов

| Этап | Период | Недели | Ключевые результаты | % веса |
|------|--------|--------|---------------------|--------|
| 0: Инициация | 20.01–02.02 | 1–2 | Паспорт, IDEF0, BUC, глоссарий | 5% |
| 1: Требования | 03.02–16.02 | 3–4 | Use Case, Domain Model, трассировка | 10% |
| 2: Архитектура | 17.02–02.03 | 5–6 | PCMEF, интерфейсы, ADR | 10% |
| 3: БД | 03.03–16.03 | 7–8 | ER, DDL, ORM-стратегия | 10% |
| 4: Детал. проектирование | 17.03–30.03 | 9–10 | Sequence, Class diagrams | 10% |
| 5: Реализация ядра | 31.03–13.04 | 11–12 | Backend + Android core | 15% |
| 6: Рефакторинг | 14.04–27.04 | 13–14 | Тесты, покрытие >40%, паттерны | 10% |
| 7: Интерфейс | 28.04–11.05 | 15–16 | 12 экранов, Material Design 3 | 15% |
| 8: Завершение | 12.05–25.05 | 17–18 | Документация, Docker, презентация | 15% |
| **ИТОГО** | 20.01–25.05 | **18 нед.** | | **100%** |

## Контрольные точки (Milestones)

| Дата | Milestone |
|------|-----------|
| 02.02.2026 | ✅ Утверждён паспорт проекта |
| 16.02.2026 | ✅ Зафиксированы требования |
| 02.03.2026 | ✅ Архитектура утверждена |
| 16.03.2026 | ✅ Схема БД готова |
| 13.04.2026 | ✅ Ядро системы реализовано |
| 27.04.2026 | ✅ Покрытие тестами >40% |
| 11.05.2026 | ✅ Мобильный интерфейс готов |
| 25.05.2026 | 🎯 Проект сдан |
