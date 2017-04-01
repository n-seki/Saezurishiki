package com.seki.saezurishiki.presenter.list;

import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.entity.UserEntity;
import com.seki.saezurishiki.model.TweetListModel;
import com.seki.saezurishiki.model.adapter.ModelMessage;

import java.util.List;


public class ReplyTimeLinePresenter extends TweetListPresenter {

    public ReplyTimeLinePresenter(TweetListView view, UserEntity listOwner, TweetListModel listModel) {
        super(view, listOwner, listModel);
    }


    @SuppressWarnings("unchecked")
    @Override
    void dispatch(ModelMessage message) {
        switch (message.type) {
            case LOAD_REPLY_LIST:
                this.view.loadTweets((List<TweetEntity>)message.data);
                break;

            case RECEIVE_TWEET:
                this.view.catchNewTweet((TweetEntity)message.data);
                break;

            case RECEIVE_FAVORITE :
            case RECEIVE_UN_FAVORITE :
            case RECEIVE_DELETION:
                this.view.updateTweet((TweetEntity)message.data);
                break;
        }
    }
}
