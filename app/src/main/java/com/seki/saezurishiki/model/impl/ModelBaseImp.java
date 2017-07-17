package com.seki.saezurishiki.model.impl;


import com.seki.saezurishiki.model.ModelBase;
import com.seki.saezurishiki.model.util.ModelObservable;
import com.seki.saezurishiki.model.util.ModelObserver;
import com.seki.saezurishiki.network.twitter.TwitterAccount;
import com.seki.saezurishiki.network.twitter.UserStreamManager;
import com.seki.saezurishiki.repository.RemoteRepositoryImp;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

abstract class ModelBaseImp implements ModelBase {

    final RemoteRepositoryImp repository;
    final UserStreamManager streamManager;
    final static ModelObservable observable;
    final ModelObservable userStreamObservable;
    final Executor executor = Executors.newCachedThreadPool();

    static {
        observable = new ModelObservable();
    }

    ModelBaseImp() {
        this.repository = RemoteRepositoryImp.getInstance();
        this.streamManager = UserStreamManager.getInstance();
        this.userStreamObservable = new ModelObservable();
    }

    public void addObserver(ModelObserver observer){
        observable.addObserver(observer);
        this.userStreamObservable.addObserver(observer);
    }

    public void removeObserver(ModelObserver observer){
        observable.removeObserver(observer);
        this.userStreamObservable.removeObserver(observer);
    }
}
