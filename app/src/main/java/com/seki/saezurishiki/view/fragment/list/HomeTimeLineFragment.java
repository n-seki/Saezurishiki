package com.seki.saezurishiki.view.fragment.list;

import android.os.Bundle;

import com.seki.saezurishiki.file.SharedPreferenceUtil;
import com.seki.saezurishiki.network.twitter.AsyncTwitterTask;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;


/**
 * ホームタイムラインFragment<br>
 * ネットワーク接続時には常時ユーザーストリームで,ログインユーザーのhome time-lineを表示します
 * @author seki
 */
public class HomeTimeLineFragment extends UserStreamTimeLineFragment {


    public static TimeLineFragment getInstance() {
        return new HomeTimeLineFragment();
    }


    @Override
    void releaseSavedStatus() {
        if (mSavedStatuses.isEmpty()) {
            return;
        }

        for (long statusId : mSavedStatuses) {
            mAdapter.insert(statusId, 0);
        }

        mAdapter.notifyDataSetChanged();
        mSavedStatuses.clear();
    }


    @Override
    public void onStop() {
        if (!mAdapter.isEmpty()) {
            SharedPreferenceUtil.writeLatestID(getActivity(), SharedPreferenceUtil.HOME, mAdapter.lastReadId());
        }
        super.onStop();
    }


    @Override
    protected long readLastID() {
        return SharedPreferenceUtil.readLatestID(getActivity(), SharedPreferenceUtil.HOME);
    }


//    @Override
//    public void onStatus(final Status status) {
//        super.onStatus(status);
//        if (!mListTopVisible || mIsLoading) {
//            mSavedStatuses.add(status.getId());
//            this.tabViewControl.requestChangeTabState(this);
//            return;
//        }
//
//        mAdapter.insert(status.getId(), 0);
//        if (!this.tabViewControl.isCurrentSelect(this)) {
//            this.tabViewControl.requestChangeTabState(this);
//        }
//    }
}


