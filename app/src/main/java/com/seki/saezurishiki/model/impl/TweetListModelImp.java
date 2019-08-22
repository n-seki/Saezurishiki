package com.seki.saezurishiki.model.impl;

import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.entity.UserEntity;
import com.seki.saezurishiki.model.TweetListModel;
import com.seki.saezurishiki.model.adapter.ModelActionType;
import com.seki.saezurishiki.model.adapter.ModelMessage;
import com.seki.saezurishiki.model.adapter.RequestInfo;
import com.seki.saezurishiki.repository.TweetRepository;
import com.seki.saezurishiki.repository.UserRepository;

import java.util.concurrent.atomic.AtomicBoolean;

import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.TwitterException;
import twitter4j.User;


abstract class TweetListModelImp extends ModelBaseImp implements TweetListModel {

    final TweetRepository mTweetRepository;
    private final UserRepository mUserRepository;
    AtomicBoolean isLoading = new AtomicBoolean(false);

    TweetListModelImp(TweetRepository tweetRepository, UserRepository userRepository) {
        super();
        mTweetRepository = tweetRepository;
        mUserRepository = userRepository;
    }

    @Override
    abstract public void request(final RequestInfo info);


    @Override
    public void onStatus(Status status) {
        final TweetEntity tweet = mTweetRepository.mappingAdd(status);
        final ModelMessage message = ModelMessage.of(ModelActionType.RECEIVE_TWEET, tweet);
        this.userStreamObservable.notifyObserver(message);
    }

    @Override
    public void onDeletionNotice(StatusDeletionNotice deletionNotice) {
        final ModelMessage message = ModelMessage.of(ModelActionType.RECEIVE_DELETION, deletionNotice);
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


    @Override
    public void favorite(final TweetEntity tweetEntity) {
        this.executor.execute(() -> {
            try {
                final TweetEntity tweet = mTweetRepository.favorite(tweetEntity.getId());
                final ModelMessage message = ModelMessage.of(ModelActionType.COMPLETE_FAVORITE, tweet);
                observable.notifyObserver(message);
            } catch (TwitterException e) {
                observable.notifyObserver(ModelMessage.error(e));
            }
        });
    }

    @Override
    public void unFavorite(final TweetEntity tweetEntity) {
        this.executor.execute(() -> {
            try {
                final TweetEntity tweet = mTweetRepository.unfavorite(tweetEntity.getId());
                final ModelMessage message = ModelMessage.of(ModelActionType.COMPLETE_UN_FAVORITE, tweet);
                observable.notifyObserver(message);
            } catch (TwitterException e) {
                observable.notifyObserver(ModelMessage.error(e));
            }
        });
    }

    @Override
    public void reTweet(final TweetEntity tweetEntity) {
        this.executor.execute(() -> {
            try {
                final TweetEntity tweet = mTweetRepository.retweet(tweetEntity.getId());
                final ModelMessage message = ModelMessage.of(ModelActionType.COMPLETE_RETWEET, tweet);
                observable.notifyObserver(message);
            } catch (TwitterException e) {
                observable.notifyObserver(ModelMessage.error(e));
            }
        });
    }

    @Override
    public void delete(final TweetEntity tweetEntity) {
        this.executor.execute(() -> {
            try {
                final TweetEntity tweet = mTweetRepository.destroy(tweetEntity.getId());
                final ModelMessage message = ModelMessage.of(ModelActionType.COMPLETE_DELETE_TWEET, tweet);
                observable.notifyObserver(message);
            } catch (TwitterException e) {
                observable.notifyObserver(ModelMessage.error(e));
            }
        });
    }

    @Override
    public boolean isDelete(final TweetEntity tweetEntity) {
        return mTweetRepository.hasDeletionNotice(tweetEntity.getId());
    }

}
