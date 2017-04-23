package com.seki.saezurishiki.model.impl;

import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.model.adapter.ModelActionType;
import com.seki.saezurishiki.model.adapter.ModelMessage;
import com.seki.saezurishiki.network.twitter.TwitterAccount;

import java.util.List;

import twitter4j.Paging;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

class SearchTweetModel extends TweetListModelImp {

    SearchTweetModel(TwitterAccount twitterAccount) {
        super(twitterAccount);
    }

    @Override
    public void request(long userId, final Paging paging) {
        final Twitter twitter = this.twitterAccount.twitter;
        this.executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final QueryResult result = twitter.search(new Query().maxId(paging.getMaxId()));
                    final List<TweetEntity> tweets = twitterAccount.getRepository().map(result.getTweets());
                    final ModelMessage message = ModelMessage.of(ModelActionType.LOAD_SEARCH, tweets);
                    observable.notifyObserver(message);
                } catch (TwitterException e) {
                    observable.notifyObserver(ModelMessage.error(e));
                }
            }
        });
    }
}
