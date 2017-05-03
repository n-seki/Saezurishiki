package com.seki.saezurishiki.model.adapter;

import java.util.List;

public class SupportCursorList<T> {

    private final List<T> list;
    private final long userId;
    private final long nextCursor;

    public SupportCursorList(List<T> list, long userId, long nextCursor) {
        this.list = list;
        this.userId = userId;
        this.nextCursor = nextCursor;
    }

    public List<T> getList() {
        return this.list;
    }

    public long getUserId() {
        return userId;
    }
    public long getNextCursor() {
        return this.nextCursor;
    }
}
