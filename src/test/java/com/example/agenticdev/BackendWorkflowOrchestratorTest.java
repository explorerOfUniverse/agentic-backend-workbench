package com.example.agenticdev;

import com.example.agenticdev.agent.ArchitectureAgent;
import com.example.agenticdev.agent.CodeGenerationAgent;
import com.example.agenticdev.agent.CodeReviewAgent;
import com.example.agenticdev.agent.RequirementAgent;
import com.example.agenticdev.agent.TestGenerationAgent;
import com.example.agenticdev.domain.BackendWorkflowRequest;
import com.example.agenticdev.llm.MockLlmClient;
import com.example.agenticdev.service.BackendWorkflowOrchestrator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BackendWorkflowOrchestratorTest {
    @Test
    void shouldRunWholeWorkflow() {
        BackendWorkflowOrchestrator orchestrator = new BackendWorkflowOrchestrator(
                new RequirementAgent(new MockLlmClient()),
                new ArchitectureAgent(),
                new CodeGenerationAgent(),
                new CodeReviewAgent(),
                new TestGenerationAgent()
        );

        var result = orchestrator.run(new BackendWorkflowRequest(
                "订单管理后端系统",
                "支持创建订单、查询订单、参数校验、异常处理和单元测试。",
                "Java 17, Spring Boot 3, Maven",
                List.of("已有用户中心"),
                true,
                true
        ));

        assertThat(result.workflowId()).isNotBlank();
        assertThat(result.requirementAnalysis().coreEntities()).contains("Order");
        assertThat(result.codeArtifacts()).isNotEmpty();
        assertThat(result.reviewFindings()).isNotEmpty();
        assertThat(result.testPlan().unitCases()).isNotEmpty();
        assertThat(result.steps()).hasSize(5);
    }
}
