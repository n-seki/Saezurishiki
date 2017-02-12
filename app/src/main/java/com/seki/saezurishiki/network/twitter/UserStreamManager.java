package com.seki.saezurishiki.network.twitter;

import android.content.Context;

import com.seki.saezurishiki.network.twitter.streamListener.CustomUserStreamListener;
import com.seki.saezurishiki.network.twitter.streamListener.DirectMessageUserStreamListener;
import com.seki.saezurishiki.network.twitter.streamListener.StatusUserStreamListener;
import com.seki.saezurishiki.network.twitter.streamListener.UserStreamUserListener;

import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

/**
 * TwitterStream管理クラス
 * @author seki
 */
final class UserStreamManager {

    private TwitterStream mTwitterStream;
    private final CustomUserStreamAdapter streamAdapter;
    private boolean isStartStream = false;

    UserStreamManager(TwitterAccount twitterAccount, Context context) {
        mTwitterStream = new TwitterStreamFactory(twitterAccount.conf).getInstance(twitterAccount.accessToken);
        streamAdapter = new CustomUserStreamAdapter(context, twitterAccount);
    }

    void start() {
        if ( isStartStream ) {
            return;
        }

        mTwitterStream.shutdown();

        mTwitterStream.addListener(streamAdapter);
        mTwitterStream.user();
        isStartStream = true;
    }


    void stop() {
        if ( !isStartStream ) {
            return;
        }

        mTwitterStream.removeListener(streamAdapter);
        mTwitterStream.shutdown();
        isStartStream = false;
    }


    void destroy() {
        if (mTwitterStream == null) {
            return;
        }

        mTwitterStream.removeListener(streamAdapter);
        mTwitterStream.shutdown();
        mTwitterStream = null;
        streamAdapter.clearListener();
        //streamAdapter = null;
        isStartStream = false;
    }


    synchronized void addListener(CustomUserStreamListener listener) {
        this.streamAdapter.addListener(listener);
    }


    public void addListener(StatusUserStreamListener listener) {
        this.streamAdapter.addListener(listener);
    }

    public void addListener(DirectMessageUserStreamListener listener) {
        this.streamAdapter.addListener(listener);
    }

    public void addListener(UserStreamUserListener listener) {
        this.streamAdapter.addListener(listener);
    }

    public void removeListener(StatusUserStreamListener listener) {
        this.streamAdapter.removeListener(listener);
    }

    public void removeListener(DirectMessageUserStreamListener listener) {
        this.streamAdapter.removeListener(listener);
    }

    public void removeListener(UserStreamUserListener listener) {
        this.streamAdapter.removeListener(listener);
    }

    public void removeListener(CustomUserStreamListener listener) {
        this.streamAdapter.removeListener(listener);
        this.streamAdapter.removeListener(listener);
        this.streamAdapter.removeListener(listener);
    }
}
