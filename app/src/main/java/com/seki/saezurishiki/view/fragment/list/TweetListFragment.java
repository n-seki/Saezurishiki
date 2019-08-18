package com.seki.saezurishiki.view.fragment.list;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.seki.saezurishiki.R;
import com.seki.saezurishiki.control.CustomToast;
import com.seki.saezurishiki.control.ScreenNav;
import com.seki.saezurishiki.control.UIControlUtil;
import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.model.GetTweetById;
import com.seki.saezurishiki.model.adapter.RequestInfo;
import com.seki.saezurishiki.network.twitter.TwitterError;
import com.seki.saezurishiki.presenter.list.TweetListPresenter;
import com.seki.saezurishiki.view.adapter.TweetListAdapter;
import com.seki.saezurishiki.view.control.FragmentControl;
import com.seki.saezurishiki.view.fragment.dialog.TweetLongClickDialog;
import com.seki.saezurishiki.view.fragment.dialog.TweetSelectDialog;
import com.seki.saezurishiki.view.fragment.dialog.YesNoSelectDialog;
import com.seki.saezurishiki.view.fragment.dialog.adapter.DialogSelectAction;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import twitter4j.TwitterException;

import static com.seki.saezurishiki.control.ScreenNav.KEY_POSITION;
import static com.seki.saezurishiki.control.ScreenNav.KEY_TWEET;
import static com.seki.saezurishiki.control.ScreenNav.KEY_TWEET_ID;
import static com.seki.saezurishiki.control.ScreenNav.KEY_USER_ID;

