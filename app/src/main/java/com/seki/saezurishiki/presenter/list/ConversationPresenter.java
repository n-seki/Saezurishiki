package com.seki.saezurishiki.presenter.list;

import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.model.TweetListModel;
import com.seki.saezurishiki.model.adapter.ModelMessage;

import twitter4j.Paging;

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
        }
    }

    private boolean hasNextReply(TweetEntity tweet) {
        return tweet.inReplyToStatusId != -1;
    }
}
