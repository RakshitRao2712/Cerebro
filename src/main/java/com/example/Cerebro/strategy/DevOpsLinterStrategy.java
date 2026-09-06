package com.example.Cerebro.strategy;

import java.nio.file.Path;

import com.example.Cerebro.record.EvalResult;
import com.example.Cerebro.record.ToolEnum;

public interface DevOpsLinterStrategy {
    EvalResult validate(Path workspace);

    ToolEnum getSupportedTool();
}
