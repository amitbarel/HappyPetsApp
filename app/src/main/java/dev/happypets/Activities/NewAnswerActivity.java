package dev.happypets.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.Objects;

import dev.happypets.Adapters.AnswerAdapter;
import dev.happypets.Database.DataManager;
import dev.happypets.Objects.Answer;
import dev.happypets.Objects.Question;
import dev.happypets.Objects.User;
import dev.happypets.R;

public class NewAnswerActivity extends AppCompatActivity {

    private DataManager dataManager;
    private MaterialTextView title, body, askedBy;
    private RecyclerView relatedAnswers;
    private TextInputEditText answer;
    private MaterialButton btnRespond;
    private ShapeableImageView back_arrow;
    private AnswerAdapter answerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_answer);

        dataManager = DataManager.getInstance(this);

        findViews();
        initViews();
    }

    private void findViews() {
        title = findViewById(R.id.actual_title);
        body = findViewById(R.id.actual_body);
        askedBy = findViewById(R.id.tv_username);
        relatedAnswers = findViewById(R.id.recycle_answers);
        answer = findViewById(R.id.et_answer);
        btnRespond = findViewById(R.id.btn_respond);
        back_arrow = findViewById(R.id.img_back_from_answer);
    }

    private void initViews() {
        Intent intent = getIntent();
        relatedAnswers.setLayoutManager(new LinearLayoutManager(this));
        String chosenQuestionId = intent.getStringExtra("question_id");
        if (chosenQuestionId == null) {
            Toast.makeText(this, "No question ID provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        back_arrow.setOnClickListener(v -> {
            Intent formerIntent = new Intent(this, MainActivity.class);
            startActivity(formerIntent);
            finish();
        });

        relatedAnswers.setLayoutManager(new LinearLayoutManager(this));

        // Fetch question details and answers
        dataManager.getQuestionById(chosenQuestionId, new DataManager.OnQuestionRetrievedListener() {
            @Override
            public void onQuestionRetrieved(Question question) {
                if (question != null) {
                    title.setText(question.getTitle());
                    body.setText(question.getText());

                    User askedByUser = question.getAskedBy();
                    if (askedByUser != null && askedByUser.getName() != null) {
                        askedBy.setText(askedByUser.getName());
                    } else {
                        askedBy.setText("Unknown user");
                    }

                    // Get answers for the question

                    dataManager.getAnswersByQuestionId(chosenQuestionId, answers -> {
                        runOnUiThread(() -> {
                            if (answers != null) {
                                answerAdapter = new AnswerAdapter(NewAnswerActivity.this, answers);
                                relatedAnswers.setAdapter(answerAdapter);
                            }
                        });
                    });

                    btnRespond.setOnClickListener(v -> {
                        String newAnswerText = Objects.requireNonNull(answer.getText()).toString();
                        if (!newAnswerText.isEmpty()) {
                            Answer newAnswer = new Answer().setText(newAnswerText);
                            dataManager.addNewAnswer(chosenQuestionId, newAnswer);
                            Toast.makeText(NewAnswerActivity.this, "Answer added successfully", Toast.LENGTH_SHORT).show();
                            // Refresh answers after adding new answer
                            dataManager.getAnswersByQuestionId(chosenQuestionId, answers -> {
                                if (answers != null) {
                                    answerAdapter = new AnswerAdapter(NewAnswerActivity.this, answers);
                                    relatedAnswers.setAdapter(answerAdapter);
                                }
                                Intent formerIntent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(formerIntent);
                                finish();
                            });
                        } else {
                            Toast.makeText(NewAnswerActivity.this, "Please enter an answer", Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    Toast.makeText(NewAnswerActivity.this, "Question not found", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(NewAnswerActivity.this, "Error fetching question: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

}
