package dev.happypets.Objects;

import java.time.LocalTime;
import java.util.ArrayList;

public class Question {
    private String questionId;
    private String category;
    private String title;
    private String text;
    private ArrayList<Answer> relatedAnswers;
    private String askedTime;
    private User askedBy;

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

    public User getAskedBy() {
        return askedBy;
    }

    public Question setAskedBy(User askedBy) {
        this.askedBy = askedBy;
        return this;
    }

    public String getQuestionId() {
        return questionId;
    }

    public Question setQuestionId(String id){
        this.questionId = id;
        return this;
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

    public void addAnswer(Answer answer) {
        if (relatedAnswers == null) {
            relatedAnswers = new ArrayList<>();
        }
        relatedAnswers.add(answer);
    }

    public Question setRelatedAnswers(ArrayList<Answer> relatedAnswers) {
        this.relatedAnswers = relatedAnswers;
        return this;
    }



}
