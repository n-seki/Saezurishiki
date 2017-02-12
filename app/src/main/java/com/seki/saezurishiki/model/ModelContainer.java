package com.seki.saezurishiki.model;

import com.seki.saezurishiki.repository.Repository;
import com.seki.saezurishiki.view.ViewType;

public final class ModelContainer {

    private static TweetModel tweetModelImp;
    private static TweetListModel getTweetLitModel;

    private ModelContainer() {}

    public static void initialize(Repository repository) {
        tweetModelImp = new TweetModelImp(repository);
        getTweetLitModel = new GetTweetListModel(repository);
    }

    public static TweetModel getTweetModel() {
        return tweetModelImp;
    }

    public static TweetListModel getTweetListModel(ViewType type) {
        switch (type) {
            case TWEETLIST:
                return getTweetLitModel;

            default:
                throw new IllegalStateException("ViewType is Illegal! : " + type);
        }
    }
}
