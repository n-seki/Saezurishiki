package com.seki.saezurishiki.view.fragment.list;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.seki.saezurishiki.R;
import com.seki.saezurishiki.control.UIControlUtil;
import com.seki.saezurishiki.entity.LoadButton;
import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.file.SharedPreferenceUtil;
import com.seki.saezurishiki.model.adapter.RequestInfo;
import com.seki.saezurishiki.network.ConnectionReceiver;
import com.seki.saezurishiki.view.control.RequestTabState;
import com.seki.saezurishiki.view.control.TabManagedView;
import com.seki.saezurishiki.view.control.TabViewControl;
import com.seki.saezurishiki.view.customview.NotificationListView;

import java.util.ArrayList;
import java.util.List;


public class UserStreamTimeLineFragment extends TweetListFragment
                                        implements ConnectionReceiver.Observer, TabManagedView {

    protected List<TweetEntity> mSavedStatuses;

    SwipeRefreshLayout mSwipeRefresher;

    protected long mLastReadId = 0L;

    boolean isNeedLoadButton = false;

    private static final String TAB_POSITION = "tab-position";
    private static final String LIST_NAME = "list-name";
    private int tabPosition;
    private String listName;
    TabViewControl tabViewControl;

    public static TweetListFragment getHomeTimeLine(int tabPosition, String listName) {
        Bundle data = new Bundle();
        data.putInt(TAB_POSITION, tabPosition);
        data.putString(LIST_NAME, listName);
        TweetListFragment home = new UserStreamTimeLineFragment();
        home.setArguments(data);
        return home;
    }

    public static TweetListFragment getReplyTimeLine(int tabPosition, String listName) {
        Bundle data = new Bundle();
        data.putInt(TAB_POSITION, tabPosition);
        data.putString(LIST_NAME, listName);
        TweetListFragment fragment = new UserStreamTimeLineFragment();
        fragment.setArguments(data);
        return fragment;
    }

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
        mAdapter.setBackgroundChange();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list_swipe_refresh, container, false);
        this.initComponents(rootView);

        rootView.setBackgroundColor(UIControlUtil.backgroundColor(this.getContext()));
        return rootView;
    }


    @Override
    protected void initComponents(View rootView) {
        super.initComponents(rootView);
        mListView = (ListView) rootView.findViewById(R.id.list);
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE) {
                    UserStreamTimeLineFragment.this.requestTabChange();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                UserStreamTimeLineFragment.this.onScroll(firstVisibleItem, visibleItemCount, totalItemCount);

            }
        });

        mSwipeRefresher = (SwipeRefreshLayout)rootView.findViewById(R.id.swipe_refresh);
        mSwipeRefresher.setColorSchemeColors(UIControlUtil.colorAccent(getActivity(), twitterAccount.setting.getTheme()));
        mSwipeRefresher.setOnRefreshListener(this::onRefresh);
    }


    private int previousFirstVisibleItem;

    void onScroll(int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (mAdapter == null || mAdapter.getCount() == 0) return;

        //このタイムラインが表示中でなければなにもする必要がない
        if (!this.tabViewControl.isCurrentSelect(this)) {
            return;
        }

        if (firstVisibleItem == 0) {
            UserStreamTimeLineFragment.this.releaseSavedStatus();
        }

        //表示されているアイテム中に未読tweetがある場合には背景色を変更する
        //ここではtab変更は行わない
        if (this.mAdapter.containsUnreadItem(firstVisibleItem, firstVisibleItem + visibleItemCount - 1)) {
            changeTweetBackground(firstVisibleItem, visibleItemCount);
        }

        if (previousFirstVisibleItem < firstVisibleItem) {
            if (firstVisibleItem + visibleItemCount + 10 == totalItemCount) {
                //フッターの読み込みボタンをクリックしたことにする
                clickReadMoreButton();
            }
        }

        this.previousFirstVisibleItem = firstVisibleItem;
    }


    void changeTweetBackground(int firstVisibleItem, int visibleItemCount) {
        ((NotificationListView)this.mListView).changeItemBackground(firstVisibleItem, visibleItemCount);
    }


    protected void onRefresh() {
        this.presenter.load(new RequestInfo().count(50).sinceID(mAdapter.getItemIdAtPosition(0)));
    }

    @Override
    public void onPause() {
        super.onPause();
        mSwipeRefresher.setRefreshing(false); //loadがcancelされるのでくるくるマークを消す
    }

    @Override
    public void loadTweets(List<TweetEntity> tweets) {
        mSwipeRefresher.setRefreshing(false);
        super.loadTweets(tweets);
    }

    @Override
    public void catchNewTweet(TweetEntity tweet) {
        if (mListView.getFirstVisiblePosition() != 0) {
            mSavedStatuses.add(tweet);
            return;
        }

        super.catchNewTweet(tweet);
    }



    @SuppressWarnings("unused")
    void addLoadButton() {
        mAdapter.insertButton(0);
        isNeedLoadButton = false;
    }


    public void onConnect() {
        //do nothing
    }


    public void onDisconnect() {
        isNeedLoadButton = true;
    }


