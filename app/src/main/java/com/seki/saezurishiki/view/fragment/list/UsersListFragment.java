package com.seki.saezurishiki.view.fragment.list;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.seki.saezurishiki.R;
import com.seki.saezurishiki.application.SaezurishikiApp;
import com.seki.saezurishiki.control.UIControlUtil;
import com.seki.saezurishiki.entity.UserEntity;
import com.seki.saezurishiki.model.GetUserById;
import com.seki.saezurishiki.presenter.list.UserListPresenter;
import com.seki.saezurishiki.view.activity.UserActivity;
import com.seki.saezurishiki.view.adapter.UserListAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;


public abstract class UsersListFragment extends Fragment implements UserListPresenter.View {

    protected static final String USER_ID = "user_id";
    UserListAdapter mUserListAdapter;
    @Inject
    UserListPresenter mPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_list, container, false);
        this.initComponents(rootView);
        rootView.setBackgroundColor(UIControlUtil.backgroundColor(rootView.getContext()));
        return rootView;
    }

    protected void initComponents(View rootView) {
        GetUserById repositoryAccessor = SaezurishikiApp.mApplicationComponent.getUserById();

        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(rootView.getContext(), LinearLayoutManager.VERTICAL, false);
        RecyclerView.ItemDecoration dividerDecoration =
                new DividerItemDecoration(rootView.getContext(), DividerItemDecoration.VERTICAL);

        UserListAdapter.OnClickUserListener listener =(user) -> mPresenter.onClickListItem(user);

        View.OnClickListener onClickFooter = (v) -> {
            mUserListAdapter.setLoading(true);
            mPresenter.request();
        };
        mUserListAdapter =
                new UserListAdapter(rootView.getContext(), repositoryAccessor, listener, onClickFooter);

        RecyclerView recyclerView = rootView.findViewById(R.id.list);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(dividerDecoration);
        recyclerView.setAdapter(mUserListAdapter);
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
        this.mPresenter.onResume();

        if (mUserListAdapter.isEmpty()) {
            mPresenter.request();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        this.mPresenter.onPause();
    }

    @Override
    public void loadUsers(List<UserEntity> users) {
        mUserListAdapter.addAll(users);
        mUserListAdapter.setLoading(false);
    }

    @Override
    public void hideFooterLoadButton() {
        mUserListAdapter.setNeedFooter(false);
    }

    @Override
    public void setPresenter(UserListPresenter presenter) {
        this.mPresenter = presenter;
    }
}
