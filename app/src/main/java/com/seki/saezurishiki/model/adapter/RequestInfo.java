package com.seki.saezurishiki.model.adapter;

import twitter4j.Paging;
import twitter4j.Query;

public class RequestInfo {

    private long userID = -1;
    private long maxID = -1;
    private long sinceID = -1;
    private long targetID = -1;
    private String query = null;
    private int count = -1;
    private String message = null;
    private ModelActionType actionType = null;

    public RequestInfo() {}

    public RequestInfo userID(long userID) {
        this.userID = userID;
        return this;
    }

    public RequestInfo maxID(long maxID) {
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

    public RequestInfo message(String message) {
        this.message = message;
        return this;
    }

    public RequestInfo actionType(ModelActionType type) {
        this.actionType = type;
        return this;
    }

    public ModelActionType getType() {
        return this.actionType;
    }

    public String getMessage() {
        return this.message;
    }

    public Paging toPaging() {
        final Paging paging = new Paging();

        if (this.maxID > 0) paging.setMaxId(this.maxID);
        if (this.sinceID > 0) paging.setSinceId(this.sinceID);
        if (this.count > 0) paging.setCount(this.count);

        return paging;
    }

    public Query toQuery() {
        final Query query = new Query();

        if (this.query == null) {
            throw new NullPointerException("Query is null");
        }

        query.setQuery(this.query);
        if (this.maxID > 0) query.setMaxId(this.maxID);
        if (this.sinceID > 0) query.setSinceId(this.sinceID);
        if (this.count > 0) query.setCount(this.count);

        return query;
    }



    public long toTargetID() {
        return this.targetID;
    }

    public long getUserID() {
        return this.userID;
    }
}
