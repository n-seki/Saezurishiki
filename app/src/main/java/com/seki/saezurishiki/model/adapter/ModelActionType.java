package com.seki.saezurishiki.model.adapter;


public enum ModelActionType {
    LOAD_TWEET(false, DisplayType.ADD),
    LOAD_HOME_LIST(false, DisplayType.ADD),
    LOAD_REPLY_LIST(false, DisplayType.ADD),
    LOAD_TWEET_LIST(false, DisplayType.ADD),
    LOAD_FAVORITE_LIST(false, DisplayType.ADD),
    LOAD_SEARCH(false, DisplayType.ADD),

    RECEIVE_TWEET(true, DisplayType.ADD),
    RECEIVE_FAVORITE(true, DisplayType.UPDATE_UNLESS_MAIN),
    RECEIVE_UN_FAVORITE(true, DisplayType.UPDATE_UNLESS_MAIN),
    RECEIVE_DELETION(true, DisplayType.UPDATE_UNLESS_MAIN),

    COMPLETE_FAVORITE(false, DisplayType.UPDATE),
    COMPLETE_UN_FAVORITE(false, DisplayType.UPDATE),
    COMPLETE_RETWEET(false, DisplayType.UPDATE),
    COMPLETE_DELETE_TWEET(false, DisplayType.DELETE),
    COMPLETE_POST_TWEET(false, DisplayType.NO_DISPLAY),

    LOAD_FRIENDS(false, DisplayType.ADD),
    LOAD_FOLLOWERS(false, DisplayType.ADD),

    LOAD_USER(false, DisplayType.NO_DISPLAY),
    LOAD_RELATIONSHIP(false, DisplayType.NO_DISPLAY),
    COMPLETE_FOLLOW(false, DisplayType.NO_DISPLAY),
    COMPLETE_REMOVE(false, DisplayType.NO_DISPLAY),
    COMPLETE_BLOCK(false, DisplayType.NO_DISPLAY),
    COMPLETE_DESTROY_BLOCK(false, DisplayType.NO_DISPLAY),

    ERROR(false, DisplayType.NO_DISPLAY);


    enum DisplayType {
        ADD,
        UPDATE,
        UPDATE_UNLESS_MAIN,
        DELETE,
        NO_DISPLAY,
    }

    final public boolean isUserStream;
    final public DisplayType displayType;

    ModelActionType(boolean isUserStream, DisplayType displayType) {
        this.isUserStream = isUserStream;
        this.displayType = displayType;
    }
}
