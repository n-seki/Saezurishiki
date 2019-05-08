package com.seki.saezurishiki.view.fragment.list;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.seki.saezurishiki.R;
import com.seki.saezurishiki.control.CustomToast;
import com.seki.saezurishiki.control.ScreenNav;
import com.seki.saezurishiki.control.UIControlUtil;
import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.entity.TwitterEntity;
import com.seki.saezurishiki.model.adapter.RequestInfo;
import com.seki.saezurishiki.network.twitter.TwitterError;
import com.seki.saezurishiki.presenter.list.TweetListPresenter;
import com.seki.saezurishiki.view.adapter.TimeLineAdapter;
import com.seki.saezurishiki.view.control.FragmentControl;
import com.seki.saezurishiki.view.fragment.dialog.TweetLongClickDialog;
import com.seki.saezurishiki.view.fragment.dialog.TweetSelectDialog;
import com.seki.saezurishiki.view.fragment.dialog.YesNoSelectDialog;
import com.seki.saezurishiki.view.fragment.dialog.adapter.DialogSelectAction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import twitter4j.TwitterException;

public abstract class TweetListFragment extends Fragment
        implements
        TweetSelectDialog.DialogCallback,
        TweetLongClickDialog.LongClickDialogListener,
        TweetListPresenter.TweetListView {

    protected static final String USER_ID = "user_id";
    final int NEW_LOADING = -0x0003;

    protected TimeLineAdapter mAdapter;
    protected ListView mListView;
    protected View mFooterView;
    protected FragmentControl fragmentControl;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list_view, container, false);
        this.initComponents(rootView);

        rootView.setBackgroundColor(UIControlUtil.backgroundColor(this.getContext()));
        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAdapter = new TimeLineAdapter(getActivity(), R.layout.tweet_layout_with_picture, getPresenter());
        mListView.setAdapter(mAdapter);
    }


    @Override
    public void onResume() {
        super.onResume();
        getPresenter().onResume();

        if (mAdapter.isEmpty()) {
            this.loadTimeLine();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        getPresenter().onPause();
    }


    @Override
    public void onDestroy() {
        mAdapter.clear();
        this.fragmentControl = null;
        super.onDestroy();
    }



    protected void initComponents(View rootView) {
        mListView = (ListView) rootView.findViewById(R.id.list);
        mListView.setOnItemClickListener((parent, view, position, id) -> TweetListFragment.this.onItemClick(position));

        mListView.setOnItemLongClickListener((adapterView, view, position, l) -> TweetListFragment.this.onItemLongClick(position));

        mFooterView = getActivity().getLayoutInflater().inflate(R.layout.read_more_tweet, null);
        mFooterView.setOnClickListener(footer -> TweetListFragment.this.clickReadMoreButton());

        mFooterView.setTag(NEW_LOADING, false);
        mListView.addFooterView(mFooterView, null, true);
        mListView.setSmoothScrollbarEnabled(true);
        mListView.setFooterDividersEnabled(false);
    }


    void onItemClick(int position) {
        final TwitterEntity entity = mAdapter.getEntity(position);
        TweetListFragment.this.showDialog((TweetEntity)entity);
    }


    boolean onItemLongClick(int position) {
        final TwitterEntity entity = mAdapter.getEntity(position);
        getPresenter().onLongClickListItem(entity);
        return true;
    }

    @Override
    public void showLongClickDialog(TweetEntity status) {
        DialogFragment dialog = TweetLongClickDialog.newInstance(status);
        dialog.setTargetFragment(this, 0);
        dialog.show(getFragmentManager(), "TweetLongClickDialog");
    }


    protected void showDialog(TweetEntity status) {
        DialogFragment dialog = TweetSelectDialog.getInstance(status.getId());
        dialog.setTargetFragment(this, 0);
        dialog.show(getFragmentManager(), "tweet_select");
    }


    @Override
    public void openReplyEditor(TweetEntity tweet) {
        final Map<String, Object> args = new HashMap<>();
        args.put("tweet", tweet);
        this.fragmentControl.requestChangeScreen(ScreenNav.TWEET_EDITOR, args);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void showFavoriteDialog(final TweetEntity tweet) {

        YesNoSelectDialog.Listener<TweetEntity> action = (YesNoSelectDialog.Listener<TweetEntity>) tweet1 -> {
            if (tweet1.isFavorited) {
                getPresenter().destroyFavorite(tweet1);
            } else {
                getPresenter().createFavorite(tweet1);
            }
        };

        DialogFragment dialogFragment = YesNoSelectDialog.newFavoriteDialog(tweet,action);
        dialogFragment.show(getChildFragmentManager(), "YesNoSelectDialog");
    }


    @SuppressWarnings("unchecked")
    @Override
    public void showReTweetDialog(final TweetEntity tweet) {
        YesNoSelectDialog.Listener<TweetEntity> action = (YesNoSelectDialog.Listener<TweetEntity>) tweet1 -> getPresenter().reTweet(tweet1);

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
        mAdapter.remove(tweet.getId());
    }

    @Override
    public void catchNewTweet(TweetEntity tweetEntity) {
        this.mAdapter.insert(tweetEntity, 0);
    }


    @Override
    public void updateTweet(TweetEntity tweet) {
        mAdapter.notifyDataSetChanged();
    }


    @Override
    public void loadTweets(List<TweetEntity> tweets) {
        mAdapter.addAll(tweets);
        TextView footerText = (TextView)mFooterView.findViewById(R.id.read_more);
        footerText.setText(R.string.click_to_load);
        mFooterView.setTag(NEW_LOADING, false);
    }

    @Override
    public void hideFooterLoadButton() {
        mFooterView.setVisibility(View.GONE);
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
        getPresenter().onClickDialogItem(selectedItem);
    }

    protected abstract TweetListPresenter getPresenter();

    @Override
    public void displayDetailTweet(long userID, long tweetID) {
        Map<String, Object> args = new HashMap<>();
        args.put("userId", userID);
        args.put("tweetId", tweetID);
        this.fragmentControl.requestChangeScreen(ScreenNav.CONVERSATION, args);
    }

    @Override
    public void showUserActivity(long userID) {
        final Map<String, Object> args = new HashMap<>();
        args.put("userId", userID);
        this.fragmentControl.requestChangeScreen(ScreenNav.USER_ACTIVITY, args);
    }

    @Override
    public void openLink(String url) {
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    @Override
    public void showPicture(TweetEntity tweet, int position) {
        final Map<String, Object> args = new HashMap<>();
        args.put("tweet", tweet);
        args.put("position", position);
        this.fragmentControl.requestChangeScreen(ScreenNav.PICTURE, args);
    }

    @Override
    public void onLongClickDialogItemSelect(DialogSelectAction<TweetEntity> selectedItem) {
        getPresenter().onClickLongClickDialog(selectedItem);
    }


    protected void clickReadMoreButton() {
        final boolean isLoading = (Boolean)mFooterView.getTag(NEW_LOADING);

        if (isLoading) {
            return;
        }

        TextView footerText = (TextView)mFooterView.findViewById(R.id.read_more);
        footerText.setText(R.string.now_loading);
        mFooterView.setTag(NEW_LOADING, true);

        loadTimeLine();
    }

    protected long getLastId() {
        if (mAdapter == null || mAdapter.getCount() == 0) {
            return -1;
        }

        return mAdapter.getItemIdAtPosition(mAdapter.getCount() - 1);
    }


    protected void loadTimeLine() {
        final long maxID = this.getLastId() - 1;
        getPresenter().load(new RequestInfo().maxID(maxID == -1 ? 0 : maxID).count(50));
    }

}
