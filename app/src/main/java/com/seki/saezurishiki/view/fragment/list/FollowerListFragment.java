package com.seki.saezurishiki.view.fragment.list;

import android.os.Bundle;

import com.seki.saezurishiki.application.SaezurishikiApp;
import com.seki.saezurishiki.view.FollowerListModule;

public class FollowerListFragment extends UsersListFragment {

    public static UsersListFragment newInstance(long userId) {
        Bundle args = new Bundle();
        args.putLong(USER_ID, userId);
        UsersListFragment fragment = new FollowerListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        long userId = getArguments().getLong(USER_ID);
        SaezurishikiApp.mApplicationComponent.followerComponentBuilder()
                .listOwnerId(userId)
                .presenterView(this)
                .module(new FollowerListModule())
                .build()
                .inject(this);
    }
}
