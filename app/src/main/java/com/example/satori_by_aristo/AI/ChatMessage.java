package com.example.satori_by_aristo.AI;

public class ChatMessage {
    public static final int TYPE_USER = 1;
    public static final int TYPE_BOT = 2;

    private String text;
    private int viewType;

    public ChatMessage(String text, int viewType) {
        this.text = text;
        this.viewType = viewType;
    }

    public String getText() {
        return text;
    }

    public int getViewType() {
        return viewType;
    }
}