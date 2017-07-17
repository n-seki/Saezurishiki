package com.seki.saezurishiki.model.impl;

import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.model.TweetEditorModel;
import com.seki.saezurishiki.model.adapter.ModelActionType;
import com.seki.saezurishiki.model.adapter.ModelMessage;

import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.TwitterException;

public class TweetEditorModelImp extends ModelBaseImp implements TweetEditorModel {

    public TweetEditorModelImp() {
        super();
    }

    @Override
    public void postTweet(StatusUpdate tweet) {
        this.executor.execute(() -> {
            try {
                final Status postedStatus = this.repository.getTwitter().updateStatus(tweet);
                final TweetEntity postedTweet = this.repository.map(postedStatus);
                final ModelMessage message = ModelMessage.of(ModelActionType.COMPLETE_POST_TWEET, postedTweet);
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
