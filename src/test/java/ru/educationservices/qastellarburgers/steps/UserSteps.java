package ru.educationservices.qastellarburgers.steps;

import com.google.gson.Gson;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import ru.educationservices.qastellarburgers.User;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;
import static ru.educationservices.qastellarburgers.config.Config.SPEC;

public class UserSteps {

    private User user;

    private static final Gson gson = new Gson();

    public void setUser(User user) {
        this.user = user;
    }

    @Step("Создать пользователя")
    public Response createUser() {
        return RestAssured.given()
                .spec(SPEC)
                .body(gson.toJson(user))
                .when()
                .post("/api/auth/register");
    }

    @Step("Логин пользователя")
    public Response loginUser() {
        return RestAssured.given()
                .spec(SPEC)
                .body(gson.toJson(user))
                .when()
                .post("/api/auth/login");
    }

    @Step("Обновить переданные поля пользователя с передачей токена")
    public Response updateUserWithToken(String accessToken, User updatedFields) {
        return RestAssured.given()
                .spec(SPEC)
                .header("Authorization", accessToken)
                .body(gson.toJson(updatedFields))
                .when()
                .patch("/api/auth/user");
    }

    @Step("Обновить переданные поля пользователя без передачи токена")
    public Response updateUserWithoutToken(User updatedFields) {
        return RestAssured.given()
                .spec(SPEC)
                .body(gson.toJson(updatedFields))
                .when()
                .patch("/api/auth/user");
    }

    @Step("Удалить пользователя по токену")
    public void deleteUser(String accessToken) {
        if (accessToken == null) {
            return;
        }
        RestAssured.given()
                .spec(SPEC)
                .header("Authorization", accessToken)
                .when()
                .delete("/api/auth/user")
                .then().assertThat()
                .statusCode(anyOf(equalTo(200), equalTo(202)));
    }
}