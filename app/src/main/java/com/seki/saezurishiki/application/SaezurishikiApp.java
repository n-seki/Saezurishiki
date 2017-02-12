package com.seki.saezurishiki.application;

import android.app.Application;

import com.seki.saezurishiki.network.twitter.TwitterAccount;

public class SaezurishikiApp extends Application {

    private TwitterAccount twitterAccount = null;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public void createTwitterAccount() {
        if (twitterAccount != null) {
            twitterAccount.finish();
            twitterAccount = null;
        }

        this.twitterAccount = new TwitterAccount(getApplicationContext());
    }

    public synchronized TwitterAccount getTwitterAccount() {
//        if (!hasTwitterAccount()) {
//            throw new IllegalStateException("TwitterAccount is null");
//        }
//        return this.twitterAccount;
        if (!hasTwitterAccount()) {
            this.createTwitterAccount();
        }

        return this.twitterAccount;
    }

    public boolean hasTwitterAccount() {
        return this.twitterAccount != null;
    }

}
