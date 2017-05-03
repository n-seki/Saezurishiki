package com.seki.saezurishiki.model;


import com.seki.saezurishiki.entity.UserEntity;

public interface GetUserById {
    UserEntity get(long id);
}
