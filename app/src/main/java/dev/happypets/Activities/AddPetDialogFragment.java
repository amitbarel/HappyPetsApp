package dev.happypets.Activities;

import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import dev.happypets.Database.DataManager;
import dev.happypets.Objects.AnimalType;
import dev.happypets.Objects.Pet;
import dev.happypets.R;


import static android.app.Activity.RESULT_OK;

public class AddPetDialogFragment extends DialogFragment {

    private TextInputEditText etPetName;
    private Spinner spinnerPetType;
    private MaterialButton btnUploadPetPhoto, btnSavePet;
    private TextView tvPhotoStatus;
    private Uri photoUri;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_add_pet_dialog_fragment, container, false);

        etPetName = view.findViewById(R.id.et_pet_name);
        spinnerPetType = view.findViewById(R.id.spinner_pet_type);
        btnUploadPetPhoto = view.findViewById(R.id.btn_upload_pet_photo);
        btnSavePet = view.findViewById(R.id.btn_save_pet);
        tvPhotoStatus = view.findViewById(R.id.tv_photo_status);

        storageReference = FirebaseStorage.getInstance().getReference("pet_images");
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        btnUploadPetPhoto.setOnClickListener(v -> selectImage());
        btnSavePet.setOnClickListener(v -> savePet());

        return view;
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
            tvPhotoStatus.setText("Photo selected");
        }
    }

    private void savePet() {
        String petName = etPetName.getText().toString().trim();
        String petType = spinnerPetType.getSelectedItem().toString();

        if (petName.isEmpty() || photoUri == null) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        StorageReference fileReference = storageReference.child(System.currentTimeMillis() + ".jpg");
        fileReference.putFile(photoUri)
                .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    String photoUrl = uri.toString();
                    String userId = currentUser.getUid();
                    DatabaseReference userRef = databaseReference.child(userId).child("pets").push();

                    AnimalType animalType = DataManager.getAnimalTypes().stream()

                            .filter(obj -> obj.getKind().equals(petType)).findFirst().orElse(null);

                    Pet pet = new Pet().setName(petName).setType(animalType).setPhotoUrl(photoUrl);

                    userRef.setValue(pet).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Pet saved successfully", Toast.LENGTH_SHORT).show();
                            dismiss();
                        } else {
                            Toast.makeText(getContext(), "Failed to save pet", Toast.LENGTH_SHORT).show();
                        }
                    });
                }))
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to upload photo", Toast.LENGTH_SHORT).show());
    }



}
