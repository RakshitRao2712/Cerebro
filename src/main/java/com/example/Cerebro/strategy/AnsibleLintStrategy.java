package com.example.Cerebro.strategy;

import java.nio.file.Path;

import org.springframework.stereotype.Component;

import com.example.Cerebro.record.EvalResult;
import com.example.Cerebro.record.ToolEnum;
import com.example.Cerebro.util.CommandRunner;

@Component
public class AnsibleLintStrategy implements DevOpsLinterStrategy {

    @Override
    public EvalResult validate(Path workspace) {
        try {
            String output = CommandRunner.runCommand(workspace, "ansible-lint", "playbook.yml");
            return new EvalResult(true, output);
        } catch (Exception e) {
            return new EvalResult(false, "Validator crashed: " + e.getMessage());
        }
    }

    @Override
    public ToolEnum getSupportedTool() {
        return ToolEnum.ANSIBLE;
    }
}
