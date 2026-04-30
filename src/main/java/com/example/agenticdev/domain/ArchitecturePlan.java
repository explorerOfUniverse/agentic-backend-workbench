package com.example.agenticdev.domain;

import java.util.List;
import java.util.Map;

public record ArchitecturePlan(
        String style,
        List<String> modules,
        Map<String, String> layers,
        List<String> dataModelSuggestions,
        List<String> transactionBoundaries,
        List<String> securityConsiderations,
        List<String> observabilitySuggestions
) {
}
