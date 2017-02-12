package com.seki.saezurishiki.view.fragment.editor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.seki.saezurishiki.application.SaezurishikiApp;
import com.seki.saezurishiki.R;
import com.seki.saezurishiki.control.CustomToast;
import com.seki.saezurishiki.control.StatusUtil;
import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.network.twitter.AsyncTwitterTask;
import com.seki.saezurishiki.network.twitter.TwitterAccount;
import com.seki.saezurishiki.network.twitter.TwitterError;
import com.seki.saezurishiki.network.twitter.TwitterTaskResult;
import com.seki.saezurishiki.network.twitter.TwitterTaskUtil;
import com.seki.saezurishiki.view.customview.TweetTextEditor;
import com.seki.saezurishiki.view.fragment.DataType;

import org.jetbrains.annotations.Contract;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import twitter4j.HashtagEntity;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.UploadedMedia;
import twitter4j.User;

/**
 * Tweet作成Fragment
 * TextEditorに入力された文字列をTweetとして送信,および返信します.
 * 送信Tweetに#で始まるタグが含まれていた場合には次回表示時に引き継がれ,
 * 一度タグを消して送受信を行うと引き継ぎが終了します
 * @author seki
 */
public class EditTweetFragment extends Fragment {


    private Callback mCallback;

    private TweetEntity mDestinationStatus;

    private int mEditorType;

    private TwitterTaskUtil mTwitterTaskUtil;
    private List<Long> mediaIds;
    private InputStream media;
    private String mFileName;

    private ImageView uploadImage1;

    private static final boolean TEST_RELEASE = true;

    private static final String EDITOR_TYPE = "editor_type";
    private static final int NORMAL_TWEET = 0x0001;
    private static final int IS_REPLY = 0x0002;
    private static final int FROM_STATUS = 0x0004;
    private static final int FROM_USER = 0x0008;
    private static final int HAS_HASH_TAG = 0x0010;
    private static final int HAS_QUOTED_TWEET = 0x0020;

    private static final int IMAGE_SELECT = 1000;
    private long loginUserId;

    public interface Callback {
        void removeEditTweetFragment(Fragment tweetEditFragment);
        void postTweet(StatusUpdate status);
    }


    public static Fragment newNormalEditor() {
        EditTweetFragment fragment = new EditTweetFragment();
        Bundle data = new Bundle();
        data.putInt(EDITOR_TYPE, NORMAL_TWEET);
        fragment.setArguments(data);
        return fragment;
    }

    public static Fragment newEditorWithHashTag(HashtagEntity[] hashTagEntities) {
        EditTweetFragment fragment = new EditTweetFragment();
        Bundle data = new Bundle();
        data.putSerializable(DataType.HASH_TAG, hashTagEntities);
        data.putInt(EDITOR_TYPE, NORMAL_TWEET | HAS_HASH_TAG);
        fragment.setArguments(data);
        return fragment;
    }


    public static Fragment newReplyEditorFromStatus(TweetEntity status) {
        Bundle data = new Bundle();
        data.putSerializable(DataType.STATUS, status);
        data.putInt(EDITOR_TYPE, IS_REPLY | FROM_STATUS);
        EditTweetFragment fragment = new EditTweetFragment();
        fragment.setArguments(data);
        return fragment;
    }


    public static Fragment newReplyEditorFromUser(User user) {
        Bundle data = new Bundle();
        data.putSerializable(DataType.USER, user);
        data.putInt(EDITOR_TYPE, IS_REPLY | FROM_USER);
        EditTweetFragment fragment = new EditTweetFragment();
        fragment.setArguments(data);
        return fragment;
    }


    public static Fragment newQuotedTweetEditor(TweetEntity quotedTweet) {
        Bundle data = new Bundle();
        data.putSerializable(DataType.QUOTED_TWEET, quotedTweet);
        data.putInt(EDITOR_TYPE, NORMAL_TWEET | HAS_QUOTED_TWEET);
        EditTweetFragment fragment = new EditTweetFragment();
        fragment.setArguments(data);
        return fragment;
    }


