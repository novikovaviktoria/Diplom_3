package ru.yandex.practicum.tests;

import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.WebDriver;
import ru.yandex.practicum.util.DriverFactory;

import static ru.yandex.practicum.util.Constants.SITE;

public class BasementTest {

    protected WebDriver driver;

    @Before
    public void setUp() {
        driver = DriverFactory.initDriver();
        driver.get(SITE);
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
