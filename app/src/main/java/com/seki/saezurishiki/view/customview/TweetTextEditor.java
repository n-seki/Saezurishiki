package com.seki.saezurishiki.view.customview;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.EditText;

import com.seki.saezurishiki.control.UIControlUtil;

import java.util.List;

import twitter4j.HashtagEntity;

/**
 * Created by seki on 2016/05/14.
 */
public class TweetTextEditor extends EditText {
    public TweetTextEditor(Context context) {
        super(context);
    }

    public TweetTextEditor(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TweetTextEditor(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TweetTextEditor(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    public void setUserName(List<String> names) {
        String text = UIControlUtil.getMentionUserNameText(names);
        setText(text);
        setSelection(text.length());
    }


    public void setUserName(String name) {
        String text = UIControlUtil.getMentionUserNameText(name);
        setText(text);
        setSelection(text.length());
    }


    public void setHashTag(HashtagEntity[] tags) {
        StringBuilder hashTagString = new StringBuilder();

        for (HashtagEntity hashTag : tags) {
            hashTagString.append(" ").append("#").append(hashTag.getText());
        }

        setText(hashTagString.toString());
        setSelection(0);
    }
}
