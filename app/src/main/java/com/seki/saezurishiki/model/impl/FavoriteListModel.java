package com.seki.saezurishiki.model.impl;

import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.model.adapter.ModelActionType;
import com.seki.saezurishiki.model.adapter.ModelMessage;
import com.seki.saezurishiki.model.adapter.RequestInfo;
import com.seki.saezurishiki.repository.TweetRepositoryKt;

import java.util.List;

import twitter4j.TwitterException;

class FavoriteListModel extends TweetListModelImp {


    FavoriteListModel() {
        super();
    }

    @Override
    public void request(final RequestInfo info) {
        this.executor.execute(() -> {
            try {
                final List<TweetEntity> tweets = TweetRepositoryKt.INSTANCE.getFavoritList(info.getUserID(), info.toPaging());
                final ModelMessage message = ModelMessage.of(ModelActionType.LOAD_FAVORITE_LIST, tweets);
                observable.notifyObserver(message);
            } catch (TwitterException e) {
                final ModelMessage error = ModelMessage.error(e);
                observable.notifyObserver(error);
            }
        });
    }
}
