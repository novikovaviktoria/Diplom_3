package ru.yandex.practicum.page.object;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class ForgetPassword {

    private final WebDriver driver;
    private final WebDriverWait defaultWait;

    private static final Duration DEFAULT = Duration.ofSeconds(5);
    private static final By RESTORE_FORM_PASSWORD = By.xpath(".//*[text()='Восстановление пароля']");
    private static final By FORGETABLE_PASSWORD_LINK = By.xpath(".//a[text()='Войти']");

    public ForgetPassword(WebDriver driver) {
        this.driver = driver;
        this.defaultWait = new WebDriverWait(driver, DEFAULT);
        waitForPageLoad();
    }

    private WebElement waitVisible(By locator) {
        return defaultWait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    private WebElement waitClickable(By locator) {
        return defaultWait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    private void click(By locator) {
        waitClickable(locator).click();
    }

    @Step("Ожидание загрузки страницы «Восстановление пароля»")
    public ForgetPassword waitForPageLoad() {
        waitVisible(RESTORE_FORM_PASSWORD);
        return this;
    }

    @Step("Нажатие по кнопке «Войти»")
    public ForgetPassword clickRememberedPassword() {
        click(FORGETABLE_PASSWORD_LINK);
        return this;
    }
}
