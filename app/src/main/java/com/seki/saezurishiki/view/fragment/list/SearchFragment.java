package com.seki.saezurishiki.view.fragment.list;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.TextView;

import com.seki.saezurishiki.R;
import com.seki.saezurishiki.network.twitter.AsyncTwitterTask;
import com.seki.saezurishiki.network.twitter.TwitterTaskResult;
import com.seki.saezurishiki.view.fragment.util.DataType;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.ResponseList;
import twitter4j.Status;

/**
 * 検索結果一覧表示Fragment<br>
 * search viewに入力された文字列でtweet検索を行い,時系列順に表示します
 * @author seki
 */
public class SearchFragment extends TweetListFragment {

    private String mQuery;
    private boolean isLoading;

    public static Fragment getInstance(String query) {
        Bundle data = new Bundle();
        data.putString(DataType.QUERY, query);
        Fragment fragment = new SearchFragment();
        fragment.setArguments(data);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mQuery = getArguments().getString(DataType.QUERY);

        final ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar== null) {
            throw new IllegalStateException("ActionBar is null!");
        }

        actionBar.setTitle("\"" + mQuery + "\"");

        setHasOptionsMenu(true);
    }

    @Override
    protected void loadTimeLine() {
        if (this.isLoading) return;
        this.isLoading = true;
        Query twitterQuery = new Query(mQuery);
        if(mAdapter.getCount() > 1) {
            twitterQuery.setMaxId(mAdapter.getItemIdAtPosition(mAdapter.getCount() - 1) - 1);
        }

        AsyncTwitterTask.AfterTask<QueryResult> afterTask = new AsyncTwitterTask.AfterTask<QueryResult>() {
            @Override
            public void onLoadFinish(TwitterTaskResult<QueryResult> result) {
                isLoading = false;
                if (result.isException()) {
                    SearchFragment.this.errorProcess(result.getException());
                    return;
                }

                for (Status status : result.getResult().getTweets()) {
                    mAdapter.add(status);
                }

                ((TextView) mFooterView.findViewById(R.id.read_more)).setText(R.string.click_to_load);

            }
        };

        mTwitterWrapper.search(twitterQuery, afterTask);
    }

    @Override
    protected void onClickLoadButton(long buttonId) {
        throw new IllegalStateException("this method shouldn't called!");
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menu.clear();
    }


    @Override
    public String toString() {
        return "\"" + mQuery + "\"";
    }


    @Override
    public void onLoadFinished(TwitterTaskResult<ResponseList<Status>> result) {
        //do nothing
    }



}
