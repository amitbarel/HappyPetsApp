package dev.happypets.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import dev.happypets.Database.DataManager;
import dev.happypets.Objects.Question;
import dev.happypets.R;

public class NewQuestionActivity extends AppCompatActivity {

    DataManager dataManager;
    AppCompatImageView backButton;
    TextInputEditText questionTitle, questionBody;
    Spinner animalKind;
    MaterialButton publishButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_question);

        dataManager = DataManager.getInstance(getApplicationContext());

        findViews();
        initViews();
    }

    private void findViews() {
        backButton = findViewById(R.id.img_back_from_question);
        questionTitle = findViewById(R.id.question_title);
        questionBody = findViewById(R.id.question_body);
        animalKind = findViewById(R.id.spinner_animal);
        publishButton = findViewById(R.id.btn_publish);
    }

    private void initViews() {
        ArrayAdapter adapter = new ArrayAdapter(
                getApplicationContext(),
                android.R.layout.simple_spinner_item,
                dataManager.getAnimalNames());

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        animalKind.setAdapter(adapter);

        backButton.setOnClickListener(v -> {
            finish();
        });
        publishButton.setOnClickListener(v -> {
            Question newQuestion = new Question()
                    .setTitle(questionTitle.getText().toString())
                    .setText(questionBody.getText().toString())
                    .setCategory(animalKind.getSelectedItem().toString());
            dataManager.addNewQuestion(newQuestion);
            finish();
        });
    }
}
