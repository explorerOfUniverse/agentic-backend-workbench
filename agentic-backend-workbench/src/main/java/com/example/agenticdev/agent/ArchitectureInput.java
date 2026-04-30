package com.example.agenticdev.agent;

import com.example.agenticdev.domain.BackendWorkflowRequest;
import com.example.agenticdev.domain.RequirementAnalysis;

public record ArchitectureInput(BackendWorkflowRequest request, RequirementAnalysis analysis) {
}
