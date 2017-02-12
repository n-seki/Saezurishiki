package com.seki.saezurishiki.view.adapter.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.seki.saezurishiki.R;

/**
 * User用ViewHolder<br>
 */
public class UserViewHolder {

    public ImageView mUserIcon;

    public TextView mUserName;

    public TextView mBioText;

    public TextView mSince;

    public UserViewHolder(View view) {
        mUserIcon = (ImageView)view.findViewById(R.id.user_user_icon);
        mUserName = (TextView)view.findViewById(R.id.user_user_name);
        mBioText  = (TextView)view.findViewById(R.id.user_bio_text);
        mSince    = (TextView)view.findViewById(R.id.user_since);
    }
}
