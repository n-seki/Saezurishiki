package com.seki.saezurishiki.view.fragment.list;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.seki.saezurishiki.R;
import com.seki.saezurishiki.control.UIControlUtil;
import com.seki.saezurishiki.entity.UserEntity;
import com.seki.saezurishiki.presenter.list.UserListPresenter;
import com.seki.saezurishiki.view.activity.UserActivity;
import com.seki.saezurishiki.view.adapter.UsersListAdapter;

import java.util.List;


public abstract class UsersListFragment extends Fragment implements UserListPresenter.View {

    UsersListAdapter mAdapter;

    private View mFooterView;
    private ListView mListView;

    private UserListPresenter presenter;

    private final int NEW_LOADING = -0x0003;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new UsersListAdapter(getActivity(), R.layout.user_info_layout);
        setRetainInstance(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list_view, container, false);
        this.initComponents(rootView);

        rootView.setBackgroundColor(UIControlUtil.backgroundColor(getActivity()));

        return rootView;
    }


    protected void initComponents(View rootView) {
        mListView = (ListView) rootView.findViewById(R.id.list);
        mListView.setOnItemClickListener((parent, view, position, id) -> {
            final UserEntity user = mAdapter.getEntity(position);
            presenter.onClickListItem(user);
        });

        mFooterView = getActivity().getLayoutInflater().inflate(R.layout.read_more_tweet, null);
        mFooterView.setOnClickListener(v -> UsersListFragment.this.clickReadMoreButton());
        mFooterView.setTag(NEW_LOADING, false);

        mListView.addFooterView(mFooterView, null ,true);
        mListView.setFooterDividersEnabled(false);
        mListView.setAdapter(mAdapter);
    }



    @Override
    public void showUser(UserEntity user) {
        Intent intent = new Intent(getActivity(), UserActivity.class);
        intent.putExtra(UserActivity.USER_ID, user.getId());
        startActivity(intent);
    }


    @Override
    public void onResume() {
        super.onResume();
        this.presenter.onResume();

        if (mAdapter.isEmpty()) {
            this.load();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        this.presenter.onPause();
    }


    @Override
    public void loadUsers(List<UserEntity> users) {
        mAdapter.addAll(users);
        mFooterView.setTag(NEW_LOADING, false);
        ((TextView) mFooterView.findViewById(R.id.read_more)).setText(R.string.click_to_load);
    }


    @Override
    public void hideFooterLoadButton() {
        mListView.removeFooterView(mFooterView);
    }


    protected void clickReadMoreButton() {
        final boolean isLoading = (Boolean)mFooterView.getTag(NEW_LOADING);

        if (isLoading) {
            return;
        }

        TextView footerText = (TextView)mFooterView.findViewById(R.id.read_more);
        footerText.setText(R.string.now_loading);
        mFooterView.setTag(NEW_LOADING, true);

        load();
    }



    private void load() {
       this.presenter.request();
    }

    @Override
    public void setPresenter(UserListPresenter presenter) {
        this.presenter = presenter;
    }


    public abstract String toString();
}
