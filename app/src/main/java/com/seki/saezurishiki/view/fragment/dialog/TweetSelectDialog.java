package com.seki.saezurishiki.view.fragment.dialog;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import android.view.Window;
import android.widget.ListView;

import com.seki.saezurishiki.R;
import com.seki.saezurishiki.application.SaezurishikiApp;
import com.seki.saezurishiki.control.Setting;
import com.seki.saezurishiki.control.StatusUtil;
import com.seki.saezurishiki.entity.Media;
import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.model.GetTweetById;
import com.seki.saezurishiki.network.twitter.TwitterProvider;
import com.seki.saezurishiki.view.fragment.dialog.adapter.DialogSelectAction;
import com.seki.saezurishiki.view.fragment.util.DataType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import twitter4j.URLEntity;

/**
 * touchされたStatusのダイアログ<br>
 * textコピーやURL選択によるブラウザ起動など
 */
public class TweetSelectDialog extends DialogFragment {

    private boolean mIsDelete;
    private TweetEntity mStatus;
    private DialogCallback mListener;
    private long loginUserId;
    private int theme;
    private Set<Integer> forbidDialogActions = new HashSet<>();

    @Inject
    GetTweetById repositoryAccessor;

    @Inject
    TwitterProvider mTwitterProvider;

    public interface DialogCallback {
        void onDialogItemClick(DialogSelectAction<TweetEntity> action);
    }

    public static DialogFragment getInstance(long statusId, int[] forbidAction) {
        DialogFragment dialog = new TweetSelectDialog();
        Bundle data = new Bundle();
        data.putLong(DataType.STATUS_ID, statusId);
        data.putIntArray(DataType.FORBID_ACTIONS, forbidAction);
        dialog.setArguments(data);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        long id = getArguments().getLong(DataType.STATUS_ID);
        int[] forbidActions = getArguments().getIntArray(DataType.FORBID_ACTIONS);
        if (forbidActions != null) {
            for (int action : forbidActions) {
                this.forbidDialogActions.add(action);
            }
        }
        this.theme = new Setting().getTheme();
        SaezurishikiApp.mApplicationComponent.inject(this);
        final TweetEntity tweet = repositoryAccessor.get(id);
        mStatus = tweet.isRetweet ? tweet.retweet : tweet;
        mIsDelete = mStatus.isDeleted();

        this.loginUserId = mTwitterProvider.getLoginUserId();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListener = (DialogCallback)TweetSelectDialog.this.getTargetFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_list);
        this.initDialog(dialog);
        return dialog;
    }

    void initDialog(Dialog dialog) {
        ListView listView = dialog.findViewById(R.id.list);
        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            @SuppressWarnings("unchecked")
            final DialogSelectAction<TweetEntity> action = (DialogSelectAction)view.getTag();
            mListener.onDialogItemClick(action);
            dismiss();
        });

        DialogItemAdapter adapter = new DialogItemAdapter(
                getActivity(),
                dialog.getLayoutInflater(),
                R.layout.dialog_list_item
        );
        listView.setAdapter(adapter);

        final boolean isThemeDark = this.theme == R.style.AppTheme_Dark;

        if (mIsDelete && !forbidDialogActions.contains(DialogSelectAction.DELETE)) {
            String userName = mStatus.user.getScreenName();
            final int icon = isThemeDark ? R.drawable.drawer_friend_follower_dark : R.drawable.drawer_friend_follower_light;
            final DialogSelectAction<TweetEntity> action = DialogSelectAction.showBiography(mStatus);
            adapter.add(new DialogItemAdapter.DialogItem(action, userName, icon));
            return;
        }

        if (!forbidDialogActions.contains(DialogSelectAction.SHOW_TWEET)) {
            final int showTweetIcon = isThemeDark ? R.drawable.drawer_tweet_dark : R.drawable.drawer_tweet_light;
            final DialogSelectAction<TweetEntity> showTweet = DialogSelectAction.showTweet(mStatus);
            adapter.add(new DialogItemAdapter.DialogItem(showTweet, getString(R.string.show_convasation), showTweetIcon));
        }

        if (!forbidDialogActions.contains(DialogSelectAction.BIOGRAPHY)) {
            List<String> usersName = StatusUtil.getAllUserMentionName(mStatus, this.loginUserId);
            List<Long> usersId = StatusUtil.getAllUserMentionId(mStatus, this.loginUserId);
            final int followerIcon = isThemeDark ? R.drawable.drawer_friend_follower_dark : R.drawable.drawer_friend_follower_light;
            for (int position = 0; position < usersId.size(); position++) {
                final DialogSelectAction<TweetEntity> action = DialogSelectAction.showBiography(mStatus, usersId.get(position));
                adapter.add(new DialogItemAdapter.DialogItem(action, usersName.get(position), followerIcon));
            }
        }

        if (!forbidDialogActions.contains(DialogSelectAction.URL)) {
            if (mStatus.urlEntities != null) {
                final int icon = isThemeDark ? R.drawable.internet_icon_dark : R.drawable.internet_icon_light;
                for (URLEntity entity : mStatus.urlEntities) {
                    final String url = entity.getURL();
                    final DialogSelectAction<TweetEntity> action = DialogSelectAction.openURL(mStatus, url);
                    adapter.add(new DialogItemAdapter.DialogItem(action, url, icon));
                }
            }
        }

        if (!forbidDialogActions.contains(DialogSelectAction.MEDIA)) {
            List<String> mediaURL = Media.mapToUrl(mStatus.mediaUrlList);
            if (!mediaURL.isEmpty()) {
                final int icon = isThemeDark ? R.drawable.image_update : R.drawable.image_update_light;
                for (int position = 0; position < mediaURL.size(); position++) {
                    final DialogSelectAction<TweetEntity> action = DialogSelectAction.mediaURL(mStatus, position);
                    adapter.add(new DialogItemAdapter.DialogItem(action, mediaURL.get(position), icon));
                }
            }
        }
    }

}
