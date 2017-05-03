package com.seki.saezurishiki.presenter.list;


import android.os.Handler;
import android.os.Looper;

import com.seki.saezurishiki.entity.UserEntity;
import com.seki.saezurishiki.model.UserListModel;
import com.seki.saezurishiki.model.adapter.ModelMessage;
import com.seki.saezurishiki.model.adapter.SupportCursorList;
import com.seki.saezurishiki.model.util.ModelObservable;
import com.seki.saezurishiki.model.util.ModelObserver;

import java.util.List;

public abstract class UserListPresenter implements ModelObserver {

    final View view;
    private final long ownerId;
    final UserListModel model;

    private long nextCursor = -1;

    public interface View {
        void showUser(UserEntity user);
        void loadUsers(List<UserEntity> users);
        void hideFooterLoadButton();
        void setPresenter(UserListPresenter presenter);
    }

    UserListPresenter(View view, UserListModel model, long ownerId) {
        this.view = view;
        this.view.setPresenter(this);
        this.model = model;
        this.ownerId = ownerId;
    }

    public void onResume() {
        this.model.addObserver(this);
    }

    public void onPause() {
        this.model.removeObserver(this);
    }


    public void onClickListItem(UserEntity user) {
        this.view.showUser(user);
    }


    public void request() {
        model.request(this.ownerId, this.nextCursor);
    }

    @Override
    public void update(ModelObservable observable, ModelMessage message) {
        new Handler(Looper.getMainLooper()).post(() -> dispatch(message));
    }


    void analyze(SupportCursorList<UserEntity> result) {
        if (result.getUserId() != this.ownerId) {
            return;
        }

        final List<UserEntity> users = result.getList();
        if (users.isEmpty()) {
            this.view.hideFooterLoadButton();
            this.nextCursor = -1;
            return;
        }

        this.nextCursor = result.getNextCursor();
        this.view.loadUsers(users);
    }

    abstract void dispatch(ModelMessage modelMessage);
}
