package dev.happypets.Database;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import dev.happypets.CallBacks.AnswerCallback;
import dev.happypets.Objects.AnimalType;
import dev.happypets.Objects.Answer;
import dev.happypets.Objects.Question;
import dev.happypets.Objects.User;
import dev.happypets.R;

public class DataManager {

    private final FirebaseAuth firebaseAuth;
    private static DataManager instance;
    private final FirebaseDatabase firebaseDatabase;
    private ArrayList<Question> questions;

    private final DatabaseReference questionsRef;
    private ArrayList<AnimalType> animalTypes;
    private FirebaseUser currentUser;

    public DataManager(Context context) {
        this.firebaseDatabase = FirebaseDatabase.getInstance();
        this.questionsRef = firebaseDatabase.getReference("Questions");
        this.questions = new ArrayList<>();
        this.animalTypes = getAnimalTypes();
        this.firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

    }

    public static synchronized DataManager getInstance(Context context) {
        if (instance == null) {
            instance = new DataManager(context.getApplicationContext());
        }
        return instance;
    }

    public static ArrayList<AnimalType> getAnimalTypes() {
        AnimalType cat = new AnimalType("Cat", R.drawable.cat);
        AnimalType dog = new AnimalType("Dog", R.drawable.dog);
        AnimalType hamster = new AnimalType("Hamster", R.drawable.hamster);
        AnimalType parrot = new AnimalType("Parrot", R.drawable.parrot);
        AnimalType rabbit = new AnimalType("Rabbit", R.drawable.rabbit);
        AnimalType fish = new AnimalType("Fish", R.drawable.clown_fish);
        return new ArrayList<>(Arrays.asList(cat, dog, hamster, parrot, rabbit, fish));
    }

    public String getCurrentUserId() {
        return this.currentUser.getUid();
    }

