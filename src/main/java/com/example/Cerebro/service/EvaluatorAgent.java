package com.example.Cerebro.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import com.example.Cerebro.record.EvalResult;

@Service
public class EvaluatorAgent {

    private final ChatClient chatClient;

    public EvaluatorAgent(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public EvalResult evaluate(String code, String rawLinterOutput) {

        boolean isPass = rawLinterOutput.contains("\"valid\":true");

        if (isPass) {
            return new EvalResult(true, "Code is valid.");
        }
        String systemInstructions = """
                You are a senior DevOps engineer reviewing a Terraform validation failure.
                You will be given the Terraform code and the raw error output from `terraform validate -json`.
                Explain clearly and concisely what is wrong and what needs to change.
                Do not rewrite the code yourself — only describe the fix needed.
                Keep it short and actionable, a few sentences max.
                """;

        String userMessage = """
                Terraform code:
                %s

                Raw validation error output:
                %s
                """.formatted(code, rawLinterOutput);

        String feedback = chatClient.prompt()
                .system(systemInstructions)
                .user(userMessage)
                .call()
                .content();

        return new EvalResult(false, feedback);

    }
}
