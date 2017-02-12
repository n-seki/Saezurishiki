package com.seki.saezurishiki.view.customview;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by seki on 2016/05/14.
 */
public class PictureThumbnail extends ImageView {

    public PictureThumbnail(Context context) {
        super(context);
    }

    public PictureThumbnail(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PictureThumbnail(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PictureThumbnail(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    public void show(String url, View.OnClickListener clickListener) {
        setClickable(true);
        setTag(url);
        setOnClickListener(clickListener);
        setVisibility(View.VISIBLE);
    }


    public void hide() {
        setImageBitmap(null);
        setClickable(false);
        setVisibility(View.GONE);
    }
}
