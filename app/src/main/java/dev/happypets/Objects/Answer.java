package dev.happypets.Objects;

import java.util.UUID;

public class Answer {
    private String answerId;
    private String title;
    private String text;
    private String relatedQuestionId;

    public Answer() {
    }

    public Answer(String title, String text, String relatedQuestionId) {
        this.answerId = UUID.randomUUID().toString();
        this.title = title;
        this.text = text;
        this.relatedQuestionId = relatedQuestionId;
    }

    public String getAnswerId() {
        return answerId;
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

    public String getRelatedQuestionId() {
        return relatedQuestionId;
    }

    public Answer setRelatedQuestionId(String relatedQuestionId) {
        this.relatedQuestionId = relatedQuestionId;
        return this;
    }
}
