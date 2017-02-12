package com.seki.saezurishiki.view.fragment;

public class DataType {

    public static final String COUNT = "count";

    private DataType() {
        throw new IllegalAccessError();
    }

    public static final String USER_ID         = "userId";
    public static final String STATUS          = "status";
    public static final String DIRECT_MESSAGE  = "directMessage";
    public static final String IS_REPLY        = "isReply";
    public static final String USER            = "user";
    public static final String QUERY           = "query";
    public static final String URL             = "url";
    public static final String IS_FRIENDS_LIST = "isFriendsList";
    public static final String IS_DELETED      = "isDeleted";
    public static final String IS_FOLLOW       = "isFollow";
    public static final String HASH_TAG        = "hash_tag";
    public static final String QUOTED_TWEET    = "quotedTweet";
    public static final String STATUS_ID       = "statusId";
}
