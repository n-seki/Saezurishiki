package com.seki.saezurishiki.application;

import android.app.Application;

import com.seki.saezurishiki.network.twitter.TwitterAccount;

public class SaezurishikiApp extends Application {
    
    @Override
    public void onCreate() {
        super.onCreate();
        TwitterAccount.onCreate(this);
    }
}
