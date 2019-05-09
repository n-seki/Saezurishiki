package com.seki.saezurishiki.view.fragment.list;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.seki.saezurishiki.R;
import com.seki.saezurishiki.application.SaezurishikiApp;
import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.model.adapter.RequestInfo;
import com.seki.saezurishiki.presenter.list.TweetListPresenter;
import com.seki.saezurishiki.view.fragment.ConversationModule;
import com.seki.saezurishiki.view.fragment.util.DataType;

import javax.inject.Inject;

/**
 * 会話表示Fragment<BR>
 * 選択されたStatusのinReplyToStatusIDを遡って表示します
 * @author seki
 */
public class ConversationFragment extends TweetListFragment {

    private long firstTweetId;

    @Inject
    TweetListPresenter presenter;

    public static ConversationFragment getInstance(long userId, long statusId) {
        ConversationFragment fragment = new ConversationFragment();
        Bundle data = new Bundle();
        data.putLong(USER_ID, userId);
        data.putLong(DataType.STATUS_ID, statusId);
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firstTweetId = getArguments().getLong(DataType.STATUS_ID);
        long listOwnerId = getArguments().getLong(USER_ID);

        SaezurishikiApp.mApplicationComponent.conversationComponentBuilder()
                .listOwnerId(listOwnerId)
                .presenterView(this)
                .module(new ConversationModule())
                .build()
                .inject(this);
    }


    @Override
    protected void initComponents(View rootView) {
        mListView = (ListView) rootView.findViewById(R.id.list);
        mListView.setOnItemLongClickListener((adapterView, view, i, l) -> {
            ConversationFragment.this.showLongClickDialog((TweetEntity)mAdapter.getEntity(i));
            return false;
        });

        mListView.setOnItemClickListener((adapterView, view, i, l) -> {
            ConversationFragment.this.showDialog((TweetEntity)mAdapter.getEntity(i));
        });
        mListView.setSmoothScrollbarEnabled(true);
        mListView.setAdapter(mAdapter);
    }

    @Override
    protected void loadTimeLine() {
        this.presenter.load(new RequestInfo().targetID(firstTweetId));
    }

    @Override
    public void catchNewTweet(TweetEntity tweetEntity) {
        this.mAdapter.add(tweetEntity);
    }

    @Override
    public TweetListPresenter getPresenter() {
        return presenter;
    }
}
