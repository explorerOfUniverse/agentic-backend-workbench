package com.example.agenticdev.agent;

import com.example.agenticdev.domain.ArchitecturePlan;
import com.example.agenticdev.domain.BackendWorkflowRequest;
import com.example.agenticdev.domain.RequirementAnalysis;

public record CodeGenerationInput(
        BackendWorkflowRequest request,
        RequirementAnalysis analysis,
        ArchitecturePlan architecturePlan
) {
}
