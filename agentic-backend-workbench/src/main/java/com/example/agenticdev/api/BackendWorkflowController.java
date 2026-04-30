package com.example.agenticdev.api;

import com.example.agenticdev.domain.BackendWorkflowRequest;
import com.example.agenticdev.domain.WorkflowResult;
import com.example.agenticdev.service.BackendWorkflowOrchestrator;
import com.example.agenticdev.service.WorkflowExportService;
import jakarta.validation.Valid;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/workflows")
public class BackendWorkflowController {
    private final BackendWorkflowOrchestrator orchestrator;
    private final WorkflowExportService exportService;

    public BackendWorkflowController(BackendWorkflowOrchestrator orchestrator, WorkflowExportService exportService) {
        this.orchestrator = orchestrator;
        this.exportService = exportService;
    }

    @PostMapping("/backend-dev")
    public WorkflowResult run(@Valid @RequestBody BackendWorkflowRequest request) {
        return orchestrator.run(request);
    }

    @PostMapping(value = "/backend-dev/zip", produces = "application/zip")
    public ResponseEntity<byte[]> runAndExport(@Valid @RequestBody BackendWorkflowRequest request) {
        WorkflowResult result = orchestrator.run(request);
        byte[] zip = exportService.toZip(result);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename("agent-generated-" + result.workflowId() + ".zip")
                                .build()
                                .toString())
                .contentType(MediaType.parseMediaType("application/zip"))
                .contentLength(zip.length)
                .body(zip);
    }
}
