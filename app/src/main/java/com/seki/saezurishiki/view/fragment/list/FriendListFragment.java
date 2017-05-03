package com.seki.saezurishiki.view.fragment.list;

public class FriendListFragment extends UsersListFragment {


    public static UsersListFragment newInstance() {
        return new FriendListFragment();
    }


    @Override
    public String toString() {
        return "Friends";
    }
}
