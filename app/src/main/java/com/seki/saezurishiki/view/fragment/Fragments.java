package com.seki.saezurishiki.view.fragment;


import android.support.v4.app.Fragment;

import com.seki.saezurishiki.model.impl.ModelContainer;
import com.seki.saezurishiki.presenter.list.FavoriteListPresenter;
import com.seki.saezurishiki.presenter.list.HomeTimeLinePresenter;
import com.seki.saezurishiki.presenter.list.ReplyTimeLinePresenter;
import com.seki.saezurishiki.presenter.list.UserTweetListPresenter;
import com.seki.saezurishiki.view.fragment.list.FavoritesFragment;
import com.seki.saezurishiki.view.fragment.list.TweetListFragment;
import com.seki.saezurishiki.view.fragment.list.UserStreamTimeLineFragment;
import com.seki.saezurishiki.view.fragment.list.UserTweetFragment;

public final class Fragments {

    private Fragments() {
        //no instance
    }

    public static Fragment createInjectHomeTimeLineFragment(final int tabPosition, final long userId) {
        final TweetListFragment fragment = UserStreamTimeLineFragment.getHomeTimeLine(tabPosition);
        new HomeTimeLinePresenter(fragment, userId, ModelContainer.getHomeTweetListModel());
        return fragment;
    }

    public static Fragment createInjectReplyTimeLineFragment(final int tabPosition, final long userId) {
        final TweetListFragment fragment = UserStreamTimeLineFragment.getReplyTimeLine(tabPosition);
        new ReplyTimeLinePresenter(fragment, userId, ModelContainer.getReplyTweetListModel());
        return fragment;
    }

    public static Fragment createInjectFavoritesFragment(final long userId, final int favoriteCount) {
        final TweetListFragment fragment = FavoritesFragment.getInstance(userId, favoriteCount);
        new FavoriteListPresenter(fragment, userId, ModelContainer.getFavoriteListModel());
        return fragment;
    }

    public static Fragment createInjectUserTweetFragment(final long userId, final int tweetCount) {
        final TweetListFragment fragment = UserTweetFragment.getInstance(userId, tweetCount);
        new UserTweetListPresenter(fragment, userId, ModelContainer.getUserTweetListModel());
        return fragment;
    }
}
