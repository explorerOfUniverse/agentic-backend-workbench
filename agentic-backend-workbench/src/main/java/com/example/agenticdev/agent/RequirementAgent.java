package com.example.agenticdev.agent;

import com.example.agenticdev.domain.BackendWorkflowRequest;
import com.example.agenticdev.domain.RequirementAnalysis;
import com.example.agenticdev.llm.LlmClient;
import com.example.agenticdev.util.TextUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RequirementAgent implements Agent<RequirementInput, RequirementAnalysis> {
    private final LlmClient llmClient;

    public RequirementAgent(LlmClient llmClient) {
        this.llmClient = llmClient;
    }

    @Override
    public String name() {
        return "RequirementAgent / 需求解析 Agent";
    }

    @Override
    public RequirementAnalysis execute(RequirementInput input) {
        BackendWorkflowRequest request = input.request();
        String requirement = request.requirement();
        String businessGoal = "围绕“" + request.projectName() + "”交付可维护、可测试、可审查的后端功能闭环。";

        List<String> entities = new ArrayList<>();
        entities.add("User");
        if (TextUtils.containsAny(requirement, "订单", "order")) entities.add("Order");
        if (TextUtils.containsAny(requirement, "商品", "product", "sku")) entities.add("Product");
        if (TextUtils.containsAny(requirement, "支付", "payment", "pay")) entities.add("Payment");
        if (TextUtils.containsAny(requirement, "库存", "stock", "inventory")) entities.add("Inventory");
        if (TextUtils.containsAny(requirement, "审批", "approve", "workflow")) entities.add("ApprovalTask");
        if (entities.size() == 1) entities.add("BusinessResource");

        List<String> userStories = List.of(
                "作为业务使用者，我希望提交结构化请求并获得明确处理结果。",
                "作为后端开发者，我希望接口边界、异常分支和验收标准能够被自动提取。",
                "作为代码审查者，我希望系统提前识别参数校验、事务边界与潜在空指针问题。"
        );

        String resource = TextUtils.containsAny(requirement, "订单", "order") ? "orders" : "resources";
        List<String> endpoints = List.of(
                "POST /api/" + resource + " - 创建资源并执行参数校验",
                "GET /api/" + resource + "/{id} - 查询资源详情",
                "PUT /api/" + resource + "/{id} - 幂等更新资源",
                "GET /api/" + resource + " - 分页查询资源列表"
        );

        List<String> validationRules = new ArrayList<>();
        validationRules.add("所有外部入参必须进行空值、长度、格式和枚举值校验。 ");
        validationRules.add("涉及金额、数量、库存等字段时必须避免负数和精度丢失。 ");
        validationRules.add("查询接口必须限制 pageSize 上限，避免大分页拖垮数据库。 ");
        if (TextUtils.containsAny(requirement, "登录", "auth", "权限", "token")) {
            validationRules.add("涉及身份认证的接口必须校验访问令牌、角色权限和资源归属。 ");
        }

        List<String> exceptionBranches = List.of(
                "参数非法：返回 400，并给出字段级错误说明。",
                "资源不存在：返回 404，避免泄漏敏感内部信息。",
                "业务状态冲突：返回 409，例如重复提交、状态不可逆或并发更新冲突。",
                "下游依赖失败：返回可观测错误码，并记录 traceId 便于排查。"
        );

        String llmNote = safeLlmNote(requirement);
        List<String> acceptanceCriteria = new ArrayList<>();
        acceptanceCriteria.add("核心接口具备单元测试与边界条件测试。 ");
        acceptanceCriteria.add("主要业务方法具备明确事务边界和异常处理策略。 ");
        acceptanceCriteria.add("生成代码通过静态审查，关键风险项已形成修复建议。 ");
        acceptanceCriteria.add("Agent 过程可追踪：需求解析、架构设计、代码生成、审查、测试生成均有日志。 ");
        if (!llmNote.isBlank()) {
            acceptanceCriteria.add("模型辅助摘要：" + llmNote);
        }

        return new RequirementAnalysis(
                businessGoal,
                TextUtils.distinct(entities),
                userStories,
                endpoints,
                validationRules,
                exceptionBranches,
                acceptanceCriteria
        );
    }

    private String safeLlmNote(String requirement) {
        try {
            return TextUtils.shortText(llmClient.complete(
                    "你是严谨的 Java 后端需求分析专家，请用一句话概括需求风险。",
                    requirement
            ), 120);
        } catch (RuntimeException ex) {
            return "";
        }
    }
}
