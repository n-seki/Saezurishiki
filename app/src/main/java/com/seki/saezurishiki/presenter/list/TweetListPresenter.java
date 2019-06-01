package com.seki.saezurishiki.presenter.list;


import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.seki.saezurishiki.control.Setting;
import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.entity.TwitterEntity;
import com.seki.saezurishiki.entity.UserEntity;
import com.seki.saezurishiki.model.TweetListModel;
import com.seki.saezurishiki.model.adapter.ModelMessage;
import com.seki.saezurishiki.model.adapter.RequestInfo;
import com.seki.saezurishiki.model.util.ModelObservable;
import com.seki.saezurishiki.model.util.ModelObserver;
import com.seki.saezurishiki.view.adapter.TimeLineAdapter;
import com.seki.saezurishiki.view.fragment.dialog.adapter.DialogSelectAction;

import java.util.List;


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
        void displayDetailTweet(long userID, long tweetID);
        void showUserActivity(long userID);
        void openLink(String url);
        void openReplyEditor(TweetEntity tweet);
        void showPicture(TweetEntity tweet, int position);
        void showTweetDialog(TweetEntity tweet, int[] forbidAction);
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
    }

    public void onItemClick(TweetEntity tweet) {
        view.showTweetDialog(tweet, getForbidDialogActions());
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
    public void onClickPicture(int position, TweetEntity tweet) {
        this.view.showPicture(tweet, position);
    }

    @Override
    public void onClickUserIcon(View view, UserEntity user) {
        view.setEnabled(false);
        this.view.showUserActivity(user.getId());
        new Handler().postDelayed(() -> view.setEnabled(true), 1000L);
    }

    @Override
    public void onClickReplyButton(TweetEntity tweet) {
        this.view.openReplyEditor(tweet);
    }

    @Override
    public void onClickReTweetButton(TweetEntity tweet, Setting.ButtonActionPattern actionPattern) {
        if (actionPattern.isLongClick) {
            return;
        }

        if (actionPattern.showDialog) {
            this.view.showReTweetDialog(tweet);
            return;
        }

        reTweet(tweet);
    }

    @Override
    public boolean onLongClickReTweetButton(TweetEntity tweet, Setting.ButtonActionPattern actionPattern) {
        if (!actionPattern.isLongClick) {
            return true;
        }

        if (actionPattern.showDialog) {
            this.view.showReTweetDialog(tweet);
            return true;
        }

        reTweet(tweet);
        return true;
    }

    @Override
    public void onClickFavoriteButton(final TweetEntity tweet, Setting.ButtonActionPattern actionPattern) {
        if (actionPattern.isLongClick) {
            return;
        }

        if (actionPattern.showDialog) {
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
    public boolean onLongClickFavoriteButton(final TweetEntity tweet, Setting.ButtonActionPattern actionPattern) {
        if (!actionPattern.isLongClick) {
            return true;
        }

        if (actionPattern.showDialog) {
            this.view.showFavoriteDialog(tweet);
            return true;
        }

        if (!tweet.isFavorited) {
            createFavorite(tweet);
        } else {
            destroyFavorite(tweet);
        }

        return true;
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
                this.view.showPicture(selectedItem.targetItem, (int)selectedItem.item);
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
        new Handler(Looper.getMainLooper()).post(() -> dispatch(message));
    }

    protected int[] getForbidDialogActions() {
        return new int[0];
    }

    abstract void dispatch(ModelMessage message);

}
