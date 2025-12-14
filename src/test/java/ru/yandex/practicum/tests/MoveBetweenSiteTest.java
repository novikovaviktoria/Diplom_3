package ru.yandex.practicum.tests;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import ru.yandex.practicum.page.object.MainPage;

import static ru.yandex.practicum.util.Constants.SITE;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MoveBetweenSiteTest extends BasementTest {

    private MainPage mainPage;
    private static final String STABLE_ACTIVE_MARKER = "tab_tab_type_current";

    @Before
    public void setUp() {
        super.setUp();
        driver.get(SITE);
        mainPage = new MainPage(driver);
    }

    @Test
    @DisplayName("Переключение с раздела «Соусы» на раздел «Булки»")
    @Description("Переход между страницами Соусы и Булки. Проверка наличия active-класса")
    public void bunsActiveOnLoadTest() {

        mainPage.waitForEnterAccountButton();
        // переключаемся на соусы и ждём активного класса
        System.out.println("Before clickSauces: " + mainPage.getClassNameSauces());
        mainPage.clickSaucesAndWaitActive(); // использует default timeout
        System.out.println("After clickSauces: " + mainPage.getClassNameSauces());
        // обратно на булки и ждём
        mainPage.clickTabAndWaitStable(MainPage.BUNS_TAB, 15);
        System.out.println("After clickBuns: " + mainPage.getClassNameBuns());
        Assert.assertThat(
                "Ожидался active-класс у вкладки 'Булки'",
                mainPage.getClassNameBuns(),
                CoreMatchers.containsString(STABLE_ACTIVE_MARKER)
        );
    }

    @Test
    @DisplayName("Переключение на раздел «Соусы»")
    @Description("Переход с раздела «Булки» на раздел «Соусы». Проверка, что вкладка «Соусы» становится активной")
    public void switchSaucesTest() {
        mainPage.waitForEnterAccountButton();

        System.out.println("Before clickSauces: " + mainPage.getClassNameSauces());
        mainPage.clickTabAndWaitStable(MainPage.SAUCES_TAB, 15);
        System.out.println("After clickSauces: " + mainPage.getClassNameSauces());

        Assert.assertThat(
                "Ожидался active-класс у вкладки 'Соусы'",
                mainPage.getClassNameSauces(),
                CoreMatchers.containsString(STABLE_ACTIVE_MARKER)
        );
    }

    @Test
    @DisplayName("Переключение на раздел «Начинки»")
    @Description("Переключение на раздел «Начинки». Проверка, что вкладка «Начинки» становится активной")
    public void switchFillingsTest() {
        mainPage.waitForEnterAccountButton();

        System.out.println("Before clickFillings: " + mainPage.getClassNameFillings());
        mainPage.clickTabAndWaitStable(MainPage.FILLINGS_TAB, 15);
        System.out.println("After clickFillings: " + mainPage.getClassNameFillings());

        Assert.assertThat(
                "Ожидался active-класс у вкладки 'Начинки'",
                mainPage.getClassNameFillings(),
                CoreMatchers.containsString(STABLE_ACTIVE_MARKER)
        );
    }
}
