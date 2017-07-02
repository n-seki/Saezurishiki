package com.seki.saezurishiki.network.server;

import com.seki.saezurishiki.entity.DirectMessageEntity;
import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.entity.TwitterEntity;
import com.seki.saezurishiki.entity.UserEntity;
import com.seki.saezurishiki.entity.mapper.EntityMapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import twitter4j.DirectMessage;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.User;

public class TwitterServer  {

    private final StatusServer ALL_STATUS;
    private final Map<Long, DirectMessageEntity> DM;
    private final Map<Long, UserEntity> USER;
    
    private final EntityMapper mapper;
    

    public TwitterServer(EntityMapper mapper) {
        ALL_STATUS = new StatusServer(mapper);
        DM = new ConcurrentHashMap<>();
        USER = new ConcurrentHashMap<>();
        this.mapper = mapper;
    }


    public void addStatus(Status status) {
        ALL_STATUS.add(status);
    }

    public void addStatus(TwitterEntity status) {
        ALL_STATUS.add(status);
    }

    public TweetEntity getTweet(long statusId) {
        return (TweetEntity)ALL_STATUS.get(statusId);
    }

    public boolean hasStatus(long statusId) {
        return ALL_STATUS.has(statusId);
    }

    public void addDeletionNotice(StatusDeletionNotice deletionNotice) {
        ALL_STATUS.addStatusDeletionNotice(deletionNotice);
    }

    public boolean hasDeletionNotice(long statusID) {
        return ALL_STATUS.hasDeletionNotice(statusID);
    }

    public StatusDeletionNotice getDeletionNotice(long statusID) {
        return ALL_STATUS.getDeletionNotice(statusID);
    }

    public  void clear() {
        ALL_STATUS.clear();
        DM.clear();
    }


    public void addSentDM(List<DirectMessage> list) {
        for (DirectMessage message : list) {
            addSentDM(message);
        }
    }

    public DirectMessageEntity addSentDM(DirectMessage message) {
        final DirectMessageEntity dm = this.mapper.map(message);
        DM.put(dm.getId(), dm);
        return dm;
    }



    public List<Long> getSentDMId(long recipientUserId) {
        List<Long> list = new ArrayList<>();
        Collection<DirectMessageEntity> messages = DM.values();
        for (DirectMessageEntity message : messages) {
            if (message.recipientId == recipientUserId) {
                list.add(message.getId());
            }
        }

        return list;
    }


    public List<DirectMessageEntity> addDM(List<DirectMessage> list) {
        final List<DirectMessageEntity> mappedList = new ArrayList<>();
        for (DirectMessage message : list) {
            final DirectMessageEntity entity = this.mapper.map(message);
            DM.put(entity.getId(), entity);
            mappedList.add(entity);
        }

        return mappedList;
    }

    public DirectMessageEntity addDM(DirectMessage message) {
        final DirectMessageEntity dm = this.mapper.map(message);
        DM.put(dm.getId(), dm);
        return dm;
    }


    public List<Long> getDMIdByUser(long senderId) {
        List<Long> list = new ArrayList<>();
        Collection<DirectMessageEntity> messages = DM.values();
        for (DirectMessageEntity message : messages) {
            if (message.sender.getId() == senderId) {
                list.add(message.getId());
            }
        }

        return list;
    }


    public DirectMessageEntity getDM(long messageId) {
        return DM.get(messageId);
    }

    public DirectMessageEntity findDM(long messageId) {
        if (DM.containsKey(messageId)) return DM.get(messageId);
        return DM.get(messageId);
    }


    public UserEntity add(User user) {
        final UserEntity userEntity = map(user);
        USER.put(user.getId(), userEntity);
        return userEntity;
    }


    public UserEntity getUser(long userId) {
        return USER.get(userId);
    }

    public void add(ResponseList<Status> result) {
        for (Status status : result) {
            this.ALL_STATUS.add(status);
        }
    }

    public void add(List<Status> tweets) {
        for (Status status : tweets) {
            this.ALL_STATUS.add(status);
        }
    }

    public TweetEntity map(Status status) {
        return this.mapper.map(status);
    }

    public List<TweetEntity> map(List<Status> statuses) {
        final List<TweetEntity> tweets = new ArrayList<>(statuses.size());
        for (final Status status : statuses) {
            tweets.add(map(status));
        }

        return tweets;
    }

    public UserEntity map(User user) {
        final UserEntity userEntity = this.mapper.map(user);
        this.USER.put(userEntity.getId(), userEntity);
        return userEntity;
    }

    public List<UserEntity> addUsers(List<User> users) {
        final List<UserEntity> userEntities = new ArrayList<>();
        for (final User user : users) {
            final UserEntity entity = map(user);
            userEntities.add(entity);
            USER.put(entity.getId(), entity);
        }

        return userEntities;
    }
}
