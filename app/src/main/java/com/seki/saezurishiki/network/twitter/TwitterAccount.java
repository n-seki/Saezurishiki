package com.seki.saezurishiki.network.twitter;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import com.seki.saezurishiki.entity.mapper.EntityMapper;
import com.seki.saezurishiki.file.SharedPreferenceUtil;
import com.seki.saezurishiki.network.server.TwitterServer;
import com.seki.saezurishiki.network.twitter.streamListener.CustomUserStreamListener;
import com.seki.saezurishiki.network.twitter.streamListener.DirectMessageUserStreamListener;
import com.seki.saezurishiki.network.twitter.streamListener.StatusUserStreamListener;
import com.seki.saezurishiki.network.twitter.streamListener.UserStreamUserListener;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.Configuration;

/**
 *
 */
public class TwitterAccount implements Application.ActivityLifecycleCallbacks {

    private final Context context;
    final AccessToken accessToken;
    public final Twitter twitter;
    private final UserStreamManager streamManager;
    private final long loginUserId;
    private final TwitterServer twitterRepository;
    final Configuration conf;

    public TwitterAccount(Context context) {
        this.context = context;
        this.accessToken = TwitterUtil.createLoginUserAccessToken(context);
        this.loginUserId = TwitterUtil.createLoginUserId(context);
        this.conf = TwitterUtil.createConfiguration(context);
        this.twitter = new TwitterFactory(conf).getInstance(this.accessToken);
        this.twitterRepository = new TwitterServer(new EntityMapper(this.loginUserId));
        this.streamManager = new UserStreamManager(this, context);
    }

    public long getLoginUserId() {
        return this.loginUserId;
    }

    public void logout() {
        this.streamManager.destroy();
        SharedPreferenceUtil.clearLoginUserInfo(this.context);
    }

    public TwitterServer getRepository() {
        return this.twitterRepository;
    }

    public void startUserStream() {
        this.streamManager.start();
    }

    public void stopUserStream() {
        this.streamManager.stop();
    }

    public synchronized void addStreamListener(CustomUserStreamListener listener) {
        this.streamManager.addListener(listener);
    }

    public synchronized void addStreamListener(StatusUserStreamListener listener) {
        this.streamManager.addListener(listener);
    }

    public synchronized void removeStreamListener(StatusUserStreamListener listener) {
        this.streamManager.removeListener(listener);
    }

    public synchronized void addStreamListener(DirectMessageUserStreamListener listener) {
        this.streamManager.addListener(listener);
    }

    public synchronized void addStreamListener(UserStreamUserListener listener) {
        this.streamManager.addListener(listener);
    }

    public void removeListener(StatusUserStreamListener listener) {
        this.streamManager.removeListener(listener);
    }

    public void removeListener(DirectMessageUserStreamListener listener) {
        this.streamManager.removeListener(listener);
    }

    public void removeListener(UserStreamUserListener listener) {
        this.streamManager.removeListener(listener);
    }

    public void removeListener(CustomUserStreamListener listener) {
        this.streamManager.removeListener(listener);
        this.streamManager.removeListener(listener);
        this.streamManager.removeListener(listener);
    }

    public void finish() {
        this.streamManager.destroy();
    }

//    @Override
//    public void finalize() throws Throwable {
//        streamManager.destroy();
//        super.finalize();
//    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        this.streamManager.destroy();
    }

}
