package com.seki.saezurishiki.model;


import com.seki.saezurishiki.entity.DirectMessageEntity;
import com.seki.saezurishiki.model.adapter.RequestInfo;

public interface DirectMessageListModel extends ModelBase {
    void request(RequestInfo info);
    DirectMessageEntity getEntityFromCache(long id);
}
