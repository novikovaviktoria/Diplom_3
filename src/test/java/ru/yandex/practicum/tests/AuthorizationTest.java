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
import ru.yandex.practicum.page.object.ForgetPassword;
import ru.yandex.practicum.api.user.User;
import ru.yandex.practicum.api.user.ApiUserSteps;

import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.Assert.assertEquals;
import static ru.yandex.practicum.util.Constants.SITE;

public class AuthorizationTest extends BasementTest {

    private MainPage mainPage;
    private Registration registration;
    private Authorization authorization;
    private Profile profile;
    private ForgetPassword forgetPassword;
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
        ValidatableResponse createResponse = apiUserSteps.creationUser(user);
        createResponse.assertThat().statusCode(SC_OK);
        String accessToken = apiUserSteps.extractAccessToken(createResponse);
        user.setAccessToken(accessToken);
        driver.get(SITE);
        mainPage = new MainPage(driver);
    }

    @Test
    @DisplayName("Вход через кнопку «Войти» на главной странице")
    @Description("Регистрация пользователя через API, вход через кнопку «Войти», проверка email в профиле")
    public void enterAccountButtonTest() {
        // Ожидание и клик кнопки входа на главной
        mainPage.waitForEnterAccountButton();
        mainPage.clickEnterAccountButton();
        // Инициализация страницы авторизации
        authorization = new Authorization(driver);
        // Ввод данных и логинимся
        authorization.enterUserDetails(user.getEmail(), user.getPassword());
        authorization.clickEnterButton();
        // Ждём появления на главной страницы кнопки оформления заказа (индикатор успешного логина)
        mainPage.waitForCheckoutButton();
        // Переход в личный кабинет и проверка профиля
        mainPage.clickPersonalAccountButton();
        profile = new Profile(driver);
        profile.waitProfilePageLoad();
        assertEquals("Email в профиле не совпадает с зарегистрированным",
                user.getEmail().toLowerCase(), profile.getEmailText().toLowerCase());
    }

    @Test
    @DisplayName("Вход через кнопку «Личный кабинет»")
    @Description("Регистрация через API, клик по 'Личный кабинет', логин, проверка email в профиле")
    public void enterButtonProfileTest() {
        // Сразу кликаем по кнопке "Личный кабинет" на шапке
        mainPage.waitForPersonalAccountButton();
        mainPage.clickPersonalAccountButton();
        // На странице авторизации вводим данные и логинимся
        authorization = new Authorization(driver);
        authorization.enterUserDetails(user.getEmail(), user.getPassword());
        authorization.clickEnterButton();
        // Подтверждение успешного входа и проверка профиля
        mainPage.waitForCheckoutButton();
        mainPage.clickPersonalAccountButton();
        profile = new Profile(driver);
        profile.waitProfilePageLoad();
        assertEquals("Email в профиле не совпадает с зарегистрированным",
                user.getEmail().toLowerCase(), profile.getEmailText().toLowerCase());
    }

    @Test
    @DisplayName("Вход через ссылку в форме регистрации")
    @Description("Из регистрации можно перейти на форму входа и залогиниться")
    public void enterLinkRegistrationTest() {
        // Открытие формы входа
        mainPage.waitForEnterAccountButton();
        mainPage.clickEnterAccountButton();

        authorization = new Authorization(driver);

        // Переходим в форму регистрации и затем возвращаемся по ссылке "Уже зарегистрированы? Войти"
        authorization.clickRegistration();
        registration = new Registration(driver);
        registration.waitForPageLoad();
        registration.clickAlreadyRegisteredLink();

        // Конструктор AuthorizationPage уже проверит загрузку страницы
        authorization = new Authorization(driver);

        // Логинимся и проверяем профиль
        authorization.enterUserDetails(user.getEmail(), user.getPassword());
        authorization.clickEnterButton();

        mainPage.waitForCheckoutButton();
        mainPage.clickPersonalAccountButton();
        profile = new Profile(driver);
        profile.waitProfilePageLoad();

        assertEquals("Email в профиле не совпадает с зарегистрированным",
                user.getEmail().toLowerCase(), profile.getEmailText().toLowerCase());
    }

    @Test
    @DisplayName("Вход через ссылку в форме восстановления пароля")
    @Description("Переход из восстановления пароля на форму входа и последующий логин")
    public void enterLinkRecoverPasswordTest() {
        // Открываем форму входа
        mainPage.waitForEnterAccountButton();
        mainPage.clickEnterAccountButton();

        authorization = new Authorization(driver);

        // Переходим на страницу восстановления пароля и возвращаемся по ссылке "Войти"
        authorization.clickRecoverPassword();
        forgetPassword = new ForgetPassword(driver);
        forgetPassword.waitForPageLoad();
        forgetPassword.clickRememberedPassword();

        // Обратно на форме входа — логинимся
        authorization = new Authorization(driver);
        authorization.enterUserDetails(user.getEmail(), user.getPassword());
        authorization.clickEnterButton();

        mainPage.waitForCheckoutButton();
        mainPage.clickPersonalAccountButton();
        profile = new Profile(driver);
        profile.waitProfilePageLoad();

        assertEquals("Email в профиле не совпадает с зарегистрированным",
                user.getEmail().toLowerCase(), profile.getEmailText().toLowerCase());
    }

    @After
    public void tearDown() {
        if (user != null && user.getAccessToken() != null) {
            apiUserSteps.deleteUser(user.getAccessToken());
        }
        super.tearDown();
    }
}
