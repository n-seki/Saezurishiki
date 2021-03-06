package com.seki.saezurishiki.model.impl;

import com.seki.saezurishiki.entity.UserEntity;
import com.seki.saezurishiki.model.GetUserById;
import com.seki.saezurishiki.repository.UserRepository;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class GetUserByIdImp implements GetUserById {

    private final UserRepository mRepository;

    @Inject
    GetUserByIdImp(UserRepository repository) {
        mRepository = repository;
    }

    @Override
    public UserEntity get(long id) {
        return mRepository.getUser(id);
    }
}
