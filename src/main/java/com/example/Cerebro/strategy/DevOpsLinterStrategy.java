package com.example.Cerebro.strategy;

import java.nio.file.Path;

import com.example.Cerebro.record.EvalResult;

public interface DevOpsLinterStrategy {
    EvalResult validate(Path workspace);
}
