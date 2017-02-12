package com.seki.saezurishiki.view.fragment.list;

import com.seki.saezurishiki.network.twitter.AsyncTwitterTask;

import twitter4j.PagableResponseList;
import twitter4j.User;


public class FollowerListFragment extends UsersListFragment {


    public static UsersListFragment newInstance(long userId, int count) {
        UsersListFragment fragment = new FollowerListFragment();
        setArgument(fragment, userId, count);
        return fragment;
    }


    @Override
    AsyncTwitterTask.AsyncTask<PagableResponseList<User>> getTask() {
        return this.twitterTask.getFollowersListTask(mUserId, mNextCursor);
    }

    @Override
    public String toString() {
        return "Follower";
    }

    @Override
    public void onFollow(User source, User followedUser) {
        if (source.getId() != twitterAccount.getLoginUserId()) {
            return;
        }

        this.mAdapter.add(followedUser);
    }

    @Override
    public void onRemove(User source, User unfollowedUser) {
        if (source.getId() != twitterAccount.getLoginUserId()) {
            return;
        }

        this.mAdapter.remove(unfollowedUser);
    }

    @Override
    public void onBlock(User source, User blockedUser) {

    }

    @Override
    public void onUnblock(User source, User unblockedUser) {

    }
}
