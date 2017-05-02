package com.seki.saezurishiki.model;


import com.seki.saezurishiki.entity.TweetEntity;

public interface GetTweetById {
    TweetEntity get(long id);
}
