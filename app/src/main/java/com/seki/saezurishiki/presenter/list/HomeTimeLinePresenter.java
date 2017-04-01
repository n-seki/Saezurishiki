package com.seki.saezurishiki.presenter.list;

import android.util.Log;

import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.entity.UserEntity;
import com.seki.saezurishiki.model.TweetListModel;
import com.seki.saezurishiki.model.adapter.ModelMessage;

import java.util.List;


public class HomeTimeLinePresenter extends TweetListPresenter {

    public HomeTimeLinePresenter(TweetListView view, UserEntity listOwner, TweetListModel listModel) {
        super(view, listOwner, listModel);
    }


    @SuppressWarnings("unchecked")
    @Override
    void dispatch(ModelMessage message) {
        switch (message.type) {
            case LOAD_HOME_LIST:
                this.view.loadTweets((List<TweetEntity>)message.data);
                break;

            case RECEIVE_TWEET:
                this.view.catchNewTweet((TweetEntity)message.data);
                Log.d("HommeTimeLinePresenter", "receive-tweet");
                break;

            case RECEIVE_FAVORITE :
            case RECEIVE_UN_FAVORITE :
            case RECEIVE_DELETION:
                this.view.updateTweet((TweetEntity)message.data);
                break;
        }
    }
}
