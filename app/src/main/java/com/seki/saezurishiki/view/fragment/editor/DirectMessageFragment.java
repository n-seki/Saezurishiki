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
import com.seki.saezurishiki.control.CustomToast;
import com.seki.saezurishiki.control.UIControlUtil;
import com.seki.saezurishiki.entity.DirectMessageEntity;
import com.seki.saezurishiki.model.adapter.RequestInfo;
import com.seki.saezurishiki.presenter.editor.DirectMessageEditorPresenter;
import com.seki.saezurishiki.view.adapter.DirectMessageAdapter;

import java.util.List;

/**
 * ダイレクトメッセージ作成Fragment<br>
 * TextEditorに入力されている文字列をダイレクトメッセージとして送信します<br>
 * @author seki
 */
public class DirectMessageFragment extends Fragment implements DirectMessageEditorPresenter.View {

    private DirectMessageAdapter mAdapter;

    private ListView mListView;

    private EditText messageArea;

    private DirectMessageEditorPresenter presenter;

    public static DirectMessageFragment getInstance() {
        DirectMessageFragment fragment = new DirectMessageFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        mAdapter = new DirectMessageAdapter(getActivity(), R.layout.direct_message_layout);

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
    public void onResume() {
        super.onResume();
        presenter.onResume();

        if (mAdapter.isEmpty()) {
            presenter.load(new RequestInfo());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        this.presenter.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void initComponents(final View rootView) {
        setupDirectMessageList(rootView);

        this.messageArea = (EditText) rootView.findViewById(R.id.message_editor);
        Button sendButton = (Button)rootView.findViewById(R.id.send_button);
        sendButton.setOnClickListener(v -> DirectMessageFragment.this.onClickSendButton());
    }


    private void setupDirectMessageList(View rootView) {
        mListView = (ListView)rootView.findViewById(R.id.list);
        mListView.setAdapter(mAdapter);
        mListView.setClickable(false);
    }


    private void onClickSendButton() {
        String message = this.messageArea.getText().toString();
        this.presenter.onClickSendButton(message);
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
    public void setPresenter(DirectMessageEditorPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void catchNewMessage(DirectMessageEntity message) {
        this.mAdapter.add(message);
        mListView.setSelection(mListView.getCount() - 1);
    }

    @Override
    public void loadMessages(List<Long> messageIds) {
        this.mAdapter.addAll(messageIds);
        mListView.setSelection(mListView.getCount() - 1);
    }

    @Override
    public void showNoMessage() {
        CustomToast.show(getActivity(), R.string.no_recently_message, Toast.LENGTH_SHORT);
    }

    @Override
    public void showInputMessageEmpty() {
        CustomToast.show(getActivity(), R.string.please_write_text, Toast.LENGTH_SHORT);
    }

    @Override
    public void onSendMessageFinish() {
        this.messageArea.setText("");
        InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            mgr.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }
}
