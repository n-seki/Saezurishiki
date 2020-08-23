package com.seki.saezurishiki.model.impl;

import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.model.adapter.ModelActionType;
import com.seki.saezurishiki.model.adapter.ModelMessage;
import com.seki.saezurishiki.model.adapter.RequestInfo;
import com.seki.saezurishiki.repository.TweetRepository;
import com.seki.saezurishiki.repository.UserRepository;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import twitter4j.TwitterException;

@Singleton
class SearchTweetModel extends TweetListModelImp {

    @Inject
    SearchTweetModel(TweetRepository tweetRepository, UserRepository userRepository) {
        super(tweetRepository, userRepository);
    }

    @Override
    public void request(final RequestInfo info) {
        if (isLoading.getAndSet(true)) {
            return;
        }
        this.executor.execute(() -> {
            try {
                final List<TweetEntity> tweets = mTweetRepository.search(info.toQuery());
                final ModelMessage message = ModelMessage.of(ModelActionType.LOAD_SEARCH, tweets);
                observable.notifyObserver(message);
            } catch (TwitterException e) {
                observable.notifyObserver(ModelMessage.error(e));
            }
            isLoading.set(false);
        });
    }
}
