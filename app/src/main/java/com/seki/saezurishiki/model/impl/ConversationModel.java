package com.seki.saezurishiki.model.impl;

import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.model.adapter.ModelActionType;
import com.seki.saezurishiki.model.adapter.ModelMessage;
import com.seki.saezurishiki.model.adapter.RequestInfo;
import com.seki.saezurishiki.repository.TweetRepository;
import com.seki.saezurishiki.repository.UserRepository;

import javax.inject.Inject;
import javax.inject.Singleton;

import twitter4j.TwitterException;

@Singleton
class ConversationModel extends TweetListModelImp {

    @Inject
    ConversationModel(TweetRepository tweetRepository, UserRepository userRepository) {
        super(tweetRepository, userRepository);
    }

    @Override
    public void request(final RequestInfo info) {
        this.executor.execute(() -> {
            try {
                final TweetEntity tweet = mTweetRepository.find(info.toTargetID());
                final ModelMessage message = ModelMessage.of(ModelActionType.LOAD_TWEET, tweet);
                observable.notifyObserver(message);
            } catch (TwitterException e) {
                final ModelMessage error = ModelMessage.error(e);
                observable.notifyObserver(error);
            }
        });
    }
}
