package com.example.agenticdev.llm;

import com.example.agenticdev.util.TextUtils;

public class MockLlmClient implements LlmClient {
    @Override
    public String complete(String systemPrompt, String userPrompt) {
        return "[mock-llm] 已根据提示完成分析：" + TextUtils.shortText(userPrompt, 160);
    }
}
