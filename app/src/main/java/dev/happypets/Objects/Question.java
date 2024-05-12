package dev.happypets.Objects;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.UUID;

public class Question {
    private String questionId;
    private String category;
    private String title;
    private String text;
    private ArrayList<Answer> relatedAnswers;
    private String askedTime;
    private boolean isFavorite = false;

    public Question() {
    }

    public Question(String questionId, String category, String title, String text) {
        this.questionId = questionId;
        this.category = category;
        this.title = title;
        this.text = text;
        this.relatedAnswers = new ArrayList<>();
        this.askedTime = LocalTime.now().toString();
    }

    public Question setQuestionId(String id){
        this.questionId = id;
        return this;
    }

    public String getQuestionId() {
        return questionId;
    }

    public String getCategory() {
        return category;
    }

    public Question setCategory(String category) {
        this.category = category;
        return this;
    }

    public String getAskedTime() {
        return askedTime;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public String getTitle() {
        return title;
    }

    public Question setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getText() {
        return text;
    }

    public Question setText(String text) {
        this.text = text;
        return this;
    }

    public ArrayList<Answer> getRelatedAnswers() {
        return relatedAnswers;
    }

    public Question setRelatedAnswers(ArrayList<Answer> relatedAnswers) {
        this.relatedAnswers = relatedAnswers;
        return this;
    }
}
