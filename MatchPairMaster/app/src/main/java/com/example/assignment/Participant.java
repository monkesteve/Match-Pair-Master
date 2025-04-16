package com.example.assignment;


public enum Participant {

    USER("User"),        // Represents the user sending a message
    MODEL("Model"),      // Represents the AI model responding
    ERROR("Error");      // Represents an error message or state

    private final String name;

    // Constructor
    Participant(String name) {
        this.name = name;
    }

    // Getter
    public String getName() {
        return name;
    }
}
