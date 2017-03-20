package com.seki.saezurishiki.model.adapter;

/**
 * Created by seki on 2017/01/22.
 */

public enum ModelActionType {
    LOAD_TWEET,
    LOAD_REPLY,
    LOAD_TWEETS,
    LOAD_FAVORITE,

    POST_TWEET,
    UPDATE_TWEET,
    DELETE_TWEET,

    LOAD_FOLLOWS,
    LOAD_FOLLOWERS,

    LOAD_MESSAGES,

    SEND_MESSAGE,

    ERROR,
}
