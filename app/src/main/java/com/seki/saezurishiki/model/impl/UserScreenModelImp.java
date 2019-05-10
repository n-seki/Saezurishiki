package com.seki.saezurishiki.model.impl;

import com.seki.saezurishiki.entity.UserEntity;
import com.seki.saezurishiki.model.UserScreenModel;
import com.seki.saezurishiki.model.adapter.ModelActionType;
import com.seki.saezurishiki.model.adapter.ModelMessage;
import com.seki.saezurishiki.network.twitter.TwitterAccount;
import com.seki.saezurishiki.repository.UserRepository;

import javax.inject.Inject;
import javax.inject.Singleton;

import twitter4j.Relationship;
import twitter4j.TwitterException;

@Singleton
class UserScreenModelImp extends ModelBaseImp implements UserScreenModel {

    @Inject
    UserScreenModelImp() {
        super();
    }

    @Override
    public void getUser(long userId) {
        this.executor.execute(() -> {
            try {
                final UserEntity user = UserRepository.INSTANCE.find(userId);
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
                final Relationship result = UserRepository.INSTANCE.showFriendship(TwitterAccount.getLoginUserId(), userId);
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
                final UserEntity user = UserRepository.INSTANCE.createFriendship(userId);
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
                final UserEntity user = UserRepository.INSTANCE.destroyFriendship(userId);
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
                final UserEntity user = UserRepository.INSTANCE.createBlock(userId);
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
                final UserEntity user = UserRepository.INSTANCE.destroyBlock(userId);
                final ModelMessage message = ModelMessage.of(ModelActionType.COMPLETE_DESTROY_BLOCK, user);
                observable.notifyObserver(message);
            } catch (TwitterException e) {
                e.printStackTrace();
            }
        });
    }
}
