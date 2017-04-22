package com.seki.saezurishiki.model.impl;

import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.model.adapter.ModelActionType;
import com.seki.saezurishiki.model.adapter.ModelMessage;
import com.seki.saezurishiki.network.twitter.TwitterAccount;

import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

class ConversationModel extends TweetListModelImp {

    ConversationModel(TwitterAccount twitterAccount) {
        super(twitterAccount);
    }

    @Override
    public void request(long userId, final Paging paging) {
        final Twitter twitter = this.twitterAccount.twitter;
        this.executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final Status status = twitter.showStatus(paging.getMaxId());
                    final TweetEntity tweet = twitterAccount.getRepository().map(status);
                    twitterAccount.getRepository().addStatus(tweet);
                    final ModelMessage message = ModelMessage.of(ModelActionType.LOAD_TWEET, tweet);
                    observable.notifyObserver(message);
                } catch (TwitterException e) {
                    final ModelMessage error = ModelMessage.error(e);
                    observable.notifyObserver(error);
                }
            }
        });
    }
}
