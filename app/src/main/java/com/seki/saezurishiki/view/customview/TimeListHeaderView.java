package com.seki.saezurishiki.view.customview;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

/**
 * Created by seki on 2016/08/21.
 */
public class TimeListHeaderView extends View {

    private TextView mTextView;

    public TimeListHeaderView(Context context) {
        super(context);
    }

    public TimeListHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TimeListHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TimeListHeaderView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


}
