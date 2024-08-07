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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dev.happypets.Activities.AddPetDialogFragment;
import dev.happypets.Adapters.PetAdapter;
import dev.happypets.Adapters.QuestionAdapter;
import dev.happypets.CallBacks.QuestionCallBack;
import dev.happypets.Database.DataManager;
import dev.happypets.Objects.Pet;
import dev.happypets.Objects.Question;
import dev.happypets.Objects.User;
import dev.happypets.R;
import androidx.fragment.app.FragmentManager;
import dev.happypets.Activities.AddPetDialogFragment;

public class ProfileFragment extends Fragment {

    private RecyclerView my_pets, my_questions;
    private ArrayList<Question> myQuestions;
    private ArrayList<Pet> myPets;
    private QuestionAdapter questionAdapter;
    private PetAdapter petAdapter;
    private MaterialTextView userTitle, vetTitle;
    private MaterialButton btn_update;
    private DataManager dataManager;
    private FirebaseUser firebaseUser;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        dataManager = DataManager.getInstance(getContext());
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        my_pets = view.findViewById(R.id.my_pets);
        my_pets.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        petAdapter = new PetAdapter(getContext(), new ArrayList<>());
        my_pets.setAdapter(petAdapter);

        findViews(view);
        setupRecyclerViews();
        fetchUserPets();

        return view;
    }

    private void findViews(View view) {
        my_pets = view.findViewById(R.id.my_pets);
        my_questions = view.findViewById(R.id.my_questions);
        userTitle = view.findViewById(R.id.header_questions);
        vetTitle = view.findViewById(R.id.header_questions_v2);
        btn_update = view.findViewById(R.id.btn_update);
        btn_update.setOnClickListener(v -> showAddPetDialog());

    }

    private void showAddPetDialog() {
//        AddPetDialogFragment addPetDialogFragment = new AddPetDialogFragment();
//        addPetDialogFragment.show(getParentFragmentManager(), "AddPetDialogFragment");
    }

    private void fetchUserPets() {
        dataManager.getCurrentUserPets(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ArrayList<Pet> pets = new ArrayList<>();
                    for (DataSnapshot petSnapshot : dataSnapshot.getChildren()) {
                        Pet pet = petSnapshot.getValue(Pet.class);
                        if (pet != null) {
                            pets.add(pet);
                        }
                    }
                    petAdapter.updatePets(pets);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ProfileFragment", "Failed to load user pets", databaseError.toException());
            }
        });
    }

    private void setupRecyclerViews() {

        // Setup RecyclerView for questions
        my_questions.setLayoutManager(new LinearLayoutManager(getContext()));
        myQuestions = new ArrayList<>();
        questionAdapter = new QuestionAdapter(getContext(), myQuestions, questionCallBack);
        my_questions.setAdapter(questionAdapter);

        dataManager.getKindOfUser(firebaseUser.getUid(), new DataManager.KindOfUserCallback() {
            @Override
            public void onResult(String kindOfUser) {
                if (kindOfUser.equals("vet")) {
                    userTitle.setVisibility(View.GONE);
                    vetTitle.setVisibility(View.VISIBLE);
                    fetchVetQuestions();

                } else if (kindOfUser.equals("user")) {
                    userTitle.setVisibility(View.VISIBLE);
                    vetTitle.setVisibility(View.GONE);
                    fetchCurrentUser();
                }
            }

            @Override
            public void onError(Exception e) {

            }
        });

        my_pets.setLayoutManager(new LinearLayoutManager(getContext()));
        myPets = new ArrayList<>();
        petAdapter = new PetAdapter(getContext(), myPets);
        my_pets.setAdapter(petAdapter);
    }

    private void fetchVetQuestions() {
        String uid = firebaseUser.getUid();
        if (uid == null) {
            Log.e("ProfileFragment", "User ID is null");
            return;
        }

        if (myQuestions == null) {
            myQuestions = new ArrayList<>();
        }

        dataManager.getQuestionsAnsweredBySpecific(uid, data -> {
            if (data != null) {
                myQuestions.clear();
                myQuestions.addAll(data);
                questionAdapter.notifyDataSetChanged();
            } else {
                Log.d("ProfileFragment", "No questions found for vet");
            }
        });
    }

    private void fetchCurrentUser() {

        dataManager.getCurrentUserName(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    currentUser = dataSnapshot.getValue(User.class);
                    if (currentUser != null) {
                        Log.d("ProfileFragment", "User email: " + currentUser.getEmail());
                        fetchUserQuestions(currentUser.getEmail());
                    } else {
                        Log.d("ProfileFragment", "Current user is null");
                    }
                } else {
                    Log.d("ProfileFragment", "No user data exists");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("ProfileFragment", "Failed to read user data", databaseError.toException());
            }
        });
    }

    private void fetchUserQuestions(String email) {
        dataManager.getQuestionsByEmail(email, questions -> {
            if (questions != null) {
                myQuestions.clear();
                myQuestions.addAll(questions);
                questionAdapter.notifyDataSetChanged();
            } else {
                Log.d("ProfileFragment", "No questions found for user");
            }
        });
    }
}
