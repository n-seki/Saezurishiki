package com.seki.saezurishiki.entity;

public interface TwitterEntity {
    Type getItemType();
    long getId();


    enum Type {
        Tweet,
        DirectMessage,
        User,
        LoadButton;
    };
}
