package com.seki.saezurishiki.model.util;


import com.seki.saezurishiki.model.adapter.ModelMessage;

public interface ModelObserver {
    void update(ModelObservable observable, ModelMessage message);
}
