package com.seki.saezurishiki.model.impl;

import com.seki.saezurishiki.entity.UserEntity;
import com.seki.saezurishiki.model.UserListModel;
import com.seki.saezurishiki.model.adapter.ModelActionType;
import com.seki.saezurishiki.model.adapter.ModelMessage;
import com.seki.saezurishiki.model.adapter.SupportCursorList;
import com.seki.saezurishiki.model.util.ModelObservable;
import com.seki.saezurishiki.model.util.ModelObserver;
import com.seki.saezurishiki.network.twitter.TwitterAccount;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import twitter4j.PagableResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;


class FriendListModel implements UserListModel {

    private final TwitterAccount account;
    private final Executor executor;
    private final ModelObservable observable;

    FriendListModel(TwitterAccount account) {
        this.account = account;
        this.executor = Executors.newCachedThreadPool();
        this.observable = new ModelObservable();
    }


    @Override
    public void request(long userId, long nextCursor) {
        final Twitter twitter = this.account.twitter;
        executor.execute(() -> {
            try {
                PagableResponseList<User> result = twitter.getFriendsList(userId, nextCursor);
                final List<UserEntity> users = account.getRepository().addUsers(result);
                final SupportCursorList<UserEntity> list = new SupportCursorList<>(users, userId, result.getNextCursor());
                final ModelMessage message = ModelMessage.of(ModelActionType.LOAD_FRIENDS, list);
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