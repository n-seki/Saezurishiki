package com.seki.saezurishiki.view.fragment.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import com.seki.saezurishiki.application.SaezurishikiApp;
import com.seki.saezurishiki.R;
import com.seki.saezurishiki.control.StatusUtil;
import com.seki.saezurishiki.control.UIControlUtil;
import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.view.fragment.DataType;
import com.seki.saezurishiki.network.server.TwitterServer;
import com.seki.saezurishiki.view.fragment.dialog.adapter.DialogSelectAction;

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

    public static final int REPLY        = R.string.do_reply;
    public static final int ReTWEET      = R.string.do_retweet;
    public static final int UN_ReTWEET   = R.string.do_un_retweet;
    public static final int CONVERSATION = R.string.show_convasation;
    public static final int FAVORITE     = R.string.do_favorite;
    public static final int UN_FAVORITE  = R.string.do_un_favorite;
    public static final int SHOW_TWEET   = R.string.do_show_tweet;
    public static final int BIOGRAPHY    = 10;
    //public static final int QUOTED_TWEET = 7;
    public static final int URL          = 20;
    public static final int MEDIA = 30;


    public interface DialogCallback {
        void onDialogItemClick(TweetEntity status, int action);
    }


    public static DialogFragment getInstance(long statusId) {
        DialogFragment dialog = new TweetSelectDialog();
        Bundle data = new Bundle();
        data.putLong(DataType.STATUS_ID, statusId);
        dialog.setArguments(data);
        return dialog;
    }

    public static DialogFragment newInstance(TweetEntity status) {
        Bundle data = new Bundle();
        data.putSerializable(DataType.STATUS, status);
        DialogFragment dialog = new TweetSelectDialog();
        dialog.setArguments(data);
        return dialog;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        long id = getArguments().getLong(DataType.STATUS_ID);

        SaezurishikiApp app = (SaezurishikiApp) getActivity().getApplication();
        TwitterServer repository = app.getTwitterAccount().getRepository();
        this.theme = app.getTwitterAccount().setting.getTheme();

        TweetEntity status = repository.getStatus(id);
        mStatus = status.isRetweet ? repository.getStatus(status.retweetedStatusId) : status;
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
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int action = (int) view.getTag();
                mListener.onDialogItemClick(mStatus, action);
                dismiss();
            }
        });

        DialogItemAdapter adapter =
                new DialogItemAdapter(getActivity(), getActivity().getLayoutInflater(), R.layout.dialog_list_item);
        listView.setAdapter(adapter);

        final boolean isThemeDark = this.theme == R.style.AppTheme_Dark;

        if (mIsDelete) {
            String userName = mStatus.user.getScreenName();
            adapter.add(BIOGRAPHY, userName, isThemeDark ? R.drawable.drawer_friend_follower_dark : R.drawable.drawer_friend_follower_light);
            return;
        }

        if (mStatus.inReplyToStatusId != -1 && mStatus.inReplyToScreenName != null && mStatus.inReplyToUserId != -1) {
            adapter.add(CONVERSATION, isThemeDark ? R.drawable.drawer_tweet_dark : R.drawable.drawer_tweet_light);
        } else {
            adapter.add(SHOW_TWEET, isThemeDark ? R.drawable.drawer_tweet_dark : R.drawable.drawer_tweet_light);
        }

        List<String> usersName = StatusUtil.getAllUserMentionName(mStatus, this.loginUserId);
        int bioCount = BIOGRAPHY;
        for (String name : usersName) {
            adapter.add(bioCount++, UIControlUtil.getMentionUserNameText(name), isThemeDark ? R.drawable.drawer_friend_follower_dark : R.drawable.drawer_friend_follower_light);
        }

        if (mStatus.urlEntities != null && mStatus.urlEntities.length != 0) {
            int urlCount = URL;
            for (URLEntity urlEntity : mStatus.urlEntities) {
                adapter.add(urlCount++, urlEntity.getDisplayURL(), isThemeDark ? R.drawable.internet_icon_dark : R.drawable.internet_icon_light);
            }
        }

        List<String> mediaURL = UIControlUtil.createMediaURLList(mStatus);
        if (!mediaURL.isEmpty()) {
            int mediaCount = MEDIA;
            for (String media : mediaURL) {
                adapter.add(mediaCount++, media, isThemeDark ? R.drawable.image_update : R.drawable.image_update_light);
            }
        }
    }


    private static DialogSelectAction showBiography(TweetEntity tweet) {
        return new DialogSelectAction(tweet.user.getId(), Long.class, BIOGRAPHY);
    }

    //これは自分のIDを考慮していないので、ダイアログの表示と動作がずれる気がする......
    private static DialogSelectAction showBiography(TweetEntity tweet, int position) {
        final long userId = tweet.userMentionEntities[position].getId();
        return new DialogSelectAction(userId, Long.class, BIOGRAPHY);
    }

    private static DialogSelectAction showTweet(TweetEntity tweet) {
        return new DialogSelectAction(tweet.getId(), Long.class, SHOW_TWEET);
    }

    private static DialogSelectAction openURL(TweetEntity tweetEntity, int position) {
        final String url = tweetEntity.urlEntities[position].getURL();
        return new DialogSelectAction(url, String.class, URL);
    }

    private static DialogSelectAction mediaURL(String mediaUrl) {
        return new DialogSelectAction(mediaUrl, String.class, MEDIA);
    }


}
