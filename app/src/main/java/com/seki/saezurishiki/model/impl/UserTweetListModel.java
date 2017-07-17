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

class UserTweetListModel extends TweetListModelImp {


    UserTweetListModel() {
        super();
    }

    @Override
    public void request(final RequestInfo info) {
        this.executor.execute(() -> {
            try {
                final ResponseList<Status> result = this.repository.getTwitter().getUserTimeline(info.getUserID(), info.toPaging());
                final List<TweetEntity> tweets = this.repository.map(result);
                this.repository.add(result);
                final ModelMessage message = ModelMessage.of(ModelActionType.LOAD_TWEET_LIST, tweets);
                observable.notifyObserver(message);
            } catch (TwitterException e) {
                final ModelMessage error = ModelMessage.error(e);
                observable.notifyObserver(error);
            }
        });
    }
}
