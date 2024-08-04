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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dev.happypets.Activities.ExistingQuestionActivity;
import dev.happypets.CallBacks.QuestionCallBack;
import dev.happypets.Database.DataManager;
import dev.happypets.Objects.Question;
import dev.happypets.R;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder> {

    private final Context context;
    private final QuestionCallBack questionCallBack;
    private ArrayList<Question> questions;
    private DataManager dataManager;
    private Set<String> favoriteQuestionIds = new HashSet<>();

    public QuestionAdapter(Context context, ArrayList<Question> questions, QuestionCallBack questionCallBack) {
        this.context = context;
        this.questions = questions;
        this.questionCallBack = questionCallBack;
        this.dataManager = DataManager.getInstance(context);
        initializeFavoriteQuestionsListener();
    }

    private void initializeFavoriteQuestionsListener() {
        String userId = dataManager.getCurrentUserId();
        dataManager.listenForFavoriteQuestions(userId, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                favoriteQuestionIds.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String questionId = snapshot.getKey();
                    if (questionId != null) {
                        favoriteQuestionIds.add(questionId);
                    }
                }
                notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }

    public class QuestionViewHolder extends RecyclerView.ViewHolder {
        private final MaterialTextView title;
        private MaterialTextView numberOfAnswers;
        private MaterialTextView questionTime;
        private AppCompatImageView favorite;

        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.txt_questionTitle);
            numberOfAnswers = itemView.findViewById(R.id.txt_numberOfAnswers);
            questionTime = itemView.findViewById(R.id.txt_question_time);
            favorite = itemView.findViewById(R.id.question_IMG_favorite);

            favorite.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position == RecyclerView.NO_POSITION) return; // Prevent crashes if position is invalid

                String userId = dataManager.getCurrentUserId();
                Question question = questions.get(position);

                if (favoriteQuestionIds.contains(question.getQuestionId())) {
                    dataManager.removeFavoriteQuestion(userId, question, () -> {
                        favoriteQuestionIds.remove(question.getQuestionId());
                        notifyItemChanged(position);
                        questionCallBack.favoriteClicked(question, position);
                    });
                } else {
                    dataManager.addFavoriteQuestion(userId, question, () -> {
                        favoriteQuestionIds.add(question.getQuestionId());
                        notifyItemChanged(position);
                        questionCallBack.favoriteClicked(question, position);
                    });
                }
            });

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                Question question = questions.get(position);
                if (question.getQuestionId() != null) {
                    Intent intent = new Intent(context, ExistingQuestionActivity.class);
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
    public QuestionAdapter.QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_item, parent, false);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionAdapter.QuestionViewHolder holder, int position) {
        Question question = questions.get(position);
        holder.title.setText(question.getTitle());
        if (question.getRelatedAnswers() == null) {
            holder.numberOfAnswers.setText("0");
        } else {
            holder.numberOfAnswers.setText(String.valueOf(question.getRelatedAnswers().size()));
        }
        holder.questionTime.setText(question.getAskedTime());

        boolean isInFavorites = favoriteQuestionIds.contains(question.getQuestionId());
        holder.favorite.setImageResource(isInFavorites ? R.drawable.fill_heart : R.drawable.heart_thin);
    }

    @Override
    public int getItemCount() {
        return questions == null ? 0 : questions.size();
    }

    public void updateQuestions(ArrayList<Question> newQuestions) {
        this.questions.clear();
        this.questions.addAll(newQuestions);
        notifyDataSetChanged();
    }
}
