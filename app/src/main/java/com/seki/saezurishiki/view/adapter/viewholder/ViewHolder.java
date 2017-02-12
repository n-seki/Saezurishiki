package com.seki.saezurishiki.view.adapter.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.seki.saezurishiki.R;
import com.seki.saezurishiki.view.customview.FavoriteStar;
import com.seki.saezurishiki.view.customview.TweetStatusBar;


public class ViewHolder {

    //ユーザー情報
    public TextView  mUserName;
    public TextView  mPostTime;

    //ツイート情報
    public TextView  mTweetText;

    //urlから画像を取得する方法が未確定
    public  ImageView mUserIcon;

    public TweetStatusBar mStatusBar;
    public View mFavoriteStar;
    public View mLockIcon;

    public View mReTweeter_info;
    public ImageView mReTweeter_icon;
    public TextView mReTweeter_name;
    public TextView retweetText;

    public LinearLayout mOperationButtons;
    public ImageView mReplyButtonMark;
    public ImageView mReTweetButtonMark;
    public FavoriteStar favoriteStar;

    public RelativeLayout replyButtonArea;
    public RelativeLayout reTweetButtonArea;
    public RelativeLayout favoriteButtonArea;

    public TextView mReTweetCount;
    public TextView mFavoriteCount;

    public RelativeLayout quotedTweetLayout;
    public ImageView quotedUserIcon;
    public TextView quotedUserName;
    public TextView quotedTweetText;

    public ViewHolder(View view) {
        mUserName  = (TextView)view.findViewById(R.id.user_name);
        mUserIcon  = (ImageView)view.findViewById(R.id.user_icon);
        mPostTime  = (TextView)view.findViewById(R.id.post_time);

        mTweetText = (TextView)view.findViewById(R.id.tweet_text);

        mStatusBar = (TweetStatusBar)view.findViewById(R.id.status_bar);
        mLockIcon = view.findViewById(R.id.lock_icon);

        mReTweeter_info = view.findViewById(R.id.reTweeter);
        mReTweeter_icon = (ImageView)view.findViewById(R.id.reTweeter_icon);
        mReTweeter_name = (TextView)view.findViewById(R.id.reTweeter_name);
        this.retweetText = (TextView)view.findViewById(R.id.reTweet_text);

        mOperationButtons = (LinearLayout)view.findViewById(R.id.operation_button);
        mReplyButtonMark = (ImageView)view.findViewById(R.id.reply_button);
        mReTweetButtonMark = (ImageView)view.findViewById(R.id.reTweet_button);
        favoriteStar = (FavoriteStar)view.findViewById(R.id.favorite_button);

        replyButtonArea = (RelativeLayout)view.findViewById(R.id.reply_button_area);
        reTweetButtonArea = (RelativeLayout)view.findViewById(R.id.retweet_button_area);
        favoriteButtonArea = (RelativeLayout)view.findViewById(R.id.favorite_button_area);

        mReTweetCount = (TextView)view.findViewById(R.id.reTweet_count);
        mFavoriteCount = (TextView)view.findViewById(R.id.favorite_count);

        quotedTweetLayout = (RelativeLayout)view.findViewById(R.id.quoted_status_layout);
        quotedUserIcon = (ImageView)view.findViewById(R.id.quoted_user_icon);
        quotedUserName = (TextView)view.findViewById(R.id.quoted_user_name);
        quotedTweetText = (TextView)view.findViewById(R.id.quoted_tweet_text);
    }

}
