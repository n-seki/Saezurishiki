package com.seki.saezurishiki.view.fragment.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.Window;
import android.widget.ListView;

import com.seki.saezurishiki.R;
import com.seki.saezurishiki.application.SaezurishikiApp;
import com.seki.saezurishiki.control.Setting;
import com.seki.saezurishiki.control.StatusUtil;
import com.seki.saezurishiki.control.UIControlUtil;
import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.network.server.TwitterServer;
import com.seki.saezurishiki.view.fragment.dialog.adapter.DialogSelectAction;
import com.seki.saezurishiki.view.fragment.util.DataType;

import java.util.List;

import twitter4j.URLEntity;

/**
 * LONG touchされたStatusのダイアログ<br>
 * textコピーやURL選択によるブラウザ起動など
 * @author seki
 */
public class TweetSelectDialog extends DialogFragment {

    private boolean mIsDelete;
    private TweetEntity mStatus;
    private DialogCallback mListener;
    private long loginUserId;
    private int theme;

    public interface DialogCallback {
        void onDialogItemClick(DialogSelectAction<TweetEntity> action);
    }


    public static DialogFragment getInstance(long statusId) {
        DialogFragment dialog = new TweetSelectDialog();
        Bundle data = new Bundle();
        data.putLong(DataType.STATUS_ID, statusId);
        dialog.setArguments(data);
        return dialog;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        long id = getArguments().getLong(DataType.STATUS_ID);

        SaezurishikiApp app = (SaezurishikiApp) getActivity().getApplication();
        TwitterServer repository = app.getTwitterAccount().getRepository();
        this.theme = new Setting().getTheme();

        TweetEntity status = repository.getTweet(id);
        mStatus = status.isRetweet ? repository.getTweet(status.retweetedStatusId) : status;
        mIsDelete = repository.hasDeletionNotice(mStatus.getId());
        this.loginUserId = app.getTwitterAccount().getLoginUserId();
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListener = (DialogCallback)TweetSelectDialog.this.getTargetFragment();
    }



    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getTargetFragment().getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_list_view);
        this.initDialog(dialog);

        return dialog;
    }


    void initDialog(Dialog dialog) {
        ListView listView = (ListView) dialog.findViewById(R.id.list);
        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            @SuppressWarnings("unchecked")
            final DialogSelectAction<TweetEntity> action = (DialogSelectAction)view.getTag();
            mListener.onDialogItemClick(action);
            dismiss();
        });

        DialogItemAdapter adapter =
                new DialogItemAdapter(getActivity(), getActivity().getLayoutInflater(), R.layout.dialog_list_item);
        listView.setAdapter(adapter);

        final boolean isThemeDark = this.theme == R.style.AppTheme_Dark;

        if (mIsDelete) {
            String userName = mStatus.user.getScreenName();
            final int icon = isThemeDark ? R.drawable.drawer_friend_follower_dark : R.drawable.drawer_friend_follower_light;
            final DialogSelectAction<TweetEntity> action = DialogSelectAction.showBiography(mStatus);
            adapter.add(new DialogItemAdapter.DialogItem(action, userName, icon));
            return;
        }

        final int showTweetIcon = isThemeDark ? R.drawable.drawer_tweet_dark : R.drawable.drawer_tweet_light;
        final DialogSelectAction<TweetEntity> showTweet = DialogSelectAction.showTweet(mStatus);
        adapter.add(new DialogItemAdapter.DialogItem(showTweet, getString(R.string.show_convasation), showTweetIcon));

        List<String> usersName = StatusUtil.getAllUserMentionName(mStatus, this.loginUserId);
        List<Long> usersId = StatusUtil.getAllUserMentionId(mStatus, this.loginUserId);
        final int followerIcon = isThemeDark ? R.drawable.drawer_friend_follower_dark : R.drawable.drawer_friend_follower_light;
        for (int position = 0; position < usersId.size(); position++) {
            final DialogSelectAction<TweetEntity> action = DialogSelectAction.showBiography(mStatus, usersId.get(position));
            adapter.add(new DialogItemAdapter.DialogItem(action, usersName.get(position), followerIcon));
        }


        if (mStatus.urlEntities != null) {
            final int icon = isThemeDark ? R.drawable.internet_icon_dark : R.drawable.internet_icon_light;
            for (URLEntity entity : mStatus.urlEntities) {
                final String url = entity.getURL();
                final DialogSelectAction<TweetEntity> action = DialogSelectAction.openURL(mStatus, url);
                adapter.add(new DialogItemAdapter.DialogItem(action, url, icon));
            }
        }

        List<String> mediaURL = mStatus.mediaUrlList;
        if (!mediaURL.isEmpty()) {
            final int icon = isThemeDark ? R.drawable.image_update : R.drawable.image_update_light;
            for (int position = 0; position < mediaURL.size(); position++) {
                final DialogSelectAction<TweetEntity> action = DialogSelectAction.mediaURL(mStatus, position);
                adapter.add(new DialogItemAdapter.DialogItem(action, mediaURL.get(position), icon));
            }
        }
    }

}
