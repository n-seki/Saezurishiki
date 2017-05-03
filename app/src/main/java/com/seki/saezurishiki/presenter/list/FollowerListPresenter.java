package com.seki.saezurishiki.presenter.list;

import com.seki.saezurishiki.entity.UserEntity;
import com.seki.saezurishiki.model.UserListModel;
import com.seki.saezurishiki.model.adapter.ModelMessage;
import com.seki.saezurishiki.model.adapter.SupportCursorList;

public class FollowerListPresenter extends UserListPresenter {

    public FollowerListPresenter(View view, UserListModel model, long ownerId) {
        super(view, model, ownerId);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void dispatch(ModelMessage message) {
        switch (message.type) {
            case LOAD_FOLLOWERS:
                final SupportCursorList<UserEntity> result = (SupportCursorList<UserEntity>)message.data;
                analyze(result);
                break;

            default:
                break;

        }
    }
}
