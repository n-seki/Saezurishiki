package com.seki.saezurishiki.model.util;

import com.seki.saezurishiki.model.adapter.ModelMessage;

import java.util.ArrayList;
import java.util.List;

public class ModelObservable {

    private List<ModelObserver> observers;

    public ModelObservable() {
        this.observers = new ArrayList<>();
    }

    public void addObserver(ModelObserver observer) {
        if (!this.observers.contains(observer))
            this.observers.add(observer);
    }

    public void removeObserver(ModelObserver observer) {
        this.observers.remove(observer);
    }

    public void removeAllObserver() {
        this.observers.clear();
    }

    public void notifyObserver(ModelMessage message) {
        for (ModelObserver observer : this.observers) {
            observer.update(this, message);
        }
    }
}
