package com.example.agenticdev.config;

import com.example.agenticdev.llm.LlmClient;
import com.example.agenticdev.llm.MockLlmClient;
import com.example.agenticdev.llm.OpenAiCompatibleLlmClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(AgentProperties.class)
public class AgentConfig {
    @Bean
    public LlmClient llmClient(AgentProperties properties, ObjectMapper objectMapper) {
        if ("openai-compatible".equalsIgnoreCase(properties.getProvider())) {
            return new OpenAiCompatibleLlmClient(properties.getOpenai(), objectMapper);
        }
        return new MockLlmClient();
    }
}
