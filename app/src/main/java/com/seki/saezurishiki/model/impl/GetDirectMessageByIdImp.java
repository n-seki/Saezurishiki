package com.seki.saezurishiki.model.impl;

import com.seki.saezurishiki.entity.DirectMessageEntity;
import com.seki.saezurishiki.model.GetDirectMessageById;
import com.seki.saezurishiki.network.twitter.TwitterAccount;


class GetDirectMessageByIdImp implements GetDirectMessageById {

    private final TwitterAccount account;

    GetDirectMessageByIdImp(TwitterAccount account) {
        this.account = account;
    }

    @Override
    public DirectMessageEntity get(long id) {
        return this.account.getRepository().getDM(id);
    }
}
