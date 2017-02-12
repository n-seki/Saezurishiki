package com.seki.saezurishiki.model;


import com.seki.saezurishiki.model.util.ModelObservable;
import com.seki.saezurishiki.model.util.ModelObserver;
import com.seki.saezurishiki.repository.Repository;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

abstract class ModelBaseImp implements ModelBase {

    final Repository repository;
    final ModelObservable observable;
    final Executor executor = Executors.newCachedThreadPool();

    ModelBaseImp(Repository repository) {
        this.repository = repository;
        this.observable = new ModelObservable();
    }

    public void addObserver(ModelObserver observer){
        this.observable.addObserver(observer);
    }

    public void removeObserver(ModelObserver observer){
        this.observable.removeObserver(observer);
    }
}
