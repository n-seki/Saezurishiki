package com.seki.saezurishiki.model.impl;

import com.seki.saezurishiki.entity.DirectMessageEntity;
import com.seki.saezurishiki.model.GetDirectMessageById;
import com.seki.saezurishiki.repository.DirectMessageRepository;


class GetDirectMessageByIdImp implements GetDirectMessageById {

    GetDirectMessageByIdImp() {
    }

    @Override
    public DirectMessageEntity get(long id) {
        return DirectMessageRepository.INSTANCE.get(id);
    }
}
