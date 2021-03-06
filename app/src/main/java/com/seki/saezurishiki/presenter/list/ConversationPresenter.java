package com.seki.saezurishiki.presenter.list;

import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.model.TweetListModel;
import com.seki.saezurishiki.model.adapter.ModelMessage;
import com.seki.saezurishiki.model.adapter.RequestInfo;
import com.seki.saezurishiki.view.fragment.dialog.adapter.DialogSelectAction;

import twitter4j.StatusDeletionNotice;

public class ConversationPresenter extends TweetListPresenter {

    public ConversationPresenter(TweetListView view, long listOwnerId, TweetListModel listModel) {
        super(view, listOwnerId, listModel);
    }

    @Override
    protected int[] getForbidDialogActions() {
        return new int[] { DialogSelectAction.SHOW_TWEET };
    }

    @Override
    void dispatch(ModelMessage message) {
        switch (message.type) {
            case LOAD_TWEET:
                final TweetEntity tweet = (TweetEntity)message.data;
                this.view.catchNewTweet(tweet);
                if (hasNextReply((TweetEntity)message.data)) {
                    this.tweetListModel.request(new RequestInfo().targetID(tweet.inReplyToStatusId));
                }
                break;

            case COMPLETE_FAVORITE:
            case COMPLETE_UN_FAVORITE:
                this.view.updateTweet((TweetEntity)message.data);
                break;

            case COMPLETE_RETWEET:
                this.view.completeReTweet((TweetEntity)message.data);
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
