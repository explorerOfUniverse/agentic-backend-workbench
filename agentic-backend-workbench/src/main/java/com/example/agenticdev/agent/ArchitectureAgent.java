package com.example.agenticdev.agent;

import com.example.agenticdev.domain.ArchitecturePlan;
import com.example.agenticdev.domain.BackendWorkflowRequest;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class ArchitectureAgent implements Agent<ArchitectureInput, ArchitecturePlan> {
    @Override
    public String name() {
        return "ArchitectureAgent / 架构设计 Agent";
    }

    @Override
    public ArchitecturePlan execute(ArchitectureInput input) {
        BackendWorkflowRequest request = input.request();
        Map<String, String> layers = new LinkedHashMap<>();
        layers.put("api", "Controller 层负责 REST 入参校验、HTTP 状态码映射和响应封装。 ");
        layers.put("application", "Service 层负责编排业务规则、事务边界和领域事件。 ");
        layers.put("domain", "Domain 层承载核心实体、状态流转和业务不变量。 ");
        layers.put("infrastructure", "Repository/Gateway 层负责数据库、缓存、消息队列及外部系统访问。 ");
        layers.put("test", "测试层覆盖单元测试、Mock 依赖、异常路径和接口集成测试。 ");

        return new ArchitecturePlan(
                "分层架构 + 多 Agent 工作流编排，技术栈：" + request.normalizedTechStack(),
                List.of(
                        "Requirement Parser：提取实体、接口、校验规则与验收标准",
                        "Architecture Planner：生成分层方案、事务边界与安全策略",
                        "Code Generator：生成 Controller/Service/DTO/Repository 样板代码",
                        "Code Reviewer：检查空指针、并发、事务、SQL、日志与可维护性风险",
                        "Test Generator：生成单元测试、集成测试和 Mock 数据"
                ),
                layers,
                List.of(
                        "实体必须包含 id、createdAt、updatedAt、status 等审计字段。 ",
                        "写操作建议采用乐观锁 version 字段降低并发覆盖风险。 ",
                        "金额字段使用 BigDecimal；时间字段使用 Instant 或 OffsetDateTime。 "
                ),
                List.of(
                        "创建、更新、状态流转等写操作以 Service 方法为事务边界。 ",
                        "外部 RPC、消息投递等副作用应在事务提交后执行或使用 Outbox 模式。 ",
                        "读接口默认不加事务，复杂一致性读取可使用只读事务。 "
                ),
                List.of(
                        "所有写接口需要身份认证与资源归属校验。 ",
                        "错误响应不得暴露 SQL、堆栈或第三方密钥。 ",
                        "对高频接口加入限流、幂等键和审计日志。 "
                ),
                List.of(
                        "统一记录 traceId、用户 id、业务资源 id 和错误码。 ",
                        "关键业务动作埋点：请求量、失败率、耗时分位数。 ",
                        "Agent 每一步输出步骤日志，便于复盘 token 消耗与质量。 "
                )
        );
    }
}
