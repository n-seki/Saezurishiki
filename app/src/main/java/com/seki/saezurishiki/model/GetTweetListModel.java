package com.seki.saezurishiki.model;

import com.seki.saezurishiki.repository.Repository;

import twitter4j.Paging;



class GetTweetListModel extends ModelBaseImp implements TweetListModel {

    GetTweetListModel(Repository repository) {
        super(repository);
    }

    @Override
    public void request(Paging paging) {

    }

}
