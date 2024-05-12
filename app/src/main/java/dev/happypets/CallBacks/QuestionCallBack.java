package dev.happypets.CallBacks;

import dev.happypets.Objects.Question;

public interface QuestionCallBack {

    void favoriteClicked(Question question, int position);

    void onClicked(Question question, int position);
}
