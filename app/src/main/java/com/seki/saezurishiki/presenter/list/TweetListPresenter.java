package com.seki.saezurishiki.presenter.list;


import android.os.Handler;
import android.os.Looper;

import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.entity.TwitterEntity;
import com.seki.saezurishiki.model.TweetListModel;
import com.seki.saezurishiki.model.adapter.ModelMessage;
import com.seki.saezurishiki.model.adapter.RequestInfo;
import com.seki.saezurishiki.model.util.ModelObservable;
import com.seki.saezurishiki.model.util.ModelObserver;
import com.seki.saezurishiki.view.adapter.TimeLineAdapter;
import com.seki.saezurishiki.view.fragment.dialog.adapter.DialogSelectAction;
import com.seki.saezurishiki.view.fragment.list.TweetListFragment;

import java.util.List;

import twitter4j.Paging;
import twitter4j.User;


public abstract class TweetListPresenter implements TimeLineAdapter.ViewListener, ModelObserver {

    final TweetListView view;
    final TweetListModel tweetListModel;
    private final long listOwnerId;

    public interface TweetListView {
        void updateTweet(TweetEntity tweetEntity);
        void loadTweets(List<TweetEntity> tweets);
        void catchNewTweet(TweetEntity tweet);
        void completeDeleteTweet(TweetEntity tweet);
        void completeReTweet(TweetEntity tweet);
        void deletionTweet(long deletedTweetId);
        void setPresenter(TweetListPresenter presenter);
        void displayDetailTweet(long userID, long tweetID);
        void showUserActivity(long userID);
        void openLink(String url);
        void openReplyEditor(TweetEntity tweet);
        void showPicture(TweetEntity tweet, String selectedMedia);
        void showReTweetDialog(TweetEntity tweet);
        void showFavoriteDialog(TweetEntity tweet);
        void showLongClickDialog(TweetEntity tweet);
        void errorProcess(Exception e);
        void hideFooterLoadButton();
    }

    public void onResume() {
        this.tweetListModel.addObserver(this);
    }

    public void onPause() {
        this.tweetListModel.removeObserver(this);
    }

    TweetListPresenter(TweetListView view, long listOwnerId, TweetListModel listModel) {
        this.view = view;
        this.listOwnerId = listOwnerId;
        this.tweetListModel = listModel;

        this.view.setPresenter(this);
    }


    public void onLongClickListItem(TwitterEntity entity) {
        if (entity.getItemType() == TwitterEntity.Type.LoadButton) {
            return;
        }

        if (this.tweetListModel.isDelete((TweetEntity) entity)) {
            return;
        }

        this.view.showLongClickDialog((TweetEntity)entity);
    }

    public void createFavorite(TweetEntity tweet) {
        this.tweetListModel.favorite(tweet);
    }


    public void destroyFavorite(TweetEntity tweet) {
        this.tweetListModel.unFavorite(tweet);
    }


    private void deleteTweet(TweetEntity tweet) {
        this.tweetListModel.delete(tweet);
    }


    public void reTweet(TweetEntity tweet) {
        this.tweetListModel.reTweet(tweet);
    }


    public void load(final RequestInfo info) {
        this.tweetListModel.request(info.userID(this.listOwnerId));
    }

    @Override
    public void onClickPicture(String pictureURL, TweetEntity tweet) {
        this.view.showPicture(tweet, pictureURL);
    }

    @Override
    public void onClickUserIcon(User user) {
        this.view.showUserActivity(user.getId());
    }

    @Override
    public void onClickReplyButton(TweetEntity tweet) {
        this.view.openReplyEditor(tweet);
    }

    @Override
    public void onClickReTweetButton(TweetEntity tweet, boolean isShowDialog) {
        if (isShowDialog) {
            this.view.showReTweetDialog(tweet);
            return;
        }

        reTweet(tweet);
    }

    @Override
    public void onClickFavoriteButton(final TweetEntity tweet, final boolean isShowDialog) {
        if (isShowDialog) {
            this.view.showFavoriteDialog(tweet);
            return;
        }

        if (!tweet.isFavorited) {
            createFavorite(tweet);
        } else {
            destroyFavorite(tweet);
        }
    }


    @Override
    public void onClickQuotedTweet(final TweetEntity tweet) {
        this.view.displayDetailTweet(this.listOwnerId, tweet.getId());
    }


    public void onClickDialogItem(DialogSelectAction<TweetEntity> selectedItem) {
        switch (selectedItem.action) {
            case DialogSelectAction.SHOW_TWEET:
                this.view.displayDetailTweet(this.listOwnerId, selectedItem.targetItem.getId());
                break;

            case DialogSelectAction.BIOGRAPHY:
                this.view.showUserActivity((Long)selectedItem.item);
                break;

            case DialogSelectAction.URL:
                this.view.openLink((String)selectedItem.item);
                break;

            case DialogSelectAction.MEDIA:
                this.view.showPicture(selectedItem.targetItem, (String)selectedItem.item);
                break;

            default:
                throw new IllegalArgumentException("action is invalid! : " + selectedItem.action);
        }
    }


    public void onClickLongClickDialog(DialogSelectAction<TweetEntity> selectedItem) {
        switch (selectedItem.action) {
            case DialogSelectAction.DELETE :
                this.deleteTweet(selectedItem.targetItem);
                break;

            case DialogSelectAction.RE_TWEET:
                this.reTweet(selectedItem.targetItem);
                break;

            case DialogSelectAction.UN_RE_TWEET:
                this.deleteTweet(selectedItem.targetItem);
                break;

            case DialogSelectAction.FAVORITE:
                this.createFavorite(selectedItem.targetItem);
                break;

            case DialogSelectAction.UN_FAVORITE:
                this.destroyFavorite(selectedItem.targetItem);
                break;

            default:
                throw new IllegalArgumentException("action is invalid! : " + selectedItem.action);
        }
    }


    @Override
    public void update(ModelObservable observable, final ModelMessage message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                dispatch(message);
            }
        });
    }


    abstract void dispatch(ModelMessage message);

}
