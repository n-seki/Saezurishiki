package com.seki.saezurishiki.model;

import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.model.util.ModelObserver;

public interface TweetModel extends ModelBase {
    void favorite(TweetEntity tweetEntity);
    void unFavorite(TweetEntity tweetEntity);
    void reTweet(TweetEntity tweetEntity);
    void delete(TweetEntity tweetEntity);

    @Override
    void addObserver(ModelObserver observer);
    @Override
    void removeObserver(ModelObserver observer);
}
