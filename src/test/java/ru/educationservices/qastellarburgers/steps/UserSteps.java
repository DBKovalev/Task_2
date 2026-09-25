package ru.educationservices.qastellarburgers.steps;

import com.google.gson.Gson;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import ru.educationservices.qastellarburgers.User;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;

public class UserSteps {

    private User user;

    private static final Gson gson = new Gson();

    public void setUser(User user) {
        this.user = user;
    }

    @Step("Создать пользователя")
    public Response createUser() {
        return RestAssured.given()
                .header("Content-type", "application/json")
                .body(gson.toJson(user))
                .when()
                .post("/register");
    }

    @Step("Логин пользователя")
    public Response loginUser() {
        return RestAssured.given()
                .header("Content-type", "application/json")
                .body(gson.toJson(user))
                .when()
                .post("/login");
    }

    @Step("Обновление полей пользователя с заданным токеном")
    public Response updateUserWithToken(String accessToken, User updatedFields) {
        return RestAssured.given()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .body(gson.toJson(updatedFields))
                .when()
                .patch("/user");
    }

    @Step("Обновление полей пользователя без токена")
    public Response updateUserWithoutToken(User updatedFields) {
        return RestAssured.given()
                .header("Content-type", "application/json")
                .body(gson.toJson(updatedFields))
                .when()
                .patch("/user");
    }

    @Step("Удалить пользователя по токену")
    public void deleteUser(String accessToken) {
        if (accessToken == null) {
            return;
        }
        RestAssured.given()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .when()
                .delete("/user")
                .then().assertThat()
                .statusCode(anyOf(equalTo(200), equalTo(202)));
    }

    @Step("Удалить пользователя")
    public void deleteUser() {
        Response loginResponse = loginUser();
        if (loginResponse.getStatusCode() != 200) {
            return;
        }
        String token = loginResponse.path("accessToken");
        deleteUser(token);
    }
}