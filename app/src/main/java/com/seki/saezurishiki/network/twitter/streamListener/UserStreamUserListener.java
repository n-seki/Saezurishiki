package com.seki.saezurishiki.network.twitter.streamListener;

import twitter4j.User;


public interface UserStreamUserListener {
    void onFollow(User source, User followedUser);

    void onRemove(User source, User removedUser);

    void onBlock(User source, User blockedUser);

    void onUnblock(User source, User unblockedUser);
}
