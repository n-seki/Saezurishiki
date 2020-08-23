package com.seki.saezurishiki.model;

import com.seki.saezurishiki.network.twitter.streamListener.CustomUserStreamListener;

public interface LoginUserScreen extends ModelBase, CustomUserStreamListener {
    void getLoginUser();
}
