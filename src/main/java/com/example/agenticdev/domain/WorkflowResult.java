package com.example.agenticdev.domain;

import java.time.Instant;
import java.util.List;

public record WorkflowResult(
        String workflowId,
        BackendWorkflowRequest request,
        RequirementAnalysis requirementAnalysis,
        ArchitecturePlan architecturePlan,
        List<CodeArtifact> codeArtifacts,
        List<ReviewFinding> reviewFindings,
        TestPlan testPlan,
        List<AgentStep> steps,
        String summary,
        Instant generatedAt
) {
}
