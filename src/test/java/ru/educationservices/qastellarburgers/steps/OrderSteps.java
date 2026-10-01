package ru.educationservices.qastellarburgers.steps;

import com.google.gson.Gson;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import ru.educationservices.qastellarburgers.Order;

import java.util.ArrayList;
import java.util.List;

import static ru.educationservices.qastellarburgers.config.Config.SPEC;

public class OrderSteps {

    private Order order;

    public void setOrder(Order order){
        this.order = order;
    }

    private static final Gson gson = new Gson();

    @Step("Создать заказ с авторизацией по токену")
    public Response createOrderWithToken(String accessToken) {
        return RestAssured.given()
                .spec(SPEC)
                .header("Authorization", accessToken)
                .body(gson.toJson(order))
                .when()
                .post("/api/orders");
    }

    @Step("Создать заказ без авторизации по токену")
    public Response createOrderWithoutToken() {
        return RestAssured.given()
                .spec(SPEC)
                .body(gson.toJson(order))
                .when()
                .post("/api/orders");
    }

    @Step("Получить заказы конкретного пользователя по токену")
    public Response getOrdersWithToken(String accessToken) {
        return RestAssured.given()
                .spec(SPEC)
                .header("Authorization", accessToken)
                .when()
                .get("/api/orders");
    }

    @Step("Получить заказы конкретного пользователя без передачи токена")
    public Response getOrdersWithoutToken() {
        return RestAssured.given()
                .spec(SPEC)
                .when()
                .get("/api/orders");
    }

    @Step("Получить последние 50 заказов без токена")
    public Response getLast50OrdersWithoutToken() {
        return RestAssured.given()
                .spec(SPEC)
                .when()
                .get("/api/orders/all");
    }

    @Step("Получить ингредиенты")
    public List<String> getIngredients() {
        return RestAssured.given()
                .spec(SPEC)
                .when()
                .get("/api/ingredients")
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
