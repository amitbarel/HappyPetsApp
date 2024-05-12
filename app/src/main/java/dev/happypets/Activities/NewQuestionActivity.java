package dev.happypets.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.android.material.button.MaterialButton;

import dev.happypets.Database.DataManager;
import dev.happypets.Objects.Question;
import dev.happypets.R;

public class NewQuestionActivity extends AppCompatActivity {

    ImageView backButton;
    EditText questionTitle, questionBody;
    Spinner animalKind;
    MaterialButton publishButton;
    DataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_question);
        findViews();
        initViews();
        backButton.setOnClickListener(v->{
            finish();
        });
        publishButton.setOnClickListener(v->{
            Question newQuestion = new Question()
                    .setTitle(questionTitle.getText().toString())
                    .setText(questionBody.getText().toString())
                    .setCategory(animalKind.getSelectedItem().toString());
        });
    }

    private void findViews() {
        backButton.findViewById(R.id.img_back_from_question);
        questionTitle.findViewById(R.id.question_title);
        questionBody.findViewById(R.id.question_body);
        animalKind.findViewById(R.id.spinner_animal);
        publishButton.findViewById(R.id.btn_publish);
    }

    private void initViews() {
        ArrayAdapter adapter = new ArrayAdapter(
                getApplicationContext(),
                android.R.layout.simple_spinner_item,
                dataManager.getAnimalTypes());

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        animalKind.setAdapter(adapter);
    }
}