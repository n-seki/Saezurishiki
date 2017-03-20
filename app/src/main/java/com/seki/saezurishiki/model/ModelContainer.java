package com.seki.saezurishiki.model;

import com.seki.saezurishiki.repository.Repository;

public final class ModelContainer {

    private static TweetModel tweetModelImp;
    private static TweetListModel getTweetLitModel;

    private ModelContainer() {}

    public static void initialize(Repository repository) {
        tweetModelImp = new TweetModelImp(repository);
    }

    public static TweetModel getTweetModel() {
        return tweetModelImp;
    }

    public static TweetListModel getTweetListModel() {
        return getTweetLitModel;
    }
}
