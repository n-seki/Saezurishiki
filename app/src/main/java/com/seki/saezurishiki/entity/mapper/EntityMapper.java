package com.seki.saezurishiki.entity.mapper;


import com.seki.saezurishiki.entity.TweetEntity;

import twitter4j.Status;

public class EntityMapper {

    private final long loginUserId;

    public EntityMapper(long loginUserId) {
        this.loginUserId = loginUserId;
    }

    public TweetEntity createTweetEntity(Status status) {
        final boolean isLoginUserStatus =
                this.loginUserId == status.getUser().getId();

        final boolean isReplyToLoginUser =
                this.loginUserId == status.getInReplyToUserId();

        return new TweetEntity(status, isLoginUserStatus, isReplyToLoginUser);
    }
}
