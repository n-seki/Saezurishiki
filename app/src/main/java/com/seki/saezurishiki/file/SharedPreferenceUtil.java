package com.seki.saezurishiki.file;

import android.content.Context;
import android.content.SharedPreferences;


public final class SharedPreferenceUtil {


    private static final String LatestID_PREF = "LatestSeenId";

    public static final String HOME = "home";
    public static final String REPLY = "reply";

    public static void writeLatestID(Context context, String key, long itemId) {
        SharedPreferences pref = context.getSharedPreferences(LatestID_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putLong(key, itemId);
        editor.apply();
    }

    public static long readLatestID(Context context, String key) {
        SharedPreferences preferences = context.getSharedPreferences("LatestSeenId", Context.MODE_PRIVATE);
        return preferences.getLong(key, Long.MAX_VALUE);
    }


    private static final String ALL_USER_NAME = "all-user-name";
    private final static String PREFERENCE_USER_ID = "All-UserEntity-ID";
    private final static String USERS_ACCESS_TOKEN = "users-access-token";

    public static void clearLoginUserInfo(Context context) {
        context.getSharedPreferences(ALL_USER_NAME, Context.MODE_PRIVATE).edit().clear().apply();
        context.getSharedPreferences(PREFERENCE_USER_ID, Context.MODE_PRIVATE).edit().clear().apply();
        context.getSharedPreferences(USERS_ACCESS_TOKEN, Context.MODE_PRIVATE).edit().clear().apply();
    }
}
