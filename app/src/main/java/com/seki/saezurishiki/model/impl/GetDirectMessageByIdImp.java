package com.seki.saezurishiki.model.impl;

import com.seki.saezurishiki.entity.DirectMessageEntity;
import com.seki.saezurishiki.model.GetDirectMessageById;
import com.seki.saezurishiki.network.twitter.TwitterAccount;
import com.seki.saezurishiki.repository.RemoteRepositoryImp;


class GetDirectMessageByIdImp implements GetDirectMessageById {

    private final RemoteRepositoryImp repository;

    GetDirectMessageByIdImp() {
        this.repository = RemoteRepositoryImp.getInstance();
    }

    @Override
    public DirectMessageEntity get(long id) {
        return this.repository.getDM(id);
    }
}
