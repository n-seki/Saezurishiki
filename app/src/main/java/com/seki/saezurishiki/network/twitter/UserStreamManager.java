package com.seki.saezurishiki.network.twitter;

import com.seki.saezurishiki.network.twitter.streamListener.CustomUserStreamListener;
import com.seki.saezurishiki.network.twitter.streamListener.DirectMessageUserStreamListener;
import com.seki.saezurishiki.network.twitter.streamListener.StatusUserStreamListener;
import com.seki.saezurishiki.network.twitter.streamListener.UserStreamUserListener;
import com.seki.saezurishiki.repository.RemoteRepositoryImp;

import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

/**
 * TwitterStream管理クラス
 * @author seki
 */
public final class UserStreamManager {

    private TwitterStream mTwitterStream;
    private final CustomUserStreamAdapter streamAdapter;
    private boolean isStartStream = false;

    private static UserStreamManager instance;

    private UserStreamManager(TwitterAccount twitterAccount) {
        mTwitterStream = new TwitterStreamFactory(twitterAccount.config.configuration).getInstance(twitterAccount.config.token);
        streamAdapter = new CustomUserStreamAdapter(RemoteRepositoryImp.getInstance());
    }

    static void onCreate(TwitterAccount twitterAccount) {
        if (instance != null) {
            return;
        }

        instance = new UserStreamManager(twitterAccount);
    }

    public static UserStreamManager getInstance() {
        if (instance == null) {
            throw new NullPointerException("singleton instance is null");
        }

        return instance;
    }

    public boolean start() {
        if (isStartStream) {
            return false;
        }

        mTwitterStream.addListener(streamAdapter);
        mTwitterStream.user();
        isStartStream = true;
        return true;
    }


    public boolean stop() {
        if ( !isStartStream ) {
            return false;
        }

        mTwitterStream.removeListener(streamAdapter);
        mTwitterStream.shutdown();
        isStartStream = false;
        return true;
    }


    public boolean destroy() {
        if (mTwitterStream == null) {
            return false;
        }

        mTwitterStream.removeListener(streamAdapter);
        mTwitterStream.shutdown();
        streamAdapter.clearListener();
        isStartStream = false;
        return true;
    }

    public synchronized void addListener(CustomUserStreamListener listener) {
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
