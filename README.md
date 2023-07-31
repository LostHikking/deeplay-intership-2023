# Grandmastery

Grandmastery - приложение для игры в шахматы, написанное на Java.

## Linter

Для проверки кода на соответствие правилам используется Gradle [checkstyle](https://docs.gradle.org/current/userguide/checkstyle_plugin.html) плагин и xml [файл](./src/main/resources/checkstyle.xml) с описанием правил.

Что бы упростить работу с проектом советуется установить [google-java-format](https://plugins.jetbrains.com/plugin/8527-google-java-format) плагин для Idea, который форматирует код по стандартам google. Проверить проходит ли код линтер можно командой:

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