package com.seki.saezurishiki.view.customview;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.seki.saezurishiki.R;

/**
 * Created by seki on 2016/05/14.
 */
public class FavoriteStar extends ImageView {

    public FavoriteStar(Context context) {
        super(context);
    }

    public FavoriteStar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FavoriteStar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FavoriteStar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void on() {
        setBackgroundResource(R.drawable.favorite_star_on);
    }


    public void off() {
        setBackgroundResource(R.drawable.favorite_star_off);
    }

}
