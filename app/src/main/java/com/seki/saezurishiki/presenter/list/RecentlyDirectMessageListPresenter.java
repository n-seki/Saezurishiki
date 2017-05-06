package com.seki.saezurishiki.presenter.list;


import android.os.Handler;
import android.os.Looper;

import com.seki.saezurishiki.entity.DirectMessageEntity;
import com.seki.saezurishiki.model.DirectMessageListModel;
import com.seki.saezurishiki.model.adapter.ModelMessage;
import com.seki.saezurishiki.model.adapter.RequestInfo;
import com.seki.saezurishiki.model.util.ModelObservable;
import com.seki.saezurishiki.model.util.ModelObserver;
import com.seki.saezurishiki.view.adapter.ListElement;

import java.util.List;

public class RecentlyDirectMessageListPresenter implements ModelObserver {

    private final DirectMessageListModel model;
    private final View view;

    public interface View {
        void loadMessages(List<DirectMessageEntity> messages);
        void updateList(DirectMessageEntity message);
        void updateList();
        void setSwipeRefreshState(boolean state);
        void openDirectMessageEditor(long messageId);
        void setPresenter(RecentlyDirectMessageListPresenter presenter);
    }

    public RecentlyDirectMessageListPresenter(View view, DirectMessageListModel model) {
        this.view = view;
        this.view.setPresenter(this);
        this.model = model;
    }

    public void onResume() {
        this.model.addObserver(this);
    }

    public void onPause() {
        this.view.setSwipeRefreshState(false);
        this.model.removeObserver(this);
    }

    public void request(RequestInfo info) {
        this.model.request(info);
    }

    public void onSwipeRefresh(RequestInfo info) {
        this.view.setSwipeRefreshState(true);
        this.request(info);
    }

    public void onItemClick(ListElement item) {
        if (!item.isSeen()) {
            item.see();
            this.view.updateList();
        }
        this.view.openDirectMessageEditor(item.id);
    }

    private void onLoadDirectMessage(List<DirectMessageEntity> messages) {
        this.view.setSwipeRefreshState(false);
        this.view.loadMessages(messages);
    }

    private void onDirectMessage(DirectMessageEntity message) {
        if (message.isSentByLoginUser) {
            return;
        }

        this.view.updateList(message);
    }

    @Override
    public void update(ModelObservable observable, ModelMessage message) {
        new Handler(Looper.getMainLooper()).post(() -> dispatch(message));
    }

    @SuppressWarnings("unchecked")
    private void dispatch(ModelMessage message) {
        switch (message.type) {
            case LOAD_DIRECT_MESSAGE:
                this.onLoadDirectMessage((List<DirectMessageEntity>)message.data);
                break;

            case RECEIVE_DIRECT_MESSAGE:
                this.onDirectMessage((DirectMessageEntity)message.data);
                break;

            default:
                break;
        }
    }
}
