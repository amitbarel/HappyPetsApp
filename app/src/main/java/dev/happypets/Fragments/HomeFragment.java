package dev.happypets.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import dev.happypets.Adapters.GridAdapter;
import dev.happypets.Adapters.PetImgAdapter;
import dev.happypets.Database.DataManager;
import dev.happypets.Objects.AnimalType;
import dev.happypets.R;

public class HomeFragment extends Fragment {

        private RecyclerView questionUpdates;
        private GridView animalTypes;
        //    private DataManager dataManager;
        private ArrayList<AnimalType> kinds;

        public HomeFragment() {
            // Required empty public constructor
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_home, container, false);
            findViews(view);
            return view;
        }

        private void findViews(View view) {
            questionUpdates = view.findViewById(R.id.question_updates);
            animalTypes = view.findViewById(R.id.animals_choose);
            kinds = DataManager.getAnimalTypes();
            PetImgAdapter adapter = new PetImgAdapter(getContext(), kinds);
            GridAdapter gridAdapter = new GridAdapter(getContext(), adapter);
            animalTypes.setAdapter(gridAdapter);
        }
    }




