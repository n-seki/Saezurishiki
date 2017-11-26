package com.seki.saezurishiki.model.impl;

import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.model.GetTweetById;
import com.seki.saezurishiki.repository.RemoteRepositoryImp;
import com.seki.saezurishiki.repository.TweetRepositoryKt;

class GetTweetByIdImp implements GetTweetById {

    private final RemoteRepositoryImp repository;

    GetTweetByIdImp() {
        this.repository = RemoteRepositoryImp.getInstance();
    }

    @Override
    public TweetEntity get(long id) {
        return TweetRepositoryKt.INSTANCE.get(id);
    }
}
