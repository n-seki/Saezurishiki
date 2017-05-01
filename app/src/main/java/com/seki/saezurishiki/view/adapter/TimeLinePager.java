package com.seki.saezurishiki.view.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.seki.saezurishiki.entity.UserEntity;
import com.seki.saezurishiki.view.fragment.Fragments;
import com.seki.saezurishiki.view.fragment.list.RecentlyDirectMessageListFragment;
import com.seki.saezurishiki.view.fragment.list.UserStreamTimeLineFragment;


/**
 * home,reply用Pagerクラス<br>
 * @author seki
 */
public class TimeLinePager extends FragmentPagerAdapter {

    public static final int POSITION_HOME = 0;
    public static final int POSITION_REPLY = 1;
    public static final int POSITION_MESSAGE = 2;

    private final long loginUserId;

    public TimeLinePager(FragmentManager fragmentManager, long loginUserId) {
        super(fragmentManager);

        this.loginUserId = loginUserId;
    }

    @Override
    public int getCount() {
        return 3;
    }


    @Override
    public Fragment getItem(int position) {
        Fragment fragment;

        switch (position) {
            case POSITION_HOME:
                fragment = Fragments.createInjectHomeTimeLineFragment(POSITION_HOME, this.loginUserId);
                break;

            case POSITION_REPLY:
                fragment = Fragments.createInjectReplyTimeLineFragment(POSITION_REPLY, this.loginUserId);
                break;

            case POSITION_MESSAGE:
                fragment = RecentlyDirectMessageListFragment.getInstance(POSITION_MESSAGE);
                break;

            default :
                throw new IllegalStateException("position is " + position );
        }

        return  fragment;
    }
}
