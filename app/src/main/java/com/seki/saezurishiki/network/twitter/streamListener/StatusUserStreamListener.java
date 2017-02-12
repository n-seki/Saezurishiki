package com.seki.saezurishiki.network.twitter.streamListener;

import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.User;

/**
 * Created by seki on 2016/10/02.
 */
public interface StatusUserStreamListener {
    void onStatus(Status status);
    void onDeletionNotice(StatusDeletionNotice obj);
    void onFavorite(User sourceUser, User targetUser, Status e3);
    void onUnFavorite(User sourceUser, User targetUser, Status e3);
}
