package com.seki.saezurishiki.presenter.editor;

import com.seki.saezurishiki.entity.DirectMessageEntity;
import com.seki.saezurishiki.model.adapter.ModelMessage;
import com.seki.saezurishiki.model.adapter.RequestInfo;
import com.seki.saezurishiki.model.adapter.SupportCursorList;
import com.seki.saezurishiki.model.impl.DirectMessageEditorModel;
import com.seki.saezurishiki.model.util.ModelObservable;
import com.seki.saezurishiki.model.util.ModelObserver;

import java.util.List;

public class DirectMessageEditorPresenter implements ModelObserver {

    final private View view;
    final private DirectMessageEditorModel model;
    final private long opponentUserId;

    public interface View {
        void setPresenter(DirectMessageEditorPresenter presenter);
        void catchNewMessage(DirectMessageEntity message);
        void loadMessages(List<Long> messageIds);
        void showNoMessage();
        void showInputMessageEmpty();
        void onSendMessageFinish();
    }

    DirectMessageEditorPresenter(View view, DirectMessageEditorModel model, long opponentUserId) {
        this.view = view;
        this.model = model;
        this.opponentUserId = opponentUserId;
    }

    public void onResume() {
        this.model.addObserver(this);
    }

    public void onPause() {
        this.model.removeObserver(this);
    }

    public void load(RequestInfo info) {
        this.model.request(info);
    }

    public void onClickSendButton(String message) {
        if (message == null || message.isEmpty()) {
            this.view.showInputMessageEmpty();
            return;
        }

        final RequestInfo info = new RequestInfo().userID(this.opponentUserId).message(message);
        this.model.request(info);

        this.view.onSendMessageFinish();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void update(ModelObservable observable, ModelMessage message) {

        switch (message.type) {
            case RECEIVE_DIRECT_MESSAGE:
                final DirectMessageEntity entity = (DirectMessageEntity)message.data;
                if (entity.sender.getId() == this.opponentUserId || entity.isSentByLoginUser && entity.recipientId == this.opponentUserId) {
                    this.view.catchNewMessage(entity);
                }
                break;

            case LOAD_DIRECT_MESSAGE_CONVERSATION:
                final SupportCursorList<Long> messageIdList = (SupportCursorList<Long>)message.data;
                if (messageIdList.getUserId() == this.opponentUserId) {
                    if (messageIdList.getList().isEmpty()) {
                        this.view.showNoMessage();
                    } else {
                        this.view.loadMessages(messageIdList.getList());
                    }
                }
                break;
        }

    }
}
