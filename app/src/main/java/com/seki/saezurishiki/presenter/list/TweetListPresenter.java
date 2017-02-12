package com.seki.saezurishiki.presenter.list;

import android.view.View;

import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.entity.User;
import com.seki.saezurishiki.model.ModelContainer;
import com.seki.saezurishiki.model.TweetListModel;
import com.seki.saezurishiki.model.adapter.ModelMessage;
import com.seki.saezurishiki.model.util.ModelObservable;
import com.seki.saezurishiki.model.util.ModelObserver;
import com.seki.saezurishiki.model.TweetModel;
import com.seki.saezurishiki.network.twitter.AsyncTwitterTask;
import com.seki.saezurishiki.network.twitter.TwitterAccount;
import com.seki.saezurishiki.network.twitter.TwitterTaskResult;
import com.seki.saezurishiki.network.twitter.TwitterTaskUtil;
import com.seki.saezurishiki.view.ViewType;
import com.seki.saezurishiki.view.fragment.list.TweetListFragment;

import java.util.List;

import twitter4j.Paging;
import twitter4j.Status;


public class TweetListPresenter implements ModelObserver {

    private final TweetListView view;
    private final TweetModel tweetModel;
    private final TwitterAccount twitterAccount;
    private final TweetListModel tweetListModel;
    private final User listOwner;

    private TwitterTaskUtil twitterTaskUtil;

    public interface TweetListView {
        void updateTweet(TweetEntity tweetEntity);
        //void reTweet(TweetEntity tweetEntity);
        //void deleteTweet(TweetEntity tweet);
        //void showTweet(TweetEntity tweet);
        //void loadTweets(List<TweetEntity> list);
        void completeDeleteTweet(TweetEntity tweet);
        void completeReTweet(TweetEntity tweet);
        void errorProcess(Exception e);
        ViewType getViewType();
    }

    public void onResume() {
        this.tweetModel.addObserver(this);
    }

    public TweetListPresenter(TweetListView view, User listOwner, TwitterAccount twitterAccount) {
        this.view = view;
        this.listOwner = listOwner;
        this.tweetModel = ModelContainer.getTweetModel();
        this.tweetListModel = ModelContainer.getTweetListModel(this.view.getViewType());
        this.twitterAccount = twitterAccount;
    }

    //TODO
    public TweetListPresenter(TwitterAccount twitterAccount) {
        this(null, null, twitterAccount);
    }


    public void onClickRetweetButton(TweetEntity tweet) {
        this.tweetModel.reTweet(tweet);
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

        this.twitterTaskUtil.createFavorite(tweet.getId(), afterTask);
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

        this.twitterTaskUtil.unFavorite(tweet.getId(), afterTask);
    }

    //Status → Entityの変換が必要
    public void deleteTweet(TweetEntity tweet) {
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

        this.twitterTaskUtil.destroyStatus(tweet.getId(), afterTask);
    }

    //Status → Entityの変換が必要
    public void reTweet(TweetEntity tweet) {
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

        this.twitterTaskUtil.createReTweet(tweet.getId(), afterTask);
    }

    public void onClickLoadButton(View view) {
        this.tweetListModel.request(new Paging());
    }

    public void onDeleteTweet(TweetEntity tweetEntity) {
        this.tweetModel.delete(tweetEntity);
    }


    @Override
    public void update(ModelObservable observable, ModelMessage message) {
        this.dispatch(message);
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
                //this.view.loadTweets((List<TweetEntity>)message.data);
                break;
            case ERROR:
                this.view.errorProcess(message.exception);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }


    //TODO
    public void setTwitterTaskUtil(TwitterTaskUtil twitterTaskUtil) {
        this.twitterTaskUtil = twitterTaskUtil;
    }


}
