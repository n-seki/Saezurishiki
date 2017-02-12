package com.seki.saezurishiki.view.customview;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.seki.saezurishiki.R;
import com.seki.saezurishiki.control.UIControlUtil;

/**
 * Created by seki on 2016/05/14.
 */
public class TweetStatusBar extends View {
    public TweetStatusBar(Context context) {
        super(context);
    }

    public TweetStatusBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TweetStatusBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TweetStatusBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setDeletedColor(Context context) {
        setVisibility(View.VISIBLE);
        setBackgroundColor(UIControlUtil.textColor(context));
    }


    public void setReTweetColor(Context context) {
        setVisibility(View.VISIBLE);
        setBackgroundColor(ContextCompat.getColor(context, R.color.background_color_reTweet));
    }


    public void setReplyToMeColor(Context context) {
        setVisibility(View.VISIBLE);
        setBackgroundColor(ContextCompat.getColor(context, R.color.background_color_reply_to_me));
    }


    public void setMyTweetColor(Context context) {
        setVisibility(View.VISIBLE);
        setBackgroundColor(ContextCompat.getColor(context, R.color.background_color_my_post_unread));
    }
}
