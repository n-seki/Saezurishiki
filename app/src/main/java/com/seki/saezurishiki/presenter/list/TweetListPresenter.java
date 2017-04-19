package com.seki.saezurishiki.presenter.list;


import android.os.Handler;
import android.os.Looper;

import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.model.TweetListModel;
import com.seki.saezurishiki.model.adapter.ModelMessage;
import com.seki.saezurishiki.model.util.ModelObservable;
import com.seki.saezurishiki.model.util.ModelObserver;
import com.seki.saezurishiki.view.fragment.dialog.adapter.DialogSelectAction;

import java.util.List;

import twitter4j.Paging;


public abstract class TweetListPresenter implements ModelObserver {

    final TweetListView view;
    private final TweetListModel tweetListModel;
    private final long listOwnerId;

    public interface TweetListView {
        void updateTweet(TweetEntity tweetEntity);
        void loadTweets(List<TweetEntity> tweets);
        void catchNewTweet(TweetEntity tweet);
        void completeDeleteTweet(TweetEntity tweet);
        void completeReTweet(TweetEntity tweet);
        void deletionTweet(long deletedTweetId);
        void setPresenter(TweetListPresenter presenter);
        void errorProcess(Exception e);
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

    public void onClickRetweetButton(TweetEntity tweet) {
        reTweet(tweet);
    }


    public void onClickFavoriteButton(TweetEntity tweet) {
        if (!tweet.isFavorited) {
            createFavorite(tweet);
        } else {
            destroyFavorite(tweet);
        }
    }


    public void createFavorite(TweetEntity tweet) {
        this.tweetListModel.favorite(tweet);
    }


    public void destroyFavorite(TweetEntity tweet) {
        this.tweetListModel.unFavorite(tweet);
    }


    public void deleteTweet(TweetEntity tweet) {
        this.tweetListModel.delete(tweet);
    }


    public void reTweet(TweetEntity tweet) {
        this.tweetListModel.reTweet(tweet);
    }


    public void onDeleteTweet(TweetEntity tweet) {
        this.view.completeDeleteTweet(tweet);
    }

    public void load(final Paging paging) {
        this.tweetListModel.request(this.listOwnerId, paging);
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
