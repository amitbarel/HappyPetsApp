package dev.happypets.Objects;

import java.util.UUID;

public class Answer {
    private String answerId;
    private String title;
    private String text;
    private String answeredByID;

    public Answer() {
    }

    public Answer(String title, String text) {
        this.answerId = UUID.randomUUID().toString();
        this.title = title;
        this.text = text;
    }

    public String getAnswerId() {
        return answerId;
    }

    public Answer setAnswerId(String answerId) {
        this.answerId = answerId;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Answer setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getText() {
        return text;
    }

    public Answer setText(String text) {
        this.text = text;
        return this;
    }

    public String getAnsweredByID() {
        return answeredByID;
    }

    public Answer setAnsweredByID(String answeredByID) {
        this.answeredByID = answeredByID;
        return this;
    }
}
