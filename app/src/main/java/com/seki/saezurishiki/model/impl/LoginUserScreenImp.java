package com.seki.saezurishiki.model.impl;

import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.entity.UserEntity;
import com.seki.saezurishiki.model.LoginUserScreen;
import com.seki.saezurishiki.model.adapter.ModelActionType;
import com.seki.saezurishiki.model.adapter.ModelMessage;
import com.seki.saezurishiki.model.util.ModelObserver;
import com.seki.saezurishiki.network.twitter.TwitterProvider;
import com.seki.saezurishiki.repository.TweetRepository;
import com.seki.saezurishiki.repository.UserRepository;

import javax.inject.Inject;
import javax.inject.Singleton;

import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.TwitterException;
import twitter4j.User;

@Singleton
public class LoginUserScreenImp extends ModelBaseImp implements LoginUserScreen {

    private final TwitterProvider mTwitterProvider;
    private final TweetRepository mTweetRepository;
    private final UserRepository mUserRepository;

    @Inject
    LoginUserScreenImp(TwitterProvider twitterProvider,
                       TweetRepository tweetRepository,
                       UserRepository userRepository) {
        super();
        mTwitterProvider = twitterProvider;
        mTweetRepository = tweetRepository;
        mUserRepository = userRepository;
    }

    @Override
    public void getLoginUser() {
        this.executor.execute(() -> {
            try {
                final UserEntity loginUser = mUserRepository.find(mTwitterProvider.getLoginUserId());
                final ModelMessage message = ModelMessage.of(ModelActionType.LOAD_USER, loginUser);
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
    public void onStatus(Status status) {
        final TweetEntity tweet = mTweetRepository.mappingAdd(status);
        final ModelMessage message = ModelMessage.of(ModelActionType.RECEIVE_TWEET, tweet);
        this.userStreamObservable.notifyObserver(message);
    }

    @Override
    public void onDeletionNotice(StatusDeletionNotice deletionNotice) {
        if (!mTweetRepository.has(deletionNotice.getStatusId())) {
            //削除されたtweetがローカルに存在しない場合には何もすることがない
            return;
        }

        final TweetEntity deletedTweet = mTweetRepository.get(deletionNotice.getStatusId());
        final ModelMessage message = ModelMessage.of(ModelActionType.RECEIVE_DELETION, deletedTweet);
        this.userStreamObservable.notifyObserver(message);
    }

    @Override
    public void onFavorite(User sourceUser, User targetUser, Status targetTweet) {
        final TweetEntity tweet = mTweetRepository.mappingAdd(targetTweet);
        final UserEntity source = mUserRepository.add(sourceUser);
        final UserEntity target = mUserRepository.add(targetUser);
        final ModelMessage message = ModelMessage.of(ModelActionType.RECEIVE_FAVORITE, tweet, source, target);
        this.userStreamObservable.notifyObserver(message);
    }

    @Override
    public void onUnFavorite(User sourceUser, User targetUser, Status targetTweet) {
        final TweetEntity tweet = mTweetRepository.mappingAdd(targetTweet);
        final UserEntity source = mUserRepository.add(sourceUser);
        final UserEntity target = mUserRepository.add(targetUser);
        final ModelMessage message = ModelMessage.of(ModelActionType.RECEIVE_UN_FAVORITE, tweet, source, target);
        this.userStreamObservable.notifyObserver(message);
    }
}
