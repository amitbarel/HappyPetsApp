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

import java.util.Objects;

import dev.happypets.Adapters.AnswerAdapter;
import dev.happypets.CallBacks.AnswerCallback;
import dev.happypets.Database.DataManager;
import dev.happypets.Objects.Answer;
import dev.happypets.Objects.Question;
import dev.happypets.R;

public class NewAnswerActivity extends AppCompatActivity {

    DataManager dataManager;
    ShapeableImageView backButton;
    MaterialTextView title, body, askedBy;
    AppCompatImageView kind_image;
    RecyclerView relatedAnswers;
    TextInputEditText answer;
    MaterialButton btnRespond;
    AnswerCallback answerCallback;

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
        askedBy = findViewById(R.id.tv_username);
        kind_image = findViewById(R.id.img_kind);
        relatedAnswers = findViewById(R.id.recycle_answers);
        answer = findViewById(R.id.et_answer);
        btnRespond = findViewById(R.id.btn_respond);
    }

    private void initViews() {
        Intent intent = getIntent();
        String chosenQuestionId = intent.getStringExtra("question_id");
        if (chosenQuestionId == null) {
            Toast.makeText(this, "No question ID provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        dataManager.getQuestionById(chosenQuestionId, new DataManager.OnQuestionRetrievedListener() {
            @Override
            public void onQuestionRetrieved(Question question) {
                if (question != null) {
                    title.setText(question.getTitle());
                    body.setText(question.getText());
                    askedBy.setText(question.getAskedBy().getName());

                    dataManager.getAnswersByQuestionId(chosenQuestionId, answers -> {
                        relatedAnswers.setAdapter(new AnswerAdapter(NewAnswerActivity.this, answers, answerCallback));
                    });

                    backButton.setOnClickListener(v -> {
                        Toast.makeText(NewAnswerActivity.this, "No answer was added", Toast.LENGTH_SHORT).show();
                        finish();
                    });

                    btnRespond.setOnClickListener(v -> {
                        Answer newAnswer = new Answer()
                                .setText(Objects.requireNonNull(answer.getText()).toString());
                        dataManager.addNewAnswer(chosenQuestionId, newAnswer);
                        Toast.makeText(NewAnswerActivity.this, "Answer was added", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                } else {
                    Toast.makeText(NewAnswerActivity.this, "Question not found", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(NewAnswerActivity.this, "Error fetching question: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }
}