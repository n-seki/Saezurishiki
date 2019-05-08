package com.seki.saezurishiki.view.fragment.list;

import android.os.Bundle;

import com.seki.saezurishiki.application.SaezurishikiApp;
import com.seki.saezurishiki.presenter.list.TweetListPresenter;
import com.seki.saezurishiki.view.fragment.FavoriteModule;
import com.seki.saezurishiki.view.fragment.util.DataType;

import javax.inject.Inject;

/**
 * お気に入りTweet表示Fragment<br>
 * ユーザーのお気に入りTweetを時系列順に表示します
 * @author seki
 */
public class FavoritesFragment extends TweetListFragment {

    int mCount;

    @Inject
    TweetListPresenter presenter;

    public static TweetListFragment getInstance(long userId, int count) {
        TweetListFragment fragment = new FavoritesFragment();
        Bundle data = new Bundle();
        data.putLong(USER_ID, userId);
        data.putInt(DataType.COUNT, count);
        fragment.setArguments(data);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle data = this.getArguments();

        if ( data == null ) {
            throw new IllegalStateException("Argument is null");
        }

        mCount = data.getInt(DataType.COUNT);

        long listOwnerId = data.getLong(USER_ID);

        SaezurishikiApp.mApplicationComponent.favoriteComponentBuilder()
                .listOwnerId(listOwnerId)
                .presenterView(this)
                .module(new FavoriteModule())
                .build()
                .inject(this);
    }

    @Override
    public String toString() {
        return "Favorite";
    }

    @Override
    public TweetListPresenter getPresenter() {
        return presenter;
    }
}
