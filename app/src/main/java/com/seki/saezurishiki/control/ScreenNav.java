package com.seki.saezurishiki.control;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.seki.saezurishiki.R;
import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.entity.UserEntity;
import com.seki.saezurishiki.view.activity.UserActivity;
import com.seki.saezurishiki.view.fragment.editor.EditTweetFragment;
import com.seki.saezurishiki.view.fragment.list.ConversationFragment;
import com.seki.saezurishiki.view.fragment.list.FavoritesFragment;
import com.seki.saezurishiki.view.fragment.list.FollowerListFragment;
import com.seki.saezurishiki.view.fragment.list.FriendListFragment;
import com.seki.saezurishiki.view.fragment.list.SearchFragment;
import com.seki.saezurishiki.view.fragment.list.UserTweetFragment;
import com.seki.saezurishiki.view.fragment.other.LicenseFragment;
import com.seki.saezurishiki.view.fragment.other.PictureFragment;
import com.seki.saezurishiki.view.fragment.other.SettingFragment;

import twitter4j.HashtagEntity;


public enum ScreenNav {

    USER_ACTIVITY {
        @Override
        public void transition(Context context, FragmentManager fragmentManager, int layoutId, Bundle args, Consumer<Fragment> callback) {
            final long userId = ScreenNav.getUserId(args);
            Intent intent = new Intent(context, UserActivity.class);
            intent.putExtra(UserActivity.USER_ID, userId);
            context.startActivity(intent);
        }

        @Override
        public int getTitleId() {
            return -1;
        }
    },

    SETTING {
        @Override
        public void transition(Context context, FragmentManager fragmentManager, int layoutId, Bundle args, Consumer<Fragment> callback) {
            Fragment fragment = SettingFragment.getInstance();
            FragmentController.add(fragmentManager, fragment, layoutId);
            callback.accept(fragment);
        }

        @Override
        public int getTitleId() {
            return R.string.title_setting;
        }
    },

    USER_TWEET {
        @Override
        public void transition(Context context, FragmentManager fragmentManager, int layoutId, Bundle args, Consumer<Fragment> callback) {
            final UserEntity user = (UserEntity)args.getSerializable(KEY_USER);
            Fragment fragment = UserTweetFragment.getInstance(user.getId(), user.getStatusesCount());
            FragmentController.add(fragmentManager, fragment, layoutId);
            callback.accept(fragment);
        }

        @Override
        public int getTitleId() {
            return R.string.title_user_tweet;
        }
    },

    FAVORITE {
        @Override
        public void transition(Context context, FragmentManager fragmentManager, int layoutId, Bundle args, Consumer<Fragment> callback) {
            final UserEntity user = (UserEntity)args.getSerializable(KEY_USER);
            Fragment fragment = FavoritesFragment.getInstance(user.getId(), user.getFavouritesCount());
            FragmentController.add(fragmentManager, fragment, layoutId);
            callback.accept(fragment);
        }

        @Override
        public int getTitleId() {
            return R.string.title_favorite;
        }
    },

    FOLLOW {
        @Override
        public void transition(Context context, FragmentManager fragmentManager, int layoutId, Bundle args, Consumer<Fragment> callback) {
            final UserEntity user = (UserEntity)args.getSerializable(KEY_USER);
            Fragment fragment =  FriendListFragment.newInstance(user.getId());
            FragmentController.add(fragmentManager, fragment, layoutId);
            callback.accept(fragment);
        }

        @Override
        public int getTitleId() {
            return R.string.title_friend;
        }
    },

    FOLLOWER {
        @Override
        public void transition(Context context, FragmentManager fragmentManager, int layoutId, Bundle args, Consumer<Fragment> callback) {
            final UserEntity user = (UserEntity)args.getSerializable(KEY_USER);
            Fragment fragment = FollowerListFragment.newInstance(user.getId());
            FragmentController.add(fragmentManager, fragment, layoutId);
            callback.accept(fragment);
        }

        @Override
        public int getTitleId() {
            return R.string.title_follower;
        }
    },

