package com.seki.saezurishiki.view.fragment;


import android.support.v4.app.Fragment;

import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.model.impl.ModelContainer;
import com.seki.saezurishiki.presenter.editor.TweetEditorPresenter;
import com.seki.saezurishiki.presenter.list.ConversationPresenter;
import com.seki.saezurishiki.presenter.list.FavoriteListPresenter;
import com.seki.saezurishiki.presenter.list.FollowerListPresenter;
import com.seki.saezurishiki.presenter.list.FriendListPresenter;
import com.seki.saezurishiki.presenter.list.HomeTimeLinePresenter;
import com.seki.saezurishiki.presenter.list.ReplyTimeLinePresenter;
import com.seki.saezurishiki.presenter.list.SearchPresenter;
import com.seki.saezurishiki.presenter.list.UserTweetListPresenter;
import com.seki.saezurishiki.view.fragment.editor.EditTweetFragment;
import com.seki.saezurishiki.view.fragment.list.ConversationFragment;
import com.seki.saezurishiki.view.fragment.list.FavoritesFragment;
import com.seki.saezurishiki.view.fragment.list.FollowerListFragment;
import com.seki.saezurishiki.view.fragment.list.FriendListFragment;
import com.seki.saezurishiki.view.fragment.list.SearchFragment;
import com.seki.saezurishiki.view.fragment.list.TweetListFragment;
import com.seki.saezurishiki.view.fragment.list.UserStreamTimeLineFragment;
import com.seki.saezurishiki.view.fragment.list.UserTweetFragment;
import com.seki.saezurishiki.view.fragment.list.UsersListFragment;

import twitter4j.HashtagEntity;
import twitter4j.User;

import static com.seki.saezurishiki.file.SharedPreferenceUtil.HOME;
import static com.seki.saezurishiki.file.SharedPreferenceUtil.REPLY;

public final class Fragments {

    private Fragments() {
        //no instance
    }

    public static Fragment createInjectHomeTimeLineFragment(final int tabPosition, final long userId) {
        final TweetListFragment fragment = UserStreamTimeLineFragment.getHomeTimeLine(tabPosition, HOME);
        new HomeTimeLinePresenter(fragment, userId, ModelContainer.getHomeTweetListModel());
        return fragment;
    }

    public static Fragment createInjectReplyTimeLineFragment(final int tabPosition, final long userId) {
        final TweetListFragment fragment = UserStreamTimeLineFragment.getReplyTimeLine(tabPosition, REPLY);
        new ReplyTimeLinePresenter(fragment, userId, ModelContainer.getReplyTweetListModel());
        return fragment;
    }

    public static Fragment createInjectFavoritesFragment(final long userId, final int favoriteCount) {
        final TweetListFragment fragment = FavoritesFragment.getInstance(favoriteCount);
        new FavoriteListPresenter(fragment, userId, ModelContainer.getFavoriteListModel());
        return fragment;
    }

    public static Fragment createInjectUserTweetFragment(final long userId, final int tweetCount) {
        final TweetListFragment fragment = UserTweetFragment.getInstance(tweetCount);
        new UserTweetListPresenter(fragment, userId, ModelContainer.getUserTweetListModel());
        return fragment;
    }

    public static Fragment createInjectConversationFragment(final long userID, final long selectedTweetID) {
        final TweetListFragment fragment = ConversationFragment.getInstance(selectedTweetID);
        new ConversationPresenter(fragment, userID, ModelContainer.getConversationModel());
        return fragment;
    }

    public static Fragment createInjectSearchFragment(final long userID, final String query) {
        final TweetListFragment fragment = SearchFragment.getInstance(query);
        new SearchPresenter(fragment, userID, ModelContainer.getSearchTweetModel());
        return  fragment;
    }

    public static Fragment newNormalEditor() {
        final EditTweetFragment fragment = EditTweetFragment.newNormalEditor();
        new TweetEditorPresenter(fragment);
        return fragment;
    }

    public static Fragment newEditorWithHashTag(HashtagEntity[] hashTagEntities) {
        final EditTweetFragment fragment = EditTweetFragment.newEditorWithHashTag(hashTagEntities);
        new TweetEditorPresenter(fragment);
        return fragment;
    }

   public static Fragment newReplyEditorFromStatus(TweetEntity tweet) {
        final EditTweetFragment fragment = EditTweetFragment.newReplyEditorFromStatus(tweet);
        new TweetEditorPresenter(fragment);
        return fragment;
   }

    public static Fragment newReplyEditorFromUser(User user) {
        final EditTweetFragment fragment = EditTweetFragment.newReplyEditorFromUser(user);
        new TweetEditorPresenter(fragment);
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
