package ru.yandex.practicum.tests;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import net.datafaker.Faker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.practicum.page.object.Authorization;
import ru.yandex.practicum.page.object.MainPage;
import ru.yandex.practicum.page.object.Profile;
import ru.yandex.practicum.page.object.Registration;
import ru.yandex.practicum.api.user.User;
import ru.yandex.practicum.api.user.ApiUserSteps;

import java.util.Locale;

import static org.junit.Assert.*;
import static ru.yandex.practicum.util.Constants.SITE;

public class RegistrationTest extends BasementTest {

    private MainPage mainPage;
    private Registration registration;
    private Authorization authorization;
    private Profile profile;

    private User user;
    private final ApiUserSteps apiUserSteps = new ApiUserSteps();

    @Before
    public void setUp() {
        super.setUp();
        Faker faker = new Faker();

        String name = faker.name().firstName();
        String email = faker.internet().emailAddress();
        String password = faker.internet().password(6, 12);

        user = new User(email, password, name, "");

        driver.get(SITE);
        mainPage = new MainPage(driver);
    }

    @Test
    @DisplayName("Регистрация нового пользователя (через UI)")
    @Description("Регистрация через UI, логин через UI, проверка email в профиле, удаление через API")
    public void successfulRegistration() {
        // Перейти на форму регистрации
        mainPage.waitForEnterAccountButton();
        mainPage.clickEnterAccountButton();
        authorization = new Authorization(driver);
        authorization.clickRegistration();
        // Инициализируем страницу регистрации
        registration = new Registration(driver);
        // Заполнить форму и зарегистрироваться
        registration.register(user.getName(), user.getEmail(), user.getPassword());
        // После успешной регистрации ожидаем попасть на форму входа (AuthorizationPage)
        authorization = new Authorization(driver);
        // Теперь логинимся через UI, чтобы убедиться, что регистрация действительно создала учётную запись
        authorization.enterUserDetails(user.getEmail(), user.getPassword());
        authorization.clickEnterButton();
        // Индикатор успешного входа — кнопка "Оформить заказ"
        mainPage.waitForCheckoutButton();
        // Переходим в профиль и проверяем email
        mainPage.clickPersonalAccountButton();
        profile = new Profile(driver);
        profile.waitProfilePageLoad();
        assertEquals("Email в профиле не совпадает с зарегистрированным",
                user.getEmail().toLowerCase(Locale.ROOT), profile.getEmailText().toLowerCase(Locale.ROOT));
    }

    @Test
    @DisplayName("Регистрация с коротким паролем, появление ошибки")
    @Description("Попытка регистрации с паролем до 6 символов должна показать ошибку под полем 'Пароль'")
    public void registrationWithShortPasswordShowError() {
        // Перейти на форму регистрации
        mainPage.waitForEnterAccountButton();
        mainPage.clickEnterAccountButton();
        authorization = new Authorization(driver);
        authorization.clickRegistration();
        registration = new Registration(driver);
        // Создаём временные данные с коротким паролем (5 символов)
        Faker faker = new Faker();
        String name = faker.name().firstName();
        String email = faker.internet().emailAddress();
        String shortPassword = "12345";
        // Пытаемся зарегистрировать
        registration.register(name, email, shortPassword);
        // Читаем ошибку под полем пароля
        String err = registration.getPasswordErrorMessage();
        assertNotNull("Ожидалось сообщение об ошибке под полем пароля", err);
        assertFalse("Ошибка под полем пароля не должна быть пустой", err.trim().isEmpty());
        String lower = err.toLowerCase(Locale.ROOT);
        assertTrue("Ожидалось сообщение, содержащее слово 'некоррект' (например: 'Некорректный пароль'). Текст ошибки: " + err,
                lower.contains("некоррект"));
    }

    @After
    public void tearDown() {
        if (user != null) {
            try {
                ValidatableResponse loginResp = apiUserSteps.loginUser(user);
                int status = loginResp.extract().statusCode();

                if (status == 200) {
                    String token = apiUserSteps.extractAccessToken(loginResp);
                    if (token != null && !token.isEmpty()) {
                        apiUserSteps.deleteUser(token);
                    }
                } else {
                    System.out.println("User deletion skipped: login returned status " + status);
                }
            } catch (Exception e) {
                System.err.println("Failed to delete user in tearDown: " + e.getMessage());
            }
        }

        super.tearDown();
    }
}

