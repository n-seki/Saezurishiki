package com.seki.saezurishiki.model.impl;

import com.seki.saezurishiki.model.TweetListModel;
import com.seki.saezurishiki.network.twitter.TwitterAccount;

import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.User;


abstract class TweetListModelImp extends ModelBaseImp implements TweetListModel {

    TweetListModelImp(TwitterAccount twitterAccount) {
        super(twitterAccount);
    }

    @Override
    abstract public void request(Paging paging);

    @Override
    public void onStatus(Status tweet) {

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

}
