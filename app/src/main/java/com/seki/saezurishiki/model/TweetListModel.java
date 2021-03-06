package com.seki.saezurishiki.model;


import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.model.adapter.RequestInfo;
import com.seki.saezurishiki.model.util.ModelObserver;
import com.seki.saezurishiki.network.twitter.streamListener.StatusUserStreamListener;

public interface TweetListModel extends ModelBase, StatusUserStreamListener {

    void request(RequestInfo info);
    void favorite(TweetEntity tweetEntity);
    void unFavorite(TweetEntity tweetEntity);
    void reTweet(TweetEntity tweetEntity);
    void delete(TweetEntity tweetEntity);
    boolean isDelete(TweetEntity tweetEntity);

    @Override
    void addObserver(ModelObserver observer);
    @Override
    void removeObserver(ModelObserver observer);
}
