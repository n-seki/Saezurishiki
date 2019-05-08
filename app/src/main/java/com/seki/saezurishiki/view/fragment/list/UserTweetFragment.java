package com.seki.saezurishiki.view.fragment.list;

import android.os.Bundle;

import com.seki.saezurishiki.application.SaezurishikiApp;
import com.seki.saezurishiki.presenter.list.TweetListPresenter;
import com.seki.saezurishiki.view.fragment.UserTweetModule;
import com.seki.saezurishiki.view.fragment.util.DataType;

import javax.inject.Inject;

/**
 * Tweet一覧表示Fragment<br>
 * ユーザーのTweetを時系列順に表示します
 * @author seki
 */
public class UserTweetFragment extends TweetListFragment {

    int mCount;

    @Inject
    TweetListPresenter presenter;

    public static TweetListFragment getInstance(long userId, int count) {
        TweetListFragment fragment = new UserTweetFragment();
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

        SaezurishikiApp.mApplicationComponent.userTweetComponentBuilder()
                .listOwnerId(listOwnerId)
                .presenterView(this)
                .module(new UserTweetModule())
                .build()
                .inject(this);
    }

    @Override
    public String toString() {
        return "Tweet";
    }

    @Override
    public TweetListPresenter getPresenter() {
        return presenter;
    }
}
