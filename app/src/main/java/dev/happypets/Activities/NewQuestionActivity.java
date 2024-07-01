package dev.happypets.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import dev.happypets.Database.DataManager;
import dev.happypets.Objects.Question;
import dev.happypets.Objects.User;
import dev.happypets.R;

public class NewQuestionActivity extends AppCompatActivity {

    private DataManager dataManager;
    private AppCompatImageView backButton;
    private TextInputEditText questionTitle, questionBody;
    private MaterialTextView nameUser;
    private Spinner animalKind;
    private MaterialButton publishButton;

    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_question);

        dataManager = DataManager.getInstance(getApplicationContext());

        findViews();
        initViews();
        fetchCurrentUser();
    }

    private void findViews() {
        backButton = findViewById(R.id.img_back_from_question);
        questionTitle = findViewById(R.id.question_title);
        questionBody = findViewById(R.id.question_body);
        animalKind = findViewById(R.id.spinner_animal);
        publishButton = findViewById(R.id.btn_publish);
        nameUser = findViewById(R.id.Q_username);
    }

    private void initViews() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getApplicationContext(),
                android.R.layout.simple_spinner_item,
                dataManager.getAnimalNames());

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        animalKind.setAdapter(adapter);

        backButton.setOnClickListener(v -> finish());

        publishButton.setOnClickListener(v -> {
            if (currentUser != null) {
                Question newQuestion = new Question()
                        .setTitle(questionTitle.getText().toString())
                        .setText(questionBody.getText().toString())
                        .setCategory(animalKind.getSelectedItem().toString())
                        .setAskedBy(currentUser);
                dataManager.addNewQuestion(newQuestion);
                finish();
            } else {
                // Handle the case where user data is not yet available
                Log.d("NewQuestionActivity", "Current user data not available");
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
                        nameUser.setText(currentUser.getName());
                    } else {
                        nameUser.setText("Unknown User");
                    }
                } else {
                    nameUser.setText("Unknown User");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("NewQuestionActivity", "Failed to read user data", databaseError.toException());
                nameUser.setText("Unknown User");
            }
        });
    }
}
