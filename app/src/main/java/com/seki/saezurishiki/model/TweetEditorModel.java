package com.seki.saezurishiki.model;

import twitter4j.StatusUpdate;

public interface TweetEditorModel extends ModelBase {
    void postTweet(StatusUpdate tweet);
    void uploadImage();
}
