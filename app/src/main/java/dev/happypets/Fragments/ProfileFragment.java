package dev.happypets.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import dev.happypets.Adapters.PetAdapter;
import dev.happypets.Adapters.QuestionAdapter;
import dev.happypets.CallBacks.QuestionCallBack;
import dev.happypets.Database.DataManager;
import dev.happypets.Objects.Question;
import dev.happypets.Objects.User;
import dev.happypets.R;

public class ProfileFragment extends Fragment {

    private RecyclerView my_pets, my_questions;
    private MaterialButton btn_update;
    private ArrayList<Question> myQuestions;
    private ArrayList<String> myPetTypes; // Assuming this will hold pet types
    private User currentUser;
    private QuestionAdapter questionAdapter;
    private DataManager dataManager;

    public ProfileFragment() {
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
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        dataManager = DataManager.getInstance(getContext());
        findViews(view);
        fetchCurrentUser();
        return view;
    }

    private void findViews(View view) {
        my_pets = view.findViewById(R.id.my_pets);
        btn_update = view.findViewById(R.id.btn_update);
        my_questions = view.findViewById(R.id.my_questions);
    }


    private void fetchCurrentUser() {
        my_questions.setLayoutManager(new LinearLayoutManager(getContext()));
        myQuestions = new ArrayList<>();
        questionAdapter = new QuestionAdapter(getContext(), myQuestions, questionCallBack);
        dataManager.getCurrentUserName(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    currentUser = dataSnapshot.getValue(User.class);
                    if (currentUser != null) {
                        Log.d("Email", currentUser.getEmail());
                        dataManager.getQuestionsByEmail(currentUser.getEmail(), questions -> {
                            if (questions != null){
                                questionAdapter.updateQuestions(questions);
                                my_questions.setAdapter(questionAdapter);
                                Log.d("NewQuestionActivity", "Questions: " + questions.size());
                                questionAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("NewQuestionActivity", "Failed to read user data", databaseError.toException());
            }
        });
    }


}
