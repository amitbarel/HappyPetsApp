package dev.happypets.Database;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import dev.happypets.Objects.AnimalType;
import dev.happypets.Objects.Answer;
import dev.happypets.Objects.Question;
import dev.happypets.R;

public class DataManager {

    private static DataManager instance;
    private final FirebaseDatabase firebaseDatabase;
    private DatabaseReference questionsRef;
    private Context context;
    private ArrayList<AnimalType> animalTypes;
    private ArrayList<Question> questions;

    public DataManager(Context context) {
        this.firebaseDatabase = FirebaseDatabase.getInstance();
        this.questionsRef = firebaseDatabase.getReference("questions");
        this.context = context;
        this.questions = new ArrayList<>();
        this.animalTypes = getAnimalTypes();
    }

    public static synchronized DataManager getInstance(Context context) {
        if (instance == null) {
            instance = new DataManager(context.getApplicationContext());
        }
        return instance;
    }

    public static ArrayList<AnimalType> getAnimalTypes() {
        AnimalType cat = new AnimalType("Cat", R.drawable.cat_image);
        AnimalType dog = new AnimalType("Dog", R.drawable.dog_image);
        AnimalType hamster = new AnimalType("Hamster", R.drawable.hamster_image);
        AnimalType parrot = new AnimalType("Parrot", R.drawable.parrot_image);
        AnimalType rabbit = new AnimalType("Rabbit", R.drawable.rabbit_image);
        AnimalType fish = new AnimalType("Fish", R.drawable.fish_image);
        return new ArrayList<>(Arrays.asList(cat, dog, hamster, parrot, rabbit, fish));
    }

    public ArrayList<String> getAnimalNames() {
        return (ArrayList<String>) animalTypes.stream()
                .map(AnimalType::getKind)
                .collect(Collectors.toList());
    }

    public DataManager setAnimalTypes(ArrayList<AnimalType> animalTypes) {
        this.animalTypes = animalTypes;
        return this;
    }

    public ArrayList<Question> getQuestions() {
        if (!questions.isEmpty()){
            return questions;
        }
        return new ArrayList<>();
    }

    public DataManager setQuestions(ArrayList<Question> questions) {
        this.questions = questions;
        return this;
    }

    public void addNewQuestion(Question question){
        String questionId = questionsRef.push().getKey();
        if (questionId != null) {
            question.setQuestionId(questionId);
            questionsRef.child(questionId).setValue(question);
        }else {
            Log.d("Error", "Question id is null");
        }
    }

    public void addNewAnswer(String questionId, Answer answer){
        DatabaseReference ref = questionsRef.child(questionId);
        String AnswerId = ref.child("answers").push().getKey();
        if (AnswerId != null) {
            answer.setAnswerId(ref.child("answers").push().getKey());
            ref.child("answers").push().setValue(answer);
        }else{
            Log.d("Error", "AnswerID is null");
        }
    }

    public ArrayList<Question> getQuestionsByCategory(String category, final OnQuestionsRetrievedListener listener) {
        ArrayList<Question> relatedQuestions = new ArrayList<>();
        questionsRef.orderByChild("category").equalTo(category).get().addOnSuccessListener(dataSnapshot -> {
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                Question question = snapshot.getValue(Question.class);
                relatedQuestions.add(question);
            }
            listener.onQuestionsRetrieved(questions);
        });
        return relatedQuestions;
    }

    public interface OnQuestionsRetrievedListener {
        void onQuestionsRetrieved(ArrayList<Question> questions);
    }
}
