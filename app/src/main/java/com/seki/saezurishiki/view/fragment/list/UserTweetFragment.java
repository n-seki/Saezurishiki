package com.seki.saezurishiki.view.fragment.list;

import android.os.Bundle;

import com.seki.saezurishiki.view.fragment.util.DataType;

/**
 * Tweet一覧表示Fragment<br>
 * ユーザーのTweetを時系列順に表示します
 * @author seki
 */
public class UserTweetFragment extends TimeLineFragment {

    int mCount;

    public static TimeLineFragment getInstance(long userID, int count) {
        TimeLineFragment fragment = new UserTweetFragment();
        Bundle data = new Bundle();
        data.putLong(DataType.USER_ID, userID);
        data.putInt(DataType.COUNT, count);
        fragment.setArguments(data);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle data = this.getArguments();

        if ( data == null ) {
            throw new IllegalStateException("Argument is null");
        }

        mUserId = data.getLong(DataType.USER_ID);
        mCount = data.getInt(DataType.COUNT);
    }

    @Override
    public String toString() {
        return "Tweet";
    }

}
