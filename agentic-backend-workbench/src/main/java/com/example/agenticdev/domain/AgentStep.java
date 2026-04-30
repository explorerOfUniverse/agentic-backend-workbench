package com.example.agenticdev.domain;

import java.time.Instant;

public record AgentStep(
        String agentName,
        String inputSummary,
        String outputSummary,
        int estimatedTokens,
        Instant finishedAt
) {
}
