package com.seki.saezurishiki.view.fragment.list;

import android.os.Bundle;
import android.widget.TextView;

import com.seki.saezurishiki.R;
import com.seki.saezurishiki.entity.TwitterEntity;
import com.seki.saezurishiki.network.twitter.AsyncTwitterTask;
import com.seki.saezurishiki.network.twitter.TwitterTaskResult;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;

/**
 * タイムライン基本クラス<br>
 * お気に入りやツイートなどの表示のみを行うようなFragmentの基本メソッドの提供
 * @author seki
 */
public abstract class TimeLineFragment extends TweetListFragment {

    protected long mUserId       = 0;

    protected void loadTimeLine() {
        this.presenter.load(createLastPaging());
    }



    @Override
    protected void onLoadFinished(TwitterTaskResult<ResponseList<Status>> result) {
        this.isFirstOpen = false;
        ((TextView)mFooterView.findViewById(R.id.read_more)).setText(R.string.click_to_load);
        if (result.isException()) {
            TimeLineFragment.this.errorProcess(result.getException());
            ((TextView)mFooterView.findViewById(R.id.read_more)).setText(R.string.click_to_load);
            return;
        }

        for (Status status : result.getResult()) {
            mAdapter.add(status);
        }
    }

    protected void onClickLoadButton(long buttonId) {
        throw new IllegalStateException("this method shouldn't call!");
    }

}
