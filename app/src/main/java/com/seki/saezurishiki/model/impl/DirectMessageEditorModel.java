package com.seki.saezurishiki.model.impl;

import com.seki.saezurishiki.entity.DirectMessageEntity;
import com.seki.saezurishiki.model.DirectMessageListModel;
import com.seki.saezurishiki.model.adapter.ModelActionType;
import com.seki.saezurishiki.model.adapter.ModelMessage;
import com.seki.saezurishiki.model.adapter.RequestInfo;
import com.seki.saezurishiki.network.twitter.TwitterAccount;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DirectMessageEditorModel extends ModelBaseImp implements DirectMessageListModel {

    DirectMessageEditorModel(TwitterAccount twitterAccount) {
        super(twitterAccount);
    }

    @Override
    public void request(RequestInfo info) {
        this.executor.execute(() -> {
            List<Long> receiveMessage = this.twitterAccount.getRepository().getDMIdByUser(info.getUserID());
            List<Long> sentMessage = this.twitterAccount.getRepository().getSentDMId(info.getUserID());

            List<Long> allMessage = new ArrayList<>(receiveMessage);
            allMessage.addAll(sentMessage);

            Collections.sort(allMessage);

            ModelMessage message = ModelMessage.of(ModelActionType.LOAD_DIRECT_MESSAGE_CONVERSATION, allMessage);
            observable.notifyObserver(message);
        });

    }

    @Override
    public DirectMessageEntity getEntityFromCache(long id) {
        //TODO
        throw new IllegalStateException("do not call!");
    }
}
