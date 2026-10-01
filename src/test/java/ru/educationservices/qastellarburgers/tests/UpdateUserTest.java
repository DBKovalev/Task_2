package ru.educationservices.qastellarburgers.tests;

import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.educationservices.qastellarburgers.User;
import ru.educationservices.qastellarburgers.steps.UserSteps;

import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;

public class UpdateUserTest {

    private String ununiqueEmail;
    private final UserSteps userSteps = new UserSteps();

    private String secondUserAccessToken;
    private String userWithFullDataAccessToken;

    private static final String PASSWORD = "TestPassword123.";
    private static final String UPDATED_PASSWORD = "NewTestPassword456.";
    private static final String NAME = "TestName";
    private static final String UPDATED_NAME = "UpdatedName";

    private static final String MISSING_TOKEN_ERROR = "You should be authorised";
    private static final String UNUNIQUE_EMAIL_ERROR = "User with such email already exists";

    @BeforeEach
    public void setUp() {
        String uniqueEmail = UUID.randomUUID() + "@example.com";
        ununiqueEmail = UUID.randomUUID() + "@example.com";

        User secondUser = new User(ununiqueEmail, PASSWORD, NAME);
        userSteps.setUser(secondUser);
        Response createSecondUser = userSteps.createUser();
        secondUserAccessToken = createSecondUser.jsonPath().getString("accessToken");

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
        if (secondUserAccessToken != null) {
            userSteps.deleteUser(secondUserAccessToken);
        }
    }

    @Test
    @DisplayName("Авторизованный пользователь может изменить email на уникальный")
    public void updateEmailToUniqueWithTokenTest(){
        String newEmail = UUID.randomUUID() + "@example.com";
        User updatedUser = new User(newEmail, null, null);
        userSteps.updateUserWithToken(userWithFullDataAccessToken, updatedUser)
                .then()
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(newEmail));
    }

    @Test
    @DisplayName("Авторизованный пользователь не может изменить email на неуникальный")
    public void updateEmailToUnuniqueWithTokenTest(){
        User updatedUser = new User(ununiqueEmail, null, null);
        userSteps.updateUserWithToken(userWithFullDataAccessToken, updatedUser)
                .then()
                .assertThat()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo(UNUNIQUE_EMAIL_ERROR));
    }

    @Test
    @DisplayName("Авторизованный пользователь может изменить пароль")
    public void updatePasswordWithTokenTest(){
        User updatedUser = new User(null, UPDATED_PASSWORD, null);
        userSteps.updateUserWithToken(userWithFullDataAccessToken, updatedUser)
                .then()
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Авторизованный пользователь может изменить имя")
    public void updateNameWithTokenTest(){
        User updatedUser = new User(null, null, UPDATED_NAME);
        userSteps.updateUserWithToken(userWithFullDataAccessToken, updatedUser)
                .then()
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.name", equalTo(UPDATED_NAME));
    }

    @Test
    @DisplayName("Неавторизованный пользователь не может изменить email")
    public void updateEmailWithoutTokenTest(){
        String newEmail = UUID.randomUUID() + "@example.com";
        User updatedUser = new User(newEmail, null, null);
        userSteps.updateUserWithoutToken(updatedUser)
                .then()
                .assertThat()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo(MISSING_TOKEN_ERROR));
    }

    @Test
    @DisplayName("Неавторизованный пользователь не может изменить пароль")
    public void updatePasswordWithoutTokenTest(){
        User updatedUser = new User(null, UPDATED_PASSWORD, null);
        userSteps.updateUserWithoutToken(updatedUser)
                .then()
                .assertThat()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo(MISSING_TOKEN_ERROR));
    }

    @Test
    @DisplayName("Неавторизованный пользователь не может изменить имя")
    public void updateNameWithoutTokenTest(){
        User updatedUser = new User(null, null, UPDATED_NAME);
        userSteps.updateUserWithoutToken(updatedUser)
                .then()
                .assertThat()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo(MISSING_TOKEN_ERROR));
    }
}
