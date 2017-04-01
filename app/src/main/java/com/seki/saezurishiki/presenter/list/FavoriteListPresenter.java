package com.seki.saezurishiki.presenter.list;

import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.model.TweetListModel;
import com.seki.saezurishiki.model.adapter.ModelMessage;

import java.util.List;


public class FavoriteListPresenter extends TweetListPresenter {


    public FavoriteListPresenter(TweetListView view, long listOwnerId, TweetListModel listModel) {
        super(view, listOwnerId, listModel);
    }


    @SuppressWarnings("unchecked")
    @Override
    void dispatch(ModelMessage message) {
        switch (message.type) {
            case LOAD_FAVORITE_LIST:
                this.view.loadTweets((List<TweetEntity>)message.data);
                break;

            case RECEIVE_FAVORITE :
            case RECEIVE_UN_FAVORITE :
            case RECEIVE_DELETION:
                this.view.updateTweet((TweetEntity)message.data);
                break;

            default:
                //no operation
        }
    }
}
