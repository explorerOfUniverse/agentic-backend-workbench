package com.example.agenticdev.agent;

import com.example.agenticdev.domain.TestPlan;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TestGenerationAgent implements Agent<TestGenerationInput, TestPlan> {
    @Override
    public String name() {
        return "TestGenerationAgent / 测试生成 Agent";
    }

    @Override
    public TestPlan execute(TestGenerationInput input) {
        List<String> unitCases = new ArrayList<>();
        unitCases.add("创建成功：合法请求应返回 id、状态 ACTIVE、创建时间和更新时间。 ");
        unitCases.add("查询成功：已有资源 id 应返回完整详情。 ");
        unitCases.add("查询失败：不存在的 id 应触发业务异常或 404 映射。 ");
        unitCases.add("参数校验：name 为空、description 超长等场景应被拒绝。 ");

        List<String> integrationCases = List.of(
                "POST + GET 闭环：创建后立即查询，验证数据一致性。",
                "异常响应格式：400/404/409 应返回统一 errorCode、message、traceId。",
                "并发写入：多个请求同时创建资源时 id 不重复。"
        );

        List<String> mockData = List.of(
                "正常资源：name=示例资源，description=用于单元测试的描述。",
                "边界资源：name 长度等于 80，description 长度等于 500。",
                "非法资源：name 为空，description 超过 500。"
        );

        List<String> coverageTargets = List.of(
                "Service 行覆盖率不低于 80%。",
                "核心业务分支覆盖率不低于 70%。",
                "所有验收标准至少绑定一个测试用例。",
                "所有 WARNING/CRITICAL 审查项都应有修复或测试说明。"
        );

        return new TestPlan(unitCases, integrationCases, mockData, coverageTargets);
    }
}
