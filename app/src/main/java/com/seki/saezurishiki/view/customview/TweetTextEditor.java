package com.seki.saezurishiki.view.customview;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;

import com.seki.saezurishiki.control.UIControlUtil;

import java.util.List;

import twitter4j.HashtagEntity;

public class TweetTextEditor extends AppCompatEditText {
    public TweetTextEditor(Context context) {
        super(context);
    }

    public TweetTextEditor(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TweetTextEditor(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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
