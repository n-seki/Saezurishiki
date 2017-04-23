package com.seki.saezurishiki.model.adapter;

import twitter4j.Paging;
import twitter4j.Query;

public class RequestInfo {

    long userID = -1;
    long maxID = -1;
    long sinceID = -1;
    long targetID = -1;
    String query = null;
    int count = -1;

    public RequestInfo() {}

    public RequestInfo userID(long userID) {
        this.userID = userID;
        return this;
    }

    public RequestInfo mexID(long maxID) {
        this.maxID = maxID;
        return this;
    }

    public RequestInfo sinceID(long sinceID) {
        this.sinceID = sinceID;
        return this;
    }

    public RequestInfo targetID(long targetID) {
        this.targetID = targetID;
        return this;
    }

    public RequestInfo query(String query) {
        this.query = query;
        return this;
    }

    public RequestInfo count(int count) {
        this.count = count;
        return this;
    }

    protected Paging toPaging() {
        return new Paging().maxId(this.maxID).sinceId(this.sinceID).count(this.count);
    }

    protected Query toQuery() {
        return new Query().maxId(this.maxID).sinceId(this.sinceID).count(this.count).query(this.query);
    }
}
