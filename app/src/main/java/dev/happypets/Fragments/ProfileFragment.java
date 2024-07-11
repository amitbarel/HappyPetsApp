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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import dev.happypets.Adapters.PetAdapter;
import dev.happypets.Adapters.QuestionAdapter;
import dev.happypets.CallBacks.QuestionCallBack;
import dev.happypets.Database.DataManager;
import dev.happypets.Objects.Pet;
import dev.happypets.Objects.Question;
import dev.happypets.Objects.User;
import dev.happypets.R;

public class ProfileFragment extends Fragment {

    private RecyclerView my_pets, my_questions;
    private ArrayList<Question> myQuestions;
    private ArrayList<Pet> myPets;
    private QuestionAdapter questionAdapter;
    private PetAdapter petAdapter;
    private DataManager dataManager;
    private User currentUser;

    public ProfileFragment() {
        // Required empty public constructor
    }

    QuestionCallBack questionCallBack = new QuestionCallBack() {
        @Override
        public void favoriteClicked(Question question, int position) {
            // The adapter handles it
        }

        @Override
        public void onClicked(Question question, int position) {
            // The adapter handles it
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        dataManager = DataManager.getInstance(getContext());
        findViews(view);
        setupRecyclerViews();
        fetchCurrentUser();
        return view;
    }

    private void findViews(View view) {
        my_pets = view.findViewById(R.id.my_pets);
        my_questions = view.findViewById(R.id.my_questions);
    }

    private void setupRecyclerViews() {
        // Setup RecyclerView for questions
        my_questions.setLayoutManager(new LinearLayoutManager(getContext()));
        myQuestions = new ArrayList<>();
        questionAdapter = new QuestionAdapter(getContext(), myQuestions, questionCallBack);
        my_questions.setAdapter(questionAdapter);

        // Setup RecyclerView for pets
        my_pets.setLayoutManager(new LinearLayoutManager(getContext()));
        myPets = new ArrayList<>();
        petAdapter = new PetAdapter(getContext(), myPets);
        my_pets.setAdapter(petAdapter);
    }

    private void fetchCurrentUser() {
        // Fetch current user's name from Firebase
        dataManager.getCurrentUserName(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    currentUser = dataSnapshot.getValue(User.class);
                    if (currentUser != null) {
                        Log.d("Email", currentUser.getEmail());
                        // Fetch questions posted by the current user
                        dataManager.getQuestionsByEmail(currentUser.getEmail(), questions -> {
                            if (questions != null) {
                                myQuestions.clear();
                                myQuestions.addAll(questions);
                                questionAdapter.notifyDataSetChanged();
                            }
                        });
                        // Fetch pets belonging to the current user
                        fetchUserPets();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("ProfileFragment", "Failed to read user data", databaseError.toException());
            }
        });
    }

    private void fetchUserPets() {
        // Fetch pets belonging to the current user from Firebase
        dataManager.getCurrentUserPets(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    myPets.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Pet pet = snapshot.getValue(Pet.class);
                        if (pet != null) {
                            Log.d("ProfileFragment", "Pet name: " + pet.getName());
                            myPets.add(pet);
                        }
                    }
                    petAdapter.notifyDataSetChanged();
                } else {
                    Log.d("ProfileFragment", "No pets found for user");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ProfileFragment", "Failed to fetch pets", databaseError.toException());
            }
        });
    }
}
