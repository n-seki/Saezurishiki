package com.seki.saezurishiki.model.impl;

import com.seki.saezurishiki.model.TweetListModel;
import com.seki.saezurishiki.network.twitter.TwitterAccount;

public final class ModelContainer {

    private static TweetListModel homeTweetListModel;
    private static TweetListModel replyTweetListModel;
    private static TweetListModel favoriteListModel;
    private static TweetListModel userTweetListModel;
    private static TweetListModel conversationModel;

    private ModelContainer() {
        //no instance
    }

    public static void start(final TwitterAccount account) {
        homeTweetListModel = new HomeTweetListModel(account);
        replyTweetListModel = new ReplyTweetListModel(account);
        favoriteListModel = new FavoriteListModel(account);
        userTweetListModel = new UserTweetListModel(account);
        conversationModel = new ConversationModel(account);
    }

    public static void destroy() {
        homeTweetListModel = null;
        replyTweetListModel = null;
        favoriteListModel = null;
        userTweetListModel = null;
        conversationModel = null;
    }

    public static TweetListModel getHomeTweetListModel() {
        return homeTweetListModel;
    }

    public static TweetListModel getReplyTweetListModel() {
        return replyTweetListModel;
    }

    public static TweetListModel getFavoriteListModel() {
        return favoriteListModel;
    }

    public static  TweetListModel getUserTweetListModel() {
        return userTweetListModel;
    }

    public static TweetListModel getConversationModel() {
        return conversationModel;
    }
}
