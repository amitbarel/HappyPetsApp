package dev.happypets.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;
import java.util.UUID;

import dev.happypets.Database.DataManager;
import dev.happypets.Objects.AnimalType;
import dev.happypets.Objects.Pet;
import dev.happypets.Objects.User;
import dev.happypets.Objects.Vet;
import dev.happypets.R;

public class SignUpActivity extends AppCompatActivity {
    private ImageButton backBTN;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private TextInputEditText vet_name, vet_email, vet_phone, vet_address, vet_password, vet_license;
    private TextInputEditText user_name, user_email, user_password, pet_name;
    private Spinner spinner_pet_type;
    private MaterialButton btn_signup, btn_vet_upload_license, btn_upload_pet_photo;
    private RadioGroup rg_user_type;
    private String imageUrl;
    private Uri imageUri;
    private Uri vetImageUri;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_IMAGE_REQUEST_VET = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();
        findViews();
        initViews();
    }

    private void findViews() {
        backBTN = findViewById(R.id.setting_btn_back);
        rg_user_type = findViewById(R.id.rg_user_type);
        vet_name = findViewById(R.id.et_vet_name);
        vet_email = findViewById(R.id.et_vet_email);
        vet_phone = findViewById(R.id.et_vet_phone);
        vet_address = findViewById(R.id.et_vet_address);
        vet_password = findViewById(R.id.et_vet_password);
        vet_license = findViewById(R.id.et_vet_license);
        btn_vet_upload_license = findViewById(R.id.btn_vet_upload_license);
        user_name = findViewById(R.id.et_user_name);
        user_email = findViewById(R.id.et_user_email);
        user_password = findViewById(R.id.et_user_password);
        pet_name = findViewById(R.id.et_pet_name);
        spinner_pet_type = findViewById(R.id.spinner_pet_type);
        btn_upload_pet_photo = findViewById(R.id.btn_upload_pet_photo);
        btn_signup = findViewById(R.id.btn_signup);
    }

    private void initViews() {
        mDatabase = FirebaseDatabase.getInstance();
        backBTN.setOnClickListener(v -> finish());
        rg_user_type.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_veterinarian) {
                findViewById(R.id.layout_veterinarian).setVisibility(View.VISIBLE);
                findViewById(R.id.layout_regular_user).setVisibility(View.GONE);
            } else if (checkedId == R.id.rb_regular_user) {
                findViewById(R.id.layout_veterinarian).setVisibility(View.GONE);
                findViewById(R.id.layout_regular_user).setVisibility(View.VISIBLE);
            }
        });

        btn_vet_upload_license.setOnClickListener(v -> openFileChooserForVet());
        btn_upload_pet_photo.setOnClickListener(v -> openFileChooser());
        btn_signup.setOnClickListener(v -> userSignUp());
    }

    private void userSignUp() {
        int selectedUserType = rg_user_type.getCheckedRadioButtonId();
        if (selectedUserType == R.id.rb_veterinarian) {
            signUpVeterinarian();
        } else if (selectedUserType == R.id.rb_regular_user) {
            signUpRegularUser();
        }
    }

    private void signUpVeterinarian() {
        String vetName = Objects.requireNonNull(vet_name.getText()).toString().trim();
        String vetEmail = Objects.requireNonNull(vet_email.getText()).toString().trim();
        String vetPhone = Objects.requireNonNull(vet_phone.getText()).toString().trim();
        String vetAddress = Objects.requireNonNull(vet_address.getText()).toString().trim();
        String vetPassword = Objects.requireNonNull(vet_password.getText()).toString().trim();
        String vetLicense = Objects.requireNonNull(vet_license.getText()).toString().trim();

        if (validateVeterinarianFields(vetName, vetEmail, vetPhone, vetAddress, vetPassword, vetLicense)) {
            uploadFileVet(vetName, vetEmail, vetPhone, vetAddress, vetPassword, vetLicense);
        }
    }

    private void uploadFileVet(String vetName, String vetEmail, String vetPhone, String vetAddress, String vetPassword, String vetLicense) {
        if (vetImageUri != null) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("vet_images/" + UUID.randomUUID().toString());
            storageReference.putFile(vetImageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        Task<Uri> downloadUrl = taskSnapshot.getStorage().getDownloadUrl();
                        downloadUrl.addOnSuccessListener(uri -> {
                            imageUrl = uri.toString();
                            mAuth.createUserWithEmailAndPassword(vetEmail, vetPassword)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Vet vet = new Vet(vetName, vetEmail, vetPhone, vetAddress, vetPassword, vetLicense, imageUrl);
                                            mDatabase.getReference("Veterinarians")
                                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                    .setValue(vet).addOnCompleteListener(dbTask -> {
                                                        if (dbTask.isSuccessful()) {
                                                            onSignupComplete(dbTask);
                                                        } else {
                                                            String errorMessage = dbTask.getException() != null ? dbTask.getException().getMessage() : "Unknown error";
                                                            Log.e("SignUpActivity", "Database write failed: " + errorMessage);
                                                            Toast.makeText(SignUpActivity.this, "Database write failed. " + errorMessage, Toast.LENGTH_LONG).show();
                                                        }
                                                    });
                                        } else {
                                            String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                                            Log.e("SignUpActivity", "Sign up failed: " + errorMessage);
                                            Toast.makeText(SignUpActivity.this, "Sign up failed. " + errorMessage, Toast.LENGTH_LONG).show();
                                        }
                                    });
                        });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(SignUpActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void signUpRegularUser() {
        String userName = Objects.requireNonNull(user_name.getText()).toString().trim();
        String userEmail = Objects.requireNonNull(user_email.getText()).toString().trim();
        String userPassword = Objects.requireNonNull(user_password.getText()).toString().trim();
        String petName = Objects.requireNonNull(pet_name.getText()).toString().trim();
        String petType = spinner_pet_type.getSelectedItem().toString();

        if (validateUserFields(userName, userEmail, userPassword, petName)) {
            uploadFileUser(userEmail, userPassword, userName, petName, petType);
        }
    }

    private void uploadFileUser(String userEmail, String userPassword, String userName, String petName, String petType) {
        if (imageUri != null) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("pet_images/" + UUID.randomUUID().toString());
            storageReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        Task<Uri> downloadUrl = taskSnapshot.getStorage().getDownloadUrl();
                        downloadUrl.addOnSuccessListener(uri -> {
                            imageUrl = uri.toString();
                            mAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            AnimalType animalType = DataManager.getAnimalTypes().stream()
                                                    .filter(obj -> obj.getKind().equals(petType)).findFirst().orElse(null);
                                            Pet pet = new Pet(petName, animalType, imageUrl);
                                            User user = new User(userName, userEmail, userPassword, pet);
                                            mDatabase.getReference("Users")
                                                    .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                                                    .setValue(user).addOnCompleteListener(this::onSignupComplete);
                                        } else {
                                            String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                                            Toast.makeText(SignUpActivity.this, "Sign up failed. " + errorMessage, Toast.LENGTH_LONG).show();
                                        }
                                    });
                        });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(SignUpActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateVeterinarianFields(String vetName, String vetEmail, String vetPhone, String vetAddress, String vetPassword, String vetLicense) {
        if (vetName.isEmpty() || vetEmail.isEmpty() || vetPhone.isEmpty() || vetAddress.isEmpty() || vetPassword.isEmpty() || vetLicense.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(vetEmail).matches()) {
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (vetPassword.length() < 6) {
            Toast.makeText(this, "Password too short", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validateUserFields(String userName, String userEmail, String userPassword, String petName) {
        if (userName.isEmpty() || userEmail.isEmpty() || userPassword.isEmpty() || petName.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (userPassword.length() < 6) {
            Toast.makeText(this, "Password too short", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void openFileChooserForVet() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST_VET);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            if (requestCode == PICK_IMAGE_REQUEST) {
                imageUri = data.getData();
            } else if (requestCode == PICK_IMAGE_REQUEST_VET) {
                vetImageUri = data.getData();
            }
        }
    }

    private void onSignupComplete(@NonNull Task<Void> task) {
        if (task.isSuccessful()) {
            Toast.makeText(this, "Sign up successful", Toast.LENGTH_SHORT).show();
            finish(); // Finish activity or navigate to another screen
        } else {
            String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error";
            Toast.makeText(this, "Sign up failed. " + errorMessage, Toast.LENGTH_LONG).show();
        }
    }
}
