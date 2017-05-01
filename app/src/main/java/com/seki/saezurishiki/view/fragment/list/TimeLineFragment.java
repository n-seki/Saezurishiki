package com.seki.saezurishiki.view.fragment.list;

import com.seki.saezurishiki.model.adapter.RequestInfo;

/**
 * タイムライン基本クラス<br>
 * お気に入りやツイートなどの表示のみを行うようなFragmentの基本メソッドの提供
 * @author seki
 */
public abstract class TimeLineFragment extends TweetListFragment {

    protected long mUserId       = 0;

    protected void loadTimeLine() {
        final long maxID = this.getLastId();
        this.presenter.load(new RequestInfo().maxID(maxID == -1 ? 0 : maxID).count(50));
    }


    protected void onClickLoadButton(long buttonId) {
        throw new IllegalStateException("this method shouldn't call!");
    }

}
