package com.seki.saezurishiki.view.customview;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.util.AttributeSet;

import com.seki.saezurishiki.view.control.RequestTabState;

import static com.seki.saezurishiki.control.TimeLinePager.POSITION_HOME;
import static com.seki.saezurishiki.control.TimeLinePager.POSITION_MESSAGE;
import static com.seki.saezurishiki.control.TimeLinePager.POSITION_REPLY;
import static com.seki.saezurishiki.control.UIControlUtil.getTabBackground;
import static com.seki.saezurishiki.control.UIControlUtil.getTabUnreadBackground;

/**
 *
 */
public class NotificationTabLayout extends TabLayout {

    private int theme;

    public NotificationTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NotificationTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public NotificationTabLayout(Context context) {
        super(context);
    }


    public void setup(int theme) {
        getTabAt(POSITION_HOME).setIcon(getTabBackground(POSITION_HOME, theme));
        getTabAt(POSITION_REPLY).setIcon(getTabBackground(POSITION_REPLY, theme));
        getTabAt(POSITION_MESSAGE).setIcon(getTabBackground(POSITION_MESSAGE, theme));

        this.theme = theme;
    }


    public void onRequestChangeTab(int position, RequestTabState state) {
        final TabLayout.Tab tab = getTabAt(position);
        final boolean isUnreadTab = isUnreadTab(tab);

        if (state.hasUnreadItem() && !isUnreadTab) {
            tab.setIcon(getTabUnreadBackground(position, this.theme));
            tab.setTag(true);
            return;
        }

        if (!state.hasUnreadItem() && isUnreadTab) {
            tab.setIcon(getTabBackground(position, theme));
            tab.setTag(false);
        }
    }


    private static boolean isUnreadTab(TabLayout.Tab tab) {
        if (tab.getTag() instanceof Boolean) {
            return (Boolean)tab.getTag();
        }

        return false;
    }

}
