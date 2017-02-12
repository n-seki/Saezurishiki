package com.seki.saezurishiki.view.adapter.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.seki.saezurishiki.R;

/**
 * ダイレクトメッセージ用ViewHolder<br>
 */
public class DirectMessageViewHolder {

    public ImageView icon;
    public TextView userName;
    public TextView sendText;
    public TextView sendTime;

    public DirectMessageViewHolder(View view) {
        icon = (ImageView)view.findViewById(R.id.user_icon);
        userName = (TextView)view.findViewById(R.id.user_name);
        sendText = (TextView)view.findViewById(R.id.send_text);
        sendTime = (TextView)view.findViewById(R.id.post_time);
    }
}
