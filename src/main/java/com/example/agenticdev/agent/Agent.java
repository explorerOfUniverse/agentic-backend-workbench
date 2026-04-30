package com.example.agenticdev.agent;

public interface Agent<I, O> {
    String name();

    O execute(I input);
}
