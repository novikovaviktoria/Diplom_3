package ru.yandex.practicum.util;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.Properties;

//
public class DriverFactory {
    private static final String BROWSER_PROPERTY_FILE = "src/test/resources/browser.properties";

    public static WebDriver initDriver() {
        try {
            Properties properties = new Properties();
            try (InputStream input = new FileInputStream(BROWSER_PROPERTY_FILE)) {
                properties.load(input);
            }

            String browserProperty = properties.getProperty("testBrowser");
            System.out.println("browserProperty = " + browserProperty);

            if (browserProperty == null || browserProperty.isEmpty()) {
                throw new RuntimeException("Browser undefined. Check browser.properties file");
            }

            String browserName = browserProperty.toUpperCase();
            System.out.println("Browser name from properties: " + browserName);

            BrowserType browserType;
            try {
                browserType = BrowserType.valueOf(browserName);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid browser type: " + browserName + ". Use 'CHROME' or 'YANDEX'");
            }

            switch (browserType) {
                case CHROME:
                    return initChromeDriver();
                case YANDEX:
                    String driverPath = properties.getProperty("webdriver.chrome.driver");
                    System.out.println("Yandex driver path = " + driverPath);
                    if (driverPath != null) {
                        System.setProperty("webdriver.chrome.driver", driverPath);
                    }
                    return initYandexDriver();
                default:
                    throw new RuntimeException("Unsupported browser: " + browserName);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load browser.properties: " + e.getMessage(), e);
        }
    }

    private static WebDriver initChromeDriver() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        return new ChromeDriver(options);
    }

    private static WebDriver initYandexDriver() {
        try {
            // 1. Путь к драйверу
            String driverPath = System.getProperty("webdriver.chrome.driver",
                    "/Users/vika/YandPrakt/yandexdriver/yandexdriver");
            System.out.println("Driver path: " + driverPath);

            // 2. Проверьте драйвер
            File driverFile = new File(driverPath);
            System.out.println("Driver exists: " + driverFile.exists());
            System.out.println("Driver can execute: " + driverFile.canExecute());

            // 3. Настройки браузера
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--remote-allow-origins=*");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");

            // 4. Путь к Яндекс.Браузеру
            String yandexPath = "/Applications/Yandex.app/Contents/MacOS/Yandex";
            System.out.println("Yandex browser path: " + yandexPath);

            options.setBinary(yandexPath);

            WebDriver driver = new ChromeDriver(options);
            System.out.println("Driver created successfully!");
            return driver;

        } catch (SessionNotCreatedException e) {
            System.err.println("Session creation failed: " + e.getMessage());
            throw new RuntimeException("Failed to create WebDriver session", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize Yandex driver: " + e.getMessage(), e);
        }
    }
}