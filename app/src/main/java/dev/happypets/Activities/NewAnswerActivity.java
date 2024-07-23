package dev.happypets.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

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
                            if (answers != null && !answers.isEmpty()) {
                                for (Answer ans : answers) {
                                    Log.d("NewAnswerActivity", "Answer: " + ans.getText());
                                }
                                answerAdapter = new AnswerAdapter(NewAnswerActivity.this, answers);
                                relatedAnswers.setAdapter(answerAdapter);
                            } else {
                                // Handle the case where no answers are found
                                Toast.makeText(NewAnswerActivity.this, "No answers found for this question", Toast.LENGTH_SHORT).show();
                            }
                        });
                    });

                    btnRespond.setOnClickListener(v -> {
                        String newAnswerText = Objects.requireNonNull(answer.getText()).toString();
                        if (!newAnswerText.isEmpty()) {
                            Answer newAnswer = new Answer();
                            newAnswer.setText(newAnswerText);
                            dataManager.addNewAnswer(chosenQuestionId, newAnswer);
                            Toast.makeText(NewAnswerActivity.this, "Answer added successfully", Toast.LENGTH_SHORT).show();
                            // Clear input field
                            answer.getText().clear();
                            // Refresh answers after adding new answer
                            dataManager.getAnswersByQuestionId(chosenQuestionId, answers -> {
                                runOnUiThread(() -> {
                                    if (answers != null && !answers.isEmpty()) {
                                        answerAdapter = new AnswerAdapter(NewAnswerActivity.this, answers);
                                        relatedAnswers.setAdapter(answerAdapter);
                                    }
                                });
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
