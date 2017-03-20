package com.seki.saezurishiki.presenter.list;


import android.os.Handler;

import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.entity.UserEntity;
import com.seki.saezurishiki.model.TweetListModel;
import com.seki.saezurishiki.model.adapter.ModelMessage;
import com.seki.saezurishiki.model.util.ModelObservable;
import com.seki.saezurishiki.model.util.ModelObserver;
import com.seki.saezurishiki.model.TweetModel;
import com.seki.saezurishiki.network.twitter.AsyncTwitterTask;
import com.seki.saezurishiki.network.twitter.TwitterAccount;
import com.seki.saezurishiki.network.twitter.TwitterTaskResult;
import com.seki.saezurishiki.network.twitter.TwitterWrapper;
import com.seki.saezurishiki.view.fragment.dialog.adapter.DialogSelectAction;

import java.util.List;


public class TweetListPresenter implements ModelObserver {

    private final TweetListView view;
    private final TweetModel tweetModel;
    private final TweetListModel tweetListModel;
    private final UserEntity listOwner;

    private TwitterWrapper twitterWrapper;

    public interface TweetListView {
        void updateTweet(TweetEntity tweetEntity);
        void loadTweets(List<TweetEntity> tweets);
        void completeDeleteTweet(TweetEntity tweet);
        void completeReTweet(TweetEntity tweet);
        void errorProcess(Exception e);
    }

    public void onResume() {
        this.tweetListModel.addObserver(this);
        this.tweetModel.addObserver(this);
    }

    public void onPause() {
        this.tweetListModel.removeObserver(this);
        this.tweetModel.removeObserver(this);
    }

    public TweetListPresenter(TweetListView view, UserEntity listOwner, TwitterAccount twitterAccount) {
        this.view = view;
        this.listOwner = listOwner;
        this.tweetModel = null;
        this.tweetListModel = null;;
    }

    public TweetListPresenter(TweetListView view, UserEntity listOwner, TweetListModel listModel, TweetModel tweetModel) {
        this.view = view;
        this.listOwner = listOwner;
        this.tweetListModel = listModel;
        this.tweetModel = tweetModel;
    }

    //TODO
    public TweetListPresenter(TwitterAccount twitterAccount) {
        this(null, null, twitterAccount);
    }


    public void onClickRetweetButton(TweetEntity tweet) {
        reTweet(tweet.getId());
    }


    public void onClickFavoriteButton(TweetEntity tweet) {
        if (!tweet.isFavorited) {
            createFavorite(tweet);
        } else {
            destroyFavorite(tweet);
        }
    }


    //Status → Entityの変換が必要
    public void createFavorite(TweetEntity tweet) {
        AsyncTwitterTask.AfterTask<TweetEntity> afterTask = new AsyncTwitterTask.AfterTask<TweetEntity>() {
            @Override
            public void onLoadFinish(TwitterTaskResult<TweetEntity> result) {
                if (result.isException()) {
                    view.errorProcess(result.getException());
                    return;
                }

                view.updateTweet(result.getResult());
            }
        };

        this.twitterWrapper.createFavorite(tweet.getId(), afterTask);
    }

    //Status → Entityの変換が必要
    public void destroyFavorite(TweetEntity tweet) {
        AsyncTwitterTask.AfterTask<TweetEntity> afterTask = new AsyncTwitterTask.AfterTask<TweetEntity>() {
            @Override
            public void onLoadFinish(TwitterTaskResult<TweetEntity> result) {
                if (result.isException()) {
                    view.errorProcess(result.getException());
                    return;
                }

                view.updateTweet(result.getResult());
            }
        };

        this.twitterWrapper.unFavorite(tweet.getId(), afterTask);
    }

    //Status → Entityの変換が必要
    public void deleteTweet(long tweetID) {
        AsyncTwitterTask.AfterTask<TweetEntity> afterTask = new AsyncTwitterTask.AfterTask<TweetEntity>() {
            @Override
            public void onLoadFinish(TwitterTaskResult<TweetEntity> result) {
                if (result.isException()) {
                    view.errorProcess(result.getException());
                    return;
                }

                view.completeDeleteTweet(result.getResult());
            }
        };

        this.twitterWrapper.destroyStatus(tweetID, afterTask);
    }

    //Status → Entityの変換が必要
    public void reTweet(long tweetID) {
        AsyncTwitterTask.AfterTask<TweetEntity> afterTask = new AsyncTwitterTask.AfterTask<TweetEntity>() {
            @Override
            public void onLoadFinish(TwitterTaskResult<TweetEntity> result) {
                if (result.isException()) {
                    view.errorProcess(result.getException());
                    return;
                }

                view.completeReTweet(result.getResult());
            }
        };

        this.twitterWrapper.createReTweet(tweetID, afterTask);
    }


    public void onDeleteTweet(TweetEntity tweetEntity) {
        this.tweetModel.delete(tweetEntity);
    }


    public void onClickLongClickDialog(DialogSelectAction<TweetEntity> selectedItem) {
        switch (selectedItem.action) {
            case DialogSelectAction.DELETE :
                this.deleteTweet(selectedItem.targetItem.getId());
                break;

            case DialogSelectAction.RE_TWEET:
                this.reTweet(selectedItem.targetItem.getId());
                break;

            case DialogSelectAction.UN_RE_TWEET:
                this.deleteTweet(selectedItem.targetItem.getId());
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
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                dispatch(message);
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void dispatch(ModelMessage message) {
        switch (message.type) {
            case LOAD_TWEET:
                //this.view.showTweet((TweetEntity) message.data);
                break;
            case UPDATE_TWEET:
                this.view.updateTweet((TweetEntity)message.data);
                break;
            case DELETE_TWEET:
                //this.view.deleteTweet((TweetEntity)message.data);
                break;
            case LOAD_TWEETS:
                this.view.loadTweets((List<TweetEntity>)message.data);
                break;
            case ERROR:
                this.view.errorProcess(message.exception);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }


    //TODO
    public void setTwitterWrapper(TwitterWrapper twitterWrapper) {
        this.twitterWrapper = twitterWrapper;
    }


}
