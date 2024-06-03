package dev.happypets.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.executor.ArchTaskExecutor;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

import dev.happypets.Objects.User;
import dev.happypets.Objects.Vet;
import dev.happypets.R;

public class SignUpActivity extends AppCompatActivity {
    //ArchTaskExecutor FirebaseStorage;
    private FirebaseAuth mAuth;
    private TextInputEditText et_vet_name, et_vet_email, et_vet_phone, et_vet_address, et_vet_password, et_vet_license;
    private TextInputEditText et_user_name, et_user_email, et_user_password, et_pet_name;
    private Spinner spinner_pet_type;
    private Button btn_signup, btn_vet_upload_license, btn_upload_pet_photo;
    private RadioGroup rg_user_type;

    private Uri imageUri;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        mAuth = FirebaseAuth.getInstance();
        findViews();
        initViews();
    }

    private void findViews() {
        rg_user_type = findViewById(R.id.rg_user_type);
        et_vet_name = findViewById(R.id.et_vet_name);
        et_vet_email = findViewById(R.id.et_vet_email);
        et_vet_phone = findViewById(R.id.et_vet_phone);
        et_vet_address = findViewById(R.id.et_vet_address);
        et_vet_password = findViewById(R.id.et_vet_password);
        et_vet_license = findViewById(R.id.et_vet_license);
        btn_vet_upload_license = findViewById(R.id.btn_vet_upload_license);
        et_user_name = findViewById(R.id.et_user_name);
        et_user_email = findViewById(R.id.et_user_email);
        et_user_password = findViewById(R.id.et_user_password);
        et_pet_name = findViewById(R.id.et_pet_name);
        spinner_pet_type = findViewById(R.id.spinner_pet_type);
        btn_upload_pet_photo = findViewById(R.id.btn_upload_pet_photo);
        btn_signup = findViewById(R.id.btn_signup);

    }

    private void initViews() {
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
        String vetName = et_vet_name.getText().toString().trim();
        String vetEmail = et_vet_email.getText().toString().trim();
        String vetPhone = et_vet_phone.getText().toString().trim();
        String vetAddress = et_vet_address.getText().toString().trim();
        String vetPassword = et_vet_password.getText().toString().trim();
        String vetLicense = et_vet_license.getText().toString().trim();

        if (!validateVeterinarianFields(vetName, vetEmail, vetPhone, vetAddress, vetPassword, vetLicense)) return;

        mAuth.createUserWithEmailAndPassword(vetEmail, vetPassword)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Vet vet = new Vet(vetName, vetEmail, vetPhone, vetAddress, vetPassword, vetLicense, "");
                        FirebaseDatabase.getInstance().getReference("Veterinarians")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue(vet).addOnCompleteListener(this::onSignupComplete);
                    } else {
                        Toast.makeText(SignUpActivity.this, "Sign up failed. Try again!", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void signUpRegularUser() {
        String userName = et_user_name.getText().toString().trim();
        String userEmail = et_user_email.getText().toString().trim();
        String userPassword = et_user_password.getText().toString().trim();
        String petName = et_pet_name.getText().toString().trim();
        String petType = spinner_pet_type.getSelectedItem().toString();

        if (!validateRegularUserFields(userName, userEmail, userPassword, petName)) return;

        mAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        User user = new User(userName, userEmail, userPassword, petName, petType, "");
                        FirebaseDatabase.getInstance().getReference("Users")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue(user).addOnCompleteListener(this::onSignupComplete);
                    } else {
                        Toast.makeText(SignUpActivity.this, "Sign up failed. Try again!", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private boolean validateVeterinarianFields(String vetName, String vetEmail, String vetPhone, String vetAddress, String vetPassword, String vetLicense) {
        if (vetName.isEmpty() || vetEmail.isEmpty() || vetPhone.isEmpty() || vetAddress.isEmpty() || vetPassword.isEmpty() || vetLicense.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(vetEmail).matches()) {
            et_vet_email.setError("Invalid email");
            et_vet_email.requestFocus();
            return false;
        }
        if (vetPassword.length() < 6) {
            et_vet_password.setError("Password must be at least 6 characters");
            et_vet_password.requestFocus();
            return false;
        }
        return true;
        }
    private boolean validateRegularUserFields(String userName, String userEmail, String userPassword, String petName) {
        if (userName.isEmpty() || userEmail.isEmpty() || userPassword.isEmpty() || petName.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            et_user_email.setError("Invalid email");
            et_user_email.requestFocus();
            return false;
        }
        if (userPassword.length() < 6) {
            et_user_password.setError("Password must be at least 6 characters");
            et_user_password.requestFocus();
            return false;
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
            imageUri = data.getData();
            // You can now upload the image to Firebase Storage
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
