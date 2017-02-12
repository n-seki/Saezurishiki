package com.seki.saezurishiki.view.customview;

import android.content.Context;
import android.support.design.widget.NavigationView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.seki.saezurishiki.R;
import com.seki.saezurishiki.view.adapter.DrawerButtonListAdapter;
import com.seki.saezurishiki.control.UIControlUtil;
import com.squareup.picasso.Picasso;

import twitter4j.User;

/**
 *
 */
public class TwitterUserDrawerView extends NavigationView {

    private DrawerButtonListAdapter buttonListAdapter;

    public TwitterUserDrawerView(Context context) {
        super(context);
    }

    public TwitterUserDrawerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TwitterUserDrawerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    public synchronized void updateUser(User user) {
        if (user == null) {
            throw new NullPointerException("User is null!");
        }

        Picasso.with(getContext()).load(user.getProfileBannerURL()).into((ImageView) findViewById(R.id.bio_header_icon));
        Picasso.with(getContext()).load(user.getBiggerProfileImageURL()).into((ImageView) findViewById(R.id.bio_icon));
        ((TextView) findViewById(R.id.bio_name)).setText(user.getName());
        String userNameText = UIControlUtil.addAtMark(user.getScreenName());
        ((TextView) findViewById(R.id.bio_screen_name)).setText(userNameText);

        ImageView lockIcon = (ImageView) findViewById(R.id.bio_lock_icon);
        if (user.isProtected()) {
            lockIcon.setVisibility(View.VISIBLE);
        } else {
            lockIcon.setVisibility(View.INVISIBLE);
        }

        updateButtonAdapter(user);
    }

    public void setAdapter(DrawerButtonListAdapter adapter) {
        this.buttonListAdapter = adapter;
    }

    private void updateButtonAdapter(User user) {
        if (!buttonListAdapter.isEmpty()) {
            buttonListAdapter.clear();
        }

        this.buttonListAdapter.setLoginUserItem(user);

        ListView drawerList = (ListView)findViewById(R.id.drawer_list);
        drawerList.setAdapter(buttonListAdapter);
    }


    public void incrementCount(int buttonId) {
        this.buttonListAdapter.incrementCount(buttonId);
    }

    public void decrementCount(int buttonId) {
        this.buttonListAdapter.decrementCount(buttonId);
    }

    public void clearCount(int buttonId) {
        this.buttonListAdapter.clearCount(buttonId);
    }

    public void setOnListButtonClickListener(AdapterView.OnItemClickListener drawerItemClickListener) {
        ListView drawerList = (ListView)findViewById(R.id.drawer_list);
        drawerList.setOnItemClickListener(drawerItemClickListener);
    }


    public DrawerButtonListAdapter.ButtonInfo getButtonAtPosition(int position) {
        ListView buttonList = (ListView)findViewById(R.id.drawer_list);
        return (DrawerButtonListAdapter.ButtonInfo)buttonList.getItemAtPosition(position);
    }
}
