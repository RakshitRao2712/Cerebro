package com.example.Cerebro.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import com.example.Cerebro.record.GeneratedArtifact;

@Service
public class GeneratorAgent {

    private final ChatClient chatClient;

    public GeneratorAgent(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public GeneratedArtifact generate(String userPrompt, String priorFeedback) {
        String systemInstructions = """
                You are a senior DevOps engineer.
                Generate ONLY raw Terraform HCL code for the user's request.
                Rules:
                - Output raw HCL only. No markdown fences, no ```hcl, no explanations, no comments about what you did.
                - The code must be syntactically valid and deployable.
                - Always include a "filename" of "main.tf".
                """;

        String userMessage = priorFeedback == null
                ? userPrompt
                : """
                        Original request: %s

                        Your previous attempt FAILED validation with this error:
                        %s

                        Fix the code and generate a corrected version.
                        """.formatted(userPrompt, priorFeedback);

        return chatClient.prompt()
                .system(systemInstructions)
                .user(userMessage)
                .call()
                .entity(GeneratedArtifact.class);
    }
}
