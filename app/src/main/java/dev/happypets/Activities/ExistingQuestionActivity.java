package dev.happypets.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
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

public class ExistingQuestionActivity extends AppCompatActivity {

    private DataManager dataManager;
    private MaterialTextView title, body, askedBy;
    private RecyclerView relatedAnswers;
    private MaterialButton btn_add_answer;
    private ShapeableImageView back_arrow;
    private AnswerAdapter answerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_existing_question);

        dataManager = DataManager.getInstance(this);

        findViews();
        initViews();
    }

    private void findViews() {
        title = findViewById(R.id.actual_title);
        body = findViewById(R.id.actual_body);
        askedBy = findViewById(R.id.tv_username);
        relatedAnswers = findViewById(R.id.recycle_answers);
        btn_add_answer = findViewById(R.id.btn_add_answer);
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
                    dataManager.getAnswersByQuestionId(chosenQuestionId, answers -> runOnUiThread(() -> {
                        if (answers != null && !answers.isEmpty()) {
                            for (Answer ans : answers) {
                                Log.d("ExistingQuestionActivity", "Answer: " + ans.getText());
                            }
                            answerAdapter = new AnswerAdapter(ExistingQuestionActivity.this, answers);
                            relatedAnswers.setAdapter(answerAdapter);
                        } else {
                            // Handle the case where no answers are found
                            Toast.makeText(ExistingQuestionActivity.this, "No answers found for this question", Toast.LENGTH_SHORT).show();
                        }
                    }));

                    btn_add_answer.setOnClickListener(v -> {
                        Dialog dialog = new Dialog(ExistingQuestionActivity.this);
                        dialog.setContentView(R.layout.dialogue_answer);
                        TextInputEditText ET_title = dialog.findViewById(R.id.ET_title);
                        TextInputEditText ET_body = dialog.findViewById(R.id.ET_body);
                        MaterialButton saveBTN = dialog.findViewById(R.id.BTN_save);

                        saveBTN.setOnClickListener(v1 -> {
                            String title = Objects.requireNonNull(ET_title.getText()).toString();
                            String body = Objects.requireNonNull(ET_body.getText()).toString();
                            if (!title.isEmpty() && !body.isEmpty()) {
                                Answer newAnswer = new Answer();
                                newAnswer.setTitle(title);
                                newAnswer.setText(body);
                                dataManager.addNewAnswer(chosenQuestionId, newAnswer);
                                Toast.makeText(ExistingQuestionActivity.this, "Answer added successfully", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                dataManager.getAnswersByQuestionId(chosenQuestionId, answers -> runOnUiThread(() -> {
                                    if (answers != null && !answers.isEmpty()) {
                                        answerAdapter = new AnswerAdapter(ExistingQuestionActivity.this, answers);
                                        relatedAnswers.setAdapter(answerAdapter);
                                    }
                                }));
                                finish();
                            }
                        });
                        dialog.setCancelable(true);
                        dialog.show();
                    });
                } else {
                    Toast.makeText(ExistingQuestionActivity.this, "Question not found", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(ExistingQuestionActivity.this, "Error fetching question: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
