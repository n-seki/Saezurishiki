package com.seki.saezurishiki.model;

public interface UserScreenModel extends ModelBase {
    void getUser(long userId);
    void getRelationship(long userId);
    void follow(long userId);
    void remove(long userId);
    void block(long userId);
    void destroyBlock(long userId);
}
