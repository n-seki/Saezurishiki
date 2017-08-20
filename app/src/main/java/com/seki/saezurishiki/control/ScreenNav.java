package com.seki.saezurishiki.control;

import android.content.Context;
import android.content.Intent;
import android.opengl.EGLObjectHandle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.entity.UserEntity;
import com.seki.saezurishiki.view.activity.UserActivity;
import com.seki.saezurishiki.view.fragment.Fragments;
import com.seki.saezurishiki.view.fragment.other.LicenseFragment;
import com.seki.saezurishiki.view.fragment.other.PictureFragment;
import com.seki.saezurishiki.view.fragment.other.SettingFragment;

import java.util.Map;

import twitter4j.HashtagEntity;


public enum ScreenNav {
    USER_ACTIVITY {
        @Override
        public void transition(Context context, FragmentManager fragmentManager, int layoutId, Map<String, Object> args, Consumer<Fragment> callback) {
            final long userId = ScreenNav.getUserId(args);
            Intent intent = new Intent(context, UserActivity.class);
            intent.putExtra(UserActivity.USER_ID, userId);
            context.startActivity(intent);
        }
    },

    SETTING {
        @Override
        public void transition(Context context, FragmentManager fragmentManager, int layoutId, Map<String, Object> args, Consumer<Fragment> callback) {
            Fragment fragment = SettingFragment.getInstance();
            FragmentController.add(fragmentManager, fragment, layoutId);
            callback.accept(fragment);
        }
    },

    USER_TWEET {
        @Override
        public void transition(Context context, FragmentManager fragmentManager, int layoutId, Map<String, Object> args, Consumer<Fragment> callback) {
            final UserEntity user = (UserEntity)args.get("user");
            Fragment fragment = Fragments.createInjectUserTweetFragment(user.getId(), user.getStatusesCount());
            FragmentController.add(fragmentManager, fragment, layoutId);
            callback.accept(fragment);
        }
    },

    FAVORITE {
        @Override
        public void transition(Context context, FragmentManager fragmentManager, int layoutId, Map<String, Object> args, Consumer<Fragment> callback) {
            final UserEntity user = (UserEntity)args.get("user");
            Fragment fragment = Fragments.createInjectFavoritesFragment(user.getId(), user.getFavouritesCount());
            FragmentController.add(fragmentManager, fragment, layoutId);
            callback.accept(fragment);
        }
    },

    FOLLOW {
        @Override
        public void transition(Context context, FragmentManager fragmentManager, int layoutId, Map<String, Object> args, Consumer<Fragment> callback) {
            final UserEntity user = (UserEntity)args.get("user");
            Fragment fragment = Fragments.newFriendListFragment(user.getId());
            FragmentController.add(fragmentManager, fragment, layoutId);
            callback.accept(fragment);
        }
    },

    FOLLOWER {
        @Override
        public void transition(Context context, FragmentManager fragmentManager, int layoutId, Map<String, Object> args, Consumer<Fragment> callback) {
            final UserEntity user = (UserEntity)args.get("user");
            Fragment fragment = Fragments.newFollowerListFragment(user.getId());
            FragmentController.add(fragmentManager, fragment, layoutId);
            callback.accept(fragment);
        }
    },

    TWEET_EDITOR {
        @Override
        public void transition(Context context, FragmentManager fragmentManager, int layoutId, Map<String, Object> args, Consumer<Fragment> callback) {
            final Fragment fragment = ScreenNav.createEditTweetFragment(args);
            FragmentController.replace(fragmentManager, fragment, layoutId);
            callback.accept(fragment);
        }
    },

    CONVERSATION {
        @Override
        public void transition(Context context, FragmentManager fragmentManager, int layoutId, Map<String, Object> args, Consumer<Fragment> callback) {
            final long userId = (Long)args.get("userId");
            final long tweetId = (Long)args.get("tweetId");
            final Fragment fragment = Fragments.createInjectConversationFragment(userId, tweetId);
            FragmentController.replace(fragmentManager, fragment, layoutId);
            callback.accept(fragment);
        }
    },

    MESSAGE_EDITOR {
        @Override
        public void transition(Context context, FragmentManager fragmentManager, int layoutId, Map<String, Object> args, Consumer<Fragment> callback) {
            final long userId = ScreenNav.getUserId(args);
            Fragment fragment = Fragments.newDirectMessageEditor(userId);
            FragmentController.replace(fragmentManager, fragment, layoutId);
            callback.accept(fragment);
        }
    },

    LICENSE {
        @Override
        public void transition(Context context, FragmentManager fragmentManager, int layoutId, Map<String, Object> args, Consumer<Fragment> callback) {
            Fragment fragment = LicenseFragment.newInstance();
            FragmentController.add(fragmentManager, fragment, layoutId);
            callback.accept(fragment);
        }
    },

    SEARCH {
        @Override
        public void transition(Context context, FragmentManager fragmentManager, int layoutId, Map<String, Object> args, Consumer<Fragment> callback) {
            final UserEntity user = (UserEntity)args.get("user");
            final String query = (String)args.get("query");
            Fragment fragment = Fragments.createInjectSearchFragment(user.getId(), query);
            FragmentController.add(fragmentManager, fragment, layoutId);
            callback.accept(fragment);
        }
    },

    PICTURE {
        @Override
        public void transition(Context context, FragmentManager fragmentManager, int layoutId, Map<String, Object> args, Consumer<Fragment> callback) {
            final TweetEntity tweet = (TweetEntity)args.get("tweet");
            final int position = (Integer)args.get("position");
            Fragment fragment = PictureFragment.getInstance(position, tweet);
            FragmentController.add(fragmentManager, fragment, layoutId);
            callback.accept(fragment);
        }
    };


    private static Fragment createEditTweetFragment(Map<String, Object> args) {
        if (args.containsKey("hashTag")) {
            return Fragments.newEditorWithHashTag((HashtagEntity[])args.get("hashTag"));
        }

        if (args.containsKey("tweet")) {
            return Fragments.newReplyEditorFromStatus((TweetEntity)args.get("tweet"));
        }

        if (args.containsKey("user")) {
            return Fragments.newReplyEditorFromUser((UserEntity)args.get("user"));
        }

        return Fragments.newNormalEditor();
    }

    private static long getUserId(Map<String, Object> args) {
        if (args.containsKey("user")) {
            return UserEntity.class.cast(args.get("user")).getId();
        }

        return (Long)args.get("userId");
    }


    public abstract void transition(Context context, FragmentManager fragmentManager, int layoutId, Map<String, Object> args, Consumer<Fragment> callback);


}
