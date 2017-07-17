package com.seki.saezurishiki.model.impl;

import com.seki.saezurishiki.entity.DirectMessageEntity;
import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.entity.UserEntity;
import com.seki.saezurishiki.model.LoginUserScreen;
import com.seki.saezurishiki.model.adapter.ModelActionType;
import com.seki.saezurishiki.model.adapter.ModelMessage;
import com.seki.saezurishiki.model.util.ModelObserver;
import com.seki.saezurishiki.network.twitter.TwitterAccount;

import twitter4j.DirectMessage;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusUpdate;
import twitter4j.TwitterException;
import twitter4j.User;

public class LoginUserScreenImp extends ModelBaseImp implements LoginUserScreen {

    LoginUserScreenImp() {
        super();
        this.streamManager.addListener(this);
    }

    @Override
    public void getLoginUser() {
        this.executor.execute(() -> {
            try {
                User loginUser = this.repository.getTwitter().showUser(TwitterAccount.getLoginUserId());
                final ModelMessage message = ModelMessage.of(ModelActionType.LOAD_USER, this.repository.map(loginUser));
                observable.notifyObserver(message);
            } catch (TwitterException e) {
                observable.notifyObserver(ModelMessage.error(e));
            }
        });
    }

    @Override
    public void postTweet(StatusUpdate status) {
        this.executor.execute(() -> {
            try {
                final Status postedStatus = this.repository.getTwitter().updateStatus(status);
                final TweetEntity postedTweet = this.repository.map(postedStatus);
                final ModelMessage message = ModelMessage.of(ModelActionType.COMPLETE_POST_TWEET, postedTweet);
                observable.notifyObserver(message);
            } catch (TwitterException e) {
                observable.notifyObserver(ModelMessage.error(e));
            }
        });
    }

    @Override
    public void addObserver(ModelObserver observer){
        observable.addObserver(observer);
        this.userStreamObservable.addObserver(observer);
    }

    @Override
    public void removeObserver(ModelObserver observer){
        observable.removeObserver(observer);
        this.userStreamObservable.removeObserver(observer);
    }

    @Override
    public void onFollow(User source, User followedUser) {
        //do noting
    }

    @Override
    public void onRemove(User source, User removedUser) {
        //do noting
    }

    @Override
    public void onBlock(User source, User blockedUser) {
        //do noting
    }

    @Override
    public void onUnblock(User source, User unblockedUser) {
        //do noting
    }

    @Override
    public void onDirectMessage(DirectMessage directMessage) {
        final DirectMessageEntity entity = this.repository.addDM(directMessage);
        final ModelMessage message = ModelMessage.of(ModelActionType.RECEIVE_DIRECT_MESSAGE, entity);
        this.userStreamObservable.notifyObserver(message);
    }

    @Override
    public void onStatus(Status status) {
        TweetEntity tweet = this.repository.map(status);
        this.repository.addStatus(tweet);
        final ModelMessage message = ModelMessage.of(ModelActionType.RECEIVE_TWEET, tweet);
        this.userStreamObservable.notifyObserver(message);
    }

    @Override
    public void onDeletionNotice(StatusDeletionNotice deletionNotice) {
        if (!this.repository.hasStatus(deletionNotice.getStatusId())) {
            //削除されたtweetがローカルに存在しない場合には何もすることがない
            return;
        }

        final TweetEntity deletedTweet = this.repository.getTweet(deletionNotice.getStatusId());
        final ModelMessage message = ModelMessage.of(ModelActionType.RECEIVE_DELETION, deletedTweet);
        this.userStreamObservable.notifyObserver(message);
    }

    @Override
    public void onFavorite(User sourceUser, User targetUser, Status targetTweet) {
        final TweetEntity tweet = this.repository.map(targetTweet);
        final UserEntity source = this.repository.map(sourceUser);
        final UserEntity target = this.repository.map(targetUser);
        final ModelMessage message = ModelMessage.of(ModelActionType.RECEIVE_FAVORITE, tweet, source, target);
        this.userStreamObservable.notifyObserver(message);
    }

    @Override
    public void onUnFavorite(User sourceUser, User targetUser, Status targetTweet) {
        final TweetEntity tweet = this.repository.map(targetTweet);
        final UserEntity source = this.repository.map(sourceUser);
        final UserEntity target = this.repository.map(targetUser);
        final ModelMessage message = ModelMessage.of(ModelActionType.RECEIVE_UN_FAVORITE, tweet, source, target);
        this.userStreamObservable.notifyObserver(message);
    }
}
