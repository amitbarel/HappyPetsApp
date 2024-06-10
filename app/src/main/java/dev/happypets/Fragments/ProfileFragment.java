package dev.happypets.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

import dev.happypets.Adapters.QuestionAdapter;
import dev.happypets.Database.DataManager;
import dev.happypets.Objects.Question;
import dev.happypets.R;

public class ProfileFragment extends Fragment {

    private MaterialTextView header_profile;
    private RecyclerView my_pets;
    private MaterialButton btn_update;
    private MaterialTextView header_questions;
    private RecyclerView my_questions;

    private ArrayList<Question> myQuestions;

    private QuestionAdapter questionAdapter;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        findViews(view);
//        myQuestions = DataManager.getInstance().getMyQuestions();
        return view;
    }

    private void findViews(View view) {
        header_profile = view.findViewById(R.id.header_profile);
        my_pets = view.findViewById(R.id.my_pets);
        btn_update = view.findViewById(R.id.btn_update);
        header_questions = view.findViewById(R.id.header_questions);
        my_questions = view.findViewById(R.id.my_questions);
    }
}