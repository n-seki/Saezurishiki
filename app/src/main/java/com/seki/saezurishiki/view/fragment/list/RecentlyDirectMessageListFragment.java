package com.seki.saezurishiki.view.fragment.list;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.seki.saezurishiki.R;
import com.seki.saezurishiki.application.SaezurishikiApp;
import com.seki.saezurishiki.control.UIControlUtil;
import com.seki.saezurishiki.entity.DirectMessageEntity;
import com.seki.saezurishiki.file.SharedPreferenceUtil;
import com.seki.saezurishiki.model.adapter.RequestInfo;
import com.seki.saezurishiki.network.ConnectionReceiver;
import com.seki.saezurishiki.network.twitter.TwitterAccount;
import com.seki.saezurishiki.presenter.list.RecentlyDirectMessageListPresenter;
import com.seki.saezurishiki.view.adapter.DirectMessageAdapter;
import com.seki.saezurishiki.view.adapter.ListElement;
import com.seki.saezurishiki.view.control.RequestTabState;
import com.seki.saezurishiki.view.control.TabManagedView;
import com.seki.saezurishiki.view.control.TabViewControl;

import java.util.List;

public class RecentlyDirectMessageListFragment extends Fragment implements ConnectionReceiver.Observer, TabManagedView, RecentlyDirectMessageListPresenter.View{

    private DirectMessageAdapter mAdapter;
    private CallBack mCallBack;
    private long mLastUnreadMessageId;
    private TwitterAccount twitterAccount;
    private TabViewControl tabViewControl;
    private int tabPosition;
    private SwipeRefreshLayout refreshLayout;

    private RecentlyDirectMessageListPresenter presenter;

    private static final String TAB_POSITION = "tab-position";

    public interface CallBack {
        void displayDirectMessageEditor(long messageId);
    }

    public static RecentlyDirectMessageListFragment getInstance(int tabPosition) {
        Bundle data = new Bundle();
        data.putInt(TAB_POSITION, tabPosition);
        RecentlyDirectMessageListFragment fragment = new RecentlyDirectMessageListFragment();
        fragment.setArguments(data);
        return fragment;
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SaezurishikiApp app = (SaezurishikiApp)getActivity().getApplication();
        this.twitterAccount = app.getTwitterAccount();
        mAdapter = new DirectMessageAdapter(getActivity(), R.layout.direct_message_layout);
        mAdapter.setBackgroundColor();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mLastUnreadMessageId = this.readLastID();
        mCallBack = (CallBack) getActivity();
        this.tabPosition = getArguments().getInt(TAB_POSITION);
        this.tabViewControl = (TabViewControl)getActivity();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ConnectionReceiver.addObserver(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_swipe_refresh, container, false);
        this.initComponents(view);

        view.setBackgroundColor(UIControlUtil.backgroundColor(getActivity()));

        return view;
    }

    private void initComponents(View view) {
        ListView list = (ListView) view.findViewById(R.id.list);
        list.setOnItemClickListener((parent, view1, position, id) -> {
            final ListElement item = mAdapter.getItem(position);
            presenter.onItemClick(item);
        });

        list.setSmoothScrollbarEnabled(true);
        list.setAdapter(mAdapter);

        this.refreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_refresh);
        this.refreshLayout.setColorSchemeColors(UIControlUtil.colorAccent(getActivity(), twitterAccount.setting.getTheme()));
        this.refreshLayout.setOnRefreshListener(RecentlyDirectMessageListFragment.this::onSwipeRefresh);
    }

    @Override
    public void onResume() {
        super.onResume();
        this.presenter.onResume();
        if (mAdapter.isEmpty()) {
            this.presenter.request(new RequestInfo().count(50));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        this.presenter.onPause();
    }

    @Override
    public void onStop() {
        if (!mAdapter.isEmpty()) {
            this.write("message", mAdapter.lastReadId());
        }

        super.onStop();
    }

    @Override
    public void onConnect() {
       // this.loadMessage();
    }

    @Override
    public void onDisconnect() {
        //do nothing
    }

    private void loadMessage() {
        final RequestInfo info = new RequestInfo().count(50).sinceID(mAdapter.getLatestItemId() - 1);
        this.presenter.request(info);
    }

    @Override
    public int tabPosition() {
        return this.tabPosition;
    }

    @Override
    public RequestTabState getRequestTabState() {
        return () -> RecentlyDirectMessageListFragment.this.mAdapter.containsUnreadItem();
    }

    private void onSwipeRefresh() {
        //this.presenter.onSwipeRefresh(new RequestInfo().count(50).sinceID(mAdapter.getLatestItemId()));
        this.refreshLayout.setRefreshing(false);
    }

    @Override
    public void loadMessages(List<DirectMessageEntity> messages) {
        this.mAdapter.updateIfSameUserMessage(messages);
    }

    @Override
    public void updateList(DirectMessageEntity message) {
        this.mAdapter.updateIfSameUserMessage(message);
    }

    @Override
    public void updateList() {
        this.mAdapter.notifyDataSetChanged();
    }

    @Override
    public void setSwipeRefreshState(boolean state) {
        this.refreshLayout.setRefreshing(state);
    }

    public void openDirectMessageEditor(long messageId) {
        this.mCallBack.displayDirectMessageEditor(messageId);
    }

    @Override
    public void setPresenter(RecentlyDirectMessageListPresenter presenter) {
        this.presenter = presenter;
    }

    protected long readLastID() {
        SharedPreferences preferences = getActivity().getSharedPreferences("LatestSeenId", Context.MODE_PRIVATE);
        return preferences.getLong("message", Long.MAX_VALUE);
    }

    public synchronized void write(String key, long id) {
        SharedPreferenceUtil.writeLatestID(getActivity(), key, id);
    }

    @Override
    public String toString() {
        return "Direct Message";
    }
}