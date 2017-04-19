package com.seki.saezurishiki.entity.mapper;


import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.entity.UserEntity;

import twitter4j.Status;
import twitter4j.User;

public class EntityMapper {

    private final long loginUserId;

    public EntityMapper(long loginUserId) {
        this.loginUserId = loginUserId;
    }

    public TweetEntity map(Status status) {
        final boolean isLoginUserStatus =
                this.loginUserId == status.getUser().getId();

        final boolean isReplyToLoginUser =
                this.loginUserId == status.getInReplyToUserId();

        return new TweetEntity(status, isLoginUserStatus, isReplyToLoginUser);
    }

    public UserEntity map(User user) {
        final boolean isLoginUser = this.loginUserId == user.getId();

        return new UserEntity(user, isLoginUser);
    }
}