//    //TODO super classの処理を全部コピーしたので問題はないが、リファクタリングで必ず修正すること
//    @Override
//    protected void onLoadFinished(TwitterTaskResult<ResponseList<Status>> result) {
//        isFirstOpen = false;
//        ((TextView)mFooterView.findViewById(R.id.read_more)).setText(R.string.click_to_load);
//
//        if ( result.isException() ) {
//            this.errorProcess(result.getException());
//            ((TextView)mFooterView.findViewById(R.id.read_more)).setText(R.string.click_to_load);
//            return;
//        }
//
//        for (Status status : result.getResult()) {
//            if (status.getId() <= mLastReadId) {
//                mAdapter.addSeenItem(status);
//            } else {
//                mAdapter.add(status);
//            }
//
//        }
//    }


    @SuppressWarnings("unused")
    protected void onClickLoadButton(final long buttonID) {

        final int buttonPosition = mAdapter.getLoadButtonPosition(buttonID);

        changeLoadButtonText(buttonID, true);

        final RequestInfo info = new RequestInfo().count(50)
                                                  .maxID(mAdapter.getItemIdAtPosition(buttonPosition-1) - 1)
                                                  .sinceID(mAdapter.getItemIdAtPosition(buttonPosition+1) + 1);

        this.presenter.load(info);
    }


    void changeLoadButtonText(long buttonID, boolean isClick) {
        final LoadButton button = mAdapter.getButton(buttonID);

        int labelResID = isClick ? R.string.now_loading : R.string.click_to_load;
        button.setLabelResId(labelResID);

        mAdapter.notifyDataSetChanged();
    }


    private void requestTabChange() {
        this.tabViewControl.requestChangeTabState(this);
    }

    @Override
    public int tabPosition() {
        return this.tabPosition;
    }


    @Override
    public void setUserVisibleHint(boolean isUserVisible) {
        if (!isUserVisible) return;
        if (this.mAdapter == null || this.mAdapter.isEmpty()) return;

        final int first = this.mListView.getFirstVisiblePosition();
        final int last = this.mListView.getLastVisiblePosition();
        this.changeTweetBackground(first, last - 1);
        tabViewControl.requestChangeTabState(this);
    }

    @Override
    public RequestTabState getRequestTabState() {
        return () -> UserStreamTimeLineFragment.this.mAdapter.hasUnreadItem() || !UserStreamTimeLineFragment.this.mSavedStatuses.isEmpty();
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


    @Override
    public void onStop() {
        if (!mAdapter.isEmpty()) {
            SharedPreferenceUtil.writeLatestID(getActivity(), this.listName, mAdapter.lastReadId());
        }
        super.onStop();
    }


    protected long readLastID() {
        return SharedPreferenceUtil.readLatestID(getActivity(), this.listName);
    }

}
