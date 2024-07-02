package dev.happypets.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import dev.happypets.Adapters.GridAdapter;
import dev.happypets.Adapters.PetImgAdapter;
import dev.happypets.Database.DataManager;
import dev.happypets.Objects.AnimalType;
import dev.happypets.Objects.User;
import dev.happypets.R;

public class HomeFragment extends Fragment {

    private RecyclerView questionUpdates;
    private MaterialTextView welcomeMSG;
    private DataManager dataManager;
    private GridView animalTypes;
    //    private DataManager dataManager;
    private ArrayList<AnimalType> kinds;
    private User currentUser;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        dataManager = DataManager.getInstance(getContext());
        findViews(view);
        fetchCurrentUser();
        return view;
    }

    private void findViews(View view) {
        questionUpdates = view.findViewById(R.id.question_updates);
        animalTypes = view.findViewById(R.id.animals_choose);
        welcomeMSG = view.findViewById(R.id.welcome_msg);
        kinds = DataManager.getAnimalTypes();
        PetImgAdapter adapter = new PetImgAdapter(getContext(), kinds);
        GridAdapter gridAdapter = new GridAdapter(getContext(), adapter);
        animalTypes.setAdapter(gridAdapter);
    }

    private void fetchCurrentUser() {
        dataManager.getCurrentUserName(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    currentUser = dataSnapshot.getValue(User.class);
                    if (currentUser != null) {
                        welcomeMSG.setText(welcomeMSG.getText().toString().concat(currentUser.getName()));
                    } else {
                        welcomeMSG.setText(welcomeMSG.getText().toString().concat("Unknown User"));
                    }
                } else {
                    welcomeMSG.setText(welcomeMSG.getText().toString().concat("Unknown User"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("NewQuestionActivity", "Failed to read user data", databaseError.toException());
            }
        });
    }
}




