package com.seki.saezurishiki.model.impl;

import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.model.TweetListModel;
import com.seki.saezurishiki.model.adapter.ModelActionType;
import com.seki.saezurishiki.model.adapter.ModelMessage;
import com.seki.saezurishiki.network.twitter.TwitterAccount;

import twitter4j.Paging;
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
    abstract public void request(Paging paging);

    @Override
    public void onStatus(Status status) {
        final TweetEntity tweet = this.twitterAccount.getRepository().map(status);
        this.twitterAccount.getRepository().addStatus(tweet);
        final ModelMessage message = ModelMessage.of(ModelActionType.RECEIVE_TWEET, tweet);
        this.observable.notifyObserver(message);
    }

    @Override
    public void onDeletionNotice(StatusDeletionNotice deletionNotice) {
        this.twitterAccount.getRepository().addDeletionNotice(deletionNotice);
        final ModelMessage message = ModelMessage.of(ModelActionType.RECEIVE_DELETION, deletionNotice);
        this.observable.notifyObserver(message);
    }

    @Override
    public void onFavorite(User sourceUser, User targetUser, Status targetTweet) {
        final TweetEntity tweet = this.twitterAccount.getRepository().map(targetTweet);
        this.twitterAccount.getRepository().addStatus(tweet);
        final ModelMessage message = ModelMessage.of(ModelActionType.RECEIVE_FAVORITE, tweet);
        this.observable.notifyObserver(message);
    }

    @Override
    public void onUnFavorite(User sourceUser, User targetUser, Status targetTweet) {
        final TweetEntity tweet = this.twitterAccount.getRepository().map(targetTweet);
        this.twitterAccount.getRepository().addStatus(tweet);
        final ModelMessage message = ModelMessage.of(ModelActionType.RECEIVE_UN_FAVORITE, tweet);
        this.observable.notifyObserver(message);
    }

    @Override
    public void favorite(final TweetEntity tweetEntity) {
        this.executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final Status result = twitterAccount.twitter.createFavorite(tweetEntity.getId());
                    final ModelMessage message = ModelMessage.of(ModelActionType.COMPLETE_FAVORITE, result);
                    observable.notifyObserver(message);
                } catch (TwitterException e) {
                    observable.notifyObserver(ModelMessage.error(e));
                }
            }
        });
    }

    @Override
    public void unFavorite(final TweetEntity tweetEntity) {
        this.executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final Status result = twitterAccount.twitter.destroyFavorite(tweetEntity.getId());
                    final ModelMessage message = ModelMessage.of(ModelActionType.COMPLETE_UN_FAVORITE, result);
                    observable.notifyObserver(message);
                } catch (TwitterException e) {
                    observable.notifyObserver(ModelMessage.error(e));
                }
            }
        });
    }

    @Override
    public void reTweet(final TweetEntity tweetEntity) {
        this.executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final Status result = twitterAccount.twitter.retweetStatus(tweetEntity.getId());
                    final ModelMessage message = ModelMessage.of(ModelActionType.COMPLETE_RETWEET, result);
                    observable.notifyObserver(message);
                } catch (TwitterException e) {
                    observable.notifyObserver(ModelMessage.error(e));
                }
            }
        });
    }

    @Override
    public void delete(final TweetEntity tweetEntity) {
        this.executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final Status result = twitterAccount.twitter.destroyStatus(tweetEntity.getId());
                    final ModelMessage message = ModelMessage.of(ModelActionType.COMPLETE_DELETE_TWEET, result);
                    observable.notifyObserver(message);
                } catch (TwitterException e) {
                    observable.notifyObserver(ModelMessage.error(e));
                }
            }
        });
    }

}
