package com.example.agenticdev.llm;

public interface LlmClient {
    String complete(String systemPrompt, String userPrompt);
}
