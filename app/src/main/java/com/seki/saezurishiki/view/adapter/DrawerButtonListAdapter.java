package com.seki.saezurishiki.view.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.seki.saezurishiki.R;
import com.seki.saezurishiki.entity.UserEntity;

import static com.seki.saezurishiki.control.FragmentController.FRAGMENT_ID_FAVORITE;
import static com.seki.saezurishiki.control.FragmentController.FRAGMENT_ID_FOLLOWER;
import static com.seki.saezurishiki.control.FragmentController.FRAGMENT_ID_FRIEND;
import static com.seki.saezurishiki.control.FragmentController.FRAGMENT_ID_SETTING;
import static com.seki.saezurishiki.control.FragmentController.FRAGMENT_ID_TWEET;
import static com.seki.saezurishiki.view.activity.UserActivity.SHOW_ACTIVITY;

/**
 * Navigation Drawerボタンアイテム用Adapter<br>
 * @author seki
 */
public class DrawerButtonListAdapter extends ArrayAdapter<DrawerButtonListAdapter.ButtonInfo> {

    public static class ButtonInfo {
        int text;
        int count;
        int icon;
        int fragmentID;
        ButtonInfo() {}
        public int getAction() {
            return this.fragmentID;
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
                .nextScreen(SHOW_ACTIVITY)
                .icon(mTheme == R.style.AppTheme_Dark ? R.drawable.drawer_show_user_dark : R.drawable.drawer_show_user_light)
                .build());

        add(new ButtonInfoBuilder()
                .text(R.string.action_settings)
                .nextScreen(FRAGMENT_ID_SETTING)
                .icon(mTheme == R.style.AppTheme_Dark ? R.drawable.drawer_settting_dark : R.drawable.drawer_settting_light)
                .build());
    }


    public void setUserItem(UserEntity user) {
        add(new ButtonInfoBuilder()
                .text(R.string.tweet)
                .count(user.getStatusesCount())
                .nextScreen(FRAGMENT_ID_TWEET)
                .icon(mTheme == R.style.AppTheme_Dark ? R.drawable.drawer_tweet_dark :R.drawable.drawer_tweet_light)
                .build());

        add(new ButtonInfoBuilder()
                .text(R.string.favorite)
                .count(user.getFavouritesCount())
                .nextScreen(FRAGMENT_ID_FAVORITE)
                .icon(mTheme == R.style.AppTheme_Dark ? R.drawable.drawer_favorite_dark : R.drawable.drawer_favorite_light)
                .build());

        add(new ButtonInfoBuilder()
                .text(R.string.follow)
                .count(user.getFriendsCount())
                .nextScreen(FRAGMENT_ID_FRIEND)
                .icon(mTheme == R.style.AppTheme_Dark ? R.drawable.drawer_friend_follower_dark :R.drawable.drawer_friend_follower_light)
                .build());

        add(new ButtonInfoBuilder()
                .text(R.string.follower)
                .count(user.getFollowersCount())
                .nextScreen(FRAGMENT_ID_FOLLOWER)
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
            if ( item.getAction() == id ) {
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
        int icon;
        int nextScreen = -1;

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
        ButtonInfoBuilder nextScreen(int screen) {
            this.nextScreen = screen;
            return this;
        }
        public ButtonInfo build() {
            ButtonInfo info = new ButtonInfo();
            if (this.text == -1) {
                throw new IllegalArgumentException("Text should be set");
            }
            info.text = this.text;
            info.count = this.count;
            if (this.icon == -1) {
                throw new IllegalArgumentException("Icon should be set");
            }
            info.icon = this.icon;
            info.fragmentID = this.nextScreen;
            return info;
        }
    }
}