public abstract class TweetListFragment extends Fragment
        implements
        TweetSelectDialog.DialogCallback,
        TweetLongClickDialog.LongClickDialogListener,
        TweetListPresenter.TweetListView {

    protected static final String USER_ID = "user_id";

    protected TweetListAdapter mAdapter;
    protected RecyclerView mRecyclerView;
    protected FragmentControl fragmentControl;

    @Inject
    TweetListPresenter presenter;

    @Inject
    GetTweetById repositoryAccessor;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.fragmentControl = (FragmentControl)getActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        Bundle argument = getArguments();
        if (argument == null) {
            throw new IllegalStateException("Argument is null");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list_view, container, false);
        this.initComponents(rootView);

        rootView.setBackgroundColor(UIControlUtil.backgroundColor(container.getContext()));
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.onResume();
        if (mAdapter.isEmpty()) {
            this.loadTimeLine();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.onPause();
    }

    @Override
    public void onDestroy() {
        this.fragmentControl = null;
        super.onDestroy();
    }

    protected void initComponents(View rootView) {
        mRecyclerView = rootView.findViewById(R.id.list);

        Context context = rootView.getContext();

        mAdapter = new TweetListAdapter(
                context,
                repositoryAccessor,
                presenter,
                (view) -> clickReadMoreButton()
        );

        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);

        RecyclerView.ItemDecoration dividerDecoration =
                new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(dividerDecoration);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void showTweetDialog(TweetEntity tweet, int[] forbidAction) {
        showDialog(tweet, forbidAction);
    }

    @Override
    public void showLongClickDialog(TweetEntity status) {
        DialogFragment dialog = TweetLongClickDialog.newInstance(status);
        dialog.setTargetFragment(this, 0);
        dialog.show(Objects.requireNonNull(getFragmentManager()), "TweetLongClickDialog");
    }

    protected void showDialog(TweetEntity status, int[] forbidActions) {
        DialogFragment dialog = TweetSelectDialog.getInstance(status.getId(), forbidActions);
        dialog.setTargetFragment(this, 0);
        dialog.show(Objects.requireNonNull(getFragmentManager()), "tweet_select");
    }

    @Override
    public void openReplyEditor(TweetEntity tweet) {
        Bundle args = new Bundle();
        args.putSerializable(KEY_TWEET, tweet);
        this.fragmentControl.requestChangeScreen(ScreenNav.TWEET_EDITOR, args);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void showFavoriteDialog(final TweetEntity tweet) {
        YesNoSelectDialog.Listener<TweetEntity> action = (YesNoSelectDialog.Listener<TweetEntity>) tweet1 -> {
            if (tweet1.isFavorited) {
                presenter.destroyFavorite(tweet1);
            } else {
                presenter.createFavorite(tweet1);
            }
        };

        DialogFragment dialogFragment = YesNoSelectDialog.newFavoriteDialog(tweet,action);
        dialogFragment.show(getChildFragmentManager(), "YesNoSelectDialog");
    }

    @SuppressWarnings("unchecked")
    @Override
    public void showReTweetDialog(final TweetEntity tweet) {
        YesNoSelectDialog.Listener<TweetEntity> action = (YesNoSelectDialog.Listener<TweetEntity>) tweet1 -> presenter.reTweet(tweet1);

        DialogFragment dialogFragment = YesNoSelectDialog.newRetweetDialog(tweet, action);
        dialogFragment.show(getChildFragmentManager(), "YesNoSelectDialog");
    }

    @Override
    public void completeReTweet(TweetEntity tweet) {
        CustomToast.show(TweetListFragment.this.getActivity(), R.string.re_tweet_done, Toast.LENGTH_SHORT);
    }

    @Override
    public void completeDeleteTweet(TweetEntity tweet) {
        CustomToast.show(TweetListFragment.this.getActivity(), R.string.delete_tweet, Toast.LENGTH_SHORT);
        mAdapter.remove(tweet);
    }

    @Override
    public void catchNewTweet(TweetEntity tweetEntity) {
        // no-op
    }

    @Override
    public void updateTweet(TweetEntity tweet) {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void loadTweets(List<TweetEntity> tweets) {
        mAdapter.addAll(tweets);
        mAdapter.setLoading(false);
    }

    @Override
    public void hideFooterLoadButton() {
        mAdapter.setNeedFooter(false);
    }

    @Override
    public void deletionTweet(long deletedTweetId) {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void errorProcess(Exception e) {
        if (e instanceof TwitterException) {
            TwitterError.showText(getContext(), (TwitterException) e);
            return;
        }

        throw new IllegalStateException(e);
    }

    @Override
    public void onDialogItemClick(DialogSelectAction<TweetEntity> selectedItem) {
        presenter.onClickDialogItem(selectedItem);
    }

    @Override
    public void displayDetailTweet(long userID, long tweetID) {
        Bundle args = new Bundle();
        args.putLong(KEY_USER_ID, userID);
        args.putLong(KEY_TWEET_ID, tweetID);
        this.fragmentControl.requestChangeScreen(ScreenNav.CONVERSATION, args);
    }

    @Override
    public void showUserActivity(long userID) {
        Bundle args = new Bundle();
        args.putLong(KEY_USER_ID, userID);
        this.fragmentControl.requestChangeScreen(ScreenNav.USER_ACTIVITY, args);
    }

    @Override
    public void openLink(String url) {
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    @Override
    public void showPicture(TweetEntity tweet, int position) {
        Bundle args = new Bundle();
        args.putSerializable(KEY_TWEET, tweet);
        args.putInt(KEY_POSITION, position);
        this.fragmentControl.requestChangeScreen(ScreenNav.PICTURE, args);
    }

    @Override
    public void onLongClickDialogItemSelect(DialogSelectAction<TweetEntity> selectedItem) {
        presenter.onClickLongClickDialog(selectedItem);
    }

    protected void clickReadMoreButton() {
        mAdapter.setLoading(true);
        loadTimeLine();
    }

    protected long getLastId() {
        if (mAdapter == null || mAdapter.isEmpty()) {
            return -1;
        }
        return mAdapter.getLastTweetId();
    }

    protected void loadTimeLine() {
        final long maxID = this.getLastId() - 1;
        presenter.load(new RequestInfo().maxID(maxID == -1 ? 0 : maxID).count(50));
    }

}
