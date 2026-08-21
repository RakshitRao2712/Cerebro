package com.example.Cerebro.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GeminiAPITest {
    private final ChatClient chatClient;

    public GeminiAPITest(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @GetMapping("/api/test_gemini")
    public String testGeminiAPI() {
        return chatClient.prompt()
                .user("Hey gemini are we connected")
                .call()
                .content();
    }

    @GetMapping("/api/test_gemini_full")
    public String testGeminiAPI_full(@RequestParam(value = "message") String message) {
        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }

}
