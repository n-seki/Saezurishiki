package com.seki.saezurishiki.entity;

import androidx.annotation.NonNull;

import com.seki.saezurishiki.control.UIControlUtil;
import com.seki.saezurishiki.entity.mapper.EntityMapper;

import java.io.Serializable;
import java.util.List;

import twitter4j.HashtagEntity;
import twitter4j.MediaEntity;
import twitter4j.Status;
import twitter4j.URLEntity;
import twitter4j.UserMentionEntity;

public class TweetEntity implements TwitterEntity, Serializable, Comparable<TweetEntity> {

    private boolean isDelete;

    public final boolean isSentByLoginUser;
    public final boolean isSentToLoginUser;

    public final UserEntity user;
    public final String createdAt;
    public final String text;
    public final long inReplyToStatusId;
    public final long inReplyToUserId;
    public final String inReplyToScreenName;
    public final boolean isFavorited;
    public final int favoriteCount;
    public final boolean isRetweet;
    public final boolean isRetweeted;
    public final TweetEntity retweet;
    public final int reTweetCount;
    public final boolean isRetweetedbyLoginUser;
    public final long retweetedStatusId;
    public final boolean hasQuotedStatus;
    public final long quotedStatusId;
    public final TweetEntity quotedTweet;
    public final UserMentionEntity[] userMentionEntities;
    public final URLEntity[] urlEntities;
    public final HashtagEntity[] hashtagEntities;
    public final MediaEntity[] mediaEntities;
    public final List<Media> mediaUrlList;

    private final long id;

    public TweetEntity(Status status, boolean isLoginUserStatus, boolean isReplyToLoginUser, EntityMapper mapper) {
        this.isDelete = false;
        this.isSentByLoginUser = isLoginUserStatus;
        this.isSentToLoginUser = isReplyToLoginUser;

        this.user = mapper.map(status.getUser());
        this.createdAt = UIControlUtil.formatDate(status.getCreatedAt());
        this.text = status.getText();
        this.inReplyToStatusId = status.getInReplyToStatusId();
        this.inReplyToUserId = status.getInReplyToUserId();
        this.inReplyToScreenName = status.getInReplyToScreenName();
        this.isFavorited = status.isFavorited();
        this.favoriteCount = status.getFavoriteCount();
        this.isRetweet = status.isRetweet();
        this.retweet = isRetweet ? mapper.map(status.getRetweetedStatus()) : null;
        this.isRetweeted = status.getRetweetCount() > 0;
        this.reTweetCount = status.getRetweetCount();
        this.isRetweetedbyLoginUser = status.isRetweetedByMe();
        this.retweetedStatusId = status.getRetweetedStatus() != null ? status.getRetweetedStatus().getId() : -1;
        this.hasQuotedStatus = status.getQuotedStatus() != null;
        this.quotedStatusId = status.getQuotedStatusId();
        this.quotedTweet = hasQuotedStatus ? mapper.map(status.getQuotedStatus()) : null;
        this.userMentionEntities = status.getUserMentionEntities();
        this.urlEntities = status.getURLEntities();
        this.hashtagEntities = status.getHashtagEntities();
        this.mediaEntities = status.getMediaEntities();

        this.id = status.getId();

        this.mediaUrlList = UIControlUtil.createMediaURLList(this);

    }

    @Override
    public Type getItemType() {
        return Type.Tweet;
    }

    @Override
    public long getId() {
        return this.id;
    }

    public void onDelete() {
        this.isDelete = true;
    }

    public boolean isDeleted() {
        return this.isDelete;
    }

    @Override
    public int compareTo(@NonNull TweetEntity o) {
        final long diff = getId() - o.getId();
        if (diff == 0) {
            return 0;
        } else if (diff < 0) {
            return 1;
        } else {
            return -1;
        }
    }
}
