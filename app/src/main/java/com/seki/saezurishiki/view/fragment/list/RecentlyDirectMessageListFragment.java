package com.seki.saezurishiki.view.fragment.list;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.seki.saezurishiki.application.SaezurishikiApp;
import com.seki.saezurishiki.R;
import com.seki.saezurishiki.view.adapter.AdapterItem;
import com.seki.saezurishiki.view.adapter.DirectMessageAdapter;
import com.seki.saezurishiki.control.UIControlUtil;
import com.seki.saezurishiki.file.SharedPreferenceUtil;
import com.seki.saezurishiki.network.ConnectionReceiver;
import com.seki.saezurishiki.network.twitter.AsyncTwitterTask;
import com.seki.saezurishiki.network.twitter.TwitterAccount;
import com.seki.saezurishiki.network.twitter.TwitterError;
import com.seki.saezurishiki.network.twitter.TwitterTaskResult;
import com.seki.saezurishiki.network.twitter.TwitterTaskUtil;
import com.seki.saezurishiki.network.twitter.streamListener.DirectMessageUserStreamListener;
import com.seki.saezurishiki.view.control.RequestTabState;
import com.seki.saezurishiki.view.control.TabManagedView;
import com.seki.saezurishiki.view.control.TabViewControl;

import java.util.List;

import twitter4j.DirectMessage;
import twitter4j.Paging;
import twitter4j.TwitterException;

/**
 * ダイレクトメッセージ一覧表示Fragment<br>
 * ログインユーザーが受信しているメッセージを時系列順に表紙します
 * @author seki
 */
public class RecentlyDirectMessageListFragment extends Fragment implements DirectMessageUserStreamListener, ConnectionReceiver.Observer, TabManagedView{

    private DirectMessageAdapter mAdapter;
    private boolean isLoading;
    private CallBack mCallBack;
    private TwitterTaskUtil mTwitterTask;
    private long mLastUnreadMessageId;
    private TwitterAccount twitterAccount;
    private TabViewControl tabViewControl;
    private int tabPosition;
    private SwipeRefreshLayout refreshLayout;

    private static final String TAB_POSITION = "tab-position";

    private void computeAdd(DirectMessage message) {
        long targetSenderId = message.getSenderId();

        for(int i = 0; i < mAdapter.getCount(); i++) {
            AdapterItem item = mAdapter.getItem(i);
            final DirectMessage m = this.twitterAccount.getRepository().getDM(item.itemID);
            if (targetSenderId == m.getSenderId()) {
                if (message.getId() > m.getId()) {
                    mAdapter.setNotifyOnChange(false);
                    mAdapter.remove(item);
                    mAdapter.insert(message.getId(), 0);
                    mAdapter.setNotifyOnChange(true);
                    mAdapter.notifyDataSetChanged();
                }
                return;
            }
        }

        if (message.getId() <= mLastUnreadMessageId) {
            mAdapter.addSeenItem(message.getId());
        } else {
            mAdapter.add(message);
        }
    }


    private void computeAdd(List<DirectMessage> messages) {
        for (DirectMessage message : messages) {
            computeAdd(message);
        }
    }

    @Override
    public void onConnect() {
        this.loadMessageComputeAdd();
    }


    private void loadMessageComputeAdd() {
        if (mAdapter.isEmpty()) {
            this.loadDirectMessage(new Paging().count(50));
            this.loadSendDirectMessage();
        } else {
            this.loadDirectMessage(new Paging().sinceId(mAdapter.getItem(mAdapter.getCount()-1).itemID));
        }
    }

    @Override
    public void onDisconnect() {
        //do nothing
    }

    @Override
    public void onDirectMessage(DirectMessage directMessage) {
        if (directMessage.getSenderId() == this.twitterAccount.getLoginUserId()) {
            return;
        }

        computeAdd(directMessage);
    }

    @Override
    public int tabPosition() {
        return this.tabPosition;
    }

    @Override
    public RequestTabState getRequestTabState() {
        return new RequestTabState() {
            @Override
            public boolean hasUnreadItem() {
                return RecentlyDirectMessageListFragment.this.mAdapter.containsUnreadItem();
            }
        };
    }

