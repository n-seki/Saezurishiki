package com.seki.saezurishiki.model.impl;

import com.seki.saezurishiki.entity.DirectMessageEntity;
import com.seki.saezurishiki.model.DirectMessageListModel;
import com.seki.saezurishiki.model.adapter.ModelActionType;
import com.seki.saezurishiki.model.adapter.ModelMessage;
import com.seki.saezurishiki.model.adapter.RequestInfo;
import com.seki.saezurishiki.model.adapter.SupportCursorList;
import com.seki.saezurishiki.network.twitter.streamListener.DirectMessageUserStreamListener;
import com.seki.saezurishiki.repository.DirectMessageRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import twitter4j.DirectMessage;
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
            List<DirectMessageEntity> receiveMessage = DirectMessageRepository.INSTANCE.getReceivedMessages(info.getUserID());
            List<DirectMessageEntity> sentMessage = DirectMessageRepository.INSTANCE.getSendMessages(info.getUserID());

            List<DirectMessageEntity> allMessage = new ArrayList<>(receiveMessage);
            allMessage.addAll(sentMessage);

            Collections.sort(allMessage, Collections.reverseOrder(DirectMessageEntity::compareTo));

            SupportCursorList<DirectMessageEntity> messageIdList = new SupportCursorList<>(allMessage, info.getUserID(), -1);
            ModelMessage message = ModelMessage.of(ModelActionType.LOAD_DIRECT_MESSAGE_CONVERSATION, messageIdList);
            observable.notifyObserver(message);
        });

    }


    private void sendMessage(RequestInfo info) {
        this.executor.execute(() -> {
            try {
                final DirectMessageEntity entity = DirectMessageRepository.INSTANCE.sendMessage(info.getUserID(), info.getMessage());
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
        final DirectMessageEntity entity = DirectMessageRepository.INSTANCE.add(directMessage);
        final ModelMessage message = ModelMessage.of(ModelActionType.RECEIVE_DIRECT_MESSAGE, entity);
        this.userStreamObservable.notifyObserver(message);
    }

    @Override
    public DirectMessageEntity getEntityFromCache(long id) {
        throw new IllegalStateException("do not call!");
    }
}
