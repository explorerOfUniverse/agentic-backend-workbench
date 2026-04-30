package com.example.agenticdev.service;

import com.example.agenticdev.agent.ArchitectureAgent;
import com.example.agenticdev.agent.ArchitectureInput;
import com.example.agenticdev.agent.CodeGenerationAgent;
import com.example.agenticdev.agent.CodeGenerationInput;
import com.example.agenticdev.agent.CodeReviewAgent;
import com.example.agenticdev.agent.RequirementAgent;
import com.example.agenticdev.agent.RequirementInput;
import com.example.agenticdev.agent.ReviewInput;
import com.example.agenticdev.agent.TestGenerationAgent;
import com.example.agenticdev.agent.TestGenerationInput;
import com.example.agenticdev.domain.AgentStep;
import com.example.agenticdev.domain.ArchitecturePlan;
import com.example.agenticdev.domain.BackendWorkflowRequest;
import com.example.agenticdev.domain.CodeArtifact;
import com.example.agenticdev.domain.RequirementAnalysis;
import com.example.agenticdev.domain.ReviewFinding;
import com.example.agenticdev.domain.TestPlan;
import com.example.agenticdev.domain.WorkflowResult;
import com.example.agenticdev.util.TextUtils;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class BackendWorkflowOrchestrator {
    private final RequirementAgent requirementAgent;
    private final ArchitectureAgent architectureAgent;
    private final CodeGenerationAgent codeGenerationAgent;
    private final CodeReviewAgent codeReviewAgent;
    private final TestGenerationAgent testGenerationAgent;

    public BackendWorkflowOrchestrator(
            RequirementAgent requirementAgent,
            ArchitectureAgent architectureAgent,
            CodeGenerationAgent codeGenerationAgent,
            CodeReviewAgent codeReviewAgent,
            TestGenerationAgent testGenerationAgent
    ) {
        this.requirementAgent = requirementAgent;
        this.architectureAgent = architectureAgent;
        this.codeGenerationAgent = codeGenerationAgent;
        this.codeReviewAgent = codeReviewAgent;
        this.testGenerationAgent = testGenerationAgent;
    }

    public WorkflowResult run(BackendWorkflowRequest request) {
        List<AgentStep> steps = new ArrayList<>();
        RequirementAnalysis analysis = requirementAgent.execute(new RequirementInput(request));
        steps.add(step(requirementAgent.name(), request.requirement(), analysis.toString()));

        ArchitecturePlan architecturePlan = architectureAgent.execute(new ArchitectureInput(request, analysis));
        steps.add(step(architectureAgent.name(), analysis.toString(), architecturePlan.toString()));

        List<CodeArtifact> artifacts = codeGenerationAgent.execute(new CodeGenerationInput(request, analysis, architecturePlan));
        steps.add(step(codeGenerationAgent.name(), architecturePlan.toString(), artifacts.size() + " 个代码/文档文件"));

        List<ReviewFinding> findings = codeReviewAgent.execute(new ReviewInput(request, analysis, architecturePlan, artifacts));
        steps.add(step(codeReviewAgent.name(), artifacts.size() + " 个文件", findings.size() + " 条审查意见"));

        TestPlan testPlan = request.shouldGenerateTests()
                ? testGenerationAgent.execute(new TestGenerationInput(request, analysis, architecturePlan, artifacts, findings))
                : new TestPlan(List.of(), List.of(), List.of(), List.of("用户关闭了测试生成。"));
        steps.add(step(testGenerationAgent.name(), findings.toString(), testPlan.toString()));

        String summary = "已完成多 Agent 长链流程：需求解析 → 架构设计 → 代码生成 → 代码审查 → 测试规划。"
                + "共生成 " + artifacts.size() + " 个文件、" + findings.size() + " 条审查意见。";
        return new WorkflowResult(
                UUID.randomUUID().toString(),
                request,
                analysis,
                architecturePlan,
                artifacts,
                findings,
                testPlan,
                steps,
                summary,
                Instant.now()
        );
    }

    private AgentStep step(String agentName, String input, String output) {
        return new AgentStep(
                agentName,
                TextUtils.shortText(input, 220),
                TextUtils.shortText(output, 260),
                TextUtils.estimateTokens(input) + TextUtils.estimateTokens(output),
                Instant.now()
        );
    }
}
