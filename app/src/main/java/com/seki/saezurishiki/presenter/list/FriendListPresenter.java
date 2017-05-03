package com.seki.saezurishiki.presenter.list;

import com.seki.saezurishiki.entity.UserEntity;
import com.seki.saezurishiki.model.UserListModel;
import com.seki.saezurishiki.model.adapter.ModelMessage;
import com.seki.saezurishiki.model.adapter.SupportCursorList;

public class FriendListPresenter extends UserListPresenter {

    public FriendListPresenter(View view, UserListModel model, long ownerId) {
        super(view, model, ownerId);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void dispatch(ModelMessage message) {
        switch (message.type) {
            case LOAD_FRIENDS:
                final SupportCursorList<UserEntity> result = (SupportCursorList<UserEntity>)message.data;
                analyze(result);
                break;

            default:
                break;

        }
    }
}
