package com.seki.saezurishiki.model.impl;

import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.model.TweetEditorModel;
import com.seki.saezurishiki.model.adapter.ModelActionType;
import com.seki.saezurishiki.model.adapter.ModelMessage;
import com.seki.saezurishiki.repository.TweetRepository;

import twitter4j.StatusUpdate;
import twitter4j.TwitterException;

public class TweetEditorModelImp extends ModelBaseImp implements TweetEditorModel {

    TweetEditorModelImp() {
        super();
    }

    @Override
    public void postTweet(StatusUpdate tweet) {
        this.executor.execute(() -> {
            try {
                final TweetEntity latestTweet = TweetRepository.INSTANCE.updateTweet(tweet);
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
