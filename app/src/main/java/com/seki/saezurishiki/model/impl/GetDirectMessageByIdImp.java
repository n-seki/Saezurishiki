package com.seki.saezurishiki.model.impl;

import com.seki.saezurishiki.entity.DirectMessageEntity;
import com.seki.saezurishiki.model.GetDirectMessageById;
import com.seki.saezurishiki.network.twitter.TwitterAccount;
import com.seki.saezurishiki.repository.DirectMessageRepository;
import com.seki.saezurishiki.repository.RemoteRepositoryImp;


class GetDirectMessageByIdImp implements GetDirectMessageById {

    GetDirectMessageByIdImp() {
    }

    @Override
    public DirectMessageEntity get(long id) {
        return DirectMessageRepository.INSTANCE.get(id);
    }
}
