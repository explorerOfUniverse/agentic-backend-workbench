package com.example.agenticdev.domain;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record BackendWorkflowRequest(
        @NotBlank(message = "projectName 不能为空") String projectName,
        @NotBlank(message = "requirement 不能为空") String requirement,
        String techStack,
        List<String> existingContext,
        Boolean generateTests,
        Boolean strictReview
) {
    public String normalizedTechStack() {
        return techStack == null || techStack.isBlank()
                ? "Java 17, Spring Boot 3, Maven, REST API, JUnit 5"
                : techStack;
    }

    public boolean shouldGenerateTests() {
        return generateTests == null || generateTests;
    }

    public boolean shouldStrictReview() {
        return strictReview == null || strictReview;
    }
}
