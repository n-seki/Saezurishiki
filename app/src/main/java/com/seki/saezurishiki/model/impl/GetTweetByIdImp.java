package com.seki.saezurishiki.model.impl;

import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.model.GetTweetById;
import com.seki.saezurishiki.network.twitter.TwitterAccount;

class GetTweetByIdImp implements GetTweetById {

    final TwitterAccount account;

    GetTweetByIdImp(TwitterAccount account) {
        this.account = account;
    }

    @Override
    public TweetEntity get(long id) {
        return account.getRepository().getTweet(id);
    }
}
