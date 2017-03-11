package com.seki.saezurishiki.view.fragment.list;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.seki.saezurishiki.application.SaezurishikiApp;
import com.seki.saezurishiki.R;
import com.seki.saezurishiki.presenter.list.TweetListPresenter;
import com.seki.saezurishiki.view.adapter.AdapterItem;
import com.seki.saezurishiki.view.adapter.TimeLineAdapter;
import com.seki.saezurishiki.control.CustomToast;
import com.seki.saezurishiki.control.StatusUtil;
import com.seki.saezurishiki.control.UIControlUtil;
import com.seki.saezurishiki.entity.LoadButton;
import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.entity.TwitterEntity;
import com.seki.saezurishiki.view.fragment.dialog.adapter.DialogSelectAction;
import com.seki.saezurishiki.view.fragment.editor.EditTweetFragment;
import com.seki.saezurishiki.view.fragment.PictureFragment;
import com.seki.saezurishiki.view.fragment.dialog.TweetLongClickDialog;
import com.seki.saezurishiki.view.fragment.dialog.TweetSelectDialog;
import com.seki.saezurishiki.view.fragment.dialog.YesNoSelectDialog;
import com.seki.saezurishiki.network.twitter.TwitterAccount;
import com.seki.saezurishiki.network.twitter.TwitterError;
import com.seki.saezurishiki.network.twitter.TwitterTaskResult;
import com.seki.saezurishiki.network.twitter.TwitterWrapper;
import com.seki.saezurishiki.network.twitter.streamListener.StatusUserStreamListener;
import com.seki.saezurishiki.view.control.FragmentControl;

import java.util.List;

import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.TwitterException;
import twitter4j.User;

/**
 * StatusをListViewで表示するFragmentの既定クラス
 * 全てのタイムライン系Fragmentはこのクラスを継承することで
 * タイムライン操作に必要なメソッドにアクセスできます
 * @author seki
 */
