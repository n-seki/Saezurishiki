package com.seki.saezurishiki.entity;


import android.support.annotation.NonNull;

import java.util.Comparator;
import java.util.Date;

import twitter4j.DirectMessage;

public class DirectMessageEntity implements TwitterEntity, Comparable<DirectMessageEntity> {

    private final long id;
    public final UserEntity sender;
    public final long recipientId;
    public final Date createAt;
    public final String text;

    public final boolean isSentByLoginUser;
    public final boolean isRecipientByLoginUser;


    public DirectMessageEntity(DirectMessage message, boolean isSentByLoginUser, boolean isRecipientByLoginUser) {
        this.id = message.getId();
        this.sender = new UserEntity(message.getSender(), isSentByLoginUser);
        this.createAt = message.getCreatedAt();
        this.text = message.getText();
        this.recipientId = message.getRecipientId();

        this.isSentByLoginUser = isSentByLoginUser;
        this.isRecipientByLoginUser = isRecipientByLoginUser;
    }

    @Override
    public Type getItemType() {
        return Type.DirectMessage;
    }

    @Override
    public long getId() {
        return this.id;
    }

    @Override
    public int compareTo(@NonNull DirectMessageEntity o) {
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
