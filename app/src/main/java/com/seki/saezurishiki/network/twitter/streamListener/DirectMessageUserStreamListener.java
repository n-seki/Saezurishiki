package com.seki.saezurishiki.network.twitter.streamListener;

import twitter4j.DirectMessage;

/**
 * Created by seki on 2016/10/02.
 */
public interface DirectMessageUserStreamListener {
    void onDirectMessage(DirectMessage directMessage);
}
