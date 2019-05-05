package com.seki.saezurishiki.model.impl;

import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.model.adapter.ModelActionType;
import com.seki.saezurishiki.model.adapter.ModelMessage;
import com.seki.saezurishiki.model.adapter.RequestInfo;
import com.seki.saezurishiki.repository.TweetRepository;

import java.util.List;

import javax.inject.Singleton;

import twitter4j.TwitterException;

@Singleton
class ReplyTweetListModel extends TweetListModelImp {


    ReplyTweetListModel() {
        super();
    }

    @Override
    public void request(final RequestInfo info) {
        this.executor.execute(() -> {
            try {
                final List<TweetEntity> tweets = TweetRepository.INSTANCE.getReplyTweetList(info.toPaging());
                final ModelMessage message = ModelMessage.of(ModelActionType.LOAD_REPLY_LIST, tweets);
                observable.notifyObserver(message);
            } catch (TwitterException e) {
                final ModelMessage error = ModelMessage.error(e);
                observable.notifyObserver(error);
            }
        });
    }
}
