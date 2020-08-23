package com.seki.saezurishiki.entity.mapper;


import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.entity.UserEntity;
import com.seki.saezurishiki.network.twitter.TwitterProvider;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import twitter4j.Status;
import twitter4j.User;

@Singleton
public class EntityMapper {

    private final TwitterProvider mTwitterProvider;

    @Inject
    public EntityMapper(TwitterProvider twitterProvider) {
        mTwitterProvider = twitterProvider;
    }

    public TweetEntity map(Status status) {
        final boolean isLoginUserStatus =
                mTwitterProvider.getLoginUserId() == status.getUser().getId();

        final boolean isReplyToLoginUser =
                mTwitterProvider.getLoginUserId() == status.getInReplyToUserId();

        return new TweetEntity(status, isLoginUserStatus, isReplyToLoginUser, this);
    }

    public List<TweetEntity> map(List<Status> statusList) {
        final List<TweetEntity> tweets = new ArrayList<>(statusList.size());
        for (final Status status : statusList) {
            tweets.add(map(status));
        }
        return tweets;
    }

    public UserEntity map(User user) {
        final boolean isLoginUser = mTwitterProvider.getLoginUserId() == user.getId();
        return new UserEntity(user, isLoginUser);
    }
}
