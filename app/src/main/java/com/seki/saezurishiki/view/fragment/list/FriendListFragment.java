package com.seki.saezurishiki.view.fragment.list;

import com.seki.saezurishiki.network.twitter.AsyncTwitterTask;

import twitter4j.PagableResponseList;
import twitter4j.User;

/**
 * Created by seki on 2016/07/18.
 */
public class FriendListFragment extends UsersListFragment {


    public static UsersListFragment newInstance(long userId, int count) {
        UsersListFragment fragment = new FriendListFragment();
        setArgument(fragment, userId, count);
        return fragment;
    }

    @Override
    AsyncTwitterTask.AsyncTask<PagableResponseList<User>> getTask() {
        return this.twitterTask.getFriendsListTask(mUserId, mNextCursor);
    }



    @Override
    public String toString() {
        return "Friends";
    }


    @Override
    public void onFollow(User source, User followedUser) {
        //do nothing
    }

    @Override
    public void onRemove(User source, User unfollowedUser) {
        //do nothing
    }

    @Override
    public void onBlock(User source, User blockedUser) {

    }

    @Override
    public void onUnblock(User source, User unblockedUser) {

    }
}
