package com.example.Cerebro.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import com.example.Cerebro.record.GeneratedArtifact;
import com.example.Cerebro.record.ToolEnum;

@Service
public class GeneratorAgent {

    private final ChatClient chatClient;

    public GeneratorAgent(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public GeneratedArtifact generate(String userPrompt, String priorFeedback, ToolEnum tool) {
        String systemInstructions = switch (tool) {
            case TERRAFORM -> """
                    You are a senior DevOps engineer.
                    Generate ONLY raw Terraform HCL code for the user's request.
                    Output raw HCL only. No markdown fences, no explanations.
                    Always include a "filename" of "main.tf".
                    """;
            case ANSIBLE -> """
                    You are a senior DevOps engineer.
                    Generate ONLY a raw Ansible playbook in YAML for the user's request.
                    Output raw YAML only. No markdown fences, no explanations.
                    Always include a "filename" of "playbook.yml".
                    """;
            case KUBERNETES -> """
                    You are a senior DevOps engineer.
                    Generate ONLY a raw Kubernetes manifest in YAML for the user's request.
                    Output raw YAML only. No markdown fences, no explanations.
                    Always include a "filename" of "deployment.yaml".
                    """;
            case DOCKERFILE -> """
                    You are a senior DevOps engineer.
                    Generate ONLY a raw Dockerfile for the user's request.
                    Output raw Dockerfile content only. No markdown fences, no explanations.
                    Always include a "filename" of "Dockerfile".
                    """;
            default -> throw new IllegalArgumentException("Unsupported tool: " + tool);
        };

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
