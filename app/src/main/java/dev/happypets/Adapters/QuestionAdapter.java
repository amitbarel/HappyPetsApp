package dev.happypets.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

import dev.happypets.Activities.NewAnswerActivity;
import dev.happypets.Objects.Question;
import dev.happypets.CallBacks.QuestionCallBack;
import dev.happypets.R;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QusetionViewHolder> {

    private final Context context;
    private final QuestionCallBack questionCallBack;
    private ArrayList<Question> questions;

    public QuestionAdapter(Context context, ArrayList<Question> questions, QuestionCallBack questionCallBack) {
        this.context = context;
        this.questions = questions;
        this.questionCallBack = questionCallBack;
    }

    public class QusetionViewHolder extends RecyclerView.ViewHolder {
        private final MaterialTextView title;
        private MaterialTextView numberOfAnswers;
        private MaterialTextView questionTime;
        private AppCompatImageView favorite;


        public QusetionViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.txt_questionTitle);
            numberOfAnswers = itemView.findViewById(R.id.txt_numberOfAnswers);
            questionTime = itemView.findViewById(R.id.txt_question_time);
            favorite = itemView.findViewById(R.id.question_IMG_favorite);

            favorite.setOnClickListener(v->{
                int position = getAdapterPosition();
                Question question = questions.get(position);
                question.setFavorite(!question.isFavorite());
                notifyItemChanged(position);
                questionCallBack.favoriteClicked(question, position);
            });

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                Question question = questions.get(position);
                if (question.getQuestionId() != null) {
                    Intent intent = new Intent(context, NewAnswerActivity.class);
                    intent.putExtra("question_id", question.getQuestionId());
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, "Question ID is null", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @NonNull
    @Override
    public QuestionAdapter.QusetionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_item, parent, false);
        return new QusetionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionAdapter.QusetionViewHolder holder, int position) {
        Question question = questions.get(position);
        holder.title.setText(question.getTitle());
        if (question.getRelatedAnswers() == null){
            holder.numberOfAnswers.setText("0");
        } else{
            holder.numberOfAnswers.setText(String.valueOf(question.getRelatedAnswers().size()));
        }
        holder.questionTime.setText(question.getAskedTime());
        holder.favorite.setImageResource(question.isFavorite() ? R.drawable.fill_heart : R.drawable.heart_thin);
    }

    @Override
    public int getItemCount() {
        return questions == null ? 0: questions.size();
    }

    public void updateQuestions(ArrayList<Question> newQuestions) {
        this.questions.clear();
        this.questions.addAll(newQuestions);
        notifyDataSetChanged();
    }
}
