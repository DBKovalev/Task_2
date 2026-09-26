package ru.educationservices.qastellarburgers.tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.educationservices.qastellarburgers.User;
import ru.educationservices.qastellarburgers.steps.UserSteps;

import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class LoginUserTest {

    private User userWithFullData;
    private String uniqueEmail;
    private String uniqueWrongEmail;
    private final UserSteps userSteps = new UserSteps();

    private String userWithFullDataAccessToken;

    private static final String PASSWORD = "TestPassword123.";
    private static final String WRONG_PASSWORD = "WrongTestPassword123.";
    private static final String NAME = "TestName";

    private static final String MISSING_CREDENTIALS_ERROR = "email or password are incorrect";

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "https://qa-stellarburgers.education-services.ru";
        RestAssured.basePath = "/api/auth";
        uniqueEmail = UUID.randomUUID() + "@example.com";
        uniqueWrongEmail = UUID.randomUUID() + "@example.com";
        User userWithFullData = new User(uniqueEmail, PASSWORD, NAME);
        userSteps.setUser(userWithFullData);
        Response createUserWithFullData = userSteps.createUser();
        userWithFullDataAccessToken = createUserWithFullData.jsonPath().getString("accessToken");
    }

    @AfterEach
    public void tearDown() {
        if (userWithFullDataAccessToken != null) {
            userSteps.deleteUser(userWithFullDataAccessToken);
        }
    }

    @Test
    @DisplayName("Пользователь может авторизоваться")
    public void loginValidUserTest(){
        userSteps.loginUser()
                .then()
                .assertThat()
                .statusCode(200)
                .body("accessToken", notNullValue());
    }

    @Test
    @DisplayName("Нельзя залогинить пользователя без email")
    public void loginUserWithoutLoginTest() {
        userSteps.setUser(new User("", PASSWORD));
        userSteps.loginUser()
                .then()
                .assertThat()
                .statusCode(401)
                .body("message", equalTo(MISSING_CREDENTIALS_ERROR));
    }

    @Test
    @DisplayName("Нельзя залогинить пользователя без пароля")
    public void loginUserWithoutPasswordTest(){
        userSteps.setUser(new User(uniqueEmail,""));
        userSteps.loginUser()
                .then()
                .assertThat()
                .statusCode(401)
                .body("message", equalTo(MISSING_CREDENTIALS_ERROR));
    }

    @Test
    @DisplayName("Нельзя залогинить пользователя с неверным email")
    public void loginUserWithWrongLoginTest(){
        userSteps.setUser(new User(uniqueWrongEmail, PASSWORD));
        userSteps.loginUser()
                .then()
                .assertThat()
                .statusCode(401)
                .body("message", equalTo(MISSING_CREDENTIALS_ERROR));
    }

    @Test
    @DisplayName("Нельзя залогинить пользователя с неверным паролем")
    public void loginUserWithWrongPasswordTest(){
        userSteps.setUser(new User(uniqueEmail, WRONG_PASSWORD));
        userSteps.loginUser()
                .then()
                .assertThat()
                .statusCode(401)
                .body("message", equalTo(MISSING_CREDENTIALS_ERROR));
    }

    @Test
    @DisplayName("Нельзя залогинить несуществующего пользователя")
    public void loginUnknownUserTest(){
        userSteps.setUser(new User(uniqueWrongEmail, WRONG_PASSWORD));
        userSteps.loginUser()
                .then()
                .assertThat()
                .statusCode(401)
                .body("message", equalTo(MISSING_CREDENTIALS_ERROR));
    }
}
