package com.seki.saezurishiki.model;

import com.seki.saezurishiki.entity.DirectMessageEntity;

public interface GetDirectMessageById {
    DirectMessageEntity get(long id);
}
