package com.seki.saezurishiki.view.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.seki.saezurishiki.view.fragment.list.HomeTimeLineFragment;
import com.seki.saezurishiki.view.fragment.list.ReplyTimeLineFragment;

import static com.seki.saezurishiki.file.SharedPreferenceUtil.HOME;
import static com.seki.saezurishiki.file.SharedPreferenceUtil.REPLY;


/**
 * home,reply用Pagerクラス<br>
 * @author seki
 */
public class TimeLinePager extends FragmentPagerAdapter {

    public static final int POSITION_HOME = 0;
    public static final int POSITION_REPLY = 1;

    private final long loginUserId;

    public TimeLinePager(FragmentManager fragmentManager, long loginUserId) {
        super(fragmentManager);
        this.loginUserId = loginUserId;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment;

        switch (position) {
            case POSITION_HOME:
                fragment = HomeTimeLineFragment.getInstance(this.loginUserId, POSITION_HOME, HOME);
                break;

            case POSITION_REPLY:
                fragment = ReplyTimeLineFragment.getInstance(this.loginUserId, POSITION_REPLY, REPLY);
                break;

            default:
                throw new IllegalStateException("position is " + position );
        }

        return  fragment;
    }
}
