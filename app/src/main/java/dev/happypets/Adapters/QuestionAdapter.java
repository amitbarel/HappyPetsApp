package dev.happypets.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

import dev.happypets.Objects.Question;
import dev.happypets.CallBacks.QuestionCallBack;
import dev.happypets.R;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QusetionViewHolder> {

    private Context context;
    private ArrayList<Question> questions;
    private QuestionCallBack questionCallBack;

    public QuestionAdapter(Context context, ArrayList<Question> questions, QuestionCallBack questionCallBack) {
        this.context = context;
        this.questions = questions;
        this.questionCallBack = questionCallBack;
    }

    public class QusetionViewHolder extends RecyclerView.ViewHolder {
        private MaterialTextView title;
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
                questionCallBack.favoriteClicked(questions.get(getAdapterPosition()),getAdapterPosition());
            });
        }
    }

    @NonNull
    @Override
    public QuestionAdapter.QusetionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_item, parent, false);
        QusetionViewHolder qvh = new QusetionViewHolder(view);
        return qvh;
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionAdapter.QusetionViewHolder holder, int position) {
        Question question = questions.get(position);
        holder.title.setText(question.getTitle());
        holder.numberOfAnswers.setText(question.getRelatedAnswers().size());
        holder.questionTime.setText(question.getAskedTime());
        if (question.isFavorite()){
            holder.favorite.setImageResource(R.drawable.fill_heart);
        }else {
            holder.favorite.setImageResource(R.drawable.heart_thin);
        }
    }

    @Override
    public int getItemCount() {
        return questions == null ? 0: questions.size();
    }
}
