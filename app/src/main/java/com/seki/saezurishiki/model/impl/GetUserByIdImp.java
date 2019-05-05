package com.seki.saezurishiki.model.impl;

import com.seki.saezurishiki.entity.UserEntity;
import com.seki.saezurishiki.model.GetUserById;
import com.seki.saezurishiki.repository.UserRepository;

import javax.inject.Singleton;

@Singleton
public class GetUserByIdImp implements GetUserById {

    GetUserByIdImp() {}

    @Override
    public UserEntity get(long id) {
        return UserRepository.INSTANCE.getUser(id);
    }
}
