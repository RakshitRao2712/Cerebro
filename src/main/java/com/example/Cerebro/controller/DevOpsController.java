package com.example.Cerebro.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Cerebro.WorkflowManager;
import com.example.Cerebro.record.WorkflowResult;

@RestController
@RequestMapping("/api/devops")
public class DevOpsController {
    private final WorkflowManager workflowManager;

    public DevOpsController(WorkflowManager workflowManager) {
        this.workflowManager = workflowManager;
    }

    @PostMapping("/generate")
    public WorkflowResult generate(@RequestBody GenerateRequest request) {
        return workflowManager.run(request.prompt());
    }

    public record GenerateRequest(String prompt) {
    }

}
