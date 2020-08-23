package com.seki.saezurishiki.entity;

import java.io.Serializable;
import java.util.Date;

import twitter4j.User;

public class UserEntity implements Serializable {
    final private twitter4j.User mUser;
    final public boolean isLoginUser;

    public UserEntity(twitter4j.User user) {
        mUser = user;
        this.isLoginUser = false;
    }

    public UserEntity(User user, boolean isLoginUser) {
        mUser = user;
        this.isLoginUser = isLoginUser;

    }

    public long getId() {
        return mUser.getId();
    }

    public String getName() {
        return mUser.getName();
    }

    public String getScreenName() {
        return mUser.getScreenName();
    }

    public Date getCreatedAt() {
        return mUser.getCreatedAt();
    }

    public String getLocation() {
        return mUser.getLocation();
    }

    public String getDescription() {
        return mUser.getDescription();
    }

    public boolean isProtected() {
        return mUser.isProtected();
    }

    public int getStatusesCount() {
        return mUser.getStatusesCount();
    }

    public int getFollowersCount() {
        return mUser.getFollowersCount();
    }

    public int getFriendsCount() {
        return mUser.getFriendsCount();
    }

    public int getFavouritesCount() {
        return mUser.getFavouritesCount();
    }

    public String getBiggerProfileImageURL() {
        return mUser.getBiggerProfileImageURLHttps();
    }

    public String getProfileBannerURL() {
        // not supported (always null)
        return mUser.getProfileBannerURL();
    }

    public String getURL() {
        return mUser.getURL();
    }
}

