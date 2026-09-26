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

public class CreateOrderTest {

    private final UserSteps userSteps = new UserSteps();
    private final OrderSteps orderSteps = new OrderSteps();

    private String userWithFullDataAccessToken;
    private static final String PASSWORD = "TestPassword123.";
    private static final String NAME = "TestName";

    private static final String MISSING_INGREDIENTS_ERROR = "Ingredient ids must be provided";

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
    @DisplayName("Создание заказа c ингредиентами и авторизацией по токену")
    public void createOrderWithIngredientsAndWithTokenTest(){
        Order order = orderSteps.prepareOrderWithIngredients(2);
        orderSteps.setOrder(order);
        Response createOrder = orderSteps.createOrderWithToken(userWithFullDataAccessToken);
        createOrder.then().assertThat()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа с ингредиентами и без авторизации по токену")
    public void createOrderWithIngredientsAndWithoutTokenTest(){
        Order order = orderSteps.prepareOrderWithIngredients(2);
        orderSteps.setOrder(order);
        Response createOrder = orderSteps.createOrderWithoutToken();
        createOrder.then().assertThat()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов и авторизацией по токену")
    public void createOrderWithoutIngredientsAndWithTokenTest(){
        Order order = orderSteps.prepareOrderWithIngredients(0);
        orderSteps.setOrder(order);
        Response createOrder = orderSteps.createOrderWithToken(userWithFullDataAccessToken);
        createOrder.then().assertThat()
                .statusCode(400)
                .body("success", equalTo(false))
                .body("message", equalTo(MISSING_INGREDIENTS_ERROR));
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов и без авторизации по токену")
    public void createOrderWithoutIngredientsAndWithoutTokenTest(){
        Order order = orderSteps.prepareOrderWithIngredients(0);
        orderSteps.setOrder(order);
        Response createOrder = orderSteps.createOrderWithoutToken();
        createOrder.then().assertThat()
                .statusCode(400)
                .body("success", equalTo(false))
                .body("message", equalTo(MISSING_INGREDIENTS_ERROR));
    }

    @Test
    @DisplayName("Создание заказа c невалидным хэшем ингредиента и авторизацией по токену")
    public void createOrderWithInvalidIngredientsAndWithTokenTest(){
        Order order = orderSteps.prepareOrderWithInvalidIdOfIngredients();
        orderSteps.setOrder(order);
        Response createOrder = orderSteps.createOrderWithToken(userWithFullDataAccessToken);
        createOrder.then().assertThat()
                .statusCode(500);
    }

    @Test
    @DisplayName("Создание заказа с невалидным хэшем ингредиента и без авторизации по токену")
    public void createOrderWithInvalidIngredientsAndWithoutTokenTest(){
        Order order = orderSteps.prepareOrderWithInvalidIdOfIngredients();
        orderSteps.setOrder(order);
        Response createOrder = orderSteps.createOrderWithoutToken();
        createOrder.then().assertThat()
                .statusCode(500);
    }
}
