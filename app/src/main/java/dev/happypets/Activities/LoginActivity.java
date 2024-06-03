package dev.happypets.Activities;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Toast;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import dev.happypets.R;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextInputEditText txt_email, txt_password;
    private MaterialButton btn_login, btn_signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        findViews();
        initViews();
    }

    private void findViews() {
        txt_email = findViewById(R.id.et_email);
        txt_password = findViewById(R.id.et_password);
        btn_login = findViewById(R.id.btn_login);
        btn_signUp = findViewById(R.id.btn_signUp);
    }

    private void initViews() {
        btn_login.setOnClickListener(v -> signIn());
        btn_signUp.setOnClickListener(v -> signup());
    }

    private void signIn() {
        String email = txt_email.getText().toString().trim();
        String password = txt_password.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Please fill up all needed fields", Toast.LENGTH_LONG).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            txt_email.setError("Please provide a valid email address!");
            txt_email.requestFocus();
            return;
        }
        if (password.length() < 6) {
            txt_password.setError("The password length should be at least 6 characters");
            txt_password.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(LoginActivity.this, "Login is successful", Toast.LENGTH_LONG).show();
                openMenu();
            } else {
                Toast.makeText(LoginActivity.this, "One or more of the fields are incorrect", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void openMenu() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void signup() {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
        finish();
    }
}
