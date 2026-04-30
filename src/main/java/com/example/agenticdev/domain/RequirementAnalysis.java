package com.example.agenticdev.domain;

import java.util.List;

public record RequirementAnalysis(
        String businessGoal,
        List<String> coreEntities,
        List<String> userStories,
        List<String> endpoints,
        List<String> validationRules,
        List<String> exceptionBranches,
        List<String> acceptanceCriteria
) {
}
