package com.example.Cerebro.controller;

import java.nio.file.Path;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Cerebro.record.GeneratedArtifact;
import com.example.Cerebro.service.FileIOService;
import com.example.Cerebro.service.GeneratorAgent;

@RestController
@RequestMapping("/api/test")
public class GeneratorTestController {

    private final GeneratorAgent generatorAgent;
    private final FileIOService fileIOService;

    public GeneratorTestController(GeneratorAgent generatorAgent, FileIOService fileIOService) {
        this.generatorAgent = generatorAgent;
        this.fileIOService = fileIOService;
    }

    @GetMapping("/generate")
    public GeneratedArtifact test(@RequestParam String prompt) {
        return generatorAgent.generate(prompt, null);
    }

    @GetMapping("/generate-and-write")
    public String testWrite(@RequestParam String prompt) {
        GeneratedArtifact artifact = generatorAgent.generate(prompt, null);
        Path workspace = fileIOService.createWorkspace();
        fileIOService.writeArtifact(workspace, artifact);
        return "Written to: " + workspace.resolve(artifact.filename());

    }
}
