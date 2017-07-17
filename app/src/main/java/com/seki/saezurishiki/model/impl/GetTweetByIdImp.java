package com.seki.saezurishiki.model.impl;

import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.model.GetTweetById;
import com.seki.saezurishiki.network.twitter.TwitterAccount;
import com.seki.saezurishiki.repository.RemoteRepositoryImp;

class GetTweetByIdImp implements GetTweetById {

    private final RemoteRepositoryImp repository;

    GetTweetByIdImp() {
        this.repository = RemoteRepositoryImp.getInstance();
    }

    @Override
    public TweetEntity get(long id) {
        return this.repository.getTweet(id);
    }
}
