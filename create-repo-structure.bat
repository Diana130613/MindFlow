@echo off

REM Создание корневых папок
mkdir docs
mkdir src\main\java\ru\edu\project
mkdir src\main\resources
mkdir src\test\java
mkdir frontend
mkdir mobile
mkdir docker
mkdir scripts

REM Создание полной структуры документации (13 папок)
mkdir docs\00-project-charter
mkdir docs\00-project-charter\images

mkdir docs\01-requirements
mkdir docs\01-requirements\images

mkdir docs\02-architecture
mkdir docs\02-architecture\adr
mkdir docs\02-architecture\diagrams
mkdir docs\02-architecture\interfaces

mkdir docs\03-database
mkdir docs\03-database\images

mkdir docs\04-detailed-design
mkdir docs\04-detailed-design\images

mkdir docs\05-implementation

mkdir docs\06-testing
mkdir docs\06-testing\jacoco-report

mkdir docs\07-refactoring

mkdir docs\08-ui
mkdir docs\08-ui\desktop
mkdir docs\08-ui\web
mkdir docs\08-ui\mobile
mkdir docs\08-ui\enterprise

mkdir docs\09-api

mkdir docs\10-deployment
mkdir docs\10-deployment\docker
mkdir docs\10-deployment\ci-cd

mkdir docs\11-user-guide

mkdir docs\12-final-report

REM Создание основных файлов
type nul > README.md
type nul > .gitignore
type nul > pom.xml

REM Создание файлов .gitkeep для отслеживания пустых папок
for /d %%i in (docs\*) do type nul > "%%i\.gitkeep" 2>nul

echo ✅ Структура репозитория успешно создана!
pause