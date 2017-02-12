package com.seki.saezurishiki.view.fragment.list;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.seki.saezurishiki.R;
import com.seki.saezurishiki.network.twitter.AsyncTwitterTask;
import com.seki.saezurishiki.network.twitter.TwitterTaskResult;
import com.seki.saezurishiki.view.fragment.DataType;

import twitter4j.ResponseList;
import twitter4j.Status;

/**
 * 会話表示Fragment<BR>
 * 選択されたStatusのinReplyToStatusIDを遡って表示します
 * @author seki
 */
public class ConversationFragment extends TweetListFragment {

    private long mStatusId;


    public static ConversationFragment getInstance(long statusId) {
        ConversationFragment fragment = new ConversationFragment();
        Bundle data = new Bundle();
        data.putLong(DataType.STATUS_ID, statusId);
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mStatusId = getArguments().getLong(DataType.STATUS_ID);
    }


    @Override
    protected void initComponents(View rootView) {
        //this.setActionBarTitle();
        mListView = (ListView) rootView.findViewById(R.id.list);
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                ConversationFragment.this.showLongClickDialog(twitterAccount.getRepository().getStatus(mAdapter.getItemIdAtPosition(i)));
                return false;
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ConversationFragment.this.showDialog(twitterAccount.getRepository().getStatus(mAdapter.getItemIdAtPosition(i)));
            }
        });


        mListView.setSmoothScrollbarEnabled(true);

        mListView.setAdapter(mAdapter);
    }


    @Override
    public void onResume() {
        super.onResume();

        if (this.isFirstOpen) {
            this.loadStatus(mStatusId);
            this.isFirstOpen = false;
        }
    }



    @Override
    protected void loadTimeLine() {
        //do nothing
    }

    @Override
    public String toString() {
        return "Conversation";
}


    private void loadReplyToStatus(final Status status) {
        if (status.getInReplyToStatusId() == -1) return;
        loadStatus(status.getInReplyToStatusId());
    }


    private void loadStatus(final long id) {
        mTwitterTaskUtil.showStatus(id, new AsyncTwitterTask.AfterTask<Status>() {
            @Override
            public void onLoadFinish(TwitterTaskResult<Status> result) {
                ConversationFragment.this.onLoadStatus(result);
            }
        });
    }


    void onLoadStatus(TwitterTaskResult<Status> result) {
        if ( result.isException() ) {
            this.errorProcess(result.getException());
            return;
        }

        Status resultStatus = result.getResult();

        if ( resultStatus == null ) {
            throw new IllegalStateException("ResponseList is null");
        }

        mAdapter.add(resultStatus);
        this.loadReplyToStatus(resultStatus);
    }

    protected void onClickLoadButton(long buttonId) {
        throw new IllegalStateException("this method shouldn't call!");
    }

    @Override
    protected void onLoadFinished(TwitterTaskResult<ResponseList<Status>> result) {
        throw  new IllegalStateException(("this method shouldn't call!"));
    }
}
