package com.seki.saezurishiki.model;

import com.seki.saezurishiki.repository.Repository;

import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.User;


class TweetListModelImp extends ModelBaseImp implements TweetListModel {

    TweetListModelImp(Repository repository) {
        super(repository);
    }

    @Override
    public void request(Paging paging) {

    }

    @Override
    public void onStatus(Status tweet) {

    }

    @Override
    public void onDeletionNotice(StatusDeletionNotice deletionNotice) {

    }

    @Override
    public void onFavorite(User sourceUser, User targetUser, Status targetTweet) {

    }

    @Override
    public void onUnFavorite(User sourceUser, User targetUser, Status targetTweet) {

    }

}
