package com.seki.saezurishiki.model.impl;

import com.seki.saezurishiki.model.GetTweetById;
import com.seki.saezurishiki.model.GetUserById;
import com.seki.saezurishiki.model.TweetListModel;
import com.seki.saezurishiki.model.UserListModel;
import com.seki.saezurishiki.network.twitter.TwitterAccount;
import com.seki.saezurishiki.presenter.list.UserListPresenter;

public final class ModelContainer {

    private static TweetListModel homeTweetListModel;
    private static TweetListModel replyTweetListModel;
    private static TweetListModel favoriteListModel;
    private static TweetListModel userTweetListModel;
    private static TweetListModel conversationModel;
    private static TweetListModel searchTweetModel;
    private static GetTweetById   getTweetById;
    private static GetUserById    getUserById;
    private static UserListModel  friendListModel;
    private static UserListModel  followerListMode;

    private ModelContainer() {
        //no instance
    }

    public static void start(final TwitterAccount account) {
        homeTweetListModel = new HomeTweetListModel(account);
        replyTweetListModel = new ReplyTweetListModel(account);
        favoriteListModel = new FavoriteListModel(account);
        userTweetListModel = new UserTweetListModel(account);
        conversationModel = new ConversationModel(account);
        searchTweetModel = new SearchTweetModel(account);
        getTweetById = new GetTweetByIdImp(account);
        getUserById = new GetUserByIdImp(account);
        friendListModel = new FriendListModel(account);
        followerListMode = new FollowerListModel(account);
    }

    public static void destroy() {
        homeTweetListModel = null;
        replyTweetListModel = null;
        favoriteListModel = null;
        userTweetListModel = null;
        conversationModel = null;
        searchTweetModel = null;
        getTweetById = null;
        getUserById = null;
        friendListModel = null;
        followerListMode = null;
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

    public static TweetListModel getSearchTweetModel() { return searchTweetModel;}

    public static GetTweetById getRepositoryAccessor() { return getTweetById; }

    public static GetUserById getUserById() {
        return getUserById;
    }

    public static UserListModel getFriendListModel() {
        return friendListModel;
    }

    public static UserListModel getFollowerListMode() {
        return followerListMode;
    }
}