    private View.OnClickListener uploadImageClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            v.setVisibility(View.GONE);
            try {
                media.close();
                media = null;
            } catch (IOException e) {
                Log.d("EditTweetFragment", "close InputStream is failure");
            }
        }
    };


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (getActivity() instanceof EditTweetFragment.Callback) {
            mCallback = (EditTweetFragment.Callback) getActivity();
        } else {
            throw new IllegalStateException("Activity is not implements Callback");
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        Bundle data = getArguments();
        if (data == null) throw new NullPointerException("Bundle data is null!");

        mEditorType = data.getInt(EDITOR_TYPE);

        final SaezurishikiApp app = (SaezurishikiApp)getActivity().getApplication();
        final TwitterAccount twitterAccount = app.getTwitterAccount();
        mTwitterTaskUtil = new TwitterTaskUtil(getActivity(), getLoaderManager(), twitterAccount);
        mediaIds = new ArrayList<>();
        this.loginUserId = twitterAccount.getLoginUserId();

        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView;

        if (this.isReply() || this.isQuotedTweet()) {
            rootView = inflater.inflate(R.layout.fragment_edit_reply, container, false);
        } else {
            rootView = inflater.inflate(R.layout.fragment_edit_tweet, container, false);
        }

        initComponents(rootView);
        return rootView;
    }


    public void initComponents(View rootView) {
        this.setupNormalView(rootView);

        if (this.isReply()) {
            this.setupReplyView(rootView);
        }

        if (this.hasHashTag()) {
            this.setupHashTag(rootView);
        }

        if (this.isQuotedTweet()) {
            this.setupQuotedTweetView(rootView);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        //mgr.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_IMPLICIT_ONLY);
        mgr.showSoftInput(getActivity().getCurrentFocus(), InputMethodManager.SHOW_IMPLICIT);
        getActivity().findViewById(R.id.tweet_editor).requestFocus();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case IMAGE_SELECT:
                    if (TEST_RELEASE) {
                        media = getMediaInputStream(data);
                        mFileName = data.getData().toString();
                        this.uploadImage1 = (ImageView)getActivity().findViewById(R.id.upload_image_1);
                        this.uploadImage1.setTag(1);
                        this.uploadImage1.setOnClickListener(this.uploadImageClickListener);
                        this.uploadImage1.setImageDrawable(Drawable.createFromStream(getMediaInputStream(data), mFileName));
                        this.uploadImage1.setVisibility(View.VISIBLE);
                    } else {
                        uploadMultiMedia(data);
                    }
                    break;

                default:
                    break;
            }
        }
    }

    private void uploadMultiMedia(final Intent data) {
        try {
            final InputStream media = getContext().getContentResolver().openInputStream(data.getData());
            final AsyncTwitterTask.AfterTask<UploadedMedia> afterTask = new AsyncTwitterTask.AfterTask<UploadedMedia>() {
                @Override
                public void onLoadFinish(TwitterTaskResult<UploadedMedia> result) {
                    EditTweetFragment.this.onUploadMediaFinished(result);
                }
            };

            mTwitterTaskUtil.uploadImage(data.toString(), media, afterTask);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private InputStream getMediaInputStream(final Intent data) {
        try {
            return getContext().getContentResolver().openInputStream(data.getData());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

    private void onUploadMediaFinished(TwitterTaskResult<UploadedMedia> result) {
        if (result.isException()) {
            TwitterError.showText(getActivity(), result.getException());
            return;
        }

        mediaIds.add(result.getResult().getMediaId());
    }


    private void setupNormalView(final View rootView) {
        final EditText tweetEditor = (EditText) rootView.findViewById(R.id.tweet_editor);
        Button tweetButton = (Button) rootView.findViewById(R.id.tweet_button);
        tweetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditTweetFragment.this.executeTweetButton(tweetEditor.getText().toString());
            }
        });


        final TextView counter = (TextView) rootView.findViewById(R.id.counter);
        counter.setText(String.valueOf(tweetEditor.getText().toString().length()));

        tweetEditor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final int length = s.length();
                final TextView counter = (TextView)rootView.findViewById(R.id.counter);

                if (length > 140) {
                    counter.setTextColor(ContextCompat.getColor(getActivity(), R.color.background_color_reply_to_me));
                } else {
                    counter.setTextColor(ContextCompat.getColor(getActivity(), R.color.white_FFFFFF));
                }

                counter.setText(String.valueOf(length));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        ImageButton imageUploadButton = (ImageButton)rootView.findViewById(R.id.image_upload_button);
        imageUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = EditTweetFragment.this.createShowGalleryIntent();
                EditTweetFragment.this.showGallery(intent);
            }
        });
    }

    private void showGallery(Intent intent) {
        startActivityForResult(intent, IMAGE_SELECT);
    }


    private void setupReplyView(View rootView) {
        if (this.isFromBiography()) {
            this.setupReplyToUserView(rootView);
            return;
        }

        mDestinationStatus = (TweetEntity)getArguments().getSerializable(DataType.STATUS);

        List<String> userNames = StatusUtil.getAllUserMentionName(mDestinationStatus, this.loginUserId);
        TweetTextEditor tweetEditor = (TweetTextEditor)rootView.findViewById(R.id.tweet_editor);
        tweetEditor.setUserName(userNames);

        String replyText = mDestinationStatus.text;
        TextView replyTextView = (TextView) rootView.findViewById(R.id.reply_text);
        replyTextView.setText(replyText);
    }


    private void setupReplyToUserView(View rootView) {
        User user = (User) getArguments().getSerializable(DataType.USER);
        String replyName = user.getScreenName();
        TweetTextEditor tweetTextEditor = (TweetTextEditor)rootView.findViewById(R.id.tweet_editor);
        tweetTextEditor.setUserName(replyName);
    }


    private void setupQuotedTweetView(View rootView) {
        Status quotedStatus = (Status)getArguments().getSerializable(DataType.QUOTED_TWEET);

        TextView replyTextView = (TextView) rootView.findViewById(R.id.reply_text);
        replyTextView.setText(quotedStatus.getText());

        //TODO 引用ツイートのAPIが公開されるまで未使用とする
        final EditText tweetEditor = (EditText)rootView.findViewById(R.id.tweet_editor);
        tweetEditor.setText("");
        tweetEditor.setSelection(0);
    }


    /**
     * Activityにハッシュタグが保存されている場合はこのEditTextに保存された全てのタグを表示する
     * （reply時を除く）
     */
    private void setupHashTag(View rootView) {
        Serializable tmp = getArguments().getSerializable(DataType.HASH_TAG);
        HashtagEntity[] hashTags;
        if (tmp instanceof HashtagEntity[]) {
            hashTags = (HashtagEntity[]) tmp;
        } else {
            throw new IllegalArgumentException("hasHashTag is true, but HashTagEntity is not got!");
        }

        TweetTextEditor editor = (TweetTextEditor)rootView.findViewById(R.id.tweet_editor);
        editor.setHashTag(hashTags);
    }



    private void executeTweetButton(String text) {
        if (text.isEmpty() && !this.hasMediaItem()) {
            CustomToast.show(getActivity(), R.string.edit_text_empty, Toast.LENGTH_SHORT);
            return;
        }

        if (text.length() > 140) {
            CustomToast.show(getActivity(), R.string.over_max_text_count, Toast.LENGTH_SHORT);
            return;
        }

        InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getActivity().getCurrentFocus() != null) {
            mgr.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }

        this.postTweet(text);
        mCallback.removeEditTweetFragment(this);
    }


    private void postTweet(final String text) {
        final StatusUpdate status = new StatusUpdate(text);

        if (this.isReply() && !this.isFromBiography()) {
            if (mDestinationStatus == null) {
                throw new IllegalStateException("DestinationStatus is null");
            }
            status.inReplyToStatusId(mDestinationStatus.getId());
        }

        if (TEST_RELEASE) {
            if (this.hasMediaItem()) {
                status.setMedia(mFileName, media);
            }
        } else {
            if (!mediaIds.isEmpty()) {
                final long[] medias = new long[mediaIds.size()];
                for (int i = 0; i < mediaIds.size(); i++) {
                    medias[i] = mediaIds.get(i);
                }
                status.setMediaIds(medias);
            }
        }

        mCallback.postTweet(status);
    }


    @Override
    public void onPause() {
        InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        if (getActivity().getCurrentFocus() != null) {
            mgr.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }

        super.onPause();
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
    }


    @Contract(pure = true)
    private boolean isReply() {
        return (mEditorType & IS_REPLY) != 0;
    }

    @Contract(pure = true)
    private boolean isFromBiography() {
        return (mEditorType & FROM_USER) != 0;
    }

    @Contract(pure = true)
    private boolean hasHashTag() {
        return (mEditorType & HAS_HASH_TAG) != 0;
    }

    @Contract(pure = true)
    private boolean isQuotedTweet() {
        return (mEditorType & HAS_QUOTED_TWEET) != 0;
    }


    private Intent createShowGalleryIntent() {
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/jpeg");
            return intent;
        }

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        return intent;
    }

    private boolean hasMediaItem() {
        return this.media != null;
    }


    @Override
    public String toString() {
        return "Edit Tweet";
    }
}