package ru.educationservices.qastellarburgers.tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.educationservices.qastellarburgers.Order;
import ru.educationservices.qastellarburgers.User;
import ru.educationservices.qastellarburgers.steps.OrderSteps;
import ru.educationservices.qastellarburgers.steps.UserSteps;

import java.util.UUID;

import static org.hamcrest.Matchers.*;

public class GetOrdersTest {

    private final UserSteps userSteps = new UserSteps();
    private final OrderSteps orderSteps = new OrderSteps();

    private String userWithFullDataAccessToken;
    private static final String PASSWORD = "TestPassword123.";
    private static final String NAME = "TestName";

    private static final String MISSING_TOKEN_ERROR = "You should be authorised";

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "https://qa-stellarburgers.education-services.ru";
        String uniqueEmail = UUID.randomUUID() + "@example.com";

        RestAssured.basePath = "/api/auth";
        User userWithFullData = new User(uniqueEmail, PASSWORD, NAME);
        userSteps.setUser(userWithFullData);
        Response createUserWithFullData = userSteps.createUser();
        userWithFullDataAccessToken = createUserWithFullData.jsonPath().getString("accessToken");

        RestAssured.basePath = "/api";
    }

    @AfterEach
    public void tearDown() {
        RestAssured.basePath = "/api/auth";
        if (userWithFullDataAccessToken != null) {
            userSteps.deleteUser(userWithFullDataAccessToken);
        }
    }

    @Test
    @DisplayName("Получение заказов c авторизацией по токену")
    public void getOrdersWithTokenTest(){
        Order order = orderSteps.prepareOrderWithIngredients(2);
        orderSteps.setOrder(order);
        Response createOrder = orderSteps.createOrderWithToken(userWithFullDataAccessToken);
        createOrder.then().assertThat()
                .statusCode(200);
        int expectedOrderNumber = createOrder.jsonPath().get("order.number");
        orderSteps.getOrdersWithToken(userWithFullDataAccessToken)
                .then()
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("orders.number", hasItem(expectedOrderNumber));
    }

    @Test
    @DisplayName("Получение заказов без авторизации по токену")
    public void getOrdersWithoutTokenTest(){
        orderSteps.getOrdersWithoutToken()
                .then()
                .assertThat()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo(MISSING_TOKEN_ERROR));
    }

    @Test
    @DisplayName("Получение последних 50 заказов без токена")
    public void getLast50OrdersWithoutTokenTest(){
        orderSteps.getLast50OrdersWithoutToken()
                .then()
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("orders", notNullValue());
    }
}
