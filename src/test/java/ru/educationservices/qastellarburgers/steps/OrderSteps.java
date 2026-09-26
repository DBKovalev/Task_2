package ru.educationservices.qastellarburgers.steps;

import com.google.gson.Gson;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import ru.educationservices.qastellarburgers.Order;

import java.util.ArrayList;
import java.util.List;

public class OrderSteps {

    private Order order;

    public void setOrder(Order order){
        this.order = order;
    }

    private static final Gson gson = new Gson();

    @Step("Создать заказ с авторизацией по токену")
    public Response createOrderWithToken(String accessToken) {
        return RestAssured.given()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .body(gson.toJson(order))
                .when()
                .post("/orders");
    }

    @Step("Создать заказ без авторизации по токену")
    public Response createOrderWithoutToken() {
        return RestAssured.given()
                .header("Content-type", "application/json")
                .body(gson.toJson(order))
                .when()
                .post("/orders");
    }

    @Step("Получить заказы конкретного пользователя по токену")
    public Response getOrdersWithToken(String accessToken) {
        return RestAssured.given()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .when()
                .get("/orders");
    }

    @Step("Получить заказы конкретного пользователя без передачи токена")
    public Response getOrdersWithoutToken() {
        return RestAssured.given()
                .header("Content-type", "application/json")
                .when()
                .get("/orders");
    }

    @Step("Получить последние 50 заказов без токена")
    public Response getLast50OrdersWithoutToken() {
        return RestAssured.given()
                .header("Content-type", "application/json")
                .when()
                .get("/orders/all");
    }

    @Step("Получить ингредиенты")
    public List<String> getIngredients() {
        return RestAssured.given()
                .header("Content-type", "application/json")
                .when()
                .get("/ingredients")
                .then()
                .extract()
                .jsonPath()
                .getList("data._id", String.class);
    }

    @Step("Создать тестовый заказ из {count} ингредиентов")
    public Order prepareOrderWithIngredients(int count) {
        List<String> allIngredients = getIngredients();
        int actualCount = Math.min(allIngredients.size(), count);
        List<String> ingredients = new ArrayList<>(allIngredients.subList(0, actualCount));
        return new Order(ingredients);
    }

    @Step("Создать тестовый заказ с невалидным id ингредиента")
    public Order prepareOrderWithInvalidIdOfIngredients() {
        List<String> allIngredients = getIngredients();
        String valid = allIngredients.get(0);
        String invalid = valid.substring(0, Math.min(10, valid.length())) + "INVALID";
        return new Order(List.of(invalid));
    }
}
