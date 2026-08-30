package com.example.Cerebro.record;

import java.util.List;

public record WorkflowResult(GeneratedArtifact finalArtifact,
        boolean success,
        int attemptsUsed,
        List<String> history) {

}
