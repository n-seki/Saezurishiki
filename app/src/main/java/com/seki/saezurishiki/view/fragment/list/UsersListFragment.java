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
import com.seki.saezurishiki.application.SaezurishikiApp;
import com.seki.saezurishiki.control.UIControlUtil;
import com.seki.saezurishiki.entity.UserEntity;
import com.seki.saezurishiki.model.GetUserById;
import com.seki.saezurishiki.presenter.list.UserListPresenter;
import com.seki.saezurishiki.view.activity.UserActivity;
import com.seki.saezurishiki.view.adapter.UsersListAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;


public abstract class UsersListFragment extends Fragment implements UserListPresenter.View {

    private final int NEW_LOADING = -0x0003;
    protected static final String USER_ID = "user_id";
    UsersListAdapter mAdapter;
    private View mFooterView;
    private ListView mListView;
    @Inject
    UserListPresenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GetUserById repositoryAccessor = SaezurishikiApp.mApplicationComponent.getUserById();
        mAdapter = new UsersListAdapter(getActivity(), R.layout.user_info_layout, repositoryAccessor);
        setRetainInstance(true);
    }


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list_view, container, false);
        this.initComponents(rootView);

        rootView.setBackgroundColor(UIControlUtil.backgroundColor(rootView.getContext()));

        return rootView;
    }


    protected void initComponents(View rootView) {
        mListView = rootView.findViewById(R.id.list);
        mListView.setOnItemClickListener((parent, view, position, id) -> {
            final UserEntity user = mAdapter.getEntity(position);
            presenter.onClickListItem(user);
        });

        mFooterView =
                LayoutInflater.from(rootView.getContext()).inflate(R.layout.read_more_tweet, null);
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

        TextView footerText = mFooterView.findViewById(R.id.read_more);
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
}
