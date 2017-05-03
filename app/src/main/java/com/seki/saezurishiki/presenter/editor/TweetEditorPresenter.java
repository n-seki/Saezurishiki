package com.seki.saezurishiki.presenter.editor;


public class TweetEditorPresenter {

    final private View view;

    private final int MAX_MESSAGE_LENGTH = 140;

    public TweetEditorPresenter(View view) {
        this.view = view;
        this.view.setPresenter(this);
    }

    public interface View {
        void showMessageEmptyError();
        void showMessageOverLengthError();
        void hideSoftKeyBoard();
        void postTweet(String message);
        void changeTextCountErrorColor();
        void changeTextCountDefaultColor();
        void setTextCount(String length);
        void hideUploadImage();
        void closeImageSource();
        void setPresenter(TweetEditorPresenter presenter);
    }

    public void onClickPostButton(String message, boolean hasMedia) {
        if (message.isEmpty() && !hasMedia) {
            this.view.showMessageEmptyError();
            return;
        }

        if (message.length() > MAX_MESSAGE_LENGTH) {
            this.view.showMessageOverLengthError();
            return;
        }

        this.view.hideSoftKeyBoard();
        this.view.postTweet(message);
    }


    public void onTextChange(int textLength) {
        if (textLength > MAX_MESSAGE_LENGTH) {
            this.view.changeTextCountErrorColor();
        } else {
            this.view.changeTextCountDefaultColor();
        }

        this.view.setTextCount(String.valueOf(textLength));
    }


    public void onClickUploadImage() {
        this.view.hideUploadImage();
        this.view.closeImageSource();
    }
}
