package com.seki.saezurishiki.control;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import com.seki.saezurishiki.R;

public final class Setting {

    private final Context context;

    //default theme is light
    private final int theme;

    //default size is 14
    private final int textSize;

    //default configuration is show
    private final boolean showThumbnail;

    //default is LONG_TAP
    private final ButtonActionPattern favoriteButtonAction;

    //default is LONG_TAP
    private final ButtonActionPattern reTweetButtonAction;


    private final String DEFAULT_BUTTON_ACTION;


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

    public Setting(Context context) {

        this.context = context;

        DEFAULT_BUTTON_ACTION = context.getString(R.string.pref_default_action_button_operation);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        this.theme = getTheme(sharedPref);
        this.textSize = getTextSize(sharedPref);
        this.showThumbnail = getShowThumbnail(sharedPref);
        this.favoriteButtonAction = getFavoriteButtonAction(sharedPref);
        this.reTweetButtonAction = getReTweetButtonAction(sharedPref);
    }

    private int getTheme(SharedPreferences sharedPreferences) {
        String theme = sharedPreferences.getString(context.getString(R.string.pref_theme_key), "");
        return convertTheme(theme);
    }


    private int convertTheme(String theme) {
        if (theme.equals(context.getString(R.string.pref_theme_color_default))) {
            return R.style.AppTheme_Dark;
        }
        return R.style.AppTheme_Light;
    }


    private int getTextSize(SharedPreferences sharedPreferences) {
        final String DEFAULT_TEXT_SIZE = "14";

        final String size = sharedPreferences.getString(context.getString(R.string.pref_text_size_key), DEFAULT_TEXT_SIZE);
        return Integer.parseInt(size);
    }

    private boolean getShowThumbnail(SharedPreferences sharedPreferences) {
        return sharedPreferences.getBoolean(context.getString(R.string.pref_show_thumbnail_key), true);
    }

    private ButtonActionPattern getFavoriteButtonAction(SharedPreferences sharedPreferences) {
        final String symbol =
                sharedPreferences.getString(context.getString(R.string.pref_favorite_operation_key), DEFAULT_BUTTON_ACTION);

        return ButtonActionPattern.convert(symbol);
    }

    private ButtonActionPattern getReTweetButtonAction(SharedPreferences sharedPreferences) {
        final String symbol =
                sharedPreferences.getString(context.getString(R.string.pref_key_reTweet_operation), DEFAULT_BUTTON_ACTION);

        return ButtonActionPattern.convert(symbol);
    }

    public int getTheme() {
        return this.theme;
    }

    public int getTextSize() {
        return this.textSize;
    }

    public boolean isShowThumbnail() {
        return this.showThumbnail;
    }

    public ButtonActionPattern getFavoriteButtonAction() { return this.favoriteButtonAction; }

    public ButtonActionPattern getReTweetButtonAction() { return this.reTweetButtonAction; }
}
