package com.example.agenticdev.service;

import com.example.agenticdev.domain.AgentStep;
import com.example.agenticdev.domain.CodeArtifact;
import com.example.agenticdev.domain.ReviewFinding;
import com.example.agenticdev.domain.WorkflowResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class WorkflowExportService {
    private final ObjectMapper objectMapper;

    public WorkflowExportService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper.copy()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public byte[] toZip(WorkflowResult result) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ZipOutputStream zip = new ZipOutputStream(bos, StandardCharsets.UTF_8)) {
            add(zip, "workflow-result.json", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(result));
            add(zip, "README.md", report(result));
            for (CodeArtifact artifact : result.codeArtifacts()) {
                add(zip, artifact.path(), artifact.content());
            }
            zip.finish();
            return bos.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("导出 ZIP 失败：" + e.getMessage(), e);
        }
    }

    private void add(ZipOutputStream zip, String path, String content) throws IOException {
        ZipEntry entry = new ZipEntry(path);
        zip.putNextEntry(entry);
        zip.write(content.getBytes(StandardCharsets.UTF_8));
        zip.closeEntry();
    }

    private String report(WorkflowResult result) {
        StringBuilder builder = new StringBuilder();
        builder.append("# ").append(result.request().projectName()).append(" - Agent 生成报告\n\n");
        builder.append("## 总结\n").append(result.summary()).append("\n\n");
        builder.append("## 核心痛点\n").append(result.requirementAnalysis().businessGoal()).append("\n\n");
        builder.append("## 核心逻辑流\n");
        for (AgentStep step : result.steps()) {
            builder.append("- ").append(step.agentName()).append("：")
                    .append(step.outputSummary()).append("\n");
        }
        builder.append("\n## 审查意见\n");
        for (ReviewFinding finding : result.reviewFindings()) {
            builder.append("- [").append(finding.severity()).append("] ")
                    .append(finding.location()).append("：")
                    .append(finding.message()).append(" 建议：")
                    .append(finding.suggestion()).append("\n");
        }
        builder.append("\n## 使用方式\n");
        builder.append("将生成的 src 目录复制到 Spring Boot 项目中，或按需迁移 Controller、Service、DTO、Domain 与测试代码。\n");
        return builder.toString();
    }
}
