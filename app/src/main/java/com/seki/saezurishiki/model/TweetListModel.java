package com.seki.saezurishiki.model;


import com.seki.saezurishiki.model.util.ModelObserver;

import twitter4j.Paging;

public interface TweetListModel extends ModelBase {

    void request(Paging paging);

    @Override
    void addObserver(ModelObserver observer);
    @Override
    void removeObserver(ModelObserver observer);
}
