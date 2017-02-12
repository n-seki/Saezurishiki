package com.seki.saezurishiki.model;

import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.model.adapter.ModelActionType;
import com.seki.saezurishiki.model.adapter.ModelMessage;
import com.seki.saezurishiki.repository.Repository;


class TweetModelImp extends ModelBaseImp implements TweetModel {

    TweetModelImp(Repository repository) {
        super(repository);
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
