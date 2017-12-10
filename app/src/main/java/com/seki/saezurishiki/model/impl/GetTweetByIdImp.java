package com.seki.saezurishiki.model.impl;

import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.model.GetTweetById;
import com.seki.saezurishiki.repository.TweetRepositoryKt;

class GetTweetByIdImp implements GetTweetById {

    GetTweetByIdImp() {}

    @Override
    public TweetEntity get(long id) {
        return TweetRepositoryKt.INSTANCE.get(id);
    }
}
