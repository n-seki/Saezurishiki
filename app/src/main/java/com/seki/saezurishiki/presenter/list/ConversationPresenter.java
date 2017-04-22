package com.seki.saezurishiki.presenter.list;

import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.model.TweetListModel;
import com.seki.saezurishiki.model.adapter.ModelMessage;

import twitter4j.Paging;
import twitter4j.StatusDeletionNotice;

public class ConversationPresenter extends TweetListPresenter {


    public ConversationPresenter(TweetListView view, long listOwnerId, TweetListModel listModel) {
        super(view, listOwnerId, listModel);
    }


    @Override
    void dispatch(ModelMessage message) {
        switch (message.type) {
            case LOAD_TWEET:
                final TweetEntity tweet = (TweetEntity)message.data;
                this.view.catchNewTweet(tweet);
                if (hasNextReply((TweetEntity)message.data)) {
                    this.tweetListModel.request(-1, new Paging().maxId(tweet.getId()));
                }
                break;

            case COMPLETE_FAVORITE:
            case COMPLETE_UN_FAVORITE:
                this.view.updateTweet((TweetEntity)message.data);
                break;

            case RECEIVE_TWEET:
                this.view.catchNewTweet((TweetEntity)message.data);
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

    private boolean hasNextReply(TweetEntity tweet) {
        return tweet.inReplyToStatusId != -1;
    }
}
