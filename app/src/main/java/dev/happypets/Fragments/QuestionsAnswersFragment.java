package dev.happypets.Fragments;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;

import dev.happypets.Adapters.QuestionAdapter;
import dev.happypets.CallBacks.QuestionCallBack;
import dev.happypets.Database.DataManager;
import dev.happypets.Objects.Question;
import dev.happypets.Objects.User;
import dev.happypets.R;

public class QuestionsAnswersFragment extends Fragment {

    MaterialButton favQuestions, newQuestion;
    SearchView searchSpecific;
    RecyclerView recyclerQuestions;
    QuestionAdapter questionAdapter;
    DataManager dataManager;
    ArrayList<Question> questionList;
    User currentUser;

    public QuestionsAnswersFragment() {
        // Required empty public constructor
    }

    QuestionCallBack questionCallBack = new QuestionCallBack() {
        @Override
        public void favoriteClicked(Question question, int position) {
            //The adapter handles it
        }

        @Override
        public void onClicked(Question question, int position) {
            //The adapter handles it
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_questions_answers, container, false);
        dataManager = DataManager.getInstance(getContext());
        findViews(view);
        initViews();
        fetchCurrentUser();
        return view;
    }

    private void initViews() {
        searchSpecific.clearFocus();
        searchSpecific.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }
        });

        recyclerQuestions.setLayoutManager(new LinearLayoutManager(getContext()));
        Bundle arguments = getArguments();
        if (arguments != null) {
            String specificAnimal = arguments.getString("kind");
            if (specificAnimal != null) {
                dataManager.getQuestionsByCategory(specificAnimal, specificQuestions -> {
                    if (specificQuestions.isEmpty()) {
                        Toast.makeText(getContext(), "No questions found", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d("Questions", specificQuestions.toString());
                    }
                    questionList = new ArrayList<>(specificQuestions);
                    setupAdapter(questionList);
                });
            }
        } else {
            questionList = new ArrayList<>(dataManager.getQuestions());
            setupAdapter(questionList);
        }

        newQuestion.setOnClickListener(v -> {
            Dialog dialog = new Dialog(getContext());
            dialog.setContentView(R.layout.dialogue_question);
            TextInputEditText title = dialog.findViewById(R.id.question_title);
            TextInputEditText body = dialog.findViewById(R.id.question_body);
            Spinner animalKind = dialog.findViewById(R.id.spinner_animal);
            MaterialButton publishButton = dialog.findViewById(R.id.btn_publish);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    getContext(),
                    android.R.layout.simple_spinner_item,
                    dataManager.getAnimalNames());
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            animalKind.setAdapter(adapter);
            publishButton.setOnClickListener(v1 -> {
                if (currentUser != null) {
                    Question newQuestion = new Question()
                            .setTitle(title.getText().toString())
                            .setText(body.getText().toString())
                            .setAskedTime(ZonedDateTime.now(ZoneId.of("Asia/Jerusalem")).toLocalTime().toString())
                            .setCategory(animalKind.getSelectedItem().toString())
                            .setAskedBy(currentUser);
                    dataManager.addNewQuestion(newQuestion);
                    dialog.dismiss();
                }
            });
            dialog.setCancelable(true);
            dialog.show();
        });
    }

    private void setupAdapter(ArrayList<Question> questions) {
        if (questionAdapter == null) {
            questionAdapter = new QuestionAdapter(getContext(), questions, questionCallBack);
            recyclerQuestions.setAdapter(questionAdapter);
        } else {
            questionAdapter.updateQuestions(questions);
            questionAdapter.notifyDataSetChanged();
        }
    }

    private void filterList(String text) {
        ArrayList<Question> filteredList = new ArrayList<>();
        for (Question question : dataManager.getQuestions()) {
            if (question.getTitle().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(question);
            }
        }
        if (filteredList.isEmpty()) {
            Toast.makeText(getContext(), "No questions found", Toast.LENGTH_SHORT).show();
        } else {
            setupAdapter(filteredList);
        }
    }

    private void findViews(View view) {
        favQuestions = view.findViewById(R.id.btn_fav_questions);
        newQuestion = view.findViewById(R.id.btn_new_question);
        searchSpecific = view.findViewById(R.id.search_view);
        recyclerQuestions = view.findViewById(R.id.recycle_questions);
    }

    private void fetchCurrentUser() {
        dataManager.getCurrentUserName(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    currentUser = dataSnapshot.getValue(User.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("NewQuestionActivity", "Failed to read user data", databaseError.toException());
            }
        });
    }
}