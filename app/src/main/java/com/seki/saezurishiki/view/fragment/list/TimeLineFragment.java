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

    protected boolean mIsLoading;

    protected final AsyncTwitterTask.AfterTask<ResponseList<Status>> TASK_AFTER_LOAD =
            new AsyncTwitterTask.AfterTask<ResponseList<Status>>() {
                @Override
                public void onLoadFinish(TwitterTaskResult<ResponseList<Status>> result) {
                    TimeLineFragment.this.onLoadFinished(result);
                }
            };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onPause() {
        super.onPause();
        mIsLoading = false;
    }

    protected void loadTimeLine() {
        if ( mIsLoading ) return;
        mIsLoading = true;

        new AsyncTwitterTask<>(getActivity(), this.getStatusesLoader(), TASK_AFTER_LOAD, getLoaderManager()).run();
    }


    protected Paging createLastPaging() {
        long lastId = this.getLastId();

        if ( lastId != -1 ) {
            return new Paging().maxId(lastId - 1).count(100); //lastIDのstatusは読み込まなくていい
        }

        //mLastIdが未設定の場合は
        //statusが1つも読み込まれていないため
        //無条件でただのPagingを返す
        return new Paging().count(50);
    }


    protected long getLastId() {
        if ( mAdapter == null || mAdapter.getCount() == 0 ) {
            return -1;
        }

        return mAdapter.getItemIdAtPosition(mAdapter.getCount() - 1);
    }


    protected long getListTopStatusID() {
        if ( mAdapter == null || mAdapter.getCount() == 0 ) {
            return -1;
        }

        if (this.twitterAccount.getRepository().getStatus(mAdapter.getItemIdAtPosition(0)).getItemType() == TwitterEntity.Type.LoadButton) {
            return -1;
        }

        return mAdapter.getItemIdAtPosition(0);
    }


    @Override
    protected void onLoadFinished(TwitterTaskResult<ResponseList<Status>> result) {
        this.isFirstOpen = false;
        this.mIsLoading = false;
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


    abstract AsyncTwitterTask.AsyncTask<ResponseList<Status>> getStatusesLoader();
}
