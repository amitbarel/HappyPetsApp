package dev.happypets.Fragments;

import static android.app.Activity.RESULT_OK;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import dev.happypets.Adapters.PetAdapter;
import dev.happypets.Adapters.QuestionAdapter;
import dev.happypets.CallBacks.QuestionCallBack;
import dev.happypets.Database.DataManager;
import dev.happypets.Objects.AnimalType;
import dev.happypets.Objects.Pet;
import dev.happypets.Objects.Question;
import dev.happypets.Objects.User;
import dev.happypets.Objects.Vet;
import dev.happypets.R;

public class ProfileFragment extends Fragment {

    private MaterialCardView vet_card;
    private RecyclerView my_pets, my_questions;
    private ArrayList<Question> myQuestions;
    private ArrayList<Pet> myPets;
    private QuestionAdapter questionAdapter;
    private PetAdapter petAdapter;
    private MaterialTextView userTitle, vetTitle, headerUser, headerVet;
    private MaterialButton btn_update;
    private DataManager dataManager;
    private FirebaseUser firebaseUser;
    private User currentUser;

    //Pet Dialog
    private Uri photoUri;
    private TextInputEditText et_name;
    private Spinner pet_type;
    private MaterialButton upload_photo;
    private MaterialTextView status;
    private MaterialButton btn_save;

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

        findViews(view);
        setupVetCard();
        setupRecyclerViews();

        return view;
    }

    private void setupVetCard() {
        dataManager.getKindOfUser(firebaseUser.getUid(), new DataManager.KindOfUserCallback() {
            @Override
            public void onResult(String kindOfUser) {
                if (kindOfUser.equals("vet")) {
                    headerVet.setVisibility(View.VISIBLE);
                    headerUser.setVisibility(View.INVISIBLE);
                    vet_card.setVisibility(View.VISIBLE);
                    View view = vet_card.getChildAt(0);
                    MaterialTextView vetName = view.findViewById(R.id.txt_vetName);
                    MaterialTextView phone = view.findViewById(R.id.txt_phone);
                    MaterialTextView address = view.findViewById(R.id.txt_address);
                    MaterialTextView email = view.findViewById(R.id.txt_email);
                    MaterialTextView licenseNumber = view.findViewById(R.id.txt_license_number);

                    DataManager.getInstance(getContext()).getCurrentUserName(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (!snapshot.exists()) {
                                return;
                            }
                            Vet vet = snapshot.getValue(Vet.class);
                            if (vet == null) {
                            } else {
                                vetName.setText(vet.getName());
                                phone.setText(vet.getPhone());
                                address.setText(vet.getAddress());
                                email.setText(vet.getEmail());
                                licenseNumber.setText(vet.getLicenseNumber());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.d("ProfileFragment", "Failed to read vet data", error.toException());
                        }
                    });


                } else if (kindOfUser.equals("user")) {
                    headerVet.setVisibility(View.INVISIBLE);
                    headerUser.setVisibility(View.VISIBLE);
                    vet_card.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    private void findViews(View view) {
        vet_card = view.findViewById(R.id.vet_card);
        my_pets = view.findViewById(R.id.my_pets);
        my_questions = view.findViewById(R.id.my_questions);
        headerUser = view.findViewById(R.id.header_profile);
        headerVet = view.findViewById(R.id.header_vet);
        userTitle = view.findViewById(R.id.header_questions);
        vetTitle = view.findViewById(R.id.header_questions_v2);
        btn_update = view.findViewById(R.id.btn_update);

    }

    private void popUpDialog() {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialogue_add_pet);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("pet_images/" + UUID.randomUUID().toString());
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());


        et_name = dialog.findViewById(R.id.et_pet_name);
        pet_type = dialog.findViewById(R.id.spinner_pet_type);
        upload_photo = dialog.findViewById(R.id.btn_upload_pet_photo);
        status = dialog.findViewById(R.id.tv_photo_status);
        btn_save = dialog.findViewById(R.id.btn_save_pet);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                dataManager.getAnimalNames());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pet_type.setAdapter(adapter);

        upload_photo.setOnClickListener(v -> selectImage());
        btn_save.setOnClickListener(v -> {
            String name = et_name.getText().toString();
            String type = pet_type.getSelectedItem().toString();
            if (name.isEmpty() || type.isEmpty() || photoUri == null) {
                Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            }
            StorageReference fileReference = storageReference.child(System.currentTimeMillis() + ".jpg");
            fileReference.putFile(photoUri)
                    .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        String photoUrl = uri.toString();

                        DatabaseReference petRef = userRef.child("pets");
                        petRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                long nextIndex = snapshot.getChildrenCount();
                                DatabaseReference nextPetRef = petRef.child(String.valueOf(nextIndex));
                                AnimalType animalType = DataManager.getAnimalTypes().stream()

                                        .filter(obj -> obj.getKind().equals(type)).findFirst().orElse(null);

                                Pet pet = new Pet().setName(name).setType(animalType).setPhotoUrl(photoUrl);
                                nextPetRef.setValue(pet).addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getContext(), "Pet saved successfully", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    } else {
                                        Toast.makeText(getContext(), "Failed to save pet", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(getContext(), "Failed to read data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }))
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to upload photo", Toast.LENGTH_SHORT).show());
        });
        dialog.setCancelable(true);
        dialog.show();
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            photoUri = data.getData();
            status.setText("Photo selected");
        }
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
                    vetTitle.setVisibility(View.VISIBLE);
                    btn_update.setVisibility(View.INVISIBLE);
                    userTitle.setVisibility(View.INVISIBLE);
                    my_pets.setVisibility(View.INVISIBLE);
                    headerUser.setVisibility(View.INVISIBLE);
                    fetchVetQuestions();

                } else if (kindOfUser.equals("user")) {
                    userTitle.setVisibility(View.VISIBLE);
                    vetTitle.setVisibility(View.INVISIBLE);
                    fetchCurrentUser();
                    my_pets.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                    petAdapter = new PetAdapter(getContext(), new ArrayList<>());
                    my_pets.setAdapter(petAdapter);
                    fetchUserPets();

                    btn_update.setOnClickListener(v -> popUpDialog());
                }
            }

            @Override
            public void onError(Exception e) {

            }
        });
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