    public void getQuestions(OnDataChangeCallback<List<Question>> callback) {
        questionsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Question> questions = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    questions.add(snapshot.getValue(Question.class));
                }
                callback.onDataChange(questions);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onDataChange(Collections.emptyList());
            }
        });
    }

    public DataManager setAnimalTypes(ArrayList<AnimalType> animalTypes) {
        this.animalTypes = animalTypes;
        return this;
    }

    public void setQuestions(ArrayList<Question> questions) {
        this.questions = questions;
    }

    public void addNewQuestion(Question question, OnQuestionAddedCallback callback) {
        String questionId = questionsRef.push().getKey();
        if (questionId != null) {
            question.setQuestionId(questionId);
            questionsRef.child(questionId).setValue(question)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            callback.onQuestionAdded(question);
                        } else {
                            Log.d("Error", "Failed to add question");
                        }
                    });
        } else {
            Log.d("Error", "Question id is null");
        }
    }

    public void getQuestionById(String id, final OnQuestionRetrievedListener listener) {
        DatabaseReference ref = questionsRef.child(id);
        ref.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot dataSnapshot = task.getResult();
                if (dataSnapshot.exists()) {
                    Question question = getQuestion(dataSnapshot);
                    listener.onQuestionRetrieved(question);
                } else {
                    listener.onError(new Exception("Question not found"));
                }
            } else {
                listener.onError(task.getException());
            }
        });
    }


    public void getFavoriteQuestions(String uID, final OnQuestionsRetrievedListener listener) {
        ArrayList<Question> favoriteQuestions = new ArrayList<>();
        DatabaseReference favoritesRef = firebaseDatabase.getReference("Users").child(uID).child("favoriteQuestions");
        favoritesRef.get().addOnSuccessListener(dataSnapshot -> {
            if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Question question = snapshot.getValue(Question.class);
                    if (question != null) {
                        favoriteQuestions.add(question);
                    }
                }
            }
            listener.onQuestionsRetrieved(favoriteQuestions);
        }).addOnFailureListener(e -> {
            // Handle potential errors here
            Log.e("DataManager", "Failed to get favorite questions", e);
            listener.onQuestionsRetrieved(favoriteQuestions);
        });
    }

    public void getQuestionsByEmail(String email, final OnQuestionsRetrievedListener listener) {
        ArrayList<Question> myQuestions = new ArrayList<>();
        questionsRef.get().addOnSuccessListener(dataSnapshot -> {
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                Question question = getQuestion(snapshot);
                if (question.getAskedBy().getEmail().equals(email)) {
                    myQuestions.add(question);
                }
            }
            listener.onQuestionsRetrieved(myQuestions);
        });
    }

    public void getQuestionsByCategory(String category,
                                       final OnDataChangeCallback listener) {
        questionsRef.orderByChild("category").equalTo(category)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Question> relatedQuestions = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    relatedQuestions.add(dataSnapshot.getValue(Question.class));
                }
                listener.onDataChange(relatedQuestions);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onDataChange(Collections.emptyList());
            }
        });
    }

    @NonNull
    public Question getQuestion(DataSnapshot snapshot) {
        Question question = new Question();
        question.setQuestionId(snapshot.getKey())
                .setText(snapshot.child("text").getValue(String.class))
                .setTitle(snapshot.child("title").getValue(String.class))
                .setAskedBy(snapshot.child("askedBy").getValue(User.class))
                .setCategory(snapshot.child("category").getValue(String.class))
                .setRelatedAnswers(addAnswersManually(snapshot.child("relatedAnswers")));
        return question;
    }

    public ArrayList<Answer> addAnswersManually(DataSnapshot relatedAnswers) {
        ArrayList<Answer> answers = new ArrayList<>();
        for (DataSnapshot snapshot : relatedAnswers.getChildren()) {
            Answer answer = snapshot.getValue(Answer.class);
            answers.add(answer);
        }
        return answers;
    }

    public ArrayList<String> getAnimalNames() {
        return (ArrayList<String>) animalTypes.stream()
                .map(AnimalType::getKind)
                .collect(Collectors.toList());
    }


    public void getCurrentUserName(ValueEventListener listener) {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            DatabaseReference userRef = firebaseDatabase.getReference("Users").child(currentUser.getUid());
            userRef.addListenerForSingleValueEvent(listener);
        }
    }

    public void addNewAnswer(String questionId, Answer answer) {
        DatabaseReference answersRef = questionsRef.child(questionId).child("relatedAnswers").push();
        String answerId = answersRef.getKey();
        if (answerId == null) return;

        answer.setAnswerId(answerId);
        answersRef.setValue(answer).addOnCompleteListener(task -> {
            if (!task.isSuccessful()) return;

            getQuestionById(questionId, new OnQuestionRetrievedListener() {
                @Override
                public void onQuestionRetrieved(Question question) {
                    if (question == null) return;
                    question.addAnswer(answer);
                    questionsRef.child(questionId).setValue(question);
                }

                @Override
                public void onError(Exception e) {
                    Log.e("DataManager", "Error adding answer to question: " + e.getMessage());
                }
            });
        });
    }


    public void getAnswersByQuestionId(String questionId, AnswerCallback callback) {
        questionsRef.child(questionId).child("relatedAnswers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Answer> answers = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Answer answer = snapshot.getValue(Answer.class);
                    if (answer != null) {
                        answers.add(answer);
                        Log.d("DataManager", "Retrieved answer: " + answer.getText());
                    }
                }
                callback.onCallback(answers); // Call the callback with the retrieved answers
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("DataManager", "Error fetching answers: " + databaseError.getMessage());
            }
        });
    }


    public void getCurrentUserPets(ValueEventListener listener) {
        if (currentUser != null) {
            DatabaseReference petsRef = firebaseDatabase.getReference("Users").child(currentUser.getUid()).child("pet");
            petsRef.addListenerForSingleValueEvent(listener);
        } else {
            Log.e("DataManager", "Current user is null");
        }
    }

    public void addFavoriteQuestion(String userId, Question question, Runnable onComplete) {
        DatabaseReference favoritesRef = firebaseDatabase.getReference("Users").child(userId).child("favoriteQuestions");
        favoritesRef.child(question.getQuestionId()).setValue(question).addOnCompleteListener(task -> onComplete.run());
    }

    public void removeFavoriteQuestion(String userId, Question question, Runnable onComplete) {
        DatabaseReference favoritesRef = firebaseDatabase.getReference("Users").child(userId).child("favoriteQuestions");
        favoritesRef.child(question.getQuestionId()).removeValue().addOnCompleteListener(task -> onComplete.run());
    }

    public void isQuestionInFavorites(String userId, Question question, OnBooleanResultListener listener) {
        DatabaseReference favoritesRef = firebaseDatabase.getReference("Users").child(userId).child("favoriteQuestions");
        favoritesRef.child(question.getQuestionId()).get().addOnSuccessListener(dataSnapshot -> {
            listener.onResult(dataSnapshot.exists());
        }).addOnFailureListener(e -> {
            Log.e("DataManager", "Failed to check if question is in favorites", e);
            listener.onResult(false);
        });
    }

    public void listenForFavoriteQuestions(String userId, ValueEventListener listener) {
        DatabaseReference favoritesRef = firebaseDatabase.getReference("Users").child(userId).child("favoriteQuestions");
        favoritesRef.addValueEventListener(listener);
    }

    public interface OnQuestionsRetrievedListener {
        void onQuestionsRetrieved(ArrayList<Question> questions);
    }

    public interface OnQuestionRetrievedListener {
        void onQuestionRetrieved(Question question);

        void onError(Exception e);
    }

    public interface OnBooleanResultListener {
        void onResult(boolean isInFavorites);
    }

    public interface OnDataChangeCallback<T> {
        void onDataChange(T data);
    }

    public interface OnQuestionAddedCallback {
        void onQuestionAdded(Question question);
    }
}
