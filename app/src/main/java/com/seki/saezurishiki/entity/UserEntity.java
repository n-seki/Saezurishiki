package com.seki.saezurishiki.entity;

import java.util.Date;

public class UserEntity {
    final private twitter4j.User mUser;

    public UserEntity(twitter4j.User user) {
        mUser = user;
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
        return mUser.getBiggerProfileImageURL();
    }

    public String getProfileBannerURL() {
        return mUser.getProfileBannerURL();
    }







}

