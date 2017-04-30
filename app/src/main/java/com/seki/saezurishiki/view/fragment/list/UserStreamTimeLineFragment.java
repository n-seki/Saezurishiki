package com.seki.saezurishiki.view.fragment.list;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.seki.saezurishiki.R;
import com.seki.saezurishiki.control.UIControlUtil;
import com.seki.saezurishiki.entity.LoadButton;
import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.model.adapter.RequestInfo;
import com.seki.saezurishiki.network.ConnectionReceiver;
import com.seki.saezurishiki.network.twitter.AsyncTwitterTask;
import com.seki.saezurishiki.network.twitter.TwitterTaskResult;
import com.seki.saezurishiki.view.control.RequestTabState;
import com.seki.saezurishiki.view.control.TabManagedView;
import com.seki.saezurishiki.view.control.TabViewControl;
import com.seki.saezurishiki.view.customview.NotificationListView;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;

/**
 * ユーザーストリームタイムライン既定クラス<br>
 * Home, replyタイムラインの親クラスです
 * @author seki
 */
public abstract class UserStreamTimeLineFragment extends TimeLineFragment
                                        implements ConnectionReceiver.Observer, TabManagedView {

    protected List<Long> mSavedStatuses;

    //アプリ起動時にタイムラインをスクロールせずに放置していても
    //UserStreamの更新は行いたいので、デフォルトをtrueにする
    protected boolean mListTopVisible = true;

    SwipeRefreshLayout mSwipeRefresher;

    protected long mLastReadId = 0L;

    private static final String TAB_POSITION = "tab-position";
    private int tabPosition;
    TabViewControl tabViewControl;

    public static TweetListFragment getHomeTimeLine(int tabPosition) {
        Bundle data = new Bundle();
        data.putInt(TAB_POSITION, tabPosition);
        TweetListFragment home = HomeTimeLineFragment.getInstance();
        home.setArguments(data);
        return home;
    }

    public static TweetListFragment getReplyTimeLine(int tabPosition) {
        Bundle data = new Bundle();
        data.putInt(TAB_POSITION, tabPosition);
        TweetListFragment fragment = ReplyTimeLineFragment.getInstance();
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.tabPosition = getArguments().getInt(TAB_POSITION);

        if (getActivity() instanceof TabViewControl) {
            this.tabViewControl = (TabViewControl)getActivity();
            //this.tabViewControl.registTabManagedView(this);
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
        mSwipeRefresher.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                UserStreamTimeLineFragment.this.onRefresh();
            }
        });

        super.initComponents(rootView);
    }

    private void requestTabChange() {
        this.tabViewControl.requestChangeTabState(this);
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
            mListTopVisible = true;
        } else {
            mListTopVisible = false;
        }

        //表示されているアイテム中に未読tweetがある場合には背景色を変更する
        //ここではtab変更は行わない
        if (this.mAdapter.containsUnreadItem(firstVisibleItem, firstVisibleItem + visibleItemCount - 1)) {
            changeTweetBackground(firstVisibleItem, visibleItemCount);
        }

        if (previousFirstVisibleItem < firstVisibleItem) {
            if (firstVisibleItem + visibleItemCount + 10 == totalItemCount) {
                loadTimeLine();
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
        mAdapter.addAll(tweets);
    }


    abstract void releaseSavedStatus();


//    @Override
//    public void onStatus(final Status status){
//
//        isNeedSwipeLoad = false; //UserStreamの更新があれば無条件にSwipeロードを無効
//
//        //AdapterにStatusがない場合はなにもしない
//        if (mAdapter.getCount() == 0) return;
//
//        //listの先頭Statusと同一だったらなにもしない
//        if (status.getId() == getListTopStatusID()) return;
//
//        //LoadButtonが不要だったら何もしない
//        if (!isNeedLoadButton) return;
//
//        addLoadButton();
//    }

    @Override
    public void catchNewTweet(TweetEntity tweet) {

        if (isNeedLoadButton) {
            addLoadButton();
        }

        super.catchNewTweet(tweet);
    }


    boolean isNeedLoadButton = false;


    void addLoadButton() {
        final LoadButton button = new LoadButton();
        this.twitterAccount.getRepository().addStatus(button);
        mAdapter.insertButton(button.getId(), 0);
        isNeedLoadButton = false;
    }


    public void onConnect() {
        //do nothing
    }


    public void onDisconnect() {
        isNeedLoadButton = true;
    }


    //TODO super classの処理を全部コピーしたので問題はないが、リファクタリングで必ず修正すること
    @Override
    protected void onLoadFinished(TwitterTaskResult<ResponseList<Status>> result) {
        isFirstOpen = false;
        ((TextView)mFooterView.findViewById(R.id.read_more)).setText(R.string.click_to_load);

        if ( result.isException() ) {
            this.errorProcess(result.getException());
            ((TextView)mFooterView.findViewById(R.id.read_more)).setText(R.string.click_to_load);
            return;
        }

        for (Status status : result.getResult()) {
            if (status.getId() <= mLastReadId) {
                mAdapter.addSeenItem(status);
            } else {
                mAdapter.add(status);
            }

        }
    }


    /**
     * LoadButtonを押した際のLoad処理
     * LoadButtonがListの先頭にあった場合には最新のTweetから200
     * 先頭ではない場合には,LoadButtonの真上のTweet以前の古いTweetをロードする
     * @param buttonID 選択されたLoadButtonのID
     */
    protected void onClickLoadButton(final long buttonID) {

        final int buttonPosition = mAdapter.getLoadButtonPosition(buttonID);

        changeLoadButtonText(buttonID, true);

        final RequestInfo info = new RequestInfo().count(200)
                                                  .maxID(mAdapter.getItemIdAtPosition(buttonPosition-1) - 1)
                                                  .sinceID(mAdapter.getItemIdAtPosition(buttonPosition+1) + 1);

        this.presenter.load(info);
    }

    @Override
    public int tabPosition() {
        return this.tabPosition;
    }

//    @Override
//    public void notifySelectedTabChange() {
//        if (this.mAdapter.isEmpty()) return;
//
//        final int first = this.mListView.getFirstVisiblePosition();
//        final int last = this.mListView.getLastVisiblePosition();
//        this.changeTweetBackground(first, last-1);
//        tabViewControl.requestChangeTabState(this);
//    }

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
        return new RequestTabState() {
            @Override
            public boolean hasUnreadItem() {
                return UserStreamTimeLineFragment.this.mAdapter.hasUnreadItem() || !UserStreamTimeLineFragment.this.mSavedStatuses.isEmpty();
            }
        };
    }


    abstract long readLastID();
}
