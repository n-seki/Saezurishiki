package com.seki.saezurishiki.view.fragment.list;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.seki.saezurishiki.network.ConnectionReceiver;
import com.seki.saezurishiki.network.twitter.AsyncTwitterTask;
import com.seki.saezurishiki.network.twitter.TwitterTaskResult;
import com.seki.saezurishiki.view.customview.NotificationListView;
import com.seki.saezurishiki.view.control.RequestTabState;
import com.seki.saezurishiki.view.control.TabManagedView;
import com.seki.saezurishiki.view.control.TabViewControl;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;

//import com.seki.saezurishiki.network.server.TwitterItem;

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

    private SwipeRefreshLayout mSwipeRefresher;

    protected long mLastReadId = 0L;

    //protected boolean isSwipeRefreshing = false;

    //SwipeRefreshによる更新が必要かどうか
    //初回起動時はロードを許可するがSwipeRefreshでは最新のStatusから取得するため
    //基本的に一度Swipeでロードしたら、その後の更新はUserStreamに任せる
    //UserStreamの切断⇒復旧時にはUserStreamによる反映がされるまではSwipeによるロードを
    //有効とするが、UserStreamによって先頭に最新Statusが追加された場合には無効として、
    //未取得Statusの取得についてはLoadButtonで行う.
    private boolean isNeedSwipeLoad = true;

    private static final String TAB_POSITION = "tab-position";
    private int tabPosition;
    TabViewControl tabViewControl;

    public static Fragment getHomeTimeLine(int tabPosition) {
        Bundle data = new Bundle();
        data.putInt(TAB_POSITION, tabPosition);
        Fragment home = HomeTimeLineFragment.getInstance();
        home.setArguments(data);
        return home;
    }

    public static Fragment getReplyTimeLine(int tabPosition) {
        Bundle data = new Bundle();
        data.putInt(TAB_POSITION, tabPosition);
        Fragment fragment = ReplyTimeLineFragment.getInstance();
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
            if (firstVisibleItem + visibleItemCount + 10 == totalItemCount && !mIsLoading) {
                UserStreamTimeLineFragment.this.loadTimeLine();
            }
        }

        this.previousFirstVisibleItem = firstVisibleItem;
    }


    void changeTweetBackground(int firstVisibleItem, int visibleItemCount) {
        ((NotificationListView)this.mListView).changeItemBackground(firstVisibleItem, visibleItemCount);
    }


    protected void onRefresh() {
        if ( !isNeedSwipeLoad ) {
            mSwipeRefresher.setRefreshing(false);
            return;
        }

        swipeRefreshTimeLine();
    }


    void swipeRefreshTimeLine() {
        new AsyncTwitterTask<>(getActivity(), this.getSwipeTask(), SWIPE_REFRESH_AFTER_TASK, getLoaderManager()).run();
    }


    @Override
    public void loadTimeLine() {
        if (mIsLoading) return;
        mIsLoading = true;

        new AsyncTwitterTask<>(getActivity(), this.getStatusesLoader(), TASK_AFTER_LOAD, getLoaderManager()).run();

    }

    @Override
    public void onPause() {
        super.onPause();
        mSwipeRefresher.setRefreshing(false); //loadがcancelされるのでくるくるマークを消す
    }


    private AsyncTwitterTask.AfterTask<ResponseList<Status>> SWIPE_REFRESH_AFTER_TASK = new AsyncTwitterTask.AfterTask<ResponseList<Status>>() {
        @Override
        public void onLoadFinish(TwitterTaskResult<ResponseList<Status>> result) {
            mIsLoading = false;
            mSwipeRefresher.setRefreshing(false);

            if(result.isException()) {
                UserStreamTimeLineFragment.this.errorProcess(result.getException());
                return;
            }

            isNeedSwipeLoad = false; //一度でもSwipeでロード成功したら次回以降は無効
            UserStreamTimeLineFragment.this.computeSwipeRefreshedStatus(result.getResult());
        }
    };


    protected void computeSwipeRefreshedStatus(ResponseList<Status> statuses) {
        isNeedLoadButton = false;

        if(statuses.size() < 1) {
            return;
        }

        //Statusの反映が初めてなら何も考えずに全てをListの先頭に反映
        if ( mAdapter.getCount() == 0 ) {
            setStatusToTop(statuses);
            return;
        }

        final Status loadOldestStatus = statuses.get(statuses.size()-1);
        final TweetEntity listTopStatus = this.twitterAccount.getRepository().getStatus(mAdapter.getItemIdAtPosition(0));

        //取得したStatusのListの最後（一番古いStatus）がListの先頭のStatusよりも古い場合は,
        //取得漏れしているStatusはないため,Listの内容と重複するStatusをremoveして先頭に反映
        if ( loadOldestStatus.getCreatedAt().compareTo(listTopStatus.createdAt) <= 0 ) {
            final List<Status> nonOverlapStatues = removeOverlapStatus(statuses);
            setStatusToTop(nonOverlapStatues);
            return;
        }

        //読み込んだもっとも古いStatusがListの先頭より新しい場合は取得漏れしているStatusがある
        //よってLoadButtonをListに設定する.設定後は何も考えずに先頭に反映すればいい（重複がないので）
        addLoadButton();
        setStatusToTop(statuses);
    }


    /**
     * Statusのリストから既にAdapterが保持しているStatusを取り除いた新しいリストを作って返す.
     * 順番は変えない. ⇒ ViewUtilityみたいなクラスを作って移動
     * @param statuses StatusのList
     * @return mAdapterとの重複を除いたStatusのList
     */
    List<Status> removeOverlapStatus(ResponseList<Status> statuses) {
        final List<Status> list = new ArrayList<>();

        final ListIterator<Status> itr = statuses.listIterator(0);
        final TweetEntity listTopStatus = this.twitterAccount.getRepository().getStatus(mAdapter.getItemIdAtPosition(0));

        while(itr.hasNext()) {
            final Status status = itr.next();
            if (listTopStatus.getId() >= status.getId()) {
                continue;
            }

            list.add(status);
        }

        return list;
    }


    /**
     * StatusのListをAdapterの先頭にinsertする ⇒ Adapterに移動
     * @param statuses StatusのList
     */
    void setStatusToTop(List<Status> statuses) {
        ListIterator<Status> itr = statuses.listIterator(statuses.size());

        while(itr.hasPrevious()) {
            Status status = itr.previous();
            if (status.getId() <= mLastReadId) {
                mAdapter.insertSeenItem(status, 0);
            } else {
                mAdapter.insert(status.getId(), 0);
            }
        }
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
        isNeedSwipeLoad = true; //ネットワーク接続が発生したらSwipeロードを有効に
    }


    //TODO super classの処理を全部コピーしたので問題はないが、リファクタリングで必ず修正すること
    @Override
    protected void onLoadFinished(TwitterTaskResult<ResponseList<Status>> result) {
        isFirstOpen = false;
        mIsLoading = false;
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
        final Paging paging = new Paging().count(200);

        final int buttonPosition = mAdapter.getLoadButtonPosition(buttonID);

        if (buttonPosition != -1) {
            //ボタンの前後のstatusはロードしない
            paging.maxId(mAdapter.getItemIdAtPosition(buttonPosition-1) - 1)
                    .sinceId(mAdapter.getItemIdAtPosition(buttonPosition+1) + 1);
        }

        changeLoadButtonText(buttonID, true);

        AsyncTwitterTask.AfterTask<ResponseList<Status>> AFTER_TASK = new AsyncTwitterTask.AfterTask<ResponseList<Status>>() {
            @Override
            public void onLoadFinish(TwitterTaskResult<ResponseList<Status>> result) {
                if (result.isException()) {
                    changeLoadButtonText(buttonID, false);
                    UserStreamTimeLineFragment.this.errorProcess(result.getException());
                    return;
                }

                UserStreamTimeLineFragment.this.setStatusIntoList(result.getResult(), buttonID);
            }
        };

        runLoadButtonClickedTask(paging, AFTER_TASK);
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
    abstract AsyncTwitterTask.AsyncTask<ResponseList<Status>> getSwipeTask();
    abstract void runLoadButtonClickedTask(Paging paging, AsyncTwitterTask.AfterTask<ResponseList<Status>> afterTask);
}
