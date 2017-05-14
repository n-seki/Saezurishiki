package com.seki.saezurishiki.control;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import com.seki.saezurishiki.R;

public final class Setting {

    //default theme is light
    private static int theme;

    //default size is 14
    private static int textSize;

    //default configuration is show
    private static boolean showThumbnail;

    //default is LONG_TAP
    private static ButtonActionPattern favoriteButtonAction;

    //default is LONG_TAP
    private static ButtonActionPattern reTweetButtonAction;


    private static String DEFAULT_BUTTON_ACTION;


    public enum ButtonActionPattern {
        ONE_TAP("1"),
        TAP_AND_DIALOG("2"),
        LONG("3");

        final String symbol;

        ButtonActionPattern(String symbol) {
            this.symbol = symbol;
        }

        public static ButtonActionPattern convert(String symbol) {
            switch (symbol) {
                case "1":
                    return ONE_TAP;

                case "2":
                    return TAP_AND_DIALOG;

                case "3":
                    return LONG;

                default:
                    throw new IllegalStateException("not found ButtonActionPattern : " + symbol);
            }
        }
    }

    public static void init(Context context) {
        DEFAULT_BUTTON_ACTION = context.getString(R.string.pref_default_action_button_operation);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        theme = getTheme(sharedPref, context);
        textSize = getTextSize(sharedPref, context);
        showThumbnail = getShowThumbnail(sharedPref, context);
        favoriteButtonAction = getFavoriteButtonAction(sharedPref, context);
        reTweetButtonAction = getReTweetButtonAction(sharedPref, context);
    }

    private static int getTheme(SharedPreferences sharedPreferences, Context context) {
        String theme = sharedPreferences.getString(context.getString(R.string.pref_theme_key), "");
        return convertTheme(theme, context);
    }


    private static int convertTheme(String theme, Context context) {
        if (theme.equals(context.getString(R.string.pref_theme_color_default))) {
            return R.style.AppTheme_Dark;
        }
        return R.style.AppTheme_Light;
    }


    private static int getTextSize(SharedPreferences sharedPreferences, Context context) {
        final String DEFAULT_TEXT_SIZE = "14";

        final String size = sharedPreferences.getString(context.getString(R.string.pref_text_size_key), DEFAULT_TEXT_SIZE);
        return Integer.parseInt(size);
    }

    private static boolean getShowThumbnail(SharedPreferences sharedPreferences, Context context) {
        return sharedPreferences.getBoolean(context.getString(R.string.pref_show_thumbnail_key), true);
    }

    private static ButtonActionPattern getFavoriteButtonAction(SharedPreferences sharedPreferences, Context context) {
        final String symbol =
                sharedPreferences.getString(context.getString(R.string.pref_favorite_operation_key), DEFAULT_BUTTON_ACTION);

        return ButtonActionPattern.convert(symbol);
    }

    private static ButtonActionPattern getReTweetButtonAction(SharedPreferences sharedPreferences, Context context) {
        final String symbol =
                sharedPreferences.getString(context.getString(R.string.pref_key_reTweet_operation), DEFAULT_BUTTON_ACTION);

        return ButtonActionPattern.convert(symbol);
    }

    public int getTheme() {
        return theme;
    }

    public int getTextSize() {
        return textSize;
    }

    public boolean isShowThumbnail() {
        return showThumbnail;
    }

    public ButtonActionPattern getFavoriteButtonAction() { return favoriteButtonAction; }

    public ButtonActionPattern getReTweetButtonAction() { return reTweetButtonAction; }
}
