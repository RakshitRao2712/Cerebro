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
    public GeneratedArtifact test(@RequestParam String prompt) {
        return generatorAgent.generate(prompt, null, ToolEnum.TERRAFORM);
    }

    @GetMapping("/generate-and-write")
    public String testWrite(@RequestParam String prompt) {
        GeneratedArtifact artifact = generatorAgent.generate(prompt, null, ToolEnum.TERRAFORM);
        Path workspace = fileIOService.createWorkspace();
        fileIOService.writeArtifact(workspace, artifact);
        return "Written to: " + workspace.resolve(artifact.filename());

    }

    @GetMapping("/generate-write-validate")
    public EvalResult testValidate(@RequestParam String prompt) {
        GeneratedArtifact artifact = generatorAgent.generate(prompt, null, ToolEnum.TERRAFORM);
        Path workspace = fileIOService.createWorkspace();
        fileIOService.writeArtifact(workspace, artifact);
        return terraformValidateStrategy.validate(workspace);
    }

    @GetMapping("/generate-writevalidateevaluate")
    public EvalResult testEvaluate(@RequestParam String prompt) {
        GeneratedArtifact artifact = generatorAgent.generate(prompt, null, ToolEnum.TERRAFORM);
        Path workspace = fileIOService.createWorkspace();
        fileIOService.writeArtifact(workspace, artifact);
        EvalResult rawResult = terraformValidateStrategy.validate(workspace);
        return evaluatorAgent.evaluate(artifact.code(), rawResult.feedback());
    }

    @GetMapping("/run-workflow")
    public WorkflowResult testWorkflow(@RequestParam String prompt) {
        return workflowManager.run(prompt);
    }
}
