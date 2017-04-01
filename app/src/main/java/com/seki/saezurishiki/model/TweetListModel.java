package com.seki.saezurishiki.model;


import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.model.util.ModelObserver;
import com.seki.saezurishiki.network.twitter.streamListener.StatusUserStreamListener;

import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.User;

public interface TweetListModel extends ModelBase, StatusUserStreamListener {

    void request(long userId, Paging paging);
    void favorite(TweetEntity tweetEntity);
    void unFavorite(TweetEntity tweetEntity);
    void reTweet(TweetEntity tweetEntity);
    void delete(TweetEntity tweetEntity);

    @Override
    void addObserver(ModelObserver observer);
    @Override
    void removeObserver(ModelObserver observer);

    @Override
    void onStatus(Status tweet);
    @Override
    void onDeletionNotice(StatusDeletionNotice deletionNotice);
    @Override
    void onFavorite(User sourceUser, User targetUser, Status targetTweet);
    @Override
    void onUnFavorite(User sourceUser, User targetUser, Status targetTweet);
}