    public interface CallBack {
        void displayDirectMessageEditor(long messageId);
    }


    public static Fragment getInstance(int tabPosition) {
        Bundle data = new Bundle();
        data.putInt(TAB_POSITION, tabPosition);
        Fragment fragment = new RecentlyDirectMessageListFragment();
        fragment.setArguments(data);
        return fragment;
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SaezurishikiApp app = (SaezurishikiApp)getActivity().getApplication();
        this.twitterAccount = app.getTwitterAccount();
        this.twitterAccount.addStreamListener(this);
        mAdapter = new DirectMessageAdapter(getActivity(), R.layout.direct_message_layout, twitterAccount.getRepository());
        mAdapter.setBackgroundColor();
        mTwitterTask = new TwitterTaskUtil(getActivity(), getLoaderManager(), twitterAccount);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ConnectionReceiver.addObserver(this);
    }


    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_swipe_refresh, container, false);
        this.initComponents(view);

        view.setBackgroundColor(UIControlUtil.backgroundColor(getActivity()));

        return view;
    }



    private void initComponents(View view) {
        ListView list = (ListView) view.findViewById(R.id.list);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view1, int position, long id) {
                AdapterItem item = mAdapter.getItem(position);
                if (!item.isSeen) {
                    item.see();
                    mAdapter.notifyDataSetChanged();
                }
                RecentlyDirectMessageListFragment.this.openDirectMessageEditor(mAdapter.getItemId(position));
            }
        });

        list.setSmoothScrollbarEnabled(true);
        list.setAdapter(mAdapter);

        this.refreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_refresh);
        this.refreshLayout.setColorSchemeColors(UIControlUtil.colorAccent(getActivity(), twitterAccount.setting.getTheme()));
        this.refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                RecentlyDirectMessageListFragment.this.onSwipeRefresh();
            }
        });
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
    public void onPause() {
        super.onPause();
        this.refreshLayout.setRefreshing(false);
        this.isLoading = false;
    }

    @Override
    public void onStop() {
        if (!mAdapter.isEmpty()) {
            this.write("message", mAdapter.lastReadId());
        }

        super.onStop();
    }


    @Override
    public void onDestroy() {
        this.twitterAccount.removeListener(this);
        super.onDestroy();
    }


    @Override
    public void onResume() {
        super.onResume();
        if (mAdapter.isEmpty()) {
            this.loadDirectMessage(new Paging().count(50));
            this.loadSendDirectMessage();
        }
    }

    private void onSwipeRefresh() {
        this.refreshLayout.setRefreshing(true);
        this.loadMessageComputeAdd();
    }


    private void loadDirectMessage(Paging paging) {
        if (isLoading) return;

        isLoading = true;
        AsyncTwitterTask.AfterTask<List<DirectMessage>> afterTask = new AsyncTwitterTask.AfterTask<List<DirectMessage>>() {
            @Override
            public void onLoadFinish(TwitterTaskResult<List<DirectMessage>> result) {
                isLoading = false;
                refreshLayout.setRefreshing(false);
                if (result.isException()) {
                    RecentlyDirectMessageListFragment.this.errorProcess(result.getException());
                    return;
                }
                twitterAccount.getRepository().addDM(result.getResult());
                computeAdd(result.getResult());
            }
        };

        mTwitterTask.getDirectMessage(afterTask, paging);
    }


    private void openDirectMessageEditor(long messageId) {
        mCallBack.displayDirectMessageEditor(messageId);
    }

    private void loadSendDirectMessage() {
        AsyncTwitterTask.AfterTask<List<DirectMessage>> afterTask1 = new AsyncTwitterTask.AfterTask<List<DirectMessage>>() {
            @Override
            public void onLoadFinish(TwitterTaskResult<List<DirectMessage>> result) {
                if (result.isException()) {
                    return;
                }

                twitterAccount.getRepository().addSentDM(result.getResult());
            }
        };

        mTwitterTask.getSentDirectMessage(afterTask1);
    }


    private void errorProcess(TwitterException exception) {
        TwitterError.showText(getActivity(), exception);
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