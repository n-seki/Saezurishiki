package com.seki.saezurishiki.model.impl;

import com.seki.saezurishiki.entity.DirectMessageEntity;
import com.seki.saezurishiki.model.DirectMessageListModel;
import com.seki.saezurishiki.model.adapter.ModelActionType;
import com.seki.saezurishiki.model.adapter.ModelMessage;
import com.seki.saezurishiki.model.adapter.RequestInfo;
import com.seki.saezurishiki.network.twitter.TwitterAccount;
import com.seki.saezurishiki.network.twitter.streamListener.DirectMessageUserStreamListener;

import java.util.List;

import twitter4j.DirectMessage;
import twitter4j.TwitterException;


class DirectMessageListModelImp extends ModelBaseImp implements DirectMessageListModel, DirectMessageUserStreamListener {

    DirectMessageListModelImp() {
        super();
        this.streamManager.addListener(this);
    }

    @Override
    public void request(RequestInfo info) {
        this.executor.execute(() -> {
            try {
                final List<DirectMessage> sendDM = this.repository.getTwitter().getSentDirectMessages(info.toPaging());
                this.repository.addSentDM(sendDM);

                final List<DirectMessage> result = this.repository.getTwitter().getDirectMessages(info.toPaging());
                final List<DirectMessageEntity> directMessageList = this.repository.addDM(result);
                final ModelMessage message = ModelMessage.of(ModelActionType.LOAD_DIRECT_MESSAGE, directMessageList);
                observable.notifyObserver(message);
            } catch (TwitterException e) {
                e.printStackTrace();
            }
        });

    }

    @Override
    public void onDirectMessage(DirectMessage directMessage) {
        final DirectMessageEntity entity = this.repository.addDM(directMessage);
        final ModelMessage message = ModelMessage.of(ModelActionType.RECEIVE_DIRECT_MESSAGE, entity);
        this.userStreamObservable.notifyObserver(message);
    }

    @Override
    public DirectMessageEntity getEntityFromCache(long id) {
        return this.repository.getDM(id);
    }
}
