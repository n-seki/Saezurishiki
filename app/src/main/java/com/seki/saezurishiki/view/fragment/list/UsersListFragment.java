package com.seki.saezurishiki.view.fragment.list;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.seki.saezurishiki.application.SaezurishikiApp;
import com.seki.saezurishiki.R;
import com.seki.saezurishiki.view.activity.UserActivity;
import com.seki.saezurishiki.view.adapter.UsersListAdapter;
import com.seki.saezurishiki.control.UIControlUtil;
import com.seki.saezurishiki.network.twitter.AsyncTwitterTask;
import com.seki.saezurishiki.network.twitter.TwitterAccount;
import com.seki.saezurishiki.network.twitter.TwitterTaskResult;
import com.seki.saezurishiki.network.twitter.TwitterTaskUtil;
import com.seki.saezurishiki.network.twitter.streamListener.UserStreamUserListener;
import com.seki.saezurishiki.view.fragment.DataType;

import twitter4j.PagableResponseList;
import twitter4j.TwitterException;
import twitter4j.User;

/**
 * ユーザー一覧表示Fragment<br>
 * @author seki
 */
public abstract class UsersListFragment extends Fragment implements UserStreamUserListener {

    UsersListAdapter mAdapter;

    long mUserId;

    long mNextCursor = -1;

    private View mListFooter;
    private ListView mListView;

    TwitterTaskUtil twitterTask;

    protected TwitterAccount twitterAccount;

    private boolean isLoading;

    int mCount;

    static void setArgument( UsersListFragment fragment, long userId, int count) {
        Bundle data = new Bundle();
        data.putLong(DataType.USER_ID, userId);
        data.putInt(DataType.COUNT, count);
        fragment.setArguments(data);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new UsersListAdapter( getActivity(), R.layout.user_info_layout );

        Bundle data = this.getArguments();

        if ( data == null ) {
            throw new IllegalStateException("Argument is null");
        }

        SaezurishikiApp app = (SaezurishikiApp)getActivity().getApplication();
        this.twitterAccount = app.getTwitterAccount();
        mUserId = data.getLong(DataType.USER_ID);
        mCount = data.getInt(DataType.COUNT);
        this.twitterTask = new TwitterTaskUtil(getActivity(), getLoaderManager(), this.twitterAccount);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("onAttach", "onAttach-invoke");
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list_view, container, false);
        this.initComponents(rootView);

        rootView.setBackgroundColor(UIControlUtil.backgroundColor(getActivity()));

        return rootView;
    }


    protected void initComponents(View rootView) {
        mListView = (ListView) rootView.findViewById(R.id.list);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UsersListFragment.this.onUserItemClick(mAdapter.getItem(position));
            }
        });

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //do nothing
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //do nothing
            }
        });

        mListFooter = getActivity().getLayoutInflater().inflate(R.layout.read_more_tweet, null);
        mListFooter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UsersListFragment.this.loadUsers();
            }
        });

        mListView.addFooterView(mListFooter, null ,true);
        mListView.setFooterDividersEnabled(false);
        mListView.setAdapter(mAdapter);
    }

    private void onUserItemClick(User user) {
        Intent intent = new Intent(getActivity(), UserActivity.class);
        intent.putExtra(UserActivity.USER, user);
        startActivity(intent);
    }


    public void onResume() {
        super.onResume();

        if (mAdapter.isEmpty()) {
            this.loadUsers();
        }
    }


    final AsyncTwitterTask.AfterTask<PagableResponseList<User>> AFTER_TASK = new AsyncTwitterTask.AfterTask<PagableResponseList<User>>() {
        @Override
        public void onLoadFinish(TwitterTaskResult<PagableResponseList<User>> result) {
            isLoading = false;
            if ( result.isException() ) {
                UsersListFragment.this.errorProcess(result.getException());
                ((TextView)mListFooter.findViewById(R.id.read_more)).setText(R.string.click_to_load);
                return;
            }

            for ( User user : result.getResult() ) {
                twitterAccount.getRepository().add(user);
                mAdapter.add( user );
            }

            if (result.getResult().isEmpty()) {
                mListView.removeFooterView(mListFooter);
            } else {
                ((TextView)mListFooter.findViewById(R.id.read_more)).setText(R.string.click_to_load);
            }

            mNextCursor = result.getResult().getNextCursor();
        }

    };



    private void loadUsers() {
        if (this.isLoading) return;
        this.isLoading = true;
        ((TextView)mListFooter.findViewById(R.id.read_more)).setText(R.string.now_loading);
         new AsyncTwitterTask<>(getActivity(), getTask(), AFTER_TASK, getLoaderManager()).run();
    }


    private void errorProcess( TwitterException exception ) {
        Toast.makeText(getActivity(), exception.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }


    public abstract String toString();
    abstract AsyncTwitterTask.AsyncTask<PagableResponseList<User>> getTask();


}
