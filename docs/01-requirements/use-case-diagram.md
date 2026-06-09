# USE CASE ДИАГРАММА

## Системные прецеденты MindFlow

### PlantUML-диаграмма

```plantuml
@startuml
left to right direction
skinparam packageStyle rectangle
skinparam usecase {
  BackgroundColor LavenderBlush
  BorderColor DarkViolet
}

actor "Пользователь" as User
actor "Администратор" as Admin
actor "Сервер (Backend)" as Server

rectangle "MindFlow - Система ментального здоровья" {
  usecase "UC-01: Регистрация" as UC01
  usecase "UC-02: Вход в систему" as UC02
  usecase "UC-03: Просмотр списка медитаций" as UC03
  usecase "UC-04: Запуск сессии медитации" as UC04
  usecase "UC-05: Записать настроение" as UC05
  usecase "UC-06: Просмотр аналитики" as UC06
  usecase "UC-07: Управление профилем" as UC07
  usecase "UC-08: Работа офлайн" as UC08
  usecase "UC-09: Управление контентом" as UC09
  usecase "UC-10: Управление пользователями" as UC10
  usecase "UC-11: Дыхательное упражнение" as UC11
  usecase "UC-12: Просмотр прогресса" as UC12
}

User --> UC01
User --> UC02
User --> UC03
User --> UC04
User --> UC05
User --> UC06
User --> UC07
User --> UC11
User --> UC12

UC04 ..> UC08 : <<include>>
UC03 ..> UC08 : <<include>>

Admin --> UC09
Admin --> UC10
Admin --> UC02

UC02 ..> Server : <<include>>
UC04 ..> Server : <<include>>
UC05 ..> Server : <<include>>

@enduml
```

## Список прецедентов

| ID | Название | Актор | Приоритет |
| :--- | :--- | :--- | :--- |
| UC-01 | Регистрация | Пользователь | Высокий |
| UC-02 | Вход в систему (JWT) | Пользователь, Администратор | Высокий |
| UC-03 | Просмотр списка медитаций | Пользователь | Высокий |
| UC-04 | Запуск сессии медитации | Пользователь | Высокий |
| UC-05 | Запись настроения в дневник | Пользователь | Высокий |
| UC-06 | Просмотр аналитики настроения | Пользователь | Средний |
| UC-07 | Управление профилем | Пользователь | Средний |
| UC-08 | Работа в офлайн-режиме | Пользователь | Высокий |
| UC-09 | Управление контентом | Администратор | Средний |
| UC-10 | Управление пользователями | Администратор | Средний |
| UC-11 | Дыхательное упражнение | Пользователь | Средний |
| UC-12 | Просмотр прогресса | Пользователь | Средний |