package com.seki.saezurishiki.model.impl;

import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.entity.UserEntity;
import com.seki.saezurishiki.model.TweetListModel;
import com.seki.saezurishiki.model.adapter.ModelActionType;
import com.seki.saezurishiki.model.adapter.ModelMessage;
import com.seki.saezurishiki.model.adapter.RequestInfo;
import com.seki.saezurishiki.network.twitter.TwitterAccount;

import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.TwitterException;
import twitter4j.User;


abstract class TweetListModelImp extends ModelBaseImp implements TweetListModel {

    TweetListModelImp(TwitterAccount twitterAccount) {
        super(twitterAccount);
        this.twitterAccount.addStreamListener(this);
    }

    @Override
    abstract public void request(final RequestInfo info);


    @Override
    public void onStatus(Status status) {
        final TweetEntity tweet = this.twitterAccount.getRepository().map(status);
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
        final TweetEntity tweet = this.twitterAccount.getRepository().map(targetTweet);
        final UserEntity source = this.twitterAccount.getRepository().map(sourceUser);
        final UserEntity target = this.twitterAccount.getRepository().map(targetUser);
        final ModelMessage message = ModelMessage.of(ModelActionType.RECEIVE_FAVORITE, tweet, source, target);
        this.userStreamObservable.notifyObserver(message);
    }

    @Override
    public void onUnFavorite(User sourceUser, User targetUser, Status targetTweet) {
        final TweetEntity tweet = this.twitterAccount.getRepository().map(targetTweet);
        final UserEntity source = this.twitterAccount.getRepository().map(sourceUser);
        final UserEntity target = this.twitterAccount.getRepository().map(targetUser);
        final ModelMessage message = ModelMessage.of(ModelActionType.RECEIVE_UN_FAVORITE, tweet, source, target);
        this.userStreamObservable.notifyObserver(message);
    }


    @Override
    public void favorite(final TweetEntity tweetEntity) {
        this.executor.execute(() -> {
            try {
                final Status result = twitterAccount.twitter.createFavorite(tweetEntity.getId());
                final TweetEntity tweet = twitterAccount.getRepository().map(result);
                twitterAccount.getRepository().addStatus(tweet);
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
                final Status result = twitterAccount.twitter.destroyFavorite(tweetEntity.getId());
                final TweetEntity tweet = twitterAccount.getRepository().map(result);
                twitterAccount.getRepository().addStatus(tweet);
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
                final Status result = twitterAccount.twitter.retweetStatus(tweetEntity.getId());
                final ModelMessage message = ModelMessage.of(ModelActionType.COMPLETE_RETWEET, result);
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
                final Status result = twitterAccount.twitter.destroyStatus(tweetEntity.getId());
                final ModelMessage message = ModelMessage.of(ModelActionType.COMPLETE_DELETE_TWEET, result);
                observable.notifyObserver(message);
            } catch (TwitterException e) {
                observable.notifyObserver(ModelMessage.error(e));
            }
        });
    }

    @Override
    public boolean isDelete(final TweetEntity tweetEntity) {
        return this.twitterAccount.getRepository().hasDeletionNotice(tweetEntity.getId());
    }

}
