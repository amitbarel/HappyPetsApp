package dev.happypets.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

import dev.happypets.Activities.NewAnswerActivity;
import dev.happypets.CallBacks.AnswerCallback;
import dev.happypets.CallBacks.QuestionCallBack;
import dev.happypets.Objects.Answer;
import dev.happypets.Objects.Question;
import dev.happypets.R;

public class AnswerAdapter extends RecyclerView.Adapter<AnswerAdapter.AnswerViewHolder> {

    private Context context;
    private AnswerCallback answerCallback;
    private ArrayList<Answer> answers;

    public AnswerAdapter(Context context, ArrayList<Answer> answers, AnswerCallback answerCallback) {
        this.context = context;
        this.answers = answers;
        this.answerCallback = answerCallback;
    }

    public static class AnswerViewHolder extends RecyclerView.ViewHolder {
        private MaterialTextView answerBody;


        public AnswerViewHolder(@NonNull View itemView) {
            super(itemView);
            answerBody = itemView.findViewById(R.id.txt_answer_body);
        }
    }

    @NonNull
    @Override
    public AnswerAdapter.AnswerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.answer_item, parent, false);
        return new AnswerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnswerAdapter.AnswerViewHolder holder, int position) {
        Answer answer = answers.get(position);
        holder.answerBody.setText(answer.getText());
    }

    @Override
    public int getItemCount() {
        return answers == null ? 0: answers.size();
    }
}
