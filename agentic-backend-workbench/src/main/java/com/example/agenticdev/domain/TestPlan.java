package com.example.agenticdev.domain;

import java.util.List;

public record TestPlan(
        List<String> unitCases,
        List<String> integrationCases,
        List<String> mockData,
        List<String> coverageTargets
) {
}
