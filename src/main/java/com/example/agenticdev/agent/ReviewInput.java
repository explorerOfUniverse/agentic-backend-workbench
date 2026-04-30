package com.example.agenticdev.agent;

import com.example.agenticdev.domain.ArchitecturePlan;
import com.example.agenticdev.domain.BackendWorkflowRequest;
import com.example.agenticdev.domain.CodeArtifact;
import com.example.agenticdev.domain.RequirementAnalysis;

import java.util.List;

public record ReviewInput(
        BackendWorkflowRequest request,
        RequirementAnalysis analysis,
        ArchitecturePlan architecturePlan,
        List<CodeArtifact> artifacts
) {
}
