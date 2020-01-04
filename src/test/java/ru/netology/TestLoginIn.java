package ru.netology;

import com.codeborne.selenide.SelenideElement;
import com.github.javafaker.Faker;
import jdk.nashorn.internal.runtime.Debug;
import lombok.val;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.junit.jupiter.api.*;
import ru.netology.mode.User;

import java.sql.DriverManager;
import java.sql.SQLException;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class TestLoginIn {
    /*@BeforeEach
    void setUp() throws SQLException {
        val faker = new Faker();
        val runner = new QueryRunner();
        val dataSQL = "INSERT INTO users(id, login, password) VALUES (?, ?, ?);";
        val codeSQL = "INSERT INTO auth_codes(id, user_id, code) VALUES (?, ?, ?);";

        try (
                val conn = DriverManager.getConnection(
                        "jdbc:mysql://192.168.99.100:3306/app", "app", "pass"
                );
        ) {
            // обычная вставка
            runner.update(conn, dataSQL, faker.random().nextInt(0,2000000), faker.name().username(), "pass");
            runner.update(conn, codeSQL, faker.random().nextInt(0,2000000), ,
                    faker.random().nextInt(0, 2000000));
        }
    }*/

    @AfterAll
    public static void dbClear() throws SQLException {
        val runner = new QueryRunner();
//        val deleteUsers = "TRUNCATE TABLE users;"; //Очищаем таблицу users
        val deleteUsers = "DELETE FROM users;"; //Очищаем таблицу users
        val deleteAuth_codes = "DELETE FROM auth_codes;";
        val deleteCards = "DELETE FROM cards;";
        val deleteCard_transactions = "DELETE FROM card_transactions;";
        try (
                val conn = DriverManager.getConnection(
                        "jdbc:mysql://192.168.99.100:3306/app", "app", "pass"
                );
        ) {
            runner.update(conn, deleteUsers);
            runner.update(conn, deleteAuth_codes);
            runner.update(conn, deleteCards);
            runner.update(conn, deleteCard_transactions);
        }
    }

    @Test
    void stubTest() throws SQLException {
        val userCode = "SELECT auth_codes.code FROM users JOIN auth_codes on users.id = auth_codes.user_id " +
                "WHERE users.login = ?";
        val usersSQL = "SELECT users.id, users.login, users.password, auth_codes.code " +
                "FROM users JOIN auth_codes on users.id = auth_codes.user_id GROUP BY users.id, auth_codes.code";
        val runner = new QueryRunner();

        try (
                val conn = DriverManager.getConnection(
                        "jdbc:mysql://192.168.99.100:3306/app", "app", "pass"
                );
        ) {

            val firstUser = runner.query(conn, usersSQL, new BeanHandler<>(User.class));
            System.out.println(firstUser);


        }

        /*open("http://localhost:9999");
        SelenideElement form = $(".form");
        form.$("[data-test-id=name] input").setValue("ПроРоолл ОллЛлл-олдд");
        form.$("[data-test-id=phone] input").setValue("+70000000000");
        form.$("[data-test-id=agreement]").click();
        form.$(".button").click();
        $("[data-test-id=order-success]").shouldHave(exactText("  Ваша заявка успешно отправлена! " +
                "Наш менеджер свяжется с вами в ближайшее время."));*/

    }
}