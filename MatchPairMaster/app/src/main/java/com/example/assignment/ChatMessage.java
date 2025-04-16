package com.example.assignment;

import java.nio.file.Path;

public class ChatMessage {

    private Participant participant;
    private String text;
    private boolean isPending;

    // Constructor
    public ChatMessage(Participant participant, String text, boolean isPending) {
        this.participant = participant;
        this.text = text;
        this.isPending = isPending;
    }

    // Getters and Setters
    public Participant getParticipant() {
        return participant;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isPending() {
        return isPending;
    }

    public void setPending(boolean pending) {
        isPending = pending;
    }
}