    TWEET_EDITOR {
        @Override
        public void transition(Context context, FragmentManager fragmentManager, int layoutId, Bundle args, Consumer<Fragment> callback) {
            final Fragment fragment = ScreenNav.createEditTweetFragment(args);
            FragmentController.add(fragmentManager, fragment, layoutId);
            callback.accept(fragment);
        }

        @Override
        public int getTitleId() {
            return R.string.title_edit_tweet;
        }
    },

    CONVERSATION {
        @Override
        public void transition(Context context, FragmentManager fragmentManager, int layoutId, Bundle args, Consumer<Fragment> callback) {
            final long userId = args.getLong(KEY_USER_ID);
            final long tweetId = args.getLong(KEY_TWEET_ID);
            final Fragment fragment = ConversationFragment.getInstance(userId, tweetId);
            FragmentController.add(fragmentManager, fragment, layoutId);
            callback.accept(fragment);
        }

        @Override
        public int getTitleId() {
            return R.string.title_conversation;
        }
    },

    LICENSE {
        @Override
        public void transition(Context context, FragmentManager fragmentManager, int layoutId, Bundle args, Consumer<Fragment> callback) {
            Fragment fragment = LicenseFragment.newInstance();
            FragmentController.add(fragmentManager, fragment, layoutId);
            callback.accept(fragment);
        }

        @Override
        public int getTitleId() {
            return R.string.title_license;
        }
    },

    SEARCH {
        @Override
        public void transition(Context context, FragmentManager fragmentManager, int layoutId, Bundle args, Consumer<Fragment> callback) {
            final UserEntity user = (UserEntity)args.getSerializable(KEY_USER);
            final String query = args.getString(KEY_QUERY);
            Fragment fragment = SearchFragment.getInstance(user.getId(), query);
            FragmentController.add(fragmentManager, fragment, layoutId);
            callback.accept(fragment);
        }

        @Override
        public int getTitleId() {
            return R.string.title_search;
        }
    },

    PICTURE {
        @Override
        public void transition(Context context, FragmentManager fragmentManager, int layoutId, Bundle args, Consumer<Fragment> callback) {
            final TweetEntity tweet = (TweetEntity)args.getSerializable(KEY_TWEET);
            final int position = args.getInt(KEY_POSITION);
            Fragment fragment = PictureFragment.getInstance(position, tweet);
            FragmentController.add(fragmentManager, fragment, layoutId);
            callback.accept(fragment);
        }

        @Override
        public int getTitleId() {
            return -1;
        }
    };


    private static Fragment createEditTweetFragment(Bundle args) {
        if (args.containsKey(KEY_HASHTAG)) {
            return EditTweetFragment.newEditorWithHashTag((HashtagEntity[])args.getSerializable(KEY_HASHTAG));
        }

        if (args.containsKey(KEY_TWEET)) {
            return EditTweetFragment.newReplyEditorFromStatus((TweetEntity)args.getSerializable(KEY_TWEET));
        }

        if (args.containsKey(KEY_USER)) {
            return EditTweetFragment.newReplyEditorFromUser((UserEntity)args.getSerializable(KEY_USER));
        }

        return EditTweetFragment.newNormalEditor();
    }

    private static long getUserId(Bundle args) {
        if (args.containsKey(KEY_USER)) {
            return ((UserEntity)args.getSerializable(KEY_USER)).getId();
        }

        return args.getLong(KEY_USER_ID);
    }


    public abstract void transition(Context context, FragmentManager fragmentManager, int layoutId, Bundle args, Consumer<Fragment> callback);
    public abstract int getTitleId();

    public static String KEY_USER = "user";
    public static String KEY_USER_ID = "user_id";
    public static String KEY_TWEET = "tweet";
    public static String KEY_TWEET_ID = "tweet_id";
    public static String KEY_QUERY = "query";
    public static String KEY_POSITION = "position";
    public static String KEY_HASHTAG = "hashTag";
}
