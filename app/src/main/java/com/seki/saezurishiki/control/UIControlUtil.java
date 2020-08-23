package com.seki.saezurishiki.control;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.util.TypedValue;

import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import androidx.core.content.ContextCompat;

import com.seki.saezurishiki.R;
import com.seki.saezurishiki.entity.Media;
import com.seki.saezurishiki.entity.TweetEntity;

import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import twitter4j.MediaEntity;

import static com.seki.saezurishiki.view.adapter.TimeLinePager.POSITION_HOME;
import static com.seki.saezurishiki.view.adapter.TimeLinePager.POSITION_REPLY;

public final class UIControlUtil {

    private UIControlUtil() {}


    public static List<Media> createMediaURLList(TweetEntity status) {
        if (status.mediaEntities == null || status.mediaEntities.length == 0) {
            return new ArrayList<>(0);
        }

        List<Media> URLList = new ArrayList<>();

        for (MediaEntity mediaEntity : status.mediaEntities) {
            if (!mediaEntity.getType().equals("video")) {
                URLList.add(Media.from(mediaEntity.getMediaURLHttps(), null, mediaEntity.getType()));
                continue;
            }

            MediaEntity.Variant[] variants = mediaEntity.getVideoVariants();
            MediaEntity.Variant targetVariant = findLowestBitrateVariant(variants);
            URLList.add(Media.from(targetVariant.getUrl(),
                    mediaEntity.getMediaURLHttps(), mediaEntity.getType()));
        }
        return URLList;
    }

    private static MediaEntity.Variant findLowestBitrateVariant(MediaEntity.Variant[] variants) {
        MediaEntity.Variant lowestBitrateVariant = variants[0];
        int lowestBitrate = Integer.MAX_VALUE;
        for (MediaEntity.Variant variant : variants) {
            if (!variant.getContentType().startsWith("video")) {
                continue;
            }
            if (variant.getBitrate() > 0 && variant.getBitrate() <= lowestBitrate) {
                lowestBitrateVariant = variant;
                lowestBitrate = variant.getBitrate();
            }
        }
        return lowestBitrateVariant;
    }


    @NonNull
    @Contract("null -> fail")
    public static String getMentionUserNameText(List<String> userNames) {
        if (userNames == null || userNames.size() == 0) {
            throw new IllegalArgumentException("userNames is empty!");
        }

        StringBuilder buffer = new StringBuilder();

        for (String name : userNames) {
            buffer.append("@").append(name).append(" ");
        }

        return buffer.toString();
    }


    @Contract(pure = true)
    public static String getMentionUserNameText(String name) {
        return "@" + name + " ";
    }


    @Contract(pure = true)
    public static String addAtMark(String name) {
        return "@" + name;
    }


    public static int textColor(Context context) {
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.textColor, outValue, true);

        return ContextCompat.getColor(context, outValue.resourceId);
    }


    public static int backgroundColor(Context context) {
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.windowBackground, outValue, true);

        return ContextCompat.getColor(context, outValue.resourceId);
    }


    public static ColorStateList buttonTint(Context context, int theme) {
        switch (theme) {
            case R.style.AppTheme_Dark :
                return ContextCompat.getColorStateList(context, R.color.blue_button_color_state);

            case R.style.AppTheme_Light :
                return ContextCompat.getColorStateList(context, R.color.light_button_color_state);

            default:
                throw new IllegalStateException("theme is invalid");
        }
    }

    public static int colorAccent(Context context, int theme) {
        if (Build.VERSION.SDK_INT >= 21) {
            TypedValue outValue = new TypedValue();
            context.getTheme().resolveAttribute(android.R.attr.colorAccent, outValue, true);

            return ContextCompat.getColor(context, outValue.resourceId);
        }

        switch (theme) {
            case R.style.AppTheme_Dark :
                return ContextCompat.getColor(context, R.color.colorAccent_dark);

            case R.style.AppTheme_Light :
                return ContextCompat.getColor(context, R.color.colorAccent_light);

            default:
                throw new IllegalArgumentException("theme does not exist! : " + theme);

        }
    }



    public static int getTabBackground(int position, @StyleRes int theme) {
        final boolean isDark = theme == R.style.AppTheme_Dark;
        switch (position) {
            case POSITION_HOME :
                return isDark ? R.drawable.tab_home_selector_dark : R.drawable.tab_home_selector_light;

            case POSITION_REPLY :
                return isDark ? R.drawable.tab_reply_selector_dark : R.drawable.tab_reply_selector_light;

            default:
                throw new IllegalStateException("position is illegal : " + position);
        }
    }

    public static int getTabUnreadBackground(int position, @StyleRes int theme) {
        switch (position) {
            case POSITION_HOME :
                return R.drawable.tab_home_unread_sample;

            case POSITION_REPLY:
                return R.drawable.tab_reply_unread_sample;

            default:
                throw new IllegalStateException("position is illegal : " + position);
        }
    }


    public static String formatDate(Date date) {
        return android.text.format.DateFormat.format("yyyy/MM/dd kk:mm:ss", date).toString();
    }
}
