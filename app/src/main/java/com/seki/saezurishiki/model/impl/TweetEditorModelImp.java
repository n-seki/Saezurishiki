package com.seki.saezurishiki.model.impl;

import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.model.TweetEditorModel;
import com.seki.saezurishiki.model.adapter.ModelActionType;
import com.seki.saezurishiki.model.adapter.ModelMessage;
import com.seki.saezurishiki.repository.TweetRepository;

import javax.inject.Inject;
import javax.inject.Singleton;

import twitter4j.StatusUpdate;
import twitter4j.TwitterException;

@Singleton
public class TweetEditorModelImp extends ModelBaseImp implements TweetEditorModel {

    private final TweetRepository mRepository;

    @Inject
    TweetEditorModelImp(TweetRepository repository) {
        super();
        mRepository = repository;
    }

    @Override
    public void postTweet(StatusUpdate tweet) {
        this.executor.execute(() -> {
            try {
                final TweetEntity latestTweet = mRepository.updateTweet(tweet);
                final ModelMessage message = ModelMessage.of(ModelActionType.COMPLETE_POST_TWEET, latestTweet);
                observable.notifyObserver(message);
            } catch (TwitterException e) {
                observable.notifyObserver(ModelMessage.error(e));
            }
        });
    }

    @Override
    public void uploadImage() {

    }
}
