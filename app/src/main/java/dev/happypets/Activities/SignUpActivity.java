package dev.happypets.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
    //ArchTaskExecutor FirebaseStorage;
    private ImageButton backBTN;
    private FirebaseAuth mAuth;
    private TextInputEditText vet_name, vet_email, vet_phone, vet_address, vet_password, vet_license;
    private TextInputEditText user_name, user_email, user_password, pet_name;
    private Spinner spinner_pet_type;
    private MaterialButton btn_signup, btn_vet_upload_license, btn_upload_pet_photo;
    private RadioGroup rg_user_type;

    private Uri imageUri;
    private static final int PICK_IMAGE_REQUEST = 1;

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

        btn_vet_upload_license.setOnClickListener(v -> {
            openFileChooser();
        });

        btn_upload_pet_photo.setOnClickListener(v -> {
            openFileChooser();
        });

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

            mAuth.createUserWithEmailAndPassword(vetEmail, vetPassword)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Vet vet = new Vet(vetName, vetEmail, vetPhone, vetAddress, vetPassword, vetLicense, "");
                            FirebaseDatabase.getInstance().getReference("Veterinarians")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(vet).addOnCompleteListener(this::onSignupComplete);
                        } else {
                            String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                            Toast.makeText(SignUpActivity.this, "Sign up failed. " + errorMessage, Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    private void signUpRegularUser() {
        String userName = Objects.requireNonNull(user_name.getText()).toString().trim();
        String userEmail = Objects.requireNonNull(user_email.getText()).toString().trim();
        String userPassword = Objects.requireNonNull(user_password.getText()).toString().trim();
        String petName = Objects.requireNonNull(pet_name.getText()).toString().trim();
        String petType = spinner_pet_type.getSelectedItem().toString();

        if (validateUserFields(userName, userEmail, userPassword, petName)) {

            mAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            AnimalType animalType = DataManager.getAnimalTypes().stream()
                                    .filter(obj -> obj.getKind().equals(petType)).findFirst().orElse(null);
                            User user = new User(userName, userEmail, userPassword, new Pet(petName, animalType, null));
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                                    .setValue(user).addOnCompleteListener(this::onSignupComplete);
                        } else {
                            String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                            Toast.makeText(SignUpActivity.this, "Sign up failed. " + errorMessage, Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    private boolean validateVeterinarianFields(String vetName, String vetEmail, String vetPhone, String vetAddress, String vetPassword, String vetLicense) {
        if (vetName.isEmpty() || vetEmail.isEmpty() || vetPhone.isEmpty() || vetAddress.isEmpty() || vetPassword.isEmpty() || vetLicense.isEmpty()) {
           // Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            if (vetEmail.isEmpty()) {
                vet_email.setError("Email is required");
                vet_email.requestFocus();
                return false;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(vetEmail).matches()) {
                vet_email.setError("Invalid email");
                vet_email.requestFocus();
                return false;
            }

            if (vetPassword.isEmpty()) {
                vet_password.setError("Password is required");
                vet_password.requestFocus();
                return false;
            }
            if (vetPassword.length() < 6) {
                vet_password.setError("Minmum password lenght should be 6 characters");
                vet_password.requestFocus();
                return false;
            }
            if(vetLicense.isEmpty()){
                vet_license.setError("vet license is required");
                vet_license.requestFocus();
                return false;
            }
            if(vetName.isEmpty()){
                vet_name.setError("vet name is required");
                vet_name.requestFocus();
                return false;
            }
            if(vetAddress.isEmpty()){
                vet_address.setError("vet address is required");
                vet_address.requestFocus();
                return false;
            }
        }

        return true;
    }


    private boolean validateUserFields(String userName, String userEmail, String userPassword, String petName) {
         if (userName.isEmpty() || userEmail.isEmpty() || userPassword.isEmpty() || petName.isEmpty()) {
           // Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            if (userName.isEmpty()) {
                user_name.setError("name user is required");
                user_name.requestFocus();
                return false;
            }
            if (userEmail.isEmpty()) {
                vet_email.setError("Email is required");
                vet_email.requestFocus();
                return false;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                user_email.setError("Invalid email");
                user_email.requestFocus();
                return false;
            }
            if (userPassword.isEmpty()) {
                vet_password.setError("Password is required");
                vet_password.requestFocus();
                return false;
            }

            if (userPassword.length() < 6) {
                user_password.setError("Password must be at least 6 characters");
                user_password.requestFocus();
                return false;
            }
        }

        return true;
    }

    private void onSignupComplete(@NonNull Task<Void> task) {
        if (task.isSuccessful()) {
            Toast.makeText(SignUpActivity.this, "Sign up successful", Toast.LENGTH_LONG).show();
            openLogin();
        } else {
            Toast.makeText(SignUpActivity.this, "Failed to save user data. Try again!", Toast.LENGTH_LONG).show();
        }
    }

    private void openLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData(); // You can now upload the image to Firebase Storage
        }
    }

    // Method to upload file to Firebase Storage
    private void uploadFile() {
        if (imageUri != null) {

            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("uploads/" + UUID.randomUUID().toString());
            storageReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Image uploaded successfully
                        // Get the download URL
                        Task<Uri> downloadUrl = taskSnapshot.getStorage().getDownloadUrl();
                        downloadUrl.addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();
                            // Now you can use the imageUrl to save it in the database or wherever needed
                        });
                    })
                    .addOnFailureListener(e -> {
                        // Handle failed upload
                        Toast.makeText(SignUpActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }
}
