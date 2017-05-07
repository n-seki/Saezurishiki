package com.seki.saezurishiki.presenter.list;

import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.model.TweetListModel;
import com.seki.saezurishiki.model.adapter.ModelMessage;

import java.util.List;

import twitter4j.StatusDeletionNotice;


public class ReplyTimeLinePresenter extends TweetListPresenter {

    public ReplyTimeLinePresenter(TweetListView view, long listOwnerId, TweetListModel listModel) {
        super(view, listOwnerId, listModel);
    }


    @SuppressWarnings("unchecked")
    @Override
    void dispatch(ModelMessage message) {
        switch (message.type) {
            case LOAD_REPLY_LIST:
                this.view.loadTweets((List<TweetEntity>)message.data);
                break;

            case COMPLETE_FAVORITE:
            case COMPLETE_UN_FAVORITE:
                this.view.updateTweet((TweetEntity)message.data);
                break;

            case RECEIVE_TWEET:
                final TweetEntity entity = (TweetEntity)message.data;
                if (entity.isSentToLoginUser) {
                    this.view.catchNewTweet((TweetEntity) message.data);
                }
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
        }
    }
}
