package com.seki.saezurishiki.view.fragment;


import android.support.v4.app.Fragment;

import com.seki.saezurishiki.entity.UserEntity;
import com.seki.saezurishiki.model.impl.ModelContainer;
import com.seki.saezurishiki.presenter.list.TweetListPresenter;
import com.seki.saezurishiki.view.fragment.list.FavoritesFragment;
import com.seki.saezurishiki.view.fragment.list.TweetListFragment;
import com.seki.saezurishiki.view.fragment.list.UserStreamTimeLineFragment;
import com.seki.saezurishiki.view.fragment.list.UserTweetFragment;

public final class Fragments {

    private Fragments() {
        //no instance
    }

    public static Fragment createInjectHomeTimeLineFragment(final int tabPosition, UserEntity owner) {
        final TweetListFragment fragment = UserStreamTimeLineFragment.getHomeTimeLine(tabPosition);
        new TweetListPresenter(fragment, owner, ModelContainer.getHomeTweetListModel());
        return fragment;
    }

    public static Fragment createInjectReplyTimeLineFragment(final int tabPosition, UserEntity owner) {
        final TweetListFragment fragment = UserStreamTimeLineFragment.getReplyTimeLine(tabPosition);
        new TweetListPresenter(fragment, owner, ModelContainer.getReplyTweetListModel());
        return fragment;
    }

    public static Fragment createInjectFavoritesFragment(final UserEntity owner, final int favoriteCount) {
        final TweetListFragment fragment = FavoritesFragment.getInstance(owner.getId(), favoriteCount);
        new TweetListPresenter(fragment, owner, ModelContainer.getFavoriteListModel());
        return fragment;
    }

    public static Fragment createInjectUserTweetFragment(final UserEntity owner, final int tweetCount) {
        final TweetListFragment fragment = UserTweetFragment.getInstance(owner.getId(), tweetCount);
        new TweetListPresenter(fragment, owner, ModelContainer.getUserTweetListModel());
        return fragment;
    }
}
