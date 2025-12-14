# Diplom_3

Тестирование приложения Stellar Burgers.
Описаны элементы, которые использованы в тестах, с помощью Page Object
Протестирована функциональность в Google Chrome и Яндекс Браузере
Подключен Allure-отчёт

Использованные технологии в проекте:
JUnit 4
REST Assured
Allure
Java 11
Selenium WebDriver
RestAssured
Allure Framework 
Maven 
WebDriverManager 


## Выбор браузера

Открыть `src/test/resources/browser.properties`
и выбрать:
1 **Для Chrome:**

testBrowser=chrome

2 **Для Яндекс.Браузера:**

testBrowser=yandex
webdriver.chrome.driver=/Users/vika/YandPrakt/yandexdriver/yandexdriver

Запуск тестов для:
Chrome:
`mvn clean test -Dbrowser=chrome`

Yandex:
`mvn clean test -Dbrowser=yandex`

Формирование Allure-отчёта:
`mvn allure:serve`
