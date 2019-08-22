package com.seki.saezurishiki.view.fragment.list;

import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;

import com.seki.saezurishiki.application.SaezurishikiApp;
import com.seki.saezurishiki.model.adapter.RequestInfo;
import com.seki.saezurishiki.view.SearchModule;
import com.seki.saezurishiki.view.fragment.util.DataType;

/**
 * 検索結果一覧表示Fragment<br>
 * search viewに入力された文字列でtweet検索を行い,時系列順に表示します
 * @author seki
 */
public class SearchFragment extends TweetListFragment {

    private String mQuery;

    public static SearchFragment getInstance(long userId, String query) {
        Bundle data = new Bundle();
        data.putLong(USER_ID, userId);
        data.putString(DataType.QUERY, query);
        SearchFragment fragment = new SearchFragment();
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

        long listOwnerId = getArguments().getLong(USER_ID);

        SaezurishikiApp.mApplicationComponent.searchComponentBuilder()
                .listOwnerId(listOwnerId)
                .presenterView(this)
                .module(new SearchModule())
                .build()
                .inject(this);

        actionBar.setTitle("\"" + mQuery + "\"");

        setHasOptionsMenu(true);
    }

    @Override
    protected void loadTimeLine() {
        final long maxID = mAdapter.isEmpty() ? -1 : mAdapter.getLastTweetId() -1;
        final RequestInfo info = new RequestInfo().query(mQuery).maxID(maxID);
        this.presenter.load(info);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menu.clear();
    }
}
