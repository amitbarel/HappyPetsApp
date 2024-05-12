package dev.happypets.Database;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Arrays;

import dev.happypets.Objects.AnimalType;
import dev.happypets.Objects.Answer;
import dev.happypets.Objects.Question;
import dev.happypets.R;

public class DataManager {

    private final FirebaseDatabase firebaseDatabase;
    private Context context;
    private ArrayList<AnimalType> animalTypes;
    private ArrayList<Question> questions;

    public DataManager(Context context) {
        this.firebaseDatabase = FirebaseDatabase.getInstance();
        this.context = context;
        this.questions = new ArrayList<>();
        this.animalTypes = getAnimalTypes();
    }

    public ArrayList<AnimalType> getAnimalTypes() {
        AnimalType cat = new AnimalType("Cat", R.drawable.cat_image);
        AnimalType dog = new AnimalType("Dog", R.drawable.dog_image);
        AnimalType hamster = new AnimalType("Hamster", R.drawable.hamster_image);
        AnimalType parrot = new AnimalType("Parrot", R.drawable.parrot_image);
        AnimalType rabbit = new AnimalType("Rabbit", R.drawable.rabbit_image);
        AnimalType fish = new AnimalType("Fish", R.drawable.fish_image);
        return new ArrayList<>(Arrays.asList(cat, dog, hamster, parrot, rabbit, fish));
    }

    public DataManager setAnimalTypes(ArrayList<AnimalType> animalTypes) {
        this.animalTypes = animalTypes;
        return this;
    }

    public ArrayList<Question> getQuestions() {
        return questions;
    }

    public DataManager setQuestions(ArrayList<Question> questions) {
        this.questions = questions;
        return this;
    }

    public void addNewQuestion(Question question){
        DatabaseReference ref = firebaseDatabase.getReference("question").child(question.getQuestionId());
        ref.child("title").setValue(question.getTitle());
        ref.child("category").setValue(question.getCategory());
        ref.child("text").setValue(question.getText());
    }

    public void addNewAnswer(Answer answer){
        DatabaseReference ref = firebaseDatabase.getReference("answer").child(answer.getAnswerId());
        ref.child("title").setValue(answer.getTitle());
        ref.child("text").setValue(answer.getText());
        ref.child("idQuestion").setValue(answer.getRelatedQuestionId());
    }

    public ArrayList<Question> getQuestionsByCategory(String category) {
        ArrayList<Question> relatedQuestions = new ArrayList<>();
        for (Question question : questions) {
            if (question.getCategory().equals(category)) {
                relatedQuestions.add(question);
            }
        }
        return relatedQuestions;
    }
}
