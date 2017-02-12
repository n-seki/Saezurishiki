package com.seki.saezurishiki.control;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

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

    public TimeLinePager(FragmentManager fragmentManager) {
        super(fragmentManager);
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
                fragment = UserStreamTimeLineFragment.getHomeTimeLine(POSITION_HOME);
                break;

            case POSITION_REPLY:
                fragment = UserStreamTimeLineFragment.getReplyTimeLine(POSITION_REPLY);
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
