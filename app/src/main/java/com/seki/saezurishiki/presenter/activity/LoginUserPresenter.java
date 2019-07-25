package com.seki.saezurishiki.presenter.activity;

import android.os.Handler;
import android.os.Looper;

import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.entity.UserEntity;
import com.seki.saezurishiki.model.LoginUserScreen;
import com.seki.saezurishiki.model.adapter.ModelMessage;
import com.seki.saezurishiki.model.util.ModelObservable;
import com.seki.saezurishiki.model.util.ModelObserver;

public class LoginUserPresenter implements ModelObserver {

    private final LoginUserScreen model;
    private final View view;

    public interface View {
        void setPresenter(LoginUserPresenter presenter);
        void onLoadLoginUser(UserEntity user);
        void showReceiveReplyMessage(TweetEntity reply);
        void onCompletePost();
        void showReceiveDeletionMessage(TweetEntity deletedTweet);
        void onCompleteDeleteTweet();
        void showFavoritedMessage(TweetEntity tweet, UserEntity user);
        void onCompleteFavorite();
        void showUnFavoritedMessage(TweetEntity tweet, UserEntity user);
        void onCompleteUnFavorite();
        void onCompletePostTweet(TweetEntity tweet);
    }

    public LoginUserPresenter(LoginUserScreen model, LoginUserPresenter.View view) {
        this.model = model;
        this.view = view;
        this.view.setPresenter(this);
    }

    public void onResume() {
        this.model.addObserver(this);
    }

    public void onPause() {
        this.model.removeObserver(this);
    }

    public void onDestroy() {
    }

    public void logout() {
    }

    public void connectNetwork() {
        loadUser();
    }

    public void disconnectNetwork() {
    }

    public void loadUser() {
        this.model.getLoginUser();
    }

    @Override
    public void update(ModelObservable observable, ModelMessage message) {
        new Handler(Looper.getMainLooper()).post(() -> this.dispatch(message));
    }

    private void dispatch(ModelMessage message) {
        switch (message.type) {
            case LOAD_USER:
                this.view.onLoadLoginUser((UserEntity) message.data);
                break;

            case COMPLETE_POST_TWEET:
                this.view.onCompletePostTweet((TweetEntity)message.data);
                break;

            case RECEIVE_TWEET:
                this.receiveTweet((TweetEntity)message.data);
                break;

            case RECEIVE_DELETION:
                this.receiveDeletion((TweetEntity)message.data);
                break;

            case RECEIVE_FAVORITE:
                this.receiveFavorite((TweetEntity)message.data, message.source, message.target);
                break;

            case RECEIVE_UN_FAVORITE:
                this.receiveUnFavorite((TweetEntity)message.data, message.source, message.target);
                break;
        }
    }


    private void receiveTweet(TweetEntity tweet) {
        if (tweet.isSentToLoginUser) {
            this.view.showReceiveReplyMessage(tweet);
        }

        if (tweet.isSentByLoginUser) {
            this.view.onCompletePost();
        }
    }


    private void receiveDeletion(TweetEntity deletedTweet) {
        this.view.showReceiveDeletionMessage(deletedTweet);

        if (deletedTweet.isSentByLoginUser) {
            this.view.onCompleteDeleteTweet();
        }
    }


    private void receiveFavorite(TweetEntity tweet, UserEntity sourceUser, UserEntity targetUser) {
        if (sourceUser.isLoginUser) {
            this.view.onCompleteFavorite();
            return;
        }

        if (targetUser.isLoginUser) {
            this.view.showFavoritedMessage(tweet, sourceUser);
        }
    }


    private void receiveUnFavorite(TweetEntity tweet, UserEntity sourceUser, UserEntity targetUser) {
        if (sourceUser.isLoginUser) {
            this.view.onCompleteUnFavorite();
            return;
        }

        if (targetUser.isLoginUser) {
            this.view.showUnFavoritedMessage(tweet, sourceUser);
        }
    }
}
