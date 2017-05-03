package com.seki.saezurishiki.model;

public interface UserListModel extends ModelBase {
    void request(long userId, long nextCursor);
}
