package com.seki.saezurishiki.view.fragment.editor;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.seki.saezurishiki.R;
import com.seki.saezurishiki.application.SaezurishikiApp;
import com.seki.saezurishiki.control.CustomToast;
import com.seki.saezurishiki.control.UIControlUtil;
import com.seki.saezurishiki.network.twitter.AsyncTwitterTask;
import com.seki.saezurishiki.network.twitter.TwitterAccount;
import com.seki.saezurishiki.network.twitter.TwitterError;
import com.seki.saezurishiki.network.twitter.TwitterWrapper;
import com.seki.saezurishiki.network.twitter.streamListener.DirectMessageUserStreamListener;
import com.seki.saezurishiki.view.adapter.DirectMessageAdapter;
import com.seki.saezurishiki.view.fragment.util.DataType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import twitter4j.DirectMessage;
import twitter4j.User;

/**
 * ダイレクトメッセージ作成Fragment<br>
 * TextEditorに入力されている文字列をダイレクトメッセージとして送信します<br>
 * @author seki
 */
public class DirectMessageFragment extends Fragment implements DirectMessageUserStreamListener {

    private long mUserID = 0L;

    private DirectMessageAdapter mAdapter;

    private TwitterWrapper mTwitterTask;

    private ListView mListView;

    private TwitterAccount twitterAccount;

    public static Fragment getInstance(User user) {
        Fragment fragment = new DirectMessageFragment();
        Bundle data  = new Bundle();
        data.putSerializable(DataType.USER, user);
        fragment.setArguments(data);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        SaezurishikiApp app = (SaezurishikiApp)getActivity().getApplication();
        this.twitterAccount = app.getTwitterAccount();
        this.twitterAccount.addStreamListener(this);

        mUserID = ((User)getArguments().getSerializable(DataType.USER)).getId();
        mAdapter = new DirectMessageAdapter(getActivity(), R.layout.direct_message_layout, twitterAccount.getRepository());
        mTwitterTask = new TwitterWrapper(getActivity(), getLoaderManager(), this.twitterAccount);

        setHasOptionsMenu(true);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_direct_message, container, false);
        initComponents(rootView);
        rootView.setBackgroundColor(UIControlUtil.backgroundColor(getActivity()));
        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }


    @Override
    public void onDestroy() {
        this.twitterAccount.removeListener(this);
        super.onDestroy();
    }


    public void initComponents(final View rootView) {
        setupDirectMessageList(rootView);

        Button sendButton = (Button)rootView.findViewById(R.id.send_button);
        sendButton.setOnClickListener(v -> DirectMessageFragment.this.onClickSendButton(rootView));


    }


    private void setupDirectMessageList(View rootView) {
        List<Long> receiveMessage = this.twitterAccount.getRepository().getDMIdByUser(mUserID);
        List<Long> sentMessage = this.twitterAccount.getRepository().getSentDMId(mUserID);

        List<Long> allMessage = new ArrayList<>(receiveMessage);
        allMessage.addAll(sentMessage);

        Collections.sort(allMessage);

        mListView = (ListView)rootView.findViewById(R.id.list);
        mListView.setAdapter(mAdapter);
        mListView.setClickable(false);
        mAdapter.addAll(allMessage);
        mListView.setSelection(mListView.getCount()-1);
    }


    private void onClickSendButton(View rootView) {
        EditText editText = (EditText) rootView.findViewById(R.id.message_editor);
        String message = editText.getText().toString();
        if (message.isEmpty()) {
            CustomToast.show(getActivity(), R.string.please_write_text, Toast.LENGTH_SHORT);
            return;
        }

        DirectMessageFragment.this.sendDirectMessage(message);
        editText.setText("");
        InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            mgr.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }


    private void sendDirectMessage(final String message) {
        AsyncTwitterTask.AfterTask<DirectMessage> afterTask = result -> {
            if (result.isException()) {
                TwitterError.showText(DirectMessageFragment.this.getActivity(), result.getException());
                return;
            }

            CustomToast.show(DirectMessageFragment.this.getActivity(), R.string.sendDM_complete, Toast.LENGTH_SHORT);
        };

        mTwitterTask.sendDirectMessage(mUserID, message, afterTask);
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
    }

    @Override
    public String toString() {
        return "Direct Message";
    }

    @Override
    public void onDirectMessage(DirectMessage directMessage) {
        final long userId = directMessage.getSenderId();
        if ((userId != mUserID) && userId != this.twitterAccount.getLoginUserId()) {
            return;
        }
        mAdapter.add(directMessage.getId());
        mListView.setSelection(mListView.getCount() - 1);
    }
}
