package com.seki.saezurishiki.view.fragment.editor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
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

import com.seki.saezurishiki.R;
import com.seki.saezurishiki.application.SaezurishikiApp;
import com.seki.saezurishiki.control.CustomToast;
import com.seki.saezurishiki.control.Setting;
import com.seki.saezurishiki.control.StatusUtil;
import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.entity.UserEntity;
import com.seki.saezurishiki.network.twitter.TwitterProvider;
import com.seki.saezurishiki.presenter.editor.TweetEditorPresenter;
import com.seki.saezurishiki.view.TweetEditorModule;
import com.seki.saezurishiki.view.customview.TweetTextEditor;
import com.seki.saezurishiki.view.fragment.util.DataType;

import org.jetbrains.annotations.Contract;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import twitter4j.HashtagEntity;
import twitter4j.Status;
import twitter4j.StatusUpdate;


public class EditTweetFragment extends Fragment implements TweetEditorPresenter.View {


    private Callback mCallback;

    private TweetEntity mDestinationStatus;

    private int mEditorType;

    private InputStream media;
    private String mFileName;

    private ImageView uploadImage1;

    @Inject
    TweetEditorPresenter presenter;
    @Inject
    TwitterProvider mTwitterProvider;

    private TextView counter;

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
    }


    public static EditTweetFragment newNormalEditor() {
        EditTweetFragment fragment = new EditTweetFragment();
        Bundle data = new Bundle();
        data.putInt(EDITOR_TYPE, NORMAL_TWEET);
        fragment.setArguments(data);
        return fragment;
    }

    public static EditTweetFragment newEditorWithHashTag(HashtagEntity[] hashTagEntities) {
        EditTweetFragment fragment = new EditTweetFragment();
        Bundle data = new Bundle();
        data.putSerializable(DataType.HASH_TAG, hashTagEntities);
        data.putInt(EDITOR_TYPE, NORMAL_TWEET | HAS_HASH_TAG);
        fragment.setArguments(data);
        return fragment;
    }


    public static EditTweetFragment newReplyEditorFromStatus(TweetEntity status) {
        Bundle data = new Bundle();
        data.putSerializable(DataType.STATUS, status);
        data.putInt(EDITOR_TYPE, IS_REPLY | FROM_STATUS);
        EditTweetFragment fragment = new EditTweetFragment();
        fragment.setArguments(data);
        return fragment;
    }


    public static EditTweetFragment newReplyEditorFromUser(UserEntity user) {
        Bundle data = new Bundle();
        data.putSerializable(DataType.USER, user);
        data.putInt(EDITOR_TYPE, IS_REPLY | FROM_USER);
        EditTweetFragment fragment = new EditTweetFragment();
        fragment.setArguments(data);
        return fragment;
    }


    @SuppressWarnings("unused")
    public static EditTweetFragment newQuotedTweetEditor(TweetEntity quotedTweet) {
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
           presenter.onClickUploadImage();
        }
    };


    @Override
    public void hideUploadImage() {
        this.uploadImage1.setVisibility(View.GONE);
    }

    @Override
    public void closeImageSource() {
        try {
            media.close();
            media = null;
        } catch (IOException e) {
            Log.d("EditTweetFragment", "close InputStream is failure");
        }
    }


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

        SaezurishikiApp.mApplicationComponent.tweetEditorComponentBuilder()
                .presenterView(this)
                .module(new TweetEditorModule())
                .build()
                .inject(this);

        mEditorType = data.getInt(EDITOR_TYPE);

        this.loginUserId = mTwitterProvider.getLoginUserId();

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
        mgr.showSoftInput(getActivity().getCurrentFocus(), InputMethodManager.SHOW_IMPLICIT);
        getActivity().findViewById(R.id.tweet_editor).requestFocus();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case IMAGE_SELECT:
                    media = getMediaInputStream(data);
                    mFileName = data.getData().toString();
                    this.uploadImage1 = (ImageView) getActivity().findViewById(R.id.upload_image_1);
                    this.uploadImage1.setTag(1);
                    this.uploadImage1.setOnClickListener(this.uploadImageClickListener);
                    this.uploadImage1.setImageDrawable(Drawable.createFromStream(getMediaInputStream(data), mFileName));
                    this.uploadImage1.setVisibility(View.VISIBLE);
                    break;

                default:
                    break;
            }
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


    final TextWatcher editorTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            presenter.onTextChange(s.length());
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };


    private void setupNormalView(final View rootView) {
        final EditText tweetEditor = (EditText) rootView.findViewById(R.id.tweet_editor);
        Button tweetButton = (Button) rootView.findViewById(R.id.tweet_button);
        tweetButton.setOnClickListener(v -> EditTweetFragment.this.executeTweetButton(tweetEditor.getText().toString()));

        this.counter = (TextView) rootView.findViewById(R.id.counter);
        this.counter.setText(String.valueOf(tweetEditor.getText().toString().length()));

        tweetEditor.addTextChangedListener(editorTextWatcher);

        ImageButton imageUploadButton = (ImageButton)rootView.findViewById(R.id.image_upload_button);
        imageUploadButton.setOnClickListener(view -> {
            Intent intent = EditTweetFragment.this.createShowGalleryIntent();
            EditTweetFragment.this.showGallery(intent);
        });

        this.setupRegisterButton(rootView);
    }


    @Override
    public void changeTextCountErrorColor() {
        this.counter.setTextColor(ContextCompat.getColor(getActivity(), R.color.background_color_reply_to_me));
    }

    @Override
    public void changeTextCountDefaultColor() {
        this.counter.setTextColor(ContextCompat.getColor(getActivity(), R.color.white_FFFFFF));
    }

    @Override
    public void setTextCount(String length) {
        this.counter.setText(length);
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
        UserEntity user = (UserEntity) getArguments().getSerializable(DataType.USER);
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


    private void setupHashTag(View rootView) {
        Serializable tmp = getArguments().getSerializable(DataType.HASH_TAG);
        HashtagEntity[] hashTags;
        if (tmp instanceof HashtagEntity[]) {
            hashTags = (HashtagEntity[]) tmp;
        } else {
            throw new IllegalArgumentException("hasHashTag is true, but HashTagEntity is not got");
        }

        TweetTextEditor editor = (TweetTextEditor)rootView.findViewById(R.id.tweet_editor);
        editor.setHashTag(hashTags);
    }

    private void setupRegisterButton(View rootView) {
        final String registeredWord1 = Setting.getRegisterWord(getContext(), 1);
        if (!registeredWord1.isEmpty()) {
            final ImageButton registerButton1 = (ImageButton)rootView.findViewById(R.id.register_word_1);
            registerButton1.setVisibility(View.VISIBLE);
            registerButton1.setOnClickListener(v -> this.autoCompleteRegisteredWord(rootView, registeredWord1));
        }

        final String registeredWord2 = Setting.getRegisterWord(getContext(), 2);
        if (!registeredWord2.isEmpty()) {
            final ImageButton registerButton2 = (ImageButton)rootView.findViewById(R.id.register_word_2);
            registerButton2.setVisibility(View.VISIBLE);
            registerButton2.setOnClickListener(v -> this.autoCompleteRegisteredWord(rootView, registeredWord2));
        }

        final String registeredWord3 = Setting.getRegisterWord(getContext(), 3);
        if (!registeredWord3.isEmpty()) {
            final ImageButton registerButton3 = (ImageButton)rootView.findViewById(R.id.register_word_3);
            registerButton3.setVisibility(View.VISIBLE);
            registerButton3.setOnClickListener(v -> this.autoCompleteRegisteredWord(rootView, registeredWord3));
        }
    }

    private void autoCompleteRegisteredWord(View rootView, String registeredWord) {
        TweetTextEditor editor = (TweetTextEditor)rootView.findViewById(R.id.tweet_editor);
        final String currentText = editor.getText().toString();
        if (currentText.length() >= 1 && currentText.charAt(currentText.length() - 1) != ' ') {
            editor.append(" ");
        }

        editor.append(registeredWord);
        editor.setSelection(editor.getText().toString().length());
    }



    private void executeTweetButton(String text) {
        this.presenter.onClickPostButton(text, this.hasMediaItem());
    }


    @Override
    public void postTweet(final String text) {
        final StatusUpdate status = new StatusUpdate(text);

        if (this.isReply() && !this.isFromBiography()) {
            if (mDestinationStatus == null) {
                throw new IllegalStateException("DestinationStatus is null");
            }
            status.inReplyToStatusId(mDestinationStatus.getId());
        }

        if (this.hasMediaItem()) {
            status.setMedia(mFileName, media);
        }

        this.presenter.postTweet(status);
        mCallback.removeEditTweetFragment(this);
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


    @Override
    public void showMessageEmptyError() {
        CustomToast.show(getActivity(), R.string.edit_text_empty, Toast.LENGTH_SHORT);
    }

    @Override
    public void showMessageOverLengthError() {
        CustomToast.show(getActivity(), R.string.over_max_text_count, Toast.LENGTH_SHORT);
    }

    @Override
    public void hideSoftKeyBoard() {
        InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getActivity().getCurrentFocus() != null) {
            mgr.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    private Intent createShowGalleryIntent() {
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            return intent;
        }

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        return intent;
    }

    private boolean hasMediaItem() {
        return this.media != null;
    }
}