package com.geekbrains.tests.automator

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until
import com.geekbrains.tests.*
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SdkSuppress(minSdkVersion = MIN_SDK_VALUE)
class BehaviorTest {

    //Класс UiDevice предоставляет доступ к вашему устройству.
    //Именно через UiDevice вы можете управлять устройством, открывать приложения
    //и находить нужные элементы на экране
    private val uiDevice: UiDevice = UiDevice.getInstance(getInstrumentation())

    //Контекст нам понадобится для запуска нужных экранов и получения packageName
    private val context = ApplicationProvider.getApplicationContext<Context>()

    //Путь к классам нашего приложения, которые мы будем тестировать
    private val packageName = context.packageName

    @Before
    fun setup() {
        //Для начала сворачиваем все приложения, если у нас что-то запущено
        uiDevice.pressHome()

        //Запускаем наше приложение
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
        //Мы уже проверяли Интент на null в предыдущем тесте, поэтому допускаем, что Интент у нас не null
        intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)//Чистим бэкстек от запущенных ранее Активити
        context.startActivity(intent)

        //Ждем, когда приложение откроется на смартфоне чтобы начать тестировать его элементы
        uiDevice.wait(Until.hasObject(By.pkg(packageName).depth(ZERO_INT_VALUE)), DELAY_TIME)
    }

    //Убеждаемся, что приложение открыто. Для этого достаточно найти на экране любой элемент
    //и проверить его на null
    @Test
    fun test_MainActivityIsStarted() {
        //Через uiDevice находим editText
        val editText = uiDevice.findObject(By.res(packageName, SEARCH_EDIT_TEXT_TEXT))
        //Проверяем на null
        Assert.assertNotNull(editText)
        Assert.assertNull(
            uiDevice.findObject(
                By.res(packageName, "$SEARCH_EDIT_TEXT_TEXT$ONE_INT_VALUE")
            )
        )
    }

    //Убеждаемся, что поиск работает как ожидается
    @Test
    fun test_SearchIsPositive() {
        val editText = uiDevice.findObject(By.res(packageName, SEARCH_EDIT_TEXT_TEXT))
        //Устанавливаем значение
        editText.text = UI_AUTOMATOR_TEXT
        //Отправляем запрос через UiAutomator
        // Пишем заглавные буквы, потому что у кнопки по-умолчанию
        // стоит атрибут android:textAllCaps="true"
//        uiDevice.findObject(UiSelector().textMatches("ПОИСК РЕПОЗИТОРИЕВ")).click()
        //Находим кнопку с запуском поиска информации
        val toDetails: UiObject2 = uiDevice.findObject(
            By.res(packageName, TO_SEARCH_ACTIVITY_BUTTON_TEXT)
        )
        //Кликаем по кнопке поиска информации
        toDetails.click()

        //Ожидаем конкретного события: появления текстового поля totalCountTextView.
        //Это будет означать, что сервер вернул ответ с какими-то данными,
        // то есть запрос отработал.
        val changedText = uiDevice.wait(
            Until.findObject(By.res(packageName, TOTAL_COUNT_TEXT_VIEW_TEXT)), DELAY_TIME
        )
        //Убеждаемся, что сервер вернул корректный результат. Обратите внимание, что количество
        //результатов может варьироваться во времени,
        // потому что количество репозиториев постоянно меняется.
        Assert.assertEquals(changedText.text.toString(), RESULT_UIAUTOMATOR_REPOSITORY_TEXT)
        Assert.assertNotEquals(
            changedText.text.toString(),
            "$RESULT_UIAUTOMATOR_REPOSITORY_TEXT$ONE_INT_VALUE"
        )
    }

    //Убеждаемся, что DetailsScreen открывается
    @Test
    fun test_OpenDetailsScreen() {
        //Находим кнопку
        val toDetails: UiObject2 = uiDevice.findObject(
            By.res(
                packageName,
                TO_DETAILS_ACTIVITY_BUTTON_TEXT
            )
        )
        //Кликаем по ней
        toDetails.click()

        //Ожидаем конкретного события: появления текстового поля totalCountTextView.
        //Это будет означать, что DetailsScreen открылся и это поле видно на экране.
        val changedText =
            uiDevice.wait(
                Until.findObject(By.res(packageName, TOTAL_COUNT_TEXT_VIEW_TEXT)),
                DELAY_TIME
            )
        //Убеждаемся, что поле видно и содержит предполагаемый текст.
        //Обратите внимание, что текст должен быть "Number of results: 0",
        //так как мы кликаем по кнопке не отправляя никаких поисковых запросов.
        //Чтобы проверить отображение определенного количества репозиториев,
        //вам в одном и том же методе нужно отправить запрос на сервер и открыть DetailsScreen.
        Assert.assertEquals(changedText.text, RESULT_ZERO_REPOSITORY_TEXT)
        Assert.assertNotEquals(changedText.text, RESULT_ONE_REPOSITORY_TEXT)
    }

    @Test // Проверка корректности работы системной кнопки back()
    fun activity_CheckWorkBackButton() {
        //Находим кнопку для перехода на DetailsActivity
        val toDetails = uiDevice.findObject(By.res(packageName, TO_DETAILS_ACTIVITY_BUTTON_TEXT))
        //Кликаем по ней
        toDetails.click()
        // Проверяем, что отображается DetailsActivity
        Assert.assertNotNull(
            uiDevice
                .wait(Until.findObject(By.text(RESULT_ZERO_REPOSITORY_TEXT)), DELAY_TIME)
        )
        Assert.assertNull(
            uiDevice
                .wait(Until.findObject(By.text(RESULT_ONE_REPOSITORY_TEXT)), DELAY_TIME)
        )
        // Кликаем на системную кнопку back()
        uiDevice.pressBack()
        // Проверяем, что отображается MainActivity
        Assert.assertNotNull(uiDevice.findObject(By.text(SEARCH_REPOSITORIES_TEXT)))
        Assert.assertNull(
            uiDevice
                .findObject(By.text("$SEARCH_REPOSITORIES_TEXT$ONE_INT_VALUE"))
        )
    }

    @Test // Проверка наличия элементов на экране MainActivity
    fun activity_ExistsElementsOnMainActivity() {
        // Проверяем, что отображается элемент с текстом подсказки
        Assert.assertNotNull(uiDevice.findObject(By.text(HINT_TEXT)))
        Assert.assertNull(uiDevice.findObject(By.text("$HINT_TEXT$ONE_INT_VALUE")))
        // Проверяем, что отображается кнопка с поиском репозиториев
        Assert.assertNotNull(uiDevice.findObject(By.text(SEARCH_REPOSITORIES_TEXT)))
        Assert.assertNull(
            uiDevice
                .findObject(By.text("$SEARCH_REPOSITORIES_TEXT$ONE_INT_VALUE"))
        )
        // Проверяем, что отображается кнопка с детализацией запроса
        Assert.assertNotNull(uiDevice.findObject(By.text(DETAILED_QUERY_TEXT)))
        Assert.assertNull(
            uiDevice
                .findObject(By.text("$DETAILED_QUERY_TEXT$ONE_INT_VALUE"))
        )
    }

    @Test
    fun activity_ExistsElementsOnDetailsActivity() {
        val toDetails = uiDevice.wait(
            Until.findObject(
                By.res(packageName, TO_DETAILS_ACTIVITY_BUTTON_TEXT)
            ), DELAY_TIME
        )
        toDetails.click()
        Assert.assertNotNull(
            uiDevice.wait(
                Until.findObject(By.res(packageName, DECREMENT_BUTTON_TEXT)), DELAY_TIME
            )
        )
        Assert.assertNull(
            uiDevice.wait(
                Until.findObject(
                    By.res(
                        packageName, "$DECREMENT_BUTTON_TEXT$ONE_INT_VALUE"
                    )
                ), DELAY_TIME
            )
        )
        Assert.assertNotNull(uiDevice.findObject(By.text(RESULT_ZERO_REPOSITORY_TEXT)))
        Assert.assertNull(uiDevice.findObject(By.text(RESULT_ONE_REPOSITORY_TEXT)))
        Assert.assertNotNull(
            uiDevice.wait(
                Until.findObject(By.res(packageName, INCREMENT_BUTTON_TEXT)), DELAY_TIME
            )
        )
        Assert.assertNull(
            uiDevice.wait(
                Until.findObject(
                    By.res(
                        packageName,
                        "$INCREMENT_BUTTON_TEXT$ONE_INT_VALUE"
                    )
                ), DELAY_TIME
            )
        )
    }

    @Test
    fun activity_DetailsScreenButtonsIsWorking() {
        val toDetails = uiDevice.wait(
            Until.findObject(
                By.res(packageName, TO_DETAILS_ACTIVITY_BUTTON_TEXT)
            ), DELAY_TIME
        )
        toDetails.click()
        val decrementButton = uiDevice.wait(
            Until.findObject(
                By.res(packageName, DECREMENT_BUTTON_TEXT)
            ), DELAY_TIME
        )
        val incrementButton = uiDevice.wait(
            Until.findObject(
                By.res(packageName, INCREMENT_BUTTON_TEXT)
            ), DELAY_TIME
        )
        incrementButton.click()
        Assert.assertNotNull(uiDevice.findObject(By.text(RESULT_ONE_REPOSITORY_TEXT)))
        Assert.assertNull(uiDevice.findObject(By.text(RESULT_ZERO_REPOSITORY_TEXT)))
        decrementButton.click()
        Assert.assertNotNull(uiDevice.findObject(By.text(RESULT_ZERO_REPOSITORY_TEXT)))
        Assert.assertNull(uiDevice.findObject(By.text(RESULT_ONE_REPOSITORY_TEXT)))
    }

    @Test
    fun activity_SuccessInsertText() {
        val editText = uiDevice.findObject(By.res(packageName, SEARCH_EDIT_TEXT_TEXT))
        editText.text = MY_REPO_NAME_TEXT
        Assert.assertNotNull(uiDevice.findObject(By.text(MY_REPO_NAME_TEXT)))
        Assert.assertNull(uiDevice.findObject(By.text("$MY_REPO_NAME_TEXT$ONE_INT_VALUE")))
    }

    @Test
    fun activity_CorrectShowRepositoryName() {
        val editText = uiDevice.findObject(By.res(packageName, SEARCH_EDIT_TEXT_TEXT))
        editText.text = MY_REPO_NAME_TEXT
        var toDetails: UiObject2 = uiDevice.wait(
            Until.findObject(
                By.res(packageName, TO_SEARCH_ACTIVITY_BUTTON_TEXT)
            ), DELAY_TIME
        )
        toDetails.click()
        editText.text = ""
        Assert.assertNotNull(
            uiDevice.wait(
                Until.findObject(By.text(MY_REPO_RESULT_NAME_TEXT)), DELAY_TIME
            )
        )
        Assert.assertNull(
            uiDevice
                .wait(Until.findObject(By.text(MY_REPO_NAME_TEXT)), DELAY_TIME)
        )
    }
}

