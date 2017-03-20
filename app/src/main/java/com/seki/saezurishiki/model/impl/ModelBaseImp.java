package com.seki.saezurishiki.model.impl;


import com.seki.saezurishiki.model.ModelBase;
import com.seki.saezurishiki.model.util.ModelObservable;
import com.seki.saezurishiki.model.util.ModelObserver;
import com.seki.saezurishiki.network.twitter.TwitterAccount;
import com.seki.saezurishiki.repository.Repository;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

abstract class ModelBaseImp implements ModelBase {

    final TwitterAccount twitterAccount;
    final ModelObservable observable;
    final Executor executor = Executors.newCachedThreadPool();

    ModelBaseImp(TwitterAccount twitterAccount) {
        this.twitterAccount = twitterAccount;
        this.observable = new ModelObservable();
    }

    public void addObserver(ModelObserver observer){
        this.observable.addObserver(observer);
    }

    public void removeObserver(ModelObserver observer){
        this.observable.removeObserver(observer);
    }
}
