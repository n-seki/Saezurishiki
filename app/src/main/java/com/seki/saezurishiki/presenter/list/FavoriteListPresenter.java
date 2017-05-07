package com.seki.saezurishiki.presenter.list;

import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.model.TweetListModel;
import com.seki.saezurishiki.model.adapter.ModelMessage;

import java.util.List;

import twitter4j.StatusDeletionNotice;


public class FavoriteListPresenter extends TweetListPresenter {


    public FavoriteListPresenter(TweetListView view, long listOwnerId, TweetListModel listModel) {
        super(view, listOwnerId, listModel);
    }


    @SuppressWarnings("unchecked")
    @Override
    void dispatch(ModelMessage message) {
        switch (message.type) {
            case LOAD_FAVORITE_LIST:
                final List<TweetEntity> result = ((List<TweetEntity>)message.data);
                this.view.loadTweets(result);
                if (result.isEmpty()) {
                    this.view.hideFooterLoadButton();
                }
                break;

            case COMPLETE_FAVORITE:
            case COMPLETE_UN_FAVORITE:
                this.view.updateTweet((TweetEntity)message.data);
                break;

            case RECEIVE_FAVORITE :
            case RECEIVE_UN_FAVORITE :
                if (message.source.isLoginUser) {
                    break;
                }
                this.view.updateTweet((TweetEntity)message.data);
                break;

            case RECEIVE_DELETION:
                this.view.deletionTweet(((StatusDeletionNotice)message.data).getStatusId());
                break;

            default:
                //no operation
        }
    }
}
