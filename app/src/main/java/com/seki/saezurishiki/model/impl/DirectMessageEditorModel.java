package com.seki.saezurishiki.model.impl;

import com.seki.saezurishiki.entity.DirectMessageEntity;
import com.seki.saezurishiki.model.DirectMessageListModel;
import com.seki.saezurishiki.model.adapter.ModelActionType;
import com.seki.saezurishiki.model.adapter.ModelMessage;
import com.seki.saezurishiki.model.adapter.RequestInfo;
import com.seki.saezurishiki.model.adapter.SupportCursorList;
import com.seki.saezurishiki.network.twitter.TwitterAccount;
import com.seki.saezurishiki.network.twitter.streamListener.DirectMessageUserStreamListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import twitter4j.DirectMessage;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class DirectMessageEditorModel extends ModelBaseImp implements DirectMessageListModel, DirectMessageUserStreamListener {

    DirectMessageEditorModel() {
        super();
        this.streamManager.addListener(this);
    }

    @Override
    public void request(RequestInfo info) {

        if (info.getType() == ModelActionType.SEND) {
            this.sendMessage(info);
            return;
        }

        this.executor.execute(() -> {
            List<Long> receiveMessage = this.repository.getDMIdByUser(info.getUserID());
            List<Long> sentMessage = this.repository.getSentDMId(info.getUserID());

            List<Long> allMessage = new ArrayList<>(receiveMessage);
            allMessage.addAll(sentMessage);

            Collections.sort(allMessage);

            SupportCursorList<Long> messageIdList = new SupportCursorList<>(allMessage, info.getUserID(), -1);
            ModelMessage message = ModelMessage.of(ModelActionType.LOAD_DIRECT_MESSAGE_CONVERSATION, messageIdList);
            observable.notifyObserver(message);
        });

    }


    private void sendMessage(RequestInfo info) {
        this.executor.execute(() -> {
            try {
                final DirectMessage dm = this.repository.getTwitter().sendDirectMessage(info.getUserID(), info.getMessage());
                final DirectMessageEntity entity = this.repository.addSentDM(dm);
                ModelMessage message = ModelMessage.of(ModelActionType.COMPLETE_SEND_MESSAGE, entity);
                observable.notifyObserver(message);
            } catch (TwitterException e) {
                ModelMessage errorMessage = ModelMessage.error(e);
                observable.notifyObserver(errorMessage);
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
        throw new IllegalStateException("do not call!");
    }
}
