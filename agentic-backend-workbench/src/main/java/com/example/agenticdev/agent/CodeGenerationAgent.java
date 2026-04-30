package com.example.agenticdev.agent;

import com.example.agenticdev.domain.CodeArtifact;
import com.example.agenticdev.util.TextUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CodeGenerationAgent implements Agent<CodeGenerationInput, List<CodeArtifact>> {
    @Override
    public String name() {
        return "CodeGenerationAgent / 代码生成 Agent";
    }

    @Override
    public List<CodeArtifact> execute(CodeGenerationInput input) {
        String entityName = chooseEntity(input);
        String variableName = TextUtils.lowerFirst(entityName);
        String packageName = "com.example.generated";
        List<CodeArtifact> artifacts = new ArrayList<>();
        artifacts.add(new CodeArtifact("src/main/java/com/example/generated/api/" + entityName + "Controller.java", "java",
                controller(packageName, entityName, variableName)));
        artifacts.add(new CodeArtifact("src/main/java/com/example/generated/application/" + entityName + "Service.java", "java",
                service(packageName, entityName, variableName)));
        artifacts.add(new CodeArtifact("src/main/java/com/example/generated/domain/" + entityName + ".java", "java",
                entity(packageName, entityName)));
        artifacts.add(new CodeArtifact("src/main/java/com/example/generated/api/dto/" + entityName + "CreateRequest.java", "java",
                createRequest(packageName, entityName)));
        artifacts.add(new CodeArtifact("src/main/java/com/example/generated/api/dto/" + entityName + "Response.java", "java",
                response(packageName, entityName)));
        artifacts.add(new CodeArtifact("src/test/java/com/example/generated/application/" + entityName + "ServiceTest.java", "java",
                serviceTest(packageName, entityName, variableName)));
        artifacts.add(new CodeArtifact("docs/agent-workflow.md", "markdown", workflowDoc(input, entityName)));
        return artifacts;
    }

    private String chooseEntity(CodeGenerationInput input) {
        String raw = input.analysis().coreEntities().stream()
                .filter(v -> !"User".equalsIgnoreCase(v))
                .findFirst()
                .orElse("BusinessResource");
        return TextUtils.toJavaClassName(raw, "BusinessResource");
    }

    private String controller(String basePackage, String entityName, String variableName) {
        String path = "/api/" + variableName.replaceAll("([a-z])([A-Z])", "$1-$2").toLowerCase() + "s";
        return """
                package %s.api;

                import %s.api.dto.%sCreateRequest;
                import %s.api.dto.%sResponse;
                import %s.application.%sService;
                import jakarta.validation.Valid;
                import org.springframework.http.HttpStatus;
                import org.springframework.web.bind.annotation.GetMapping;
                import org.springframework.web.bind.annotation.PathVariable;
                import org.springframework.web.bind.annotation.PostMapping;
                import org.springframework.web.bind.annotation.RequestBody;
                import org.springframework.web.bind.annotation.RequestMapping;
                import org.springframework.web.bind.annotation.ResponseStatus;
                import org.springframework.web.bind.annotation.RestController;

                import java.util.List;

                @RestController
                @RequestMapping("%s")
                public class %sController {
                    private final %sService %sService;

                    public %sController(%sService %sService) {
                        this.%sService = %sService;
                    }

                    @PostMapping
                    @ResponseStatus(HttpStatus.CREATED)
                    public %sResponse create(@Valid @RequestBody %sCreateRequest request) {
                        return %sService.create(request);
                    }

                    @GetMapping("/{id}")
                    public %sResponse findById(@PathVariable Long id) {
                        return %sService.findById(id);
                    }

                    @GetMapping
                    public List<%sResponse> list() {
                        return %sService.list();
                    }
                }
                """.formatted(basePackage, basePackage, entityName, basePackage, entityName, basePackage, entityName,
                path, entityName, entityName, variableName, entityName, entityName, variableName, variableName, variableName,
                entityName, entityName, variableName, entityName, variableName, entityName, variableName);
    }

    private String service(String basePackage, String entityName, String variableName) {
        return """
                package %s.application;

                import %s.api.dto.%sCreateRequest;
                import %s.api.dto.%sResponse;
                import %s.domain.%s;
                import org.springframework.stereotype.Service;
                import org.springframework.transaction.annotation.Transactional;

                import java.time.Instant;
                import java.util.ArrayList;
                import java.util.List;
                import java.util.Map;
                import java.util.concurrent.ConcurrentHashMap;
                import java.util.concurrent.atomic.AtomicLong;

                @Service
                public class %sService {
                    private final AtomicLong idGenerator = new AtomicLong(1000);
                    private final Map<Long, %s> store = new ConcurrentHashMap<>();

                    @Transactional
                    public %sResponse create(%sCreateRequest request) {
                        Long id = idGenerator.incrementAndGet();
                        %s entity = new %s(id, request.name(), request.description(), "ACTIVE", Instant.now(), Instant.now());
                        store.put(id, entity);
                        return toResponse(entity);
                    }

                    public %sResponse findById(Long id) {
                        %s entity = store.get(id);
                        if (entity == null) {
                            throw new IllegalArgumentException("%s not found: " + id);
                        }
                        return toResponse(entity);
                    }

                    public List<%sResponse> list() {
                        return new ArrayList<>(store.values()).stream()
                                .map(this::toResponse)
                                .toList();
                    }

                    private %sResponse toResponse(%s entity) {
                        return new %sResponse(entity.id(), entity.name(), entity.description(), entity.status(), entity.createdAt(), entity.updatedAt());
                    }
                }
                """.formatted(basePackage, basePackage, entityName, basePackage, entityName, basePackage, entityName,
                entityName, entityName, entityName, entityName, entityName, variableName, entityName,
                entityName, entityName, entityName, entityName, entityName, entityName, entityName, entityName);
    }

    private String entity(String basePackage, String entityName) {
        return """
                package %s.domain;

                import java.time.Instant;

                public record %s(
                        Long id,
                        String name,
                        String description,
                        String status,
                        Instant createdAt,
                        Instant updatedAt
                ) {
                    public %s {
                        if (id == null) {
                            throw new IllegalArgumentException("id 不能为空");
                        }
                        if (name == null || name.isBlank()) {
                            throw new IllegalArgumentException("name 不能为空");
                        }
                    }
                }
                """.formatted(basePackage, entityName, entityName);
    }

    private String createRequest(String basePackage, String entityName) {
        return """
                package %s.api.dto;

                import jakarta.validation.constraints.NotBlank;
                import jakarta.validation.constraints.Size;

                public record %sCreateRequest(
                        @NotBlank(message = "名称不能为空")
                        @Size(max = 80, message = "名称长度不能超过 80")
                        String name,

                        @Size(max = 500, message = "描述长度不能超过 500")
                        String description
                ) {
                }
                """.formatted(basePackage, entityName);
    }

    private String response(String basePackage, String entityName) {
        return """
                package %s.api.dto;

                import java.time.Instant;

                public record %sResponse(
                        Long id,
                        String name,
                        String description,
                        String status,
                        Instant createdAt,
                        Instant updatedAt
                ) {
                }
                """.formatted(basePackage, entityName);
    }

    private String serviceTest(String basePackage, String entityName, String variableName) {
        return """
                package %s.application;

                import %s.api.dto.%sCreateRequest;
                import org.junit.jupiter.api.Test;

                import static org.assertj.core.api.Assertions.assertThat;
                import static org.assertj.core.api.Assertions.assertThatThrownBy;

                class %sServiceTest {
                    private final %sService %sService = new %sService();

                    @Test
                    void shouldCreate%s() {
                        var response = %sService.create(new %sCreateRequest("示例资源", "由 Agent 生成的测试数据"));

                        assertThat(response.id()).isNotNull();
                        assertThat(response.name()).isEqualTo("示例资源");
                        assertThat(response.status()).isEqualTo("ACTIVE");
                    }

                    @Test
                    void shouldRejectMissing%s() {
                        assertThatThrownBy(() -> %sService.findById(999L))
                                .isInstanceOf(IllegalArgumentException.class)
                                .hasMessageContaining("not found");
                    }
                }
                """.formatted(basePackage, basePackage, entityName, entityName, entityName, variableName, entityName,
                entityName, variableName, entityName, entityName, variableName);
    }

    private String workflowDoc(CodeGenerationInput input, String entityName) {
        return """
                # Agent 工作流说明

                ## 项目
                %s

                ## 核心实体
                %s

                ## 长链推理链路
                1. RequirementAgent：解析需求、实体、接口、校验规则与异常分支。
                2. ArchitectureAgent：生成分层架构、事务边界、安全策略与可观测性建议。
                3. CodeGenerationAgent：生成 Controller、Service、DTO、Domain、测试代码。
                4. CodeReviewAgent：检查空指针、事务、并发、SQL、日志与可维护性风险。
                5. TestGenerationAgent：生成单元测试、集成测试和覆盖率目标。

                ## 原始需求摘要
                %s
                """.formatted(input.request().projectName(), entityName, TextUtils.shortText(input.request().requirement(), 600));
    }
}
