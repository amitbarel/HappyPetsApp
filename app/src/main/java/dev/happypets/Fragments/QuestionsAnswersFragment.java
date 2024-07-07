package dev.happypets.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

import dev.happypets.Activities.NewQuestionActivity;
import dev.happypets.Adapters.QuestionAdapter;
import dev.happypets.CallBacks.QuestionCallBack;
import dev.happypets.Database.DataManager;
import dev.happypets.Objects.Question;
import dev.happypets.R;

public class QuestionsAnswersFragment extends Fragment {

    MaterialButton favQuestions, newQuestion;
    SearchView searchSpecific;
    RecyclerView recyclerQuestions;
    QuestionAdapter questionAdapter;
    DataManager dataManager;
    ArrayList<Question> questionList;

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
            Intent intent = new Intent(getActivity(), NewQuestionActivity.class);
            startActivity(intent);
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
}