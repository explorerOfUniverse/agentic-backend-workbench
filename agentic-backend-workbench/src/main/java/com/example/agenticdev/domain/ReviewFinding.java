package com.example.agenticdev.domain;

public record ReviewFinding(
        Severity severity,
        String location,
        String message,
        String suggestion
) {
    public enum Severity {
        INFO, WARNING, CRITICAL
    }
}
