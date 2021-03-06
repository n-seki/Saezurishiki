package com.seki.saezurishiki.model.impl;

import com.seki.saezurishiki.entity.UserEntity;
import com.seki.saezurishiki.model.UserListModel;
import com.seki.saezurishiki.model.adapter.ModelActionType;
import com.seki.saezurishiki.model.adapter.ModelMessage;
import com.seki.saezurishiki.model.adapter.SupportCursorList;
import com.seki.saezurishiki.model.util.ModelObservable;
import com.seki.saezurishiki.model.util.ModelObserver;
import com.seki.saezurishiki.repository.UserRepository;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Singleton;

import twitter4j.TwitterException;

@Singleton
public class FollowerListModel implements UserListModel {

    private final Executor executor;
    private final ModelObservable observable;
    private final UserRepository mRepository;

    @Inject
    FollowerListModel(UserRepository repository) {
        this.executor = Executors.newCachedThreadPool();
        this.observable = new ModelObservable();
        mRepository = repository;
    }


    @Override
    public void request(long userId, long nextCursor) {
        executor.execute(() -> {
            try {
                final SupportCursorList<UserEntity> list = mRepository.getFollowerList(userId, nextCursor);
                final ModelMessage message = ModelMessage.of(ModelActionType.LOAD_FOLLOWERS, list);
                observable.notifyObserver(message);
            } catch (TwitterException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void addObserver(ModelObserver observer) {
        this.observable.addObserver(observer);
    }

    @Override
    public void removeObserver(ModelObserver observer) {
        this.observable.removeObserver(observer);
    }

}
