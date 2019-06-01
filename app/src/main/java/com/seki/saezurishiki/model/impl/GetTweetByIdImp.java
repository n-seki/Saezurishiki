package com.seki.saezurishiki.model.impl;

import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.model.GetTweetById;
import com.seki.saezurishiki.repository.TweetRepository;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
class GetTweetByIdImp implements GetTweetById {

    @Inject
    GetTweetByIdImp() {}

    @Override
    public TweetEntity get(long id) {
        return TweetRepository.INSTANCE.get(id);
    }
}
