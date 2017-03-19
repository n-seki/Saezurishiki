package com.seki.saezurishiki.view.fragment.list;

import android.os.Bundle;

import com.seki.saezurishiki.file.SharedPreferenceUtil;
import com.seki.saezurishiki.network.twitter.AsyncTwitterTask;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;


/**
 * リプライタイムラインFragment<br>
 * ネットワーク接続時には常時ユーザーストリームで,ログインユーザーのreply time-lineを表示します
 * @author seki
 */
public class ReplyTimeLineFragment extends UserStreamTimeLineFragment {


    public static TimeLineFragment getInstance() {
        return new ReplyTimeLineFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }




    @Override
    AsyncTwitterTask.AsyncTask<ResponseList<Status>> getStatusesLoader() {
        return mTwitterWrapper.getMentionTimeLineLoader(createLastPaging());
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



//    @Override
//    public void onStatus(final Status status) {
//        if (status.getInReplyToUserId()!= this.twitterAccount.getLoginUserId()) {
//            return;
//        }
//        super.onStatus(status);
//
//        if (!mListTopVisible || mIsLoading) {
//            mSavedStatuses.add(status.getId());
//            //notifyUnreadItem(true);
//            this.tabViewControl.requestChangeTabState(this);
//            return;
//        }
//
//        mAdapter.insert(status.getId(), 0);
//        if (!this.tabViewControl.isCurrentSelect(this)) {
//            this.tabViewControl.requestChangeTabState(this);
//        }
//    }

    @Override
    protected AsyncTwitterTask.AsyncTask<ResponseList<Status>> getSwipeTask() {
        final int count = isFirstOpen ? 50 : 200;
        return mTwitterWrapper.getMentionTimeLineLoader(new Paging().count(count));
    }


    @Override
    protected long readLastID() {
        return SharedPreferenceUtil.readLatestID(getActivity(),SharedPreferenceUtil.REPLY);
    }

    @Override
    public void onStop() {
        if (!mAdapter.isEmpty()) {
            SharedPreferenceUtil.writeLatestID(getActivity(), SharedPreferenceUtil.REPLY, mAdapter.lastReadId());
        }

        super.onStop();
    }


    void runLoadButtonClickedTask(Paging paging, AsyncTwitterTask.AfterTask<ResponseList<Status>> afterTask) {
        mTwitterWrapper.getMentionTimeLine(paging, afterTask);
    }


}
