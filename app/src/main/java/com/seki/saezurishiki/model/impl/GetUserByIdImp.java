package com.seki.saezurishiki.model.impl;

import com.seki.saezurishiki.entity.UserEntity;
import com.seki.saezurishiki.model.GetUserById;
import com.seki.saezurishiki.network.twitter.TwitterAccount;

public class GetUserByIdImp implements GetUserById {

    private final TwitterAccount account;

    GetUserByIdImp(TwitterAccount account) {
        this.account = account;
    }

    @Override
    public UserEntity get(long id) {
        return this.account.getRepository().getUser(id);
    }
}
