package com.seki.saezurishiki.model.impl;

import com.seki.saezurishiki.entity.UserEntity;
import com.seki.saezurishiki.model.GetUserById;
import com.seki.saezurishiki.network.twitter.TwitterAccount;
import com.seki.saezurishiki.repository.RemoteRepositoryImp;

public class GetUserByIdImp implements GetUserById {

    private final RemoteRepositoryImp repository;

    GetUserByIdImp() {
        this.repository = RemoteRepositoryImp.getInstance();
    }

    @Override
    public UserEntity get(long id) {
        return this.repository.getUser(id);
    }
}
