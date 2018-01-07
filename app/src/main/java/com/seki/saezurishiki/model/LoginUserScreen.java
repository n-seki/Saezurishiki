package com.seki.saezurishiki.model;

import com.seki.saezurishiki.network.twitter.streamListener.CustomUserStreamListener;

import twitter4j.StatusUpdate;

public interface LoginUserScreen extends ModelBase, CustomUserStreamListener {
    void getLoginUser();
    void startUserStream();
    void stopUserStream();
    void finishUserStream();

}
