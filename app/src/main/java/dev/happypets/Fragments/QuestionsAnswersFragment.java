package dev.happypets.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.material.button.MaterialButton;

import dev.happypets.Activities.NewAnswerActivity;
import dev.happypets.Activities.NewQuestionActivity;
import dev.happypets.Adapters.QuestionAdapter;
import dev.happypets.CallBacks.QuestionCallBack;
import dev.happypets.Database.DataManager;
import dev.happypets.Objects.Question;
import dev.happypets.R;

public class QuestionsAnswersFragment extends Fragment {

    MaterialButton favQuestions, newQuestion;
    EditText searchSpecific;
    RecyclerView recyclerQuestions;
    QuestionAdapter questionAdapter;
    DataManager dataManager;

    public QuestionsAnswersFragment() {
        // Required empty public constructor
    }

    QuestionCallBack questionCallBack = new QuestionCallBack() {
        @Override
        public void favoriteClicked(Question question, int position) {

        }

        @Override
        public void onClicked(Question question, int position) {
            Intent intent = new Intent(getActivity(), NewAnswerActivity.class);
            startActivity(intent);
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
        recyclerQuestions.setLayoutManager(new LinearLayoutManager(getContext()));
        questionAdapter = new QuestionAdapter(getContext(), dataManager.getQuestions(), questionCallBack);
        recyclerQuestions.setAdapter(questionAdapter);
        newQuestion.setOnClickListener(v->{
            Intent intent = new Intent(getActivity(), NewQuestionActivity.class);
            startActivity(intent);
        });
    }

    private void findViews(View view) {
        favQuestions = view.findViewById(R.id.btn_fav_questions);
        newQuestion = view.findViewById(R.id.btn_new_question);
        searchSpecific = view.findViewById(R.id.et_specific);
        recyclerQuestions = view.findViewById(R.id.recycle_questions);
    }
}