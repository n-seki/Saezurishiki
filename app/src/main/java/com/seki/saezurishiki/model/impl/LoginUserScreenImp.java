package com.seki.saezurishiki.model.impl;

import com.seki.saezurishiki.entity.DirectMessageEntity;
import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.entity.UserEntity;
import com.seki.saezurishiki.model.LoginUserScreen;
import com.seki.saezurishiki.model.adapter.ModelActionType;
import com.seki.saezurishiki.model.adapter.ModelMessage;
import com.seki.saezurishiki.model.util.ModelObserver;
import com.seki.saezurishiki.network.twitter.TwitterAccount;
import com.seki.saezurishiki.network.twitter.UserStreamManager;
import com.seki.saezurishiki.repository.DirectMessageRepository;
import com.seki.saezurishiki.repository.TweetRepository;
import com.seki.saezurishiki.repository.UserRepository;

import twitter4j.DirectMessage;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
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
                final UserEntity loginUser = UserRepository.INSTANCE.find(TwitterAccount.getLoginUserId());
                final ModelMessage message = ModelMessage.of(ModelActionType.LOAD_USER, loginUser);
                observable.notifyObserver(message);
            } catch (TwitterException e) {
                observable.notifyObserver(ModelMessage.error(e));
            }
        });
    }

    @Override
    public void startUserStream() {
        this.executor.execute(() ->  {
            if (UserStreamManager.getInstance().start()) {
                final ModelMessage message = ModelMessage.of(ModelActionType.START_USER_STREAM, null);
                observable.notifyObserver(message);
            }
        });
    }

    @Override
    public void stopUserStream() {
        this.executor.execute(() -> {
            if (UserStreamManager.getInstance().stop()) {
                final ModelMessage message = ModelMessage.of(ModelActionType.STOP_USER_STREAM, null);
                observable.notifyObserver(message);
            }
        });
    }

    @Override
    public void finishUserStream() {
        this.executor.execute(() -> {
            if (UserStreamManager.getInstance().destroy()) {
                final ModelMessage message = ModelMessage.of(ModelActionType.DESTROY_USER_STREAM, null);
                observable.notifyObserver(message);
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
        final DirectMessageEntity entity = DirectMessageRepository.INSTANCE.add(directMessage);
        final ModelMessage message = ModelMessage.of(ModelActionType.RECEIVE_DIRECT_MESSAGE, entity);
        this.userStreamObservable.notifyObserver(message);
    }

    @Override
    public void onStatus(Status status) {
        final TweetEntity tweet = TweetRepository.INSTANCE.mappingAdd(status);
        final ModelMessage message = ModelMessage.of(ModelActionType.RECEIVE_TWEET, tweet);
        this.userStreamObservable.notifyObserver(message);
    }

    @Override
    public void onDeletionNotice(StatusDeletionNotice deletionNotice) {
        if (!TweetRepository.INSTANCE.has(deletionNotice.getStatusId())) {
            //削除されたtweetがローカルに存在しない場合には何もすることがない
            return;
        }

        final TweetEntity deletedTweet = TweetRepository.INSTANCE.get(deletionNotice.getStatusId());
        final ModelMessage message = ModelMessage.of(ModelActionType.RECEIVE_DELETION, deletedTweet);
        this.userStreamObservable.notifyObserver(message);
    }

    @Override
    public void onFavorite(User sourceUser, User targetUser, Status targetTweet) {
        final TweetEntity tweet = TweetRepository.INSTANCE.mappingAdd(targetTweet);
        final UserEntity source = UserRepository.INSTANCE.add(sourceUser);
        final UserEntity target = UserRepository.INSTANCE.add(targetUser);
        final ModelMessage message = ModelMessage.of(ModelActionType.RECEIVE_FAVORITE, tweet, source, target);
        this.userStreamObservable.notifyObserver(message);
    }

    @Override
    public void onUnFavorite(User sourceUser, User targetUser, Status targetTweet) {
        final TweetEntity tweet = TweetRepository.INSTANCE.mappingAdd(targetTweet);
        final UserEntity source = UserRepository.INSTANCE.add(sourceUser);
        final UserEntity target = UserRepository.INSTANCE.add(targetUser);
        final ModelMessage message = ModelMessage.of(ModelActionType.RECEIVE_UN_FAVORITE, tweet, source, target);
        this.userStreamObservable.notifyObserver(message);
    }
}
