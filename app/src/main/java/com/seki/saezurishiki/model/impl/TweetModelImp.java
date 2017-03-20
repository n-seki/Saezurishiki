package com.seki.saezurishiki.model.impl;

import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.model.TweetModel;
import com.seki.saezurishiki.model.adapter.ModelActionType;
import com.seki.saezurishiki.model.adapter.ModelMessage;
import com.seki.saezurishiki.model.impl.ModelBaseImp;
import com.seki.saezurishiki.network.twitter.TwitterAccount;
import com.seki.saezurishiki.repository.Repository;


public class TweetModelImp extends ModelBaseImp implements TweetModel {

    TweetModelImp(TwitterAccount twitterAccount) {
        super(twitterAccount);
    }

    public static TweetModel getInstance(TwitterAccount account) {
        return new TweetModelImp(account);
    }


    @Override
    public void favorite(TweetEntity tweetEntity) {
        //ロード後はこんな感じ！
        final TweetEntity result = null;
        ModelMessage message = ModelMessage.of(ModelActionType.UPDATE_TWEET, result);
        this.observable.notifyObserver(message);

    }

    @Override
    public void unFavorite(TweetEntity tweetEntity) {

    }

    @Override
    public void reTweet(TweetEntity tweetEntity) {

    }

    @Override
    public void delete(TweetEntity tweetEntity) {

    }
}
