package com.seki.saezurishiki.model;

import com.seki.saezurishiki.model.util.ModelObserver;

public interface ModelBase {
    void addObserver(ModelObserver observer);
    void removeObserver(ModelObserver observer);
}
