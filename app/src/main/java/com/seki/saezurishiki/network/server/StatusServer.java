package com.seki.saezurishiki.network.server;
import com.seki.saezurishiki.entity.mapper.EntityMapper;
import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.entity.TwitterEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import twitter4j.Status;
import twitter4j.StatusDeletionNotice;

/**
 * Status保存クラス<br>
 * 受信した全てのStatus,および生成したLoadButtonを保持します
 * @author seki
 */
final class StatusServer {

    private final Map<Long, TwitterEntity> ALL_STATUS;
    private final Map<Long, StatusDeletionNotice> DELETE_STATUS;

    private final EntityMapper mapper;


    StatusServer(EntityMapper entityMapper) {
        ALL_STATUS = new ConcurrentHashMap<>();
        DELETE_STATUS = new HashMap<>();
        this.mapper = entityMapper;
    }


    synchronized void add(Status status) {
        if (status == null) {
            throw new IllegalStateException("Status is null!");
        }

        this.put(status);

        if (status.getQuotedStatus() != null) {
            this.put(status.getQuotedStatus());
        }

        //再帰
        if (status.isRetweet()) {
            this.add(status.getRetweetedStatus());
        }
    }


    void add(TwitterEntity entity) {
        ALL_STATUS.put(entity.getId(), entity);
    }


    private void put(Status status) {
        if (status == null) {
            throw new NullPointerException("Status is null!");
        }

        ALL_STATUS.put(status.getId(), mapper.createTweetEntity(status));
    }



    TwitterEntity get(long statusID) {
        return ALL_STATUS.get(statusID);
    }


    boolean has(long statusId) {
        return ALL_STATUS.containsKey(statusId);
    }


    void clear() {
        ALL_STATUS.clear();
        DELETE_STATUS.clear();
    }


    void addStatusDeletionNotice(StatusDeletionNotice deletionNotice) {
        DELETE_STATUS.put(deletionNotice.getStatusId(), deletionNotice);
        final TwitterEntity entity = ALL_STATUS.get(deletionNotice.getStatusId());
        if (entity != null && entity.getItemType() == TwitterEntity.Type.Tweet) {
            ((TweetEntity)entity).onDelete();
        }
    }


    boolean hasDeletionNotice(long statusID) {
        return DELETE_STATUS.containsKey(statusID);
    }

    StatusDeletionNotice getDeletionNotice(long statusID) {
        return DELETE_STATUS.get(statusID);
    }

}
