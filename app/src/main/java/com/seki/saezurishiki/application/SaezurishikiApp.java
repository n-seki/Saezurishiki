package com.seki.saezurishiki.application;

import android.app.Application;

public class SaezurishikiApp extends Application {

    public static ApplicationComponent mApplicationComponent;
    
    @Override
    public void onCreate() {
        super.onCreate();
        mApplicationComponent = DaggerApplicationComponent.builder().build();
    }
}
