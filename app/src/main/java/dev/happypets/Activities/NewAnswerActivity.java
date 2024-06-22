package dev.happypets.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import dev.happypets.Adapters.AnswerAdapter;
import dev.happypets.Database.DataManager;
import dev.happypets.Objects.Answer;
import dev.happypets.Objects.Question;
import dev.happypets.R;

public class NewAnswerActivity extends AppCompatActivity {

    DataManager dataManager;
    ShapeableImageView backButton;
    MaterialTextView title, body;
    AppCompatImageView kind_image;
    RecyclerView relatedAnswers;
    TextInputEditText answer;
    MaterialButton btnRespond;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_answer);
        dataManager = DataManager.getInstance(this);
        findViews();
        initViews();
    }

    private void findViews() {
        backButton = findViewById(R.id.img_back_from_answer);
        title = findViewById(R.id.actual_title);
        body = findViewById(R.id.actual_body);
        kind_image = findViewById(R.id.img_kind);
        relatedAnswers = findViewById(R.id.recycle_answers);
        answer = findViewById(R.id.et_answer);
        btnRespond = findViewById(R.id.btn_respond);
    }

    private void initViews() {
        Intent intent = getIntent();
        String chosenQuestionId = intent.getStringExtra("questionId");
        Question chosen = dataManager.getQuestions().get(Integer.parseInt(chosenQuestionId));
        title.setText(chosen.getTitle());
        body.setText(chosen.getText());
        relatedAnswers.setAdapter(new AnswerAdapter(this, chosen.getRelatedAnswers()));
        backButton.setOnClickListener(v->{
            Toast.makeText(this, "No answer was added", Toast.LENGTH_SHORT).show();
            finish();
        });
        btnRespond.setOnClickListener(v->{
            Answer newAnswer = new Answer()
                    .setTitle(title.getText().toString())
                    .setText(body.getText().toString());
            dataManager.addNewAnswer(chosenQuestionId, newAnswer);
            Toast.makeText(this, "Answer was added", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}