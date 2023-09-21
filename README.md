# Grandmastery

[![codecov](https://codecov.io/gh/LostHikking/grandmastery/branch/main/graph/badge.svg?token=1NT8RP2OJY)](https://codecov.io/gh/LostHikking/grandmastery)
[![example workflow](https://github.com/LostHikking/grandmastery/actions/workflows/gradle_ubuntu.yml/badge.svg)](https://github.com/LostHikking/grandmastery/actions)
[![example workflow](https://github.com/LostHikking/grandmastery/actions/workflows/gradle_windows.yml/badge.svg)](https://github.com/LostHikking/grandmastery/actions)
[![CodeFactor](https://www.codefactor.io/repository/github/losthikking/grandmastery/badge)](https://www.codefactor.io/repository/github/losthikking/grandmastery)

Grandmastery - приложение для игры в шахматы, написанное на Java.

## Linter

Для проверки кода на соответствие правилам используется
Gradle [checkstyle](https://docs.gradle.org/current/userguide/checkstyle_plugin.html) плагин и
xml [файл](./config/checkstyle/checkstyle.xml) с описанием правил.

Что бы упростить работу с проектом советуется
установить [google-java-format](https://plugins.jetbrains.com/plugin/8527-google-java-format) плагин для Idea, который
форматирует код по стандартам google. Проверить проходит ли код линтер можно командой:

Для основного кода:

```bash
./gradlew checkstyleMain
```

Для тестов:

```bash
./gradlew checkstyleTest
```

Для всего проекта:

```bash
./gradlew build
```

## Вики

Подробное описание по работе каждого модуля читать на [вики](https://github.com/LostHikking/grandmastery/wiki)

## Модули

- ### bots
    - ##### bots-factory: Фабрика ботов
    - ##### ljedmitry-bot: Боты [Димы](https://github.com/LjeDmitr)
    - ##### melniknow-bots: Боты [Серёжи](https://github.com/melniknow)
    - ##### yurkevich-bots: Боты [Матвея](https://github.com/motomoto8913)

- ### bot-farm

Модуль с бот-фермой

- ### client

Модуль для запуска клиента

- ### server

Модуль для запуска сервера

- ### game

Модуль содержащий классы с логикой игры

- ### conversation

Модуль содержащий DTO и классы для сериализации/десериализации

- ### local

Модуль для запуска локальной игры

- ### gui

Модуль содержащий реализацию графического интерфейса

- ### tui

Модуль содержащий реализацию текстового интерфейса

- ### lichess-client

Модуль содержащий lichess клиент