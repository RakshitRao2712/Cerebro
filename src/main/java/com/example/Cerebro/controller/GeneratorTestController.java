package com.example.Cerebro.controller;

import java.nio.file.Path;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Cerebro.WorkflowManager;
import com.example.Cerebro.record.EvalResult;
import com.example.Cerebro.record.GeneratedArtifact;
import com.example.Cerebro.record.ToolEnum;
import com.example.Cerebro.record.WorkflowResult;
import com.example.Cerebro.service.EvaluatorAgent;
import com.example.Cerebro.service.FileIOService;
import com.example.Cerebro.service.GeneratorAgent;
import com.example.Cerebro.strategy.TerraformValidatorStrategy;

@RestController
@RequestMapping("/api/test")
public class GeneratorTestController {

    private final GeneratorAgent generatorAgent;
    private final FileIOService fileIOService;
    private final TerraformValidatorStrategy terraformValidateStrategy;
    private final EvaluatorAgent evaluatorAgent;
    private final WorkflowManager workflowManager;

    public GeneratorTestController(GeneratorAgent generatorAgent, FileIOService fileIOService,
            TerraformValidatorStrategy terraformValidateStrategy, EvaluatorAgent evaluatorAgent,
            WorkflowManager workflowManager) {
        this.generatorAgent = generatorAgent;
        this.fileIOService = fileIOService;
        this.terraformValidateStrategy = terraformValidateStrategy;
        this.evaluatorAgent = evaluatorAgent;
        this.workflowManager = workflowManager;
    }

    @GetMapping("/generate")
    public GeneratedArtifact test(@RequestParam String prompt, @RequestParam(defaultValue = "TERRAFORM") ToolEnum tool) {
        return generatorAgent.generate(prompt, null, tool);
    }

    @GetMapping("/generate-and-write")
    public String testWrite(@RequestParam String prompt, @RequestParam(defaultValue = "TERRAFORM") ToolEnum tool) {
        GeneratedArtifact artifact = generatorAgent.generate(prompt, null, tool);
        Path workspace = fileIOService.createWorkspace();
        fileIOService.writeArtifact(workspace, artifact);
        return "Written to: " + workspace.resolve(artifact.filename());
    }

    @GetMapping("/run-workflow")
    public WorkflowResult testWorkflow(@RequestParam String prompt, @RequestParam(defaultValue = "TERRAFORM") ToolEnum tool) {
        return workflowManager.run(prompt, tool);
    }

    @GetMapping("/test-ansible")
    public WorkflowResult testAnsible(@RequestParam String prompt) {
        return workflowManager.run(prompt, ToolEnum.ANSIBLE);
    }

    @GetMapping("/test-k8s")
    public WorkflowResult testK8s(@RequestParam String prompt) {
        return workflowManager.run(prompt, ToolEnum.KUBERNETES);
    }

    @GetMapping("/test-hadolint")
    public WorkflowResult testHadolint(@RequestParam String prompt) {
        return workflowManager.run(prompt, ToolEnum.DOCKERFILE);
    }
}
