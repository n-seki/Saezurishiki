package com.seki.saezurishiki.model.impl;

import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.model.adapter.ModelActionType;
import com.seki.saezurishiki.model.adapter.ModelMessage;
import com.seki.saezurishiki.model.adapter.RequestInfo;
import com.seki.saezurishiki.network.twitter.TwitterAccount;

import java.util.List;

import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

class FavoriteListModel extends TweetListModelImp {


    FavoriteListModel(TwitterAccount twitterAccount) {
        super(twitterAccount);
    }

    @Override
    public void request(final RequestInfo info) {
        final Twitter twitter = this.twitterAccount.twitter;
        this.executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final ResponseList<Status> result = twitter.getFavorites(info.getUserID(), info.toPaging());
                    final List<TweetEntity> tweets = twitterAccount.getRepository().map(result);
                    twitterAccount.getRepository().add(result);
                    final ModelMessage message = ModelMessage.of(ModelActionType.LOAD_FAVORITE_LIST, tweets);
                    observable.notifyObserver(message);
                } catch (TwitterException e) {
                    final ModelMessage error = ModelMessage.error(e);
                    observable.notifyObserver(error);
                }
            }
        });
    }
}