public abstract class TweetListFragment extends Fragment
        implements
        StatusUserStreamListener,
        TweetSelectDialog.DialogCallback,
        TweetLongClickDialog.LongClickDialogListener,
        TweetListPresenter.TweetListView {

    protected TimeLineAdapter mAdapter;

    protected TwitterWrapper mTwitterWrapper;

    protected ListView mListView;

    protected View mFooterView;

    protected FragmentControl fragmentControl;

    protected TwitterAccount twitterAccount;


    TweetListPresenter presenter;


    protected final TimeLineAdapter.ViewListener listener = new TimeLineAdapter.ViewListener() {
        @Override
        public void onClickPicture(String pictureURL, TweetEntity status) {
            Fragment picture = PictureFragment.getInstance(pictureURL, status);
            fragmentControl.requestShowFragment(picture);
        }

        @Override
        public void onClickUserIcon(User user) {
            fragmentControl.requestShowUser(user.getId());
        }

        @Override
        public void onClickReplyButton(TweetEntity status) {
            TweetListFragment.this.openReplyEditor(status);
        }

        @Override
        public void onClickReTweetButton(TweetEntity status, boolean isShowDialog) {
            if (isShowDialog) {
                TweetListFragment.this.showReTweetDialog(status);
                return;
            }

            presenter.onClickRetweetButton(status);
        }

        @Override
        public void onClickFavoriteButton(final TweetEntity status, final boolean isShowDialog) {
            if (isShowDialog) {
                TweetListFragment.this.showFavoriteDialog(status);
                return;
            }

            presenter.onClickFavoriteButton(status);
        }

        @Override
        public void onClickQuotedTweet(final TweetEntity status) {
            TweetListFragment.this.displayDetailTweet(status);
        }
    };


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.fragmentControl = (FragmentControl)getActivity();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SaezurishikiApp app = (SaezurishikiApp)getActivity().getApplication();
        this.twitterAccount = app.getTwitterAccount();
        this.twitterAccount.addStreamListener(this);
        setRetainInstance(true);

        this.presenter = new TweetListPresenter(this, null, this.twitterAccount);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list_view, container, false);
        this.initComponents(rootView);

        rootView.setBackgroundColor(UIControlUtil.backgroundColor(this.getContext()));
        return rootView;
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAdapter = new TimeLineAdapter(getActivity(), R.layout.tweet_layout_with_picture, listener, twitterAccount);
        mListView.setAdapter(mAdapter);
        mTwitterWrapper = new TwitterWrapper(getActivity(), getLoaderManager(), this.twitterAccount);

        this.presenter.setTwitterWrapper(this.mTwitterWrapper);
    }


    /**
     * onResume
     * 初回起動時であればTimeLimeの読み込みを行う
     */
    @Override
    public void onResume() {
        super.onResume();

        if (mAdapter.isEmpty()) {
            this.loadTimeLine();
        }
    }


    @Override
    public void onDestroy() {
        mAdapter.clear();
        this.twitterAccount.removeListener(this);
        this.fragmentControl = null;
        super.onDestroy();
    }



    protected void initComponents(View rootView) {
        mListView = (ListView) rootView.findViewById(R.id.list);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TweetListFragment.this.onItemClick(position);
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                return TweetListFragment.this.onItemLongClick(position);
            }
        });

        mFooterView = getActivity().getLayoutInflater().inflate(R.layout.read_more_tweet, null);
        mFooterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View footer) {
                TweetListFragment.this.clickReadMoreButton(footer);
            }
        });

        mListView.addFooterView(mFooterView, null, true);
        mListView.setSmoothScrollbarEnabled(true);
        mListView.setFooterDividersEnabled(false);
    }


    void onItemClick(int position) {
        TwitterEntity item = this.twitterAccount.getRepository().getTwitterEntity(mAdapter.getItemIdAtPosition(position));
        if (item.getItemType() == TwitterEntity.Type.LoadButton) {
            TweetListFragment.this.onClickLoadButton(item.getId());
            return;
        }

        TweetListFragment.this.showDialog((TweetEntity)item);
    }


    boolean onItemLongClick(int position) {
        TwitterEntity item = this.twitterAccount.getRepository().getStatus(mAdapter.getItemIdAtPosition(position));
        if (mAdapter.getItem(position).isButton()) {
            return true;
        }

        if (this.twitterAccount.getRepository().hasDeletionNotice(item.getId())) {
            return true;
        }

        TweetListFragment.this.showLongClickDialog((TweetEntity)item);
        return true;
    }



    void showLongClickDialog(TweetEntity status) {
        DialogFragment dialog = TweetLongClickDialog.newInstance(status);
        dialog.setTargetFragment(this, 0);
        dialog.show(getChildFragmentManager(), "TweetLongClickDialog");
    }


    /**
     * Fragmentが初回表示か否かのフラグ
     * より正確にはonResumeを通るのが初回か否かを示す
     * onResumeでtweetsの読み込みを行うため、このフラグがtrueの場合にはmAdapterの要素が
     * 0以上であることを示す。このフラグがfalseになるのはisFirstOpenがtrue時のloadTimeLine
     * 呼び出しで、非同期通信の結果が正常だった時である。
     */
    protected boolean isFirstOpen = true;


    public void errorProcess( TwitterException twitterException ) {
        TwitterError.showText(getActivity(), twitterException);
    }


    protected void showDialog(TweetEntity status) {
        DialogFragment dialog = TweetSelectDialog.getInstance(status.getId());
        dialog.setTargetFragment(this, 0);
        dialog.show(getFragmentManager(), "tweet_select");
    }


    /**
     * EditTweetFragmentを表示する
     * mStatusがreTweetである場合には元TweetのUserに対してのReplyとなる
     */
    protected void openReplyEditor(TweetEntity status) {
        Fragment fragment = EditTweetFragment.newReplyEditorFromStatus(status);
        this.fragmentControl.requestShowFragment(fragment);
    }


    //このメソッドはpresenterに移譲しない。Dialogの表示処理であるため。
    @SuppressWarnings("unchecked")
    private void showFavoriteDialog(final TweetEntity status) {

        YesNoSelectDialog.Listener<TweetEntity> action = new YesNoSelectDialog.Listener<TweetEntity>() {
            @Override
            public void onItemClick(TweetEntity tweet) {
                if (status.isFavorited) {
                    TweetListFragment.this.destroyFavorite(status);
                } else {
                    TweetListFragment.this.createFavorite(status);
                }
            }
        };

        DialogFragment dialogFragment = YesNoSelectDialog.newFavoriteDialog(status,action);
        dialogFragment.show(getChildFragmentManager(), "YesNoSelectDialog");
    }


    //このメソッドはPresenterに処理移譲する必要があったため、移譲した。
    /**
     * 非同期でFavoriteを行う
     * 正常時はToast表示後にListView更新メソッドを呼び出し,favorite_starを表示する
     * ⇒ここではcreateFavoriteを叩くだけで、後はuserStreamのコールバックで行う
     */
    protected void createFavorite(TweetEntity tweet) {
        this.presenter.createFavorite(tweet);
    }


    protected void createFavorite(long statusID) {
        this.createFavorite(twitterAccount.getRepository().getStatus(statusID));
    }


    /**
     * 非同期でUnFavoriteを行う
     * 正常時はToast表示後にListView更新メソッドを呼び出し,favorite_starを非表示にする
     * ⇒ここではunFavoriteをたたくだけで、あとはUserStreamのコールバックで行う
     */
    protected void destroyFavorite(TweetEntity tweet) {
        this.presenter.destroyFavorite(tweet);
    }


    protected void destroyFavorite(long statusID) {
        this.destroyFavorite(twitterAccount.getRepository().getStatus(statusID));
    }



    /**
     * ConversationFragment表示依頼
     */
    protected void displayDetailTweet(TweetEntity status) {
        Fragment conversation = ConversationFragment.getInstance(status.getId());
        this.fragmentControl.requestShowFragment(conversation);
    }


    /**
     * BiographyActivity表示依頼
     * 保持しているStatusがリツイートの場合にはリツイートされたStatusのUser情報表示
     * を依頼する
     */
    private void displayBiography(TweetEntity status, int position) {
        List<Long> users = StatusUtil.getAllUserMentionId(status, this.twitterAccount.getLoginUserId());
        this.fragmentControl.requestShowUser(users.get(position));
    }


    @SuppressWarnings("unchecked")
    private void showReTweetDialog(final TweetEntity tweet) {
        YesNoSelectDialog.Listener<TweetEntity> action = new YesNoSelectDialog.Listener<TweetEntity>() {
            @Override
            public void onItemClick(TweetEntity item) {
                TweetListFragment.this.reTweet(tweet);
            }
        };

        DialogFragment dialogFragment = YesNoSelectDialog.newRetweetDialog(tweet, action);
        dialogFragment.show(getChildFragmentManager(), "YesNoSelectDialog");
    }


    @Override
    public void completeReTweet(TweetEntity tweet) {
        CustomToast.show(TweetListFragment.this.getActivity(), R.string.re_tweet_done, Toast.LENGTH_SHORT);
    }



    protected void reTweet(TweetEntity tweet) {
        this.presenter.reTweet(tweet.getId());
    }


    @Override
    public void completeDeleteTweet(TweetEntity tweet) {
        CustomToast.show(TweetListFragment.this.getActivity(), R.string.delete_tweet, Toast.LENGTH_SHORT);
        mAdapter.remove(tweet.getId());
    }



    protected void deletePost(long tweetID) {
        this.presenter.deleteTweet(tweetID);
    }


    /**
     * Statusのtextをコピーします
     */
    private void copyText(final TweetEntity status) {
        ClipboardManager clipboardManager = (ClipboardManager)TweetListFragment.this.getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("tweet", status.text);
        clipboardManager.setPrimaryClip(clip);

        CustomToast.show(getActivity(), R.string.copy_done, Toast.LENGTH_SHORT);
    }

    //Activityの表示処理（暗示的Intent)なのでPresenterに処理移譲はしない。
    /**
     * TweetDialogFragmentでclickされたURLを表示する
     * URLをuriとして暗黙的Intentで外部アプリを起動する
     * @param url 表示したいURL
     */
    private void openLink(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }


    /**
     * loadTimeLineでの非同期通信の結果が正常だった場合に
     * どのFragmentでも行う共通の処理
     */
    abstract void onLoadFinished(TwitterTaskResult<ResponseList<Status>> result);


    /**
     * LoadButtonの押下でロードしたStatusをAdapterにセットする
     * @param statusList StatusのList
     * @param buttonID 選択されたLoad ButtonのID
     */
    void setStatusIntoList(List<Status> statusList, long buttonID) {
        //読み込んだStatusが0の場合には取得漏れはないと判断してButtonを消して終わり
        if (statusList.isEmpty()) {
            removeLoadButton(buttonID);
            return;
        }

        int buttonPosition = mAdapter.getLoadButtonPosition(buttonID);

        for (Status status : statusList) {
            mAdapter.insert(status.getId(), buttonPosition++);
        }

        if (statusList.size() < 200) {
            removeLoadButton(buttonID);
            return;
        }

        changeLoadButtonText(buttonID, false);
    }


    void removeLoadButton(long buttonID) {
        mAdapter.remove(buttonID);
    }


    @Override
    public void onDeletionNotice(final StatusDeletionNotice deletionNotice) {
        if (deletionNotice.getUserId() == this.twitterAccount.getLoginUserId()) {
            mAdapter.remove(deletionNotice.getStatusId());
            return;
        }

        this.updateTweet(deletionNotice.getStatusId());
    }

    @Override
    public void onStatus(final Status status) {
        //do nothing
    }

    @Override
    public void onFavorite(User sourceUser, User targetUser, Status status) {
        if (sourceUser.getId() == twitterAccount.getLoginUserId()) {
            return;
        }
        updateTweet(status.getId());
    }
    @Override
    public void onUnFavorite(User sourceUser, User targetUser, Status status) {
        if (sourceUser.getId() == twitterAccount.getLoginUserId()) {
            return;
        }
        updateTweet(status.getId());
    }


    public synchronized void updateTweet(long id) {
        if (this.mAdapter.isEmpty()) return;
        int visibleTop = mListView.getFirstVisiblePosition();
        int visibleLast = mListView.getLastVisiblePosition();

        for (int position = visibleTop; position <= visibleLast; position++) {
            long itemId = ((AdapterItem)mListView.getItemAtPosition(position)).itemID;
            if (id == itemId) {
                View view = mListView.getChildAt(position - visibleTop);
                mAdapter.getView(position, view, null);
                return;
            }
        }

        mListView.getAdapter();
    }


    @Override
    public void updateTweet(TweetEntity tweet) {
        this.updateTweet(tweet.getId());
    }

    @Override
    public void errorProcess(Exception e) {
        if (e instanceof TwitterException) {
            TwitterError.showText(getContext(), (TwitterException) e);
            return;
        }

        throw new IllegalStateException(e);
    }



    public void onDialogItemClick(DialogSelectAction<TweetEntity> selectedItem) {
        switch (selectedItem.action) {
            case DialogSelectAction.SHOW_TWEET:
                this.displayDetailTweet(selectedItem.targetItem);
                break;

            case DialogSelectAction.BIOGRAPHY:
                final long targetUserId = (Long)selectedItem.item;
                this.fragmentControl.requestShowUser(targetUserId);
                break;

            case DialogSelectAction.URL:
                final String targetUrl = (String)selectedItem.item;
                this.openLink(targetUrl);
                break;

            case DialogSelectAction.MEDIA:
                final String targetMedia = (String)selectedItem.item;
                Fragment f = PictureFragment.getInstance(targetMedia, selectedItem.targetItem);
                this.fragmentControl.requestShowFragment(f);
                break;

            default:
                throw new IllegalArgumentException("action is invalid! : " + selectedItem.action);
        }
    }

    @Override
    public void onLongClickDialogItemSelect(DialogSelectAction<TweetEntity> selectedItem) {
        this.presenter.onClickLongClickDialog(selectedItem);
    }


    protected void clickReadMoreButton(View footer) {
        TextView footerText = (TextView)footer.findViewById(R.id.read_more);
        footerText.setText(R.string.now_loading);

        loadTimeLine();
    }


    protected void changeLoadButtonText(long buttonID, boolean isClick) {
        TwitterEntity entity = this.twitterAccount.getRepository().getTwitterEntity(buttonID);

        if (entity == null || entity.getItemType() != TwitterEntity.Type.LoadButton) {
            return;
        }

        final LoadButton button = (LoadButton)entity;

        int labelResID = isClick ? R.string.now_loading : R.string.click_to_load;
        button.setLabelResId(labelResID);

        mAdapter.notifyDataSetChanged();
    }


    @SuppressWarnings("unused")
    protected void openQuotedTweetEditor(TweetEntity status) {
        Fragment fragment = EditTweetFragment.newQuotedTweetEditor(status);
        this.fragmentControl.requestShowFragment(fragment);
    }



    /**
     * タイムラインロード処理
     * このメソッド内でTwitterAsyncTaskによってStatus等のTwitter情報を取得する
     * このクラスを継承するクラスでは当メソッドをOverrideする必要がある
     */
    protected abstract void loadTimeLine();
    protected abstract void onClickLoadButton(long buttonId);

}
