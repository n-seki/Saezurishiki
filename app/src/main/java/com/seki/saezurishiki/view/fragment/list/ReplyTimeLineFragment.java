package com.seki.saezurishiki.view.fragment.list;

import com.seki.saezurishiki.file.SharedPreferenceUtil;


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
}
