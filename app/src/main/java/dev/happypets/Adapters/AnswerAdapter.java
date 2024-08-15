package dev.happypets.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import dev.happypets.Database.DataManager;
import dev.happypets.Objects.Answer;
import dev.happypets.R;

public class AnswerAdapter extends RecyclerView.Adapter<AnswerAdapter.AnswerViewHolder> {
    private Context context;
    private List<Answer> answers;

    public AnswerAdapter(Context context, List<Answer> answers) {
        this.context = context;
        this.answers = answers;
    }

    @NonNull
    @Override
    public AnswerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.answer_item, parent, false);
        return new AnswerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnswerViewHolder holder, int position) {
        Answer answer = answers.get(position);
        holder.txtAnswerBody.setText(answer.getText());
        holder.txtAnswerTitle.setText(answer.getTitle());

        String answeredByID = answer.getAnsweredByID();
        if (answeredByID == null) {
            holder.txtAnsweredBy.setText("Unknown");
            return; // Exit early since we cannot proceed without an ID
        }

        DataManager.getInstance(context).getKindOfUser(answeredByID, new DataManager.KindOfUserCallback() {
            @Override
            public void onResult(String kindOfUser) {
                DatabaseReference baseRef = FirebaseDatabase.getInstance().getReference();
                DatabaseReference insideRef = null;

                if ("vet".equals(kindOfUser)) {
                    insideRef = baseRef.child("Veterinarians").child(answeredByID);
                } else if ("user".equals(kindOfUser)) {
                    insideRef = baseRef.child("Users").child(answeredByID);
                }

                if (insideRef != null) {
                    insideRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                String userName = snapshot.child("name").getValue(String.class);
                                if ("vet".equals(kindOfUser)) {
                                    holder.txtAnsweredBy.setText("Vet: " + userName);
                                    holder.txtAnsweredBy.setTypeface(null, Typeface.BOLD);
                                } else {
                                    holder.txtAnsweredBy.setText(userName);
                                    holder.txtAnsweredBy.setTypeface(null, Typeface.NORMAL);
                                }
                            } else {
                                holder.txtAnsweredBy.setText("Unknown");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            holder.txtAnsweredBy.setText("Unknown");
                        }
                    });
                } else {
                    holder.txtAnsweredBy.setText("Unknown");
                }
            }

            @Override
            public void onError(Exception e) {
                holder.txtAnsweredBy.setText("Unknown");
            }
        });
    }


    @Override
    public int getItemCount() {
        return answers.size();
    }

    public static class AnswerViewHolder extends RecyclerView.ViewHolder {
        TextView txtAnswerBody;
        TextView txtAnswerTitle;
        TextView txtAnsweredBy;

        public AnswerViewHolder(@NonNull View itemView) {
            super(itemView);
            txtAnswerBody = itemView.findViewById(R.id.txt_answer_body);
            txtAnswerTitle = itemView.findViewById(R.id.txt_answer_title);
            txtAnsweredBy = itemView.findViewById(R.id.txt_answered_by);
        }
    }
}

