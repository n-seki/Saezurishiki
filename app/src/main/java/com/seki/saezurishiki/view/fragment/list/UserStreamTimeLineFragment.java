package com.seki.saezurishiki.view.fragment.list;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.seki.saezurishiki.R;
import com.seki.saezurishiki.control.Setting;
import com.seki.saezurishiki.control.UIControlUtil;
import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.file.SharedPreferenceUtil;
import com.seki.saezurishiki.model.adapter.RequestInfo;
import com.seki.saezurishiki.network.ConnectionReceiver;
import com.seki.saezurishiki.view.control.TabViewControl;

import java.util.ArrayList;
import java.util.List;


public abstract class UserStreamTimeLineFragment extends TweetListFragment
                                        implements ConnectionReceiver.Observer {

    protected List<TweetEntity> mSavedStatuses;

    SwipeRefreshLayout mSwipeRefresher;

    protected long mLastReadId = 0L;

    boolean isNeedLoadButton = false;

    protected static final String TAB_POSITION = "tab-position";
    protected static final String LIST_NAME = "list-name";
    private int tabPosition;
    private String listName;
    TabViewControl tabViewControl;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.tabPosition = getArguments().getInt(TAB_POSITION);
        this.listName = getArguments().getString(LIST_NAME);

        if (getActivity() instanceof TabViewControl) {
            this.tabViewControl = (TabViewControl)getActivity();
        } else {
            throw new IllegalStateException("Activity is not implements UnreadItemNotify!");
        }

        mLastReadId = readLastID();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mSavedStatuses = new ArrayList<>();
        ConnectionReceiver.addObserver(this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list_swipe_refresh, container, false);
        this.initComponents(rootView);

        rootView.setBackgroundColor(UIControlUtil.backgroundColor(container.getContext()));
        return rootView;
    }


    @Override
    protected void initComponents(View rootView) {
        super.initComponents(rootView);
        mRecyclerView = rootView.findViewById(R.id.list);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                // TODO implement
//                UserStreamTimeLineFragment.this.onScroll(firstVisibleItem, visibleItemCount, totalItemCount);
            }
        });

        mSwipeRefresher = rootView.findViewById(R.id.swipe_refresh);
        mSwipeRefresher.setColorSchemeColors(UIControlUtil.colorAccent(getActivity(), new Setting().getTheme()));
        mSwipeRefresher.setOnRefreshListener(this::onRefresh);
    }


    private int previousFirstVisibleItem;

    void onScroll(int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (mAdapter == null || mAdapter.isEmpty()) return;

        if (firstVisibleItem == 0) {
            UserStreamTimeLineFragment.this.releaseSavedStatus();
        }

        if (previousFirstVisibleItem < firstVisibleItem) {
            if (firstVisibleItem + visibleItemCount + 10 == totalItemCount) {
                //フッターの読み込みボタンをクリックしたことにする
                clickReadMoreButton();
            }
        }

        this.previousFirstVisibleItem = firstVisibleItem;
    }


    protected void onRefresh() {
        presenter.load(new RequestInfo().count(50).sinceID(mAdapter.getTweetIdAt(0)));
    }

    @Override
    public void onPause() {
        super.onPause();
        mSwipeRefresher.setRefreshing(false); //loadがcancelされるのでくるくるマークを消す
    }

    @Override
    public void loadTweets(List<TweetEntity> tweets) {
        // TODO model層で判定する
        if (mSwipeRefresher.isRefreshing()) {
            mAdapter.addAllFirst(tweets);
            mSwipeRefresher.setRefreshing(false);
        } else {
            super.loadTweets(tweets);
        }
    }

    public void onConnect() {
        //do nothing
    }

    public void onDisconnect() {
        isNeedLoadButton = true;
    }

    void releaseSavedStatus() {
        //内容が変更していないにも関わらずnotifyDataSetChangeをコールすると問題があるため、
        //セーブデータがない場合には処理を終える
        if (mSavedStatuses == null || mSavedStatuses.isEmpty()) {
            return;
        }

        mAdapter.addAll(mSavedStatuses);
        mAdapter.notifyDataSetChanged();
        mSavedStatuses.clear();
    }

    protected long readLastID() {
        return SharedPreferenceUtil.readLatestID(getActivity(), this.listName);
    }
}
