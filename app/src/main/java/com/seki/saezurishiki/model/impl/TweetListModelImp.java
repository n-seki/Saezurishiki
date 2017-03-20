package com.seki.saezurishiki.model.impl;

import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.model.TweetListModel;
import com.seki.saezurishiki.model.adapter.ModelActionType;
import com.seki.saezurishiki.model.adapter.ModelMessage;
import com.seki.saezurishiki.network.twitter.TwitterAccount;

import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
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
        final ModelMessage message = ModelMessage.of(ModelActionType.LOAD_TWEET, tweet);
        this.observable.notifyObserver(message);
    }

    @Override
    public void onDeletionNotice(StatusDeletionNotice deletionNotice) {

    }

    @Override
    public void onFavorite(User sourceUser, User targetUser, Status targetTweet) {

    }

    @Override
    public void onUnFavorite(User sourceUser, User targetUser, Status targetTweet) {

    }

    @Override
    public void favorite(TweetEntity tweetEntity) {

    }

    @Override
    public void unFavorite(TweetEntity tweetEntity) {

    }

    @Override
    public void reTweet(TweetEntity tweetEntity) {

    }

    @Override
    public void delete(TweetEntity tweetEntity) {

    }

}
