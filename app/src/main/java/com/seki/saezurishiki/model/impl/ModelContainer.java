package com.seki.saezurishiki.model.impl;

import com.seki.saezurishiki.model.DirectMessageListModel;
import com.seki.saezurishiki.model.GetDirectMessageById;
import com.seki.saezurishiki.model.GetTweetById;
import com.seki.saezurishiki.model.GetUserById;
import com.seki.saezurishiki.model.TweetListModel;
import com.seki.saezurishiki.model.UserListModel;
import com.seki.saezurishiki.model.UserScreenModel;
import com.seki.saezurishiki.network.twitter.TwitterAccount;
import com.seki.saezurishiki.presenter.list.UserListPresenter;

public final class ModelContainer {

    private static UserScreenModel userScreenModel;
    private static TweetListModel homeTweetListModel;
    private static TweetListModel replyTweetListModel;
    private static TweetListModel favoriteListModel;
    private static TweetListModel userTweetListModel;
    private static TweetListModel conversationModel;
    private static TweetListModel searchTweetModel;
    private static GetTweetById   getTweetById;
    private static GetUserById    getUserById;
    private static GetDirectMessageById getDirectMessageById;
    private static UserListModel  friendListModel;
    private static UserListModel  followerListMode;
    private static DirectMessageListModel directMessageListModel;
    private static DirectMessageEditorModel directMessageEditorModel;

    private ModelContainer() {
        //no instance
    }

    public static void start(final TwitterAccount account) {
        userScreenModel = new UserScreenModelImp(account);
        homeTweetListModel = new HomeTweetListModel(account);
        replyTweetListModel = new ReplyTweetListModel(account);
        favoriteListModel = new FavoriteListModel(account);
        userTweetListModel = new UserTweetListModel(account);
        conversationModel = new ConversationModel(account);
        searchTweetModel = new SearchTweetModel(account);
        getTweetById = new GetTweetByIdImp(account);
        getUserById = new GetUserByIdImp(account);
        getDirectMessageById = new GetDirectMessageByIdImp(account);
        friendListModel = new FriendListModel(account);
        followerListMode = new FollowerListModel(account);
        directMessageListModel = new DirectMessageListModelImp(account);
        directMessageEditorModel = new DirectMessageEditorModel(account);

    }

    public static void destroy() {
        userScreenModel = null;
        homeTweetListModel = null;
        replyTweetListModel = null;
        favoriteListModel = null;
        userTweetListModel = null;
        conversationModel = null;
        searchTweetModel = null;
        getTweetById = null;
        getUserById = null;
        getDirectMessageById = null;
        friendListModel = null;
        followerListMode = null;
        directMessageListModel = null;
        directMessageEditorModel = null;
    }

    public static UserScreenModel getUserScreenModel() {
        return userScreenModel;
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

    public static GetDirectMessageById getDirectMessageById() {
        return getDirectMessageById;
    }

    public static UserListModel getFriendListModel() {
        return friendListModel;
    }

    public static UserListModel getFollowerListMode() {
        return followerListMode;
    }

    public static DirectMessageListModel getDirectMessageListModel() {
        return directMessageListModel;
    }

    public static DirectMessageEditorModel getDirectMessageEditorModel() {
        return directMessageEditorModel;
    }
}
