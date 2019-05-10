package com.seki.saezurishiki.view.fragment.list;

import android.os.Bundle;

import com.seki.saezurishiki.application.SaezurishikiApp;
import com.seki.saezurishiki.view.FriendListModule;

public class FriendListFragment extends UsersListFragment {

    public static UsersListFragment newInstance(long userId) {
        Bundle args = new Bundle();
        args.putLong(USER_ID, userId);
        UsersListFragment fragment = new FriendListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        long userId = getArguments().getLong(USER_ID);
        SaezurishikiApp.mApplicationComponent.friendComponentBuilder()
                .listOwnerId(userId)
                .presenterView(this)
                .module(new FriendListModule())
                .build()
                .inject(this);
    }
}
