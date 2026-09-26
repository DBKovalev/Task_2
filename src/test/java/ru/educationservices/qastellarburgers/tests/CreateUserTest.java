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

public class CreateUserTest {

    private User userWithFullData;
    private String uniqueEmail;
    private final UserSteps userSteps = new UserSteps();

    private String userWithFullDataAccessToken;

    private static final String PASSWORD = "TestPassword123.";
    private static final String NAME = "TestName";

    private static final String DUPLICATE_EMAIL_ERROR = "User already exists";
    private static final String MISSING_REQUIRED_FIELDS_ERROR = "Email, password and name are required fields";

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "https://qa-stellarburgers.education-services.ru";
        RestAssured.basePath = "/api/auth";
        uniqueEmail = UUID.randomUUID() + "@example.com";
        userWithFullData = new User(uniqueEmail, PASSWORD, NAME);
    }

    @AfterEach
    public void tearDown() {
        if (userWithFullDataAccessToken != null) {
            userSteps.deleteUser(userWithFullDataAccessToken);
        }
    }

    @Test
    @DisplayName("Можно создать нового пользователя")
    public void createNewUserTest(){
        userSteps.setUser(userWithFullData);
        Response createUserWithFullData = userSteps.createUser();
        userWithFullDataAccessToken = createUserWithFullData.jsonPath().getString("accessToken");
        createUserWithFullData
                .then().assertThat()
                .statusCode(200)
                .body("accessToken", notNullValue());
    }

    @Test
    @DisplayName("Нельзя создать дубль уже существующего пользователя")
    public void createDuplicateUserTest(){
        userSteps.setUser(userWithFullData);
        Response createUserWithFullData = userSteps.createUser();
        userWithFullDataAccessToken = createUserWithFullData.jsonPath().getString("accessToken");
        createUserWithFullData
                .then().assertThat()
                .statusCode(200);
        userSteps.createUser()
                .then().assertThat()
                .statusCode(403)
                .body("message", equalTo(DUPLICATE_EMAIL_ERROR));
    }

    @Test
    @DisplayName("Нельзя создать пользователя без email")
    public void createUserWithoutEmailTest(){
        userSteps.setUser(new User("", PASSWORD, NAME));
        userSteps.createUser()
                .then().assertThat()
                .statusCode(403)
                .body("message", equalTo(MISSING_REQUIRED_FIELDS_ERROR));
    }

    @Test
    @DisplayName("Нельзя создать пользователя без пароля")
    public void createUserWithoutPasswordTest(){
        userSteps.setUser(new User(uniqueEmail,"", NAME));
        userSteps.createUser()
                .then().assertThat()
                .statusCode(403)
                .body("message", equalTo(MISSING_REQUIRED_FIELDS_ERROR));
    }

    @Test
    @DisplayName("Нельзя создать пользователя без имени")
    public void createUserWithoutFirstNameTest(){
        userSteps.setUser(new User(uniqueEmail, PASSWORD,""));
        userSteps.createUser()
                .then().assertThat()
                .statusCode(403)
                .body("message", equalTo(MISSING_REQUIRED_FIELDS_ERROR));
    }
}
