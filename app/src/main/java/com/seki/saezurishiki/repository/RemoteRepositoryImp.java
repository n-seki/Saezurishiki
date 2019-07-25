package com.seki.saezurishiki.repository;

import com.seki.saezurishiki.entity.UserEntity;
import com.seki.saezurishiki.entity.mapper.EntityMapper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import twitter4j.Twitter;
import twitter4j.User;

public class RemoteRepositoryImp implements Repository {

    private final Map<Long, UserEntity> USER;
    
    private final EntityMapper mapper;

    private final Twitter twitter;

    private static RemoteRepositoryImp instance;

    private RemoteRepositoryImp(Twitter twitter, EntityMapper mapper) {
        USER = new ConcurrentHashMap<>();
        this.mapper = mapper;
        this.twitter = twitter;
    }

    public static void onCreate(Twitter twitter, EntityMapper mapper) {
        instance = new RemoteRepositoryImp(twitter, mapper);
    }

    public static RemoteRepositoryImp getInstance() {
        return instance;
    }

    public Twitter getTwitter() {
        return this.twitter;
    }

    public void clear() {
        USER.clear();
        instance = null;
    }

    public UserEntity add(User user) {
        final UserEntity userEntity = map(user);
        USER.put(user.getId(), userEntity);
        return userEntity;
    }

    private UserEntity map(User user) {
        final UserEntity userEntity = this.mapper.map(user);
        this.USER.put(userEntity.getId(), userEntity);
        return userEntity;
    }
}
