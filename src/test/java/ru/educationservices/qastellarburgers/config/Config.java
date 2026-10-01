package ru.educationservices.qastellarburgers.config;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

public class Config {
    public static final String BASE_URL = "https://qa-stellarburgers.education-services.ru";

    public static final RequestSpecification SPEC = new RequestSpecBuilder()
            .setBaseUri(BASE_URL)
            .setContentType("application/json")
            .build();
}
