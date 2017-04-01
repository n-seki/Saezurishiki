package com.seki.saezurishiki.presenter.list;

import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.entity.UserEntity;
import com.seki.saezurishiki.model.TweetListModel;
import com.seki.saezurishiki.model.adapter.ModelMessage;

import java.util.List;


public class FavoriteListPresenter extends TweetListPresenter {


    public FavoriteListPresenter(TweetListView view, UserEntity listOwner, TweetListModel listModel) {
        super(view, listOwner, listModel);
    }


    @SuppressWarnings("unchecked")
    @Override
    void dispatch(ModelMessage message) {
        switch (message.type) {
            case LOAD_FAVORITE_LIST:
                this.view.loadTweets((List<TweetEntity>)message.data);
                break;

            default:
                //no operation
        }
    }
}
