package com.seki.saezurishiki.model.impl;

import com.seki.saezurishiki.model.GetTweetById;
import com.seki.saezurishiki.model.GetUserById;
import com.seki.saezurishiki.model.LoginUserScreen;
import com.seki.saezurishiki.model.TweetEditorModel;
import com.seki.saezurishiki.model.TweetListModel;
import com.seki.saezurishiki.model.UserListModel;
import com.seki.saezurishiki.model.UserScreenModel;

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
    private static UserListModel  friendListModel;
    private static UserListModel  followerListMode;
    private static LoginUserScreen loginUserScreen;
    private static TweetEditorModel tweetEditorModel;

    private ModelContainer() {
        //no instance
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
        friendListModel = null;
        followerListMode = null;
        loginUserScreen = null;
        tweetEditorModel = null;
    }

    public static UserScreenModel getUserScreenModel() {
        if (userScreenModel == null) {
            userScreenModel = new UserScreenModelImp();
        }
        return userScreenModel;
    }

    public static TweetListModel getHomeTweetListModel() {
        if (homeTweetListModel == null) {
            homeTweetListModel = new HomeTweetListModel();
        }
        return homeTweetListModel;
    }

    public static TweetListModel getReplyTweetListModel() {
        if (replyTweetListModel == null) {
            replyTweetListModel = new ReplyTweetListModel();
        }
        return replyTweetListModel;
    }

    public static TweetListModel getFavoriteListModel() {
        if (favoriteListModel == null) {
            favoriteListModel = new FavoriteListModel();
        }
        return favoriteListModel;
    }

    public static  TweetListModel getUserTweetListModel() {
        if (userTweetListModel == null) {
            userTweetListModel = new UserTweetListModel();
        }
        return userTweetListModel;
    }

    public static TweetListModel getConversationModel() {
        if (conversationModel == null) {
            conversationModel = new ConversationModel();
        }
        return conversationModel;
    }

    public static TweetListModel getSearchTweetModel() {
        if (searchTweetModel == null) {
            searchTweetModel = new SearchTweetModel();
        }
        return searchTweetModel;
    }

    public static GetTweetById getRepositoryAccessor() {
        if (getTweetById == null) {
            getTweetById = new GetTweetByIdImp();
        }
        return getTweetById;
    }

    public static GetUserById getUserById() {
        if (getUserById == null) {
            getUserById = new GetUserByIdImp();
        }
        return getUserById;
    }

    public static UserListModel getFriendListModel() {
        if (friendListModel == null) {
            friendListModel = new FriendListModel();
        }
        return friendListModel;
    }

    public static UserListModel getFollowerListMode() {
        if (followerListMode == null) {
            followerListMode = new FollowerListModel();
        }
        return followerListMode;
    }

    public static LoginUserScreen getLoginUserScreen() {
        if (loginUserScreen == null) {
            loginUserScreen = new LoginUserScreenImp();
        }
        return loginUserScreen;
    }

    public static TweetEditorModel getTweetEditorModel() {
        if (tweetEditorModel == null) {
            tweetEditorModel = new TweetEditorModelImp();
        }
        return tweetEditorModel;
    }
}
