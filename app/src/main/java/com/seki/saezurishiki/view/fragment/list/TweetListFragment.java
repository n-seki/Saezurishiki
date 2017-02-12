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
import com.seki.saezurishiki.view.fragment.editor.EditTweetFragment;
import com.seki.saezurishiki.view.fragment.PictureFragment;
import com.seki.saezurishiki.view.fragment.dialog.TweetLongClickDialog;
import com.seki.saezurishiki.view.fragment.dialog.TweetSelectDialog;
import com.seki.saezurishiki.view.fragment.dialog.YesNoSelectDialog;
import com.seki.saezurishiki.network.twitter.AsyncTwitterTask;
import com.seki.saezurishiki.network.twitter.TwitterAccount;
import com.seki.saezurishiki.network.twitter.TwitterError;
import com.seki.saezurishiki.network.twitter.TwitterTaskResult;
import com.seki.saezurishiki.network.twitter.TwitterTaskUtil;
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

    protected TwitterTaskUtil mTwitterTaskUtil;

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
            //TweetListFragment.this.reTweet(status);
        }

        @Override
        public void onClickFavoriteButton(final TweetEntity status, final boolean isShowDialog) {
            if (isShowDialog) {
                TweetListFragment.this.showFavoriteDialog(status);
                return;
            }
            if (status.isFavorited) {
                TweetListFragment.this.destroyFavorite(status);
            } else {
                TweetListFragment.this.createFavorite(status);
            }
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
        mTwitterTaskUtil = new TwitterTaskUtil(getActivity(), getLoaderManager(), this.twitterAccount);

        this.presenter.setTwitterTaskUtil(this.mTwitterTaskUtil);
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


    //このメソッドはpresenterに処理移譲しない
    //Adapter<Tweet>となり、Repositoryも参照しないようになるため
    void onItemClick(int position) {
        TwitterEntity item = this.twitterAccount.getRepository().getTwitterEntity(mAdapter.getItemIdAtPosition(position));
        if (item.getItemType() == TwitterEntity.Type.LoadButton) {
            TweetListFragment.this.onClickLoadButton(item.getId());
            return;
        }

        TweetListFragment.this.showDialog((TweetEntity)item);
    }

    //このメソッドはpresenterに処理移譲しない。理由は上記メソッド参照。
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



    //このメソッドはPresenterに移譲しない。Dialogの表示処理であるため。
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

    //このメソッドはListViewインターフェイスの実装部とし、
    //presenterからコール可能とする
    public void errorProcess( TwitterException twitterException ) {
        TwitterError.showText(getActivity(), twitterException);
    }


    //このメソッドはPresenterに移譲しない。Dialogの表示処理であるため。
    protected void showDialog(TweetEntity status) {
        DialogFragment dialog = TweetSelectDialog.getInstance(status.getId());
        dialog.setTargetFragment(this, 0);
        dialog.show(getFragmentManager(), "tweet_select");
    }


    /**
     * EditTweetFragmentを表示する
     * mStatusがreTweetである場合には元TweetのUserに対してのReplyとなる
     */
    //このメソッドはPresenterに移譲しない。Fragmentの表示処理であるため。
    protected void openReplyEditor(TweetEntity status) {
        Fragment fragment = EditTweetFragment.newReplyEditorFromStatus(status);
        this.fragmentControl.requestShowFragment(fragment);
    }


    //このメソッドはpresenterに移譲しない。Dialogの表示処理であるため。
    @SuppressWarnings("unchecked")
    private void showFavoriteDialog(final TweetEntity status) {
//        DialogFragment dialogFragment =
//                new YesNoSelectDialog.Builder<TweetEntity>()
//                        .setItem(status)
//                        .setTitle(status.isFavorited ? R.string.do_you_un_favorite : R.string.do_you_favorite)
//                        .setSummary(status.user.getName() + "\n" + status.text)
//                        .setPositiveAction(new YesNoSelectDialog.Listener<TweetEntity>() {
//                            @Override
//                            public void onItemClick(TweetEntity item) {
//                                if (status.isFavorited) {
//                                    TweetListFragment.this.destroyFavorite(status);
//                                } else {
//                                    TweetListFragment.this.createFavorite(status);
//                                }
//                            }
//                        })
//                        .setNegativeAction(new YesNoSelectDialog.Listener<TweetEntity>() {
//                            @Override
//                            public void onItemClick(TweetEntity item) {
//                                //do nothing
//                            }
//                        })
//                        .build();

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
//        AsyncTwitterTask.AfterTask<Status> afterTask = new AsyncTwitterTask.AfterTask<Status>() {
//            @Override
//            public void onLoadFinish(TwitterTaskResult<Status> result) {
//                if (result.isException()) {
//                    TweetListFragment.this.errorProcess(result.getException());
//                    return;
//                }
//
//                TweetListFragment.this.setFavoriteStarAndCount(result.getResult());
//            }
//        };
//
//        mTwitterTaskUtil.createFavorite(status.getId(), afterTask);

        this.presenter.createFavorite(tweet);
    }

    //このメソッドは上記メソッドのオーバーロードメソッドであり、presenterに移譲する必要はない。
    protected void createFavorite(long statusID) {
        this.createFavorite(twitterAccount.getRepository().getStatus(statusID));
    }

   //このメソッドはPresenterに処理移譲する必要があるため、移譲した
    /**
     * 非同期でUnFavoriteを行う
     * 正常時はToast表示後にListView更新メソッドを呼び出し,favorite_starを非表示にする
     * ⇒ここではunFavoriteをたたくだけで、あとはUserStreamのコールバックで行う
     */
    protected void destroyFavorite(TweetEntity tweet) {
//        AsyncTwitterTask.AfterTask<Status> afterTask = new AsyncTwitterTask.AfterTask<Status>() {
//            @Override
//            public void onLoadFinish(TwitterTaskResult<Status> result) {
//                if (result.isException()) {
//                    TweetListFragment.this.errorProcess(result.getException());
//                    return;
//                }
//
//                TweetListFragment.this.setFavoriteStarAndCount(result.getResult());
//            }
//        };
//
//        mTwitterTaskUtil.unFavorite(status.getId(), afterTask);

        this.presenter.destroyFavorite(tweet);
    }


    //このメソッドは上記メソッドのオーバーロードメソッドであり、presenterに移譲する必要はない。
    protected void destroyFavorite(long statusID) {
        this.destroyFavorite(twitterAccount.getRepository().getStatus(statusID));
    }


    //このメソッドはPresenterに処理移譲する必要がない。Fragmentの表示処理であるため。
    /**
     * ConversationFragment表示依頼
     */
    protected void displayDetailTweet(TweetEntity status) {
        Fragment conversation = ConversationFragment.getInstance(status.getId());
        this.fragmentControl.requestShowFragment(conversation);
    }


    //このメソッドはPresenterに移譲する必要はない。Activityの表示処理であるため。
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
//        DialogFragment dialogFragment =
//                new YesNoSelectDialog.Builder<TweetEntity>()
//                        .setItem(status)
//                        .setTitle(R.string.do_you_retweet)
//                        .setSummary(status.user.getName() + "\n" + status.text)
//                        .setPositiveAction(new YesNoSelectDialog.Listener<TweetEntity>() {
//                            @Override
//                            public void onItemClick(TweetEntity item) {
//                                TweetListFragment.this.reTweet(status);
//                            }
//                        })
//                        .setNegativeAction(new YesNoSelectDialog.Listener<TweetEntity>() {
//                            @Override
//                            public void onItemClick(TweetEntity item) {
//                                //do nothing
//                            }
//                        })
//                        .build();
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
    /**
     * リツイートを行う
     * mStatusのリツイートを非同期で行う.
     * 正常時はToast表示を行うのみで,その後の処理はUserStreamでの通知時に行う
     */
    private void reTweet(final TweetEntity tweet) {
//        AsyncTwitterTask.AfterTask<Status> afterTask = new AsyncTwitterTask.AfterTask<Status>() {
//            @Override
//            public void onLoadFinish(TwitterTaskResult<Status> result) {
//                if (result.isException()) {
//                    TweetListFragment.this.errorProcess(result.getException());
//                    return;
//                }
//                CustomToast.show(TweetListFragment.this.getActivity(), R.string.re_tweet_done, Toast.LENGTH_SHORT);
//            }
//        };
//
//        mTwitterTaskUtil.createReTweet(status.getId(), afterTask);

        this.presenter.reTweet(tweet);
    }

    protected void reTweet(long statusID) {
        TweetEntity status = this.twitterAccount.getRepository().getStatus(statusID);
        this.reTweet(status);
    }


    @Override
    public void completeDeleteTweet(TweetEntity tweet) {
        CustomToast.show(TweetListFragment.this.getActivity(), R.string.delete_tweet, Toast.LENGTH_SHORT);
        mAdapter.remove(tweet.getId());
    }

    //このメソッドはPresenterへの処理移譲が必要だったため、移譲した。
    /**
     * Status削除処理
     * ログインユーザーのStatusを削除する
     * 削除依頼の応答が正常であればリスト上から該当Statusを消去する
     */
    private void deletePost(TweetEntity tweet) {
//        AsyncTwitterTask.AfterTask<Status> afterTask = new AsyncTwitterTask.AfterTask<Status>() {
//            @Override
//            public void onLoadFinish(TwitterTaskResult<Status> result) {
//                if (result.isException()) {
//                    TweetListFragment.this.errorProcess(result.getException());
//                    return;
//                }
//                CustomToast.show(TweetListFragment.this.getActivity(), R.string.delete_tweet, Toast.LENGTH_SHORT);
//                mAdapter.remove(result.getResult().getId());
//            }
//        };
//
//        mTwitterTaskUtil.destroyStatus(status.getId(), afterTask);

        this.presenter.deleteTweet(tweet);
    }


    //このメソッドは上記メソッドのオーバーロードメソッドであり、presenterに移譲する必要はない。
    protected void deletePost(long statusID) {
        this.deletePost(this.twitterAccount.getRepository().getStatus(statusID));
    }


//Presenter.View.updateTweetの実装に変更したため削除
//    /**
//     * 他のユーザーがログインユーザーのStatusをお気に入りに登録、またはお気に入りから削除したときに
//     * リストから該当するStatusを検索し、画面上に表示中である場合にはfavorite_starを表示状態を変更する。
//     * またListで保持しているStatusを更新する
//     * @param status UserStreamで検知されたお気に入りStatus
//     */
//    private void setFavoriteStarAndCount(final Status status) {
//        this.update(status.getId());
//    }


    //Contextが必要なので一旦移譲しない。Presenterの責務範囲決定後に移譲するかも。
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
     * @param position clickされたURLのURLEntity[]内でのposition
     */
    private void openLink(TweetEntity status, int position) {
        String url = status.urlEntities[position].getURL();
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

        this.update(deletionNotice.getStatusId());
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
        update(status.getId());
    }
    @Override
    public void onUnFavorite(User sourceUser, User targetUser, Status status) {
        if (sourceUser.getId() == twitterAccount.getLoginUserId()) {
            return;
        }
        update(status.getId());
    }


    public synchronized void update(long id) {
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



    public void onDialogItemClick(TweetEntity status, int position) {
        switch ( position ) {
            case TweetSelectDialog.REPLY :
                this.openReplyEditor(status);
                break;

            case TweetSelectDialog.FAVORITE :
                this.createFavorite(status);
                break;

            case TweetSelectDialog.ReTWEET :
                this.reTweet(status);
                break;

//            case TweetSelectDialog.QUOTED_TWEET:
//                this.openQuotedTweetEditor(status);
//                break;

            case TweetSelectDialog.CONVERSATION :
                this.displayDetailTweet(status);
                break;

            case TweetSelectDialog.UN_FAVORITE:
                this.destroyFavorite(status);
                break;

            case TweetSelectDialog.SHOW_TWEET:
                this.displayDetailTweet(status);
                break;

            default:
                if (TweetSelectDialog.BIOGRAPHY <= position && position < TweetSelectDialog.URL) {
                    this.displayBiography(status, position - TweetSelectDialog.BIOGRAPHY);
                    break;
                }

                if (TweetSelectDialog.URL <= position && position < TweetSelectDialog.MEDIA) {
                    this.openLink(status, position - TweetSelectDialog.URL);
                    break;
                }

                if (TweetSelectDialog.MEDIA <= position) {
                    List<String> medias = UIControlUtil.createMediaURLList(status);
                    Fragment f = PictureFragment.getInstance(medias.get(position - TweetSelectDialog.MEDIA), status);
                    this.fragmentControl.requestShowFragment(f);
                    break;
                }

                throw new IllegalStateException("resultCode is not legal");
        }
    }

    @Override
    public void onLongClickDialogItemSelect(int action, long statusID) {
        switch (action) {
            case TweetLongClickDialog.DELETE :
                this.deletePost(statusID);
                break;

            case TweetLongClickDialog.RE_TWEET:
                this.reTweet(statusID);
                break;

            case TweetLongClickDialog.UN_RE_TWEET:
                //TODO
                this.deletePost(statusID);
                break;

            case TweetLongClickDialog.FAVORITE:
                this.createFavorite(statusID);
                break;

            case TweetLongClickDialog.UN_FAVORITE:
                this.destroyFavorite(statusID);
                break;

            default:
                throw new IllegalArgumentException("action is invalid! : " + action);
        }
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
