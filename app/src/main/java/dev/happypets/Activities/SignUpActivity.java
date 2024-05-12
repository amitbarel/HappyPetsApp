package dev.happypets.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import dev.happypets.Objects.User;
import dev.happypets.R;

public class SignUpActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    EditText et_email, et_name, et_password;
    MaterialButton btn_signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        mAuth = FirebaseAuth.getInstance();
        findViews();
        initViews();
    }

    private void initViews() {
        btn_signup.setOnClickListener(v->{
            userSignUp();
        });
    }

    private void userSignUp() {
        String email = et_email.getText().toString().trim();
        String name = et_name.getText().toString().trim();
        String password = et_email.getText().toString().trim();

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            et_email.setError("Please enter a valid email address");
            et_email.requestFocus();
            return;
        }
        else if (name.isEmpty()){
            et_name.setError("Please enter a valid name");
            et_name.requestFocus();
            return;
        }
        else if (password.isEmpty() || password.length() < 6){
            et_password.setError("Please enter a valid password");
            et_password.requestFocus();
            return;
        }
        else{
            mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            User user = new User().setName(name).setEmail(email).setPassword(password);


                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(SignUpActivity.this,"Sign up successful",Toast.LENGTH_LONG).show();
                                                openLogin();
                                            }else{
                                                Toast.makeText(SignUpActivity.this,"Failed to Sign up. Try again!",Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });

                        }
                    });
        }
    }

    private void openLogin() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void findViews() {
        et_email.findViewById(R.id.signup_et_email);
        et_name.findViewById(R.id.signup_et_name);
        et_password.findViewById(R.id.signup_et_password);
        btn_signup.findViewById(R.id.btn_signup);
    }
}