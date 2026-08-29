package com.example.Cerebro.strategy;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import com.example.Cerebro.record.EvalResult;

@Component
public class TerraformValidatorStrategy implements DevOpsLinterStrategy {

    @Override
    public EvalResult validate(Path workspace) {
        try {
            runCommand(workspace, "terraform", "init", "-backend=false");
            String output = runCommand(workspace, "terraform", "validate", "-json");
            boolean isPass = output.contains("\"valid\":true");
            return new EvalResult(isPass, output);
        } catch (Exception e) {
            return new EvalResult(false, "Validator crashed: " + e.getMessage());
        }

    }

    private String runCommand(Path workspace, String... command) throws Exception {
        ProcessBuilder builder = new ProcessBuilder(command);
        builder.directory(workspace.toFile());
        builder.redirectErrorStream(true);

        Process process = builder.start();

        StringBuilder result = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line).append("\n");
            }
        }

        boolean finished = process.waitFor(30, TimeUnit.SECONDS);
        if (!finished) {
            process.destroyForcibly();
            throw new RuntimeException("Command timed out: " + String.join(" ", command));
        }

        return result.toString();
    }

}
