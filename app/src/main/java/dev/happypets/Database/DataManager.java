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
    import java.util.stream.Collectors;

    import dev.happypets.CallBacks.AnswerCallback;
    import dev.happypets.Objects.AnimalType;
    import dev.happypets.Objects.Answer;
    import dev.happypets.Objects.Question;
    import dev.happypets.Objects.User;
    import dev.happypets.R;

    public class DataManager {

        private FirebaseAuth firebaseAuth;
        private static DataManager instance;
        private final FirebaseDatabase firebaseDatabase;
        private ArrayList<Question> questions;

        private final DatabaseReference questionsRef;
        private ArrayList<AnimalType> animalTypes;

        public DataManager(Context context) {
            this.firebaseDatabase = FirebaseDatabase.getInstance();
            this.questionsRef = firebaseDatabase.getReference("questions");
            this.questions = new ArrayList<>();
            this.animalTypes = getAnimalTypes();
            this.firebaseAuth = FirebaseAuth.getInstance();
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

        public ArrayList<Question> getQuestions() {
            return questions;
        }
        public DataManager setAnimalTypes(ArrayList<AnimalType> animalTypes) {
            this.animalTypes = animalTypes;
            return this;
        }

        public void setQuestions(ArrayList<Question> questions) {
            this.questions = questions;
        }

        public void addNewQuestion(Question question){
            String questionId = questionsRef.push().getKey();
            if (questionId != null) {
                question.setQuestionId(questionId);
                questionsRef.child(questionId).setValue(question);
            } else {
                Log.d("Error", "Question id is null");
            }
        }

//        public void addNewAnswer(String questionId, Answer answer){
//            DatabaseReference ref = questionsRef.child(questionId);
//            String answerId = ref.child("answers").push().getKey();
//            if (answerId != null) {
//                answer.setAnswerId(answerId);
//                ref.child("answers").push().setValue(answer);
//                addToRelatedQuestions(questionId, answer);
//            } else {
//                Log.d("Error", "AnswerID is null");
//            }
//        }

        public void getQuestionById(String id, final OnQuestionRetrievedListener listener) {
            DatabaseReference ref = questionsRef.child(id);
            ref.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DataSnapshot dataSnapshot = task.getResult();
                    Question question = dataSnapshot.getValue(Question.class);
                    listener.onQuestionRetrieved(question);
                } else {
                    listener.onError(task.getException());
                }
            });
        }

        public ArrayList<Question> getQuestionsByCategory(String category, final OnQuestionsRetrievedListener listener) {
            ArrayList<Question> relatedQuestions = new ArrayList<>();
            questionsRef.orderByChild("category").equalTo(category).get().addOnSuccessListener(dataSnapshot -> {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Question question = snapshot.getValue(Question.class);
                    relatedQuestions.add(question);
                }
                listener.onQuestionsRetrieved(relatedQuestions);
            });
            return relatedQuestions;
        }



        public void addToRelatedQuestions(String questionId, Answer answer) {
            questions.stream().filter(q -> q.getQuestionId().equals(questionId)).findFirst().ifPresent(question -> question.addAnswer(answer));
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
        public void getAnswersByQuestionId(String questionId, AnswerCallback callback) {
            questionsRef.child(questionId).child("answers").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ArrayList<Answer> answers = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Answer answer = snapshot.getValue(Answer.class);
                        if (answer != null) {
                            answers.add(answer);
                        }
                    }
                    callback.onCallback(answers);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("DataManager", "Error fetching answers: " + databaseError.getMessage());
                }
            });
        }
        public void addNewAnswer(String questionId, Answer answer) {
            DatabaseReference ref = questionsRef.child(questionId).child("answers").push();
            String answerId = ref.getKey();
            if (answerId != null) {
                answer.setAnswerId(answerId);
                ref.setValue(answer)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Update question with the new answer locally
                                getQuestionById(questionId, new OnQuestionRetrievedListener() {
                                    @Override
                                    public void onQuestionRetrieved(Question question) {
                                        if (question != null) {
                                            question.addAnswer(answer);
                                            questionsRef.child(questionId).setValue(question)
                                                    .addOnCompleteListener(task1 -> {
                                                        if (task1.isSuccessful()) {
                                                            Log.d("DataManager", "Question updated with new answer");
                                                        } else {
                                                            Log.e("DataManager", "Error updating question: " + task1.getException().getMessage());
                                                        }
                                                    });
                                        }
                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        Log.e("DataManager", "Error fetching question: " + e.getMessage());
                                    }
                                });
                            } else {
                                Log.e("DataManager", "Error adding answer: " + task.getException().getMessage());
                            }
                        });
            } else {
                Log.e("DataManager", "AnswerID is null");
            }
        }


        public void addNewAnswert(String questionId, Answer answer) {
            questionsRef.child("questions").child(questionId).child("answers").push().setValue(answer)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            getQuestionById(questionId, new OnQuestionRetrievedListener() {
                                @Override
                                public void onQuestionRetrieved(Question question) {
                                    if (question != null) {
                                        if (question.getRelatedAnswers() == null) {
                                            question.setRelatedAnswers(new ArrayList<>());
                                        }
                                        question.addAnswer(answer);
                                        questionsRef.child("questions").child(questionId).setValue(question);
                                    }
                                }

                                @Override
                                public void onError(Exception e) {

                                }
                            });
                        }
                    });
        }


        public interface OnQuestionsRetrievedListener {
            void onQuestionsRetrieved(ArrayList<Question> questions);
        }

        public interface OnQuestionRetrievedListener {
            void onQuestionRetrieved(Question question);
            void onError(Exception e);
        }
    }
