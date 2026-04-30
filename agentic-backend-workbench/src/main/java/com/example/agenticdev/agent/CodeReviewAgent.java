package com.example.agenticdev.agent;

import com.example.agenticdev.domain.CodeArtifact;
import com.example.agenticdev.domain.ReviewFinding;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CodeReviewAgent implements Agent<ReviewInput, List<ReviewFinding>> {
    @Override
    public String name() {
        return "CodeReviewAgent / 代码审查 Agent";
    }

    @Override
    public List<ReviewFinding> execute(ReviewInput input) {
        List<ReviewFinding> findings = new ArrayList<>();
        for (CodeArtifact artifact : input.artifacts()) {
            if (artifact.path().endsWith("Service.java")) {
                findings.add(new ReviewFinding(
                        ReviewFinding.Severity.INFO,
                        artifact.path(),
                        "Service 层已集中处理创建和查询逻辑，便于后续迁移到真实 Repository。",
                        "生产环境应将内存 Map 替换为 JPA/MyBatis Repository，并增加数据库唯一索引。"
                ));
                findings.add(new ReviewFinding(
                        ReviewFinding.Severity.WARNING,
                        artifact.path(),
                        "当前示例使用 ConcurrentHashMap 作为演示存储，不适合多实例部署后的数据一致性要求。",
                        "接入数据库后，写操作保留 @Transactional，并补充乐观锁 version 字段。"
                ));
            }
            if (artifact.path().endsWith("Controller.java") && !artifact.content().contains("@Valid")) {
                findings.add(new ReviewFinding(
                        ReviewFinding.Severity.CRITICAL,
                        artifact.path(),
                        "Controller 入参缺少 @Valid，可能导致非法数据进入业务层。",
                        "在 @RequestBody 参数上添加 @Valid，并在 DTO 中使用 Bean Validation 注解。"
                ));
            }
            if (artifact.content().contains("throw new IllegalArgumentException")) {
                findings.add(new ReviewFinding(
                        ReviewFinding.Severity.WARNING,
                        artifact.path(),
                        "示例代码直接抛出 IllegalArgumentException，API 层需要统一异常映射。",
                        "建议新增 GlobalExceptionHandler，将业务异常转换为统一错误响应。"
                ));
            }
        }

        if (input.request().shouldStrictReview()) {
            findings.add(new ReviewFinding(
                    ReviewFinding.Severity.INFO,
                    "workflow",
                    "严格审查已启用：将额外关注事务边界、幂等性、日志可观测性与资源归属校验。",
                    "真实接入项目时，可把 Checkstyle、SpotBugs、JaCoCo 和 CI 状态作为审查 Agent 的输入。"
            ));
        }
        return findings;
    }
}
