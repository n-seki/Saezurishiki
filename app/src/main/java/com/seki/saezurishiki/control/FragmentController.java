package com.seki.saezurishiki.control;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.seki.saezurishiki.R;
import com.seki.saezurishiki.view.fragment.editor.DirectMessageFragment;
import com.seki.saezurishiki.view.fragment.editor.EditTweetFragment;
import com.seki.saezurishiki.view.fragment.list.FavoritesFragment;
import com.seki.saezurishiki.view.fragment.list.FollowerListFragment;
import com.seki.saezurishiki.view.fragment.list.FriendListFragment;
import com.seki.saezurishiki.view.fragment.SettingFragment;
import com.seki.saezurishiki.view.fragment.list.UserTweetFragment;

import org.jetbrains.annotations.Contract;

import twitter4j.User;

/**
 * Fragment管理クラス<br>
 * @author seki
 */
public final class FragmentController {


    public static final int FRAGMENT_ID_TWEET = 1;
    public static final int FRAGMENT_ID_FAVORITE = 2;
    public static final int FRAGMENT_ID_FRIEND = 3;
    public static final int FRAGMENT_ID_FOLLOWER = 4;
    public static final int FRAGMENT_ID_RECENTLY_DIRECT_MESSAGE = 5;
    public static final int FRAGMENT_ID_SETTING = 6;
    public static final int FRAGMENT_ID_TWEET_EDITOR = 7;
    public static final int FRAGMENT_ID_DIRECT_MESSAGE_EDITOR = 8;

    private final FragmentManager mFragmentManager;

    public FragmentController(FragmentManager fragmentManager) {
        mFragmentManager = fragmentManager;
    }


    public void add(Fragment fragment, int containerViewId) {
        mFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.activity_enter_in_anim, R.anim.activity_exit_out_anim, R.anim.activity_enter_in_anim, R.anim.activity_exit_out_anim)
                        .add(containerViewId, fragment)
                        .addToBackStack(null)
                        .commit();
    }



    public void replace(Fragment fragment, int containerViewId) {
        mFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.activity_enter_in_anim, R.anim.activity_exit_out_anim, R.anim.activity_enter_in_anim, R.anim.activity_exit_out_anim)
                        .replace(containerViewId, fragment)
                        .addToBackStack(null)
                        .commit();

        mFragmentManager.executePendingTransactions();
    }


    public Fragment createFragment(int fragmentId, User user) {
        if (fragmentId < 0) {
            throw new IllegalArgumentException("DrawerList item position is illegal! :" + fragmentId);
        }

        switch (fragmentId) {
            case FRAGMENT_ID_TWEET:
                return UserTweetFragment.getInstance(user.getId(), user.getStatusesCount());

            case FRAGMENT_ID_FAVORITE:
                return FavoritesFragment.getInstance(user.getId(), user.getFavouritesCount());

            case FRAGMENT_ID_FRIEND:
                return FriendListFragment.newInstance(user.getId(), user.getFriendsCount());

            case FRAGMENT_ID_FOLLOWER:
                return FollowerListFragment.newInstance(user.getId(), user.getFollowersCount());

//            case FRAGMENT_ID_RECENTLY_DIRECT_MESSAGE:
//                return RecentlyDirectMessageListFragment.getInstance();

            case FRAGMENT_ID_SETTING:
                return  SettingFragment.getInstance();

            case FRAGMENT_ID_TWEET_EDITOR:
                return EditTweetFragment.newReplyEditorFromUser(user);

            case FRAGMENT_ID_DIRECT_MESSAGE_EDITOR:
                return DirectMessageFragment.getInstance(user);

            default:
                throw new IllegalArgumentException("DrawerList item position is illegal! :" + fragmentId);
        }
    }


    @Contract(pure = true)
    public boolean hasFragment() {
        return mFragmentManager.getBackStackEntryCount() != 0;
    }


    public Fragment getFragment(int viewId) {
        return mFragmentManager.findFragmentById(viewId);
    }

    public void removeCurrentFragment(int viewId) {
        mFragmentManager.popBackStack(null, 0);
        mFragmentManager.executePendingTransactions();
    }


    public void removeAllFragment(int layout) {
        while(hasFragment()) {
            removeCurrentFragment(layout);
        }
    }
}
