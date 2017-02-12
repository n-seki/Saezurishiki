package com.seki.saezurishiki.entity;

/**
 * TwitterEntity
 * Twitterからロードした情報を管理するためのインターフェイス
 */
public interface TwitterEntity {
    Type getItemType();
    long getId();
    boolean isSeenByUser();
    void userSee();


    enum Type {
        Tweet,
        DirectMessage,
        User,
        LoadButton;
    };
}
