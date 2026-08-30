package com.example.Cerebro;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.Cerebro.record.EvalResult;
import com.example.Cerebro.record.GeneratedArtifact;
import com.example.Cerebro.record.WorkflowResult;
import com.example.Cerebro.service.EvaluatorAgent;
import com.example.Cerebro.service.FileIOService;
import com.example.Cerebro.service.GeneratorAgent;
import com.example.Cerebro.strategy.TerraformValidatorStrategy;

@Service
public class WorkflowManager {

    private static final int MAX_RETRIES = 5;

    private final GeneratorAgent generatorAgent;
    private final FileIOService fileIOService;
    private final TerraformValidatorStrategy terraformValidateStrategy;
    private final EvaluatorAgent evaluatorAgent;

    public WorkflowManager(GeneratorAgent generatorAgent, FileIOService fileIOService,
            TerraformValidatorStrategy terraformValidateStrategy,
            EvaluatorAgent evaluatorAgent) {
        this.generatorAgent = generatorAgent;
        this.fileIOService = fileIOService;
        this.terraformValidateStrategy = terraformValidateStrategy;
        this.evaluatorAgent = evaluatorAgent;
    }

    public WorkflowResult run(String UserPrompt) {
        Path workspace = fileIOService.createWorkspace();
        List<String> history = new ArrayList<>();
        String feedback = null;
        GeneratedArtifact lastArtifact = null;
        int attempts = 0;

        try {
            while (attempts < MAX_RETRIES) {
                attempts++;

                // 1. Generate (first try = no feedback, retries = pass in prior error)
                GeneratedArtifact artifact = generatorAgent.generate(UserPrompt, feedback);
                lastArtifact = artifact;

                // 2. Write to disk
                fileIOService.writeArtifact(workspace, artifact);

                // 3. Validate with Terraform CLI
                EvalResult rawResult = terraformValidateStrategy.validate(workspace);

                // 4. Evaluate (turns raw JSON into clean feedback if failed)
                EvalResult eval = evaluatorAgent.evaluate(artifact.code(), rawResult.feedback());

                history.add("Attempt " + attempts + ": " + eval.feedback());

                if (eval.isPass()) {
                    return new WorkflowResult(artifact, true, attempts, history);
                }

                feedback = eval.feedback(); // feed this into the next generate() call
            }

            // Ran out of retries — return the last attempt, marked as failed
            return new WorkflowResult(lastArtifact, false, attempts, history);

        } finally {
            fileIOService.cleanup(workspace); // always clean up, pass or fail
        }
    }
}
