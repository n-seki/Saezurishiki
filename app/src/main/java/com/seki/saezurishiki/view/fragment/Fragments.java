package com.seki.saezurishiki.view.fragment;


import android.support.v4.app.Fragment;

import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.entity.UserEntity;
import com.seki.saezurishiki.model.TweetEditorModel;
import com.seki.saezurishiki.model.impl.ModelContainer;
import com.seki.saezurishiki.presenter.editor.TweetEditorPresenter;
import com.seki.saezurishiki.presenter.list.FollowerListPresenter;
import com.seki.saezurishiki.presenter.list.FriendListPresenter;
import com.seki.saezurishiki.view.fragment.editor.EditTweetFragment;
import com.seki.saezurishiki.view.fragment.list.FollowerListFragment;
import com.seki.saezurishiki.view.fragment.list.FriendListFragment;
import com.seki.saezurishiki.view.fragment.list.UsersListFragment;

import twitter4j.HashtagEntity;

public final class Fragments {

    private Fragments() {
        //no instance
    }

//    public static Fragment createInjectHomeTimeLineFragment(final int tabPosition, final long userId) {
//        final TweetListFragment fragment = UserStreamTimeLineFragment.getHomeTimeLine(userId, tabPosition, HOME);
//        new HomeTimeLinePresenter(ModelContainer.getHomeTweetListModel());
//        return fragment;
//    }

//    public static Fragment createInjectReplyTimeLineFragment(final int tabPosition, final long userId) {
//        final TweetListFragment fragment = UserStreamTimeLineFragment.getReplyTimeLine(userId, tabPosition, REPLY);
//        new ReplyTimeLinePresenter(ModelContainer.getReplyTweetListModel());
//        return fragment;
//    }

//    public static Fragment createInjectFavoritesFragment(final long userId, final int favoriteCount) {
//        final TweetListFragment fragment = FavoritesFragment.getInstance(userId, favoriteCount);
//        new FavoriteListPresenter(ModelContainer.getFavoriteListModel());
//        return fragment;
//    }

//    public static Fragment createInjectUserTweetFragment(final long userId, final int tweetCount) {
//        final TweetListFragment fragment = UserTweetFragment.getInstance(userId, tweetCount);
//        new UserTweetListPresenter(ModelContainer.getUserTweetListModel());
//        return fragment;
//    }

//    public static Fragment createInjectConversationFragment(final long userId, final long selectedTweetID) {
//        final TweetListFragment fragment = ConversationFragment.getInstance(userId, selectedTweetID);
//        new ConversationPresenter(ModelContainer.getConversationModel());
//        return fragment;
//    }
//
//    public static Fragment createInjectSearchFragment(final long userId, final String query) {
//        final TweetListFragment fragment = SearchFragment.getInstance(userId, query);
//        new SearchPresenter(ModelContainer.getSearchTweetModel());
//        return  fragment;
//    }

    public static Fragment newNormalEditor() {
        final EditTweetFragment fragment = EditTweetFragment.newNormalEditor();
        final TweetEditorModel model = ModelContainer.getTweetEditorModel();
        new TweetEditorPresenter(fragment, model);
        return fragment;
    }

    public static Fragment newEditorWithHashTag(HashtagEntity[] hashTagEntities) {
        final EditTweetFragment fragment = EditTweetFragment.newEditorWithHashTag(hashTagEntities);
        final TweetEditorModel model = ModelContainer.getTweetEditorModel();
        new TweetEditorPresenter(fragment, model);
        return fragment;
    }

   public static Fragment newReplyEditorFromStatus(TweetEntity tweet) {
        final EditTweetFragment fragment = EditTweetFragment.newReplyEditorFromStatus(tweet);
       final TweetEditorModel model = ModelContainer.getTweetEditorModel();
        new TweetEditorPresenter(fragment, model);
        return fragment;
   }

    public static Fragment newReplyEditorFromUser(UserEntity user) {
        final EditTweetFragment fragment = EditTweetFragment.newReplyEditorFromUser(user);
        final TweetEditorModel model = ModelContainer.getTweetEditorModel();
        new TweetEditorPresenter(fragment, model);
        return fragment;
    }

    public static Fragment newFriendListFragment(long userId) {
        final UsersListFragment fragment = FriendListFragment.newInstance();
        new FriendListPresenter(fragment, ModelContainer.getFriendListModel(), userId);
        return fragment;
    }

    public static Fragment newFollowerListFragment(long userId) {
        final UsersListFragment fragment = FollowerListFragment.newInstance();
        new FollowerListPresenter(fragment, ModelContainer.getFollowerListMode(), userId);
        return fragment;
    }
}
