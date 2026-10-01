# Task_2
## Описание
Stellar Burgers https://qa-stellarburgers.education-services.ru/

Тестирование API-ручек создания, логина, изменения пользователя, содания, получения заказов.

## Стек
**Инструменты сборки и запуска:**
- Maven — система сборки и управления зависимостями
- Maven Surefire Plugin — запуск тестов, интеграция AspectJ Weaver для перехвата аннотаций Allure
- Allure Maven Plugin — генерация HTML‑отчётов Allure из Maven

**Тестирование:**
- JUnit 5 (Jupiter) — фреймворк тестирования (через junit-bom)
- Hamcrest — матчеры для ассертов (equalTo, notNullValue, containsString и т. д.)
- REST Assured — библиотека для HTTP‑запросов и проверки ответов REST API
- Allure REST Assured — интеграция, логирует запросы и ответы в отчёт Allure

**Отчётность:**
- Allure Framework (через allure-bom) — генерация тестовых отчётов
- Allure JUnit 5 — связка Allure с JUnit 5 (аннотации @Step, @DisplayName, вложения)
- AspectJ Weaver — рантайм-перехват аннотаций Allure (через javaagent в Surefire)

**Сериализация:**
- Gson — (не)сериализация JSON. Хотя REST Assured использует свой встроенный Jackson/Gson, явная зависимость может быть для ручной работы с JSON в тестах



## Запуск тестов
### Реализация запуска всех тестов и построения отчета
```bash
mvn clean test allure:serve
```

### Реализация запуска тестов по одному и построения отчета
#### CreateUserTest
```bash
mvn clean test -Dtest=CreateUserTest allure:serve
```

#### LoginUserTest
```bash
mvn clean test -Dtest=LoginUserTest allure:serve
```

#### UpdateUserTest
```bash
mvn clean test -Dtest=UpdateUserTest allure:serve
```

#### GetOrdersTest
```bash
mvn clean test -Dtest=GetOrdersTest allure:serve
```

#### CreateOrderTest
```bash
mvn clean test -Dtest=CreateOrderTest allure:serve
```



