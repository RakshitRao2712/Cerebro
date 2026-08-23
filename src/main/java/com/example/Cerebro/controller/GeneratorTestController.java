package com.example.Cerebro.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Cerebro.record.GeneratedArtifact;
import com.example.Cerebro.service.GeneratorAgent;

@RestController
@RequestMapping("/api/test")
public class GeneratorTestController {

    private final GeneratorAgent generatorAgent;

    public GeneratorTestController(GeneratorAgent generatorAgent) {
        this.generatorAgent = generatorAgent;
    }

    @GetMapping("/generate")
    public GeneratedArtifact test(@RequestParam String prompt) {
        return generatorAgent.generate(prompt, null);
    }
}
