package com.seki.saezurishiki.presenter.list;

import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.entity.UserEntity;
import com.seki.saezurishiki.model.TweetListModel;
import com.seki.saezurishiki.model.adapter.ModelMessage;

import java.util.List;

public class UserTweetListPresenter extends TweetListPresenter {


    public UserTweetListPresenter(TweetListView view, UserEntity listOwner, TweetListModel listModel) {
        super(view, listOwner, listModel);
    }


    @SuppressWarnings("unchecked")
    @Override
    public void dispatch(ModelMessage message) {

        switch (message.type) {

            case LOAD_TWEET_LIST:
                this.view.loadTweets((List<TweetEntity>)message.data);
                break;

            case ERROR:
                this.view.errorProcess(message.exception);
                break;
            default:
                //no operation
        }
    }
}
