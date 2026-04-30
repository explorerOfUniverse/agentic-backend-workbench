# Agentic Backend Workbench

这是一个可直接运行的「多 Agent 后端研发提效系统」原型，用于展示从需求到代码、审查、测试规划的完整闭环。

## 解决的核心痛点

后端研发常见痛点包括：需求理解与代码实现割裂、接口边界不清晰、异常分支遗漏、测试覆盖不足、代码审查质量不稳定、重复样板代码过多。本项目通过多 Agent 工作流将这些步骤串联起来，形成可追踪、可复用、可导出的研发流程。

## 核心逻辑流

系统包含长链推理与多 Agent 协作：

1. `RequirementAgent`：解析需求，提取业务目标、核心实体、接口、校验规则、异常分支和验收标准。
2. `ArchitectureAgent`：生成分层架构、事务边界、安全策略与可观测性建议。
3. `CodeGenerationAgent`：生成 Controller、Service、Domain、DTO、测试代码和工作流文档。
4. `CodeReviewAgent`：检查参数校验、事务、并发、异常处理、可维护性等风险。
5. `TestGenerationAgent`：生成单元测试、集成测试、Mock 数据和覆盖率目标。

## 技术栈

- Java 17
- Spring Boot 3
- Maven
- REST API
- JUnit 5
- OpenAI-compatible HTTP client（可选）
- Mock 模式（默认，无需模型密钥即可演示）

## 快速启动

```bash
mvn spring-boot:run
```

浏览器访问：

```text
http://localhost:8080
```

## API 调用

### 运行工作流

```bash
curl -X POST http://localhost:8080/api/workflows/backend-dev \
  -H 'Content-Type: application/json' \
  -d @docs/sample-request.json
```

### 导出生成代码 ZIP

```bash
curl -X POST http://localhost:8080/api/workflows/backend-dev/zip \
  -H 'Content-Type: application/json' \
  -d @docs/sample-request.json \
  -o generated-code.zip
```

## 使用真实模型

默认配置为 Mock 模式，适合本地演示。若要使用兼容 OpenAI Chat Completions 的模型服务，可设置环境变量：

```bash
export AGENT_PROVIDER=openai-compatible
export OPENAI_API_KEY=你的密钥
export OPENAI_BASE_URL=https://api.openai.com/v1
export OPENAI_MODEL=gpt-4.1-mini
mvn spring-boot:run
```

## Docker 启动

```bash
docker compose up --build
```

## 项目结构

```text
src/main/java/com/example/agenticdev
├── agent       # 多 Agent 实现
├── api         # REST API 与异常处理
├── config      # 配置项与 LLM Client Bean
├── domain      # 请求、结果、代码文件、审查意见等领域模型
├── llm         # Mock 与 OpenAI-compatible 客户端
├── service     # 工作流编排与 ZIP 导出
└── util        # 文本工具
```

## 可作为申请材料的证明方式

- Web UI 截图：展示需求输入、工作流输出与 ZIP 导出。
- API 日志：展示多个 Agent 的顺序执行过程。
- ZIP 产物：展示生成的 Controller、Service、DTO、Domain、测试代码与报告。
- GitHub 仓库：提交完整代码、README、示例请求、运行说明。
