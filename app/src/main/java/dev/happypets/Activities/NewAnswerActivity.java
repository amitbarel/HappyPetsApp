package dev.happypets.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import dev.happypets.Database.DataManager;
import dev.happypets.Objects.Question;
import dev.happypets.R;

public class NewAnswerActivity extends AppCompatActivity {

    ImageView backButton;
    MaterialTextView title, body;
    AppCompatImageView kind_image;
    RecyclerView relatedAnswers;
    EditText answer;
    MaterialButton btnRespond;
    DataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_answer);
        findViews();
        initViews();
        backButton.setOnClickListener(v->{
            finish();
        });
        btnRespond.setOnClickListener(v->{

        });
    }

    private void findViews() {
        backButton.findViewById(R.id.img_back_from_answer);
        title.findViewById(R.id.actual_title);
        body.findViewById(R.id.actual_body);
        kind_image.findViewById(R.id.img_kind);
        relatedAnswers.findViewById(R.id.recycle_answers);
        answer.findViewById(R.id.et_answer);
        btnRespond.findViewById(R.id.btn_respond);
    }

    private void initViews() {
        Intent intent = getIntent();
        String chosenQuestionId = intent.getStringExtra("questionId");
        Question chosen = dataManager.getQuestions().get(Integer.parseInt(chosenQuestionId));
        title.setText(chosen.getTitle());
        body.setText(chosen.getText());
//        kind_image.setImageResource();
    }
}