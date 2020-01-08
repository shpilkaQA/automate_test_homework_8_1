package ru.netology;

import com.codeborne.selenide.SelenideElement;
import lombok.val;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.junit.jupiter.api.*;

import java.sql.DriverManager;
import java.sql.SQLException;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class TestLoginIn {
    private static final String WEBSITE = "http://localhost:9999";
    private static final String URLDB = "jdbc:mysql://192.168.99.100:3306/app";
    private static final String USER = "app";
    private static final String PASSWORD = "pass";

    @AfterAll
    public static void dbClear() throws SQLException {
        val runner = new QueryRunner();
        val deleteUsers = "DELETE FROM users;";
        val deleteAuth_codes = "DELETE FROM auth_codes;";
        val deleteCards = "DELETE FROM cards;";
        val deleteCard_transactions = "DELETE FROM card_transactions;";
        try (
                val conn = DriverManager.getConnection(
                        URLDB, USER, PASSWORD
                );
        ) {
            runner.update(conn, deleteAuth_codes);
            runner.update(conn, deleteCards);
            runner.update(conn, deleteCard_transactions);
            runner.update(conn, deleteUsers);
            System.out.println("Таблицы очищены");
        }
    }
    @Test
    @DisplayName("Все поля заполнены верно")
    void validTestLoginIn() throws SQLException {
        val login = "vasya";
        val password = "qwerty123";
        open(WEBSITE);
        SelenideElement form = $(".form");
        form.$("[data-test-id=login] input").setValue(login);
        form.$("[data-test-id=password] input").setValue(password);
        form.$(".button").click();
        form.$("[data-test-id=code]").waitUntil(visible, 1000);
        form.$("[data-test-id=code] input").setValue(getAuthCode());
        form.$(".button").click();
        $("[data-test-id=dashboard]").waitUntil(visible, 1000);
    }
    @Test
    @DisplayName("Поля не заполнены")
    void shouldErrorTestOfNull() {
        open(WEBSITE);
        SelenideElement form = $(".form");
        form.$("[data-test-id=login] input").setValue("");
        form.$("[data-test-id=password] input").setValue("");
        form.$(".button").click();
        $("[data-test-id=login] span.input__sub").shouldHave(exactText("Поле обязательно для заполнения"));
        $("[data-test-id=password] span.input__sub").shouldHave(exactText("Поле обязательно для заполнения"));
    }
    @Test
    @DisplayName("Поля заполнены неверно")
    void shouldErrorTestOfLogOrPass() {
        open(WEBSITE);
        SelenideElement form = $(".form");
        form.$("[data-test-id=login] input").setValue("fghfhjhj");
        form.$("[data-test-id=password] input").setValue("123");
        form.$(".button").click();
        $("[data-test-id=error-notification]").shouldHave(text("Неверно указан логин или пароль"));
    }
    @Test
    @DisplayName("Одно из полей заполнено неверно")
    void shouldErrorTestOfPass() {
        open(WEBSITE);
        SelenideElement form = $(".form");
        form.$("[data-test-id=login] input").setValue("vasya");
        form.$("[data-test-id=password] input").setValue("123");
        form.$(".button").click();
        $("[data-test-id=error-notification]").shouldHave(text("Неверно указан логин или пароль"));
    }
    @Test
    @DisplayName("Поле 'код' заполнено неверно")
    void shouldErrorTestOfCode() {
        open(WEBSITE);
        SelenideElement form = $(".form");
        form.$("[data-test-id=login] input").setValue("vasya");
        form.$("[data-test-id=password] input").setValue("qwerty123");
        form.$(".button").click();
        form.$("[data-test-id=code] input").setValue("88888");
        form.$(".button").click();
        $("[data-test-id=error-notification]").shouldHave(text("Неверно указан код! Попробуйте ещё раз."));
    }
    public static String getAuthCode() throws SQLException {
        val userCode = "SELECT auth_codes.code FROM users JOIN auth_codes on users.id = auth_codes.user_id " +
                "WHERE users.login = 'vasya';";
        val runner = new QueryRunner();
        try (
                val conn = DriverManager.getConnection(
                        URLDB, USER, PASSWORD
                );
        ) {
            val code = runner.query(conn, userCode, new ScalarHandler<>());
            return String.valueOf(code);
        }
    }
}