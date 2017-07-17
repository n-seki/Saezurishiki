package com.seki.saezurishiki.model.impl;

import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.entity.UserEntity;
import com.seki.saezurishiki.model.UserScreenModel;
import com.seki.saezurishiki.model.adapter.ModelActionType;
import com.seki.saezurishiki.model.adapter.ModelMessage;
import com.seki.saezurishiki.network.twitter.TwitterAccount;

import twitter4j.Relationship;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.TwitterException;
import twitter4j.User;


class UserScreenModelImp extends ModelBaseImp implements UserScreenModel {

    UserScreenModelImp() {
        super();
    }

    @Override
    public void getUser(long userId) {
        this.executor.execute(() -> {
            try {
                final User result = this.repository.getTwitter().showUser(userId);
                final UserEntity user = this.repository.map(result);
                final ModelMessage message = ModelMessage.of(ModelActionType.LOAD_USER, user);
                observable.notifyObserver(message);
            } catch (TwitterException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void getRelationship(long userId) {
        this.executor.execute(() -> {
            try {
                final Relationship result = this.repository.getTwitter().showFriendship(TwitterAccount.getLoginUserId(), userId);
                final ModelMessage message = ModelMessage.of(ModelActionType.LOAD_RELATIONSHIP, result);
                observable.notifyObserver(message);
            } catch (TwitterException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void follow(long userId) {
        this.executor.execute(() -> {
            try {
                final User result = this.repository.getTwitter().createFriendship(userId);
                final UserEntity user = this.repository.map(result);
                final ModelMessage message = ModelMessage.of(ModelActionType.COMPLETE_FOLLOW, user);
                observable.notifyObserver(message);
            } catch (TwitterException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void remove(long userId) {
        this.executor.execute(() -> {
            try {
                final User result = this.repository.getTwitter().destroyFriendship(userId);
                final UserEntity user = this.repository.map(result);
                final ModelMessage message = ModelMessage.of(ModelActionType.COMPLETE_REMOVE, user);
                observable.notifyObserver(message);
            } catch (TwitterException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void block(long userId) {
        this.executor.execute(() -> {
            try {
                final User result = this.repository.getTwitter().createBlock(userId);
                final UserEntity user = this.repository.map(result);
                final ModelMessage message = ModelMessage.of(ModelActionType.COMPLETE_BLOCK, user);
                observable.notifyObserver(message);
            } catch (TwitterException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void destroyBlock(long userId) {
        this.executor.execute(() -> {
            try {
                final User result = this.repository.getTwitter().destroyBlock(userId);
                final UserEntity user = this.repository.map(result);
                final ModelMessage message = ModelMessage.of(ModelActionType.COMPLETE_DESTROY_BLOCK, user);
                observable.notifyObserver(message);
            } catch (TwitterException e) {
                e.printStackTrace();
            }
        });
    }
}
