package com.example.agenticdev.domain;

public record CodeArtifact(
        String path,
        String language,
        String content
) {
}
