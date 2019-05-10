package com.seki.saezurishiki.view.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.seki.saezurishiki.R;
import com.seki.saezurishiki.control.ScreenNav;
import com.seki.saezurishiki.entity.UserEntity;
import com.seki.saezurishiki.view.customview.TwitterUserDrawerView;

/**
 * Navigation Drawerボタンアイテム用Adapter<br>
 * @author seki
 */
public class DrawerButtonListAdapter extends ArrayAdapter<DrawerButtonListAdapter.ButtonInfo> {

    public static class ButtonInfo {
        final int text;
        int count;
        final int icon;
        final int position;
        public final ScreenNav screenNav;
        ButtonInfo(int text, int count, int icon, int position, ScreenNav screenNav) {
            this.text = text;
            this.count = count;
            this.icon = icon;
            this.position = position;
            this.screenNav = screenNav;
        }
    }


    private static class ButtonViewHolder {
        final View icon;
        final TextView text;
        final TextView count;

        ButtonViewHolder(View view) {
            this.icon = view.findViewById(R.id.button_icon);
            this.text = (TextView)view.findViewById(R.id.button_text);
            this.count = (TextView)view.findViewById(R.id.button_count);
        }
    }


    private LayoutInflater mLayoutInflater;
    private int mTheme;


    public DrawerButtonListAdapter(Context context , int resourceId, int theme) {
        super(context, resourceId);
        mLayoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mTheme = theme;
    }


    @Override
    @NonNull
    public View getView(int position, View convertedView, ViewGroup parent) {
        ButtonViewHolder viewHolder;

        if (convertedView == null) {
            convertedView = mLayoutInflater.inflate(R.layout.drawer_list_button, null);
            viewHolder = new ButtonViewHolder(convertedView);
            convertedView.setTag(viewHolder);
        } else {
            viewHolder = (ButtonViewHolder)convertedView.getTag();
        }

        ButtonInfo info = this.getItem(position);

        viewHolder.icon.setBackgroundResource(info.icon);
        viewHolder.text.setText(info.text);

        if (info.count == -1) {
            viewHolder.count.setText("");
        } else {
            viewHolder.count.setText(String.valueOf(info.count));
        }

        convertedView.setTag(viewHolder);

        return convertedView;
    }


    public void setLoginUserItem(UserEntity user) {

        this.setUserItem(user);

        add(new ButtonInfoBuilder()
                .text(R.string.show_biography)
                .nextScreen(ScreenNav.USER_ACTIVITY)
                .position(TwitterUserDrawerView.SHOW_USER)
                .icon(mTheme == R.style.AppTheme_Dark ? R.drawable.drawer_show_user_dark : R.drawable.drawer_show_user_light)
                .build());

        add(new ButtonInfoBuilder()
                .text(R.string.action_settings)
                .nextScreen(ScreenNav.SETTING)
                .position(TwitterUserDrawerView.SETTING)
                .icon(mTheme == R.style.AppTheme_Dark ? R.drawable.drawer_settting_dark : R.drawable.drawer_settting_light)
                .build());
    }


    public void setUserItem(UserEntity user) {
        add(new ButtonInfoBuilder()
                .text(R.string.tweet)
                .count(user.getStatusesCount())
                .nextScreen(ScreenNav.USER_TWEET)
                .position(TwitterUserDrawerView.TWEET)
                .icon(mTheme == R.style.AppTheme_Dark ? R.drawable.drawer_tweet_dark :R.drawable.drawer_tweet_light)
                .build());

        add(new ButtonInfoBuilder()
                .text(R.string.favorite)
                .count(user.getFavouritesCount())
                .nextScreen(ScreenNav.FAVORITE)
                .position(TwitterUserDrawerView.FAVORITE)
                .icon(mTheme == R.style.AppTheme_Dark ? R.drawable.drawer_favorite_dark : R.drawable.drawer_favorite_light)
                .build());

        add(new ButtonInfoBuilder()
                .text(R.string.follow)
                .count(user.getFriendsCount())
                .nextScreen(ScreenNav.FOLLOW)
                .position(TwitterUserDrawerView.FOLLOW)
                .icon(mTheme == R.style.AppTheme_Dark ? R.drawable.drawer_friend_follower_dark :R.drawable.drawer_friend_follower_light)
                .build());

        add(new ButtonInfoBuilder()
                .text(R.string.follower)
                .count(user.getFollowersCount())
                .position(TwitterUserDrawerView.FOLLOWER)
                .nextScreen(ScreenNav.FOLLOWER)
                .icon(mTheme == R.style.AppTheme_Dark ? R.drawable.drawer_friend_follower_dark :R.drawable.drawer_friend_follower_light)
                .build());
    }


    public void incrementCount(int buttonId) {
        ButtonInfo info = getButtonItem(buttonId);

        if (info.count == -1) {
            info.count = 0;
        }

        info.count++;
        notifyDataSetChanged();
    }



    public void decrementCount(int buttonId) {
        ButtonInfo info = getButtonItem(buttonId);
        info.count--;
        notifyDataSetChanged();
    }


    public void clearCount(int buttonId) {
        ButtonInfo info = getButtonItem(buttonId);
        info.count = -1;
        notifyDataSetChanged();
    }



    private ButtonInfo getButtonItem(int id) {
        for ( int count = 0; count < getCount(); count++ ) {
            ButtonInfo item = getItem(count);
            if ( item.position == id ) {
                return item;
            }
        }

        throw new AssertionError();
    }

    @Override
    @NonNull
    public DrawerButtonListAdapter.ButtonInfo getItem(int position) {
        final DrawerButtonListAdapter.ButtonInfo item = super.getItem(position);
        if (item == null) {
            throw new NullPointerException("item is null, position : " + position);
        }
        return item;
    }



    private static class ButtonInfoBuilder {
        int text = -1;
        int count = -1;
        int position = -1;
        int icon;
        ScreenNav screenNav = null;

        ButtonInfoBuilder() {
        }
        public ButtonInfoBuilder text(int text) {
            this.text = text;
            return this;
        }
        public ButtonInfoBuilder count(int count) {
            this.count = count;
            return this;
        }
        public ButtonInfoBuilder icon(int icon) {
            this.icon = icon;
            return this;
        }
        public ButtonInfoBuilder position(int position) {
            this.position = position;
            return this;
        }
        ButtonInfoBuilder nextScreen(ScreenNav screenNav) {
            this.screenNav = screenNav;
            return this;
        }
        public ButtonInfo build() {
            if (this.text == -1) {
                throw new IllegalArgumentException("Text should be set");
            }
            if (this.icon == -1) {
                throw new IllegalArgumentException("Icon should be set");
            }

            final ButtonInfo info = new ButtonInfo(this.text, this.count, this.icon, this.position, this.screenNav);
            return info;
        }
    }
}
