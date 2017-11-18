package com.seki.saezurishiki.view.activity;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.seki.saezurishiki.R;
import com.seki.saezurishiki.control.CustomToast;
import com.seki.saezurishiki.control.FragmentController;
import com.seki.saezurishiki.control.ScreenNav;
import com.seki.saezurishiki.control.Setting;
import com.seki.saezurishiki.entity.UserEntity;
import com.seki.saezurishiki.model.impl.ModelContainer;
import com.seki.saezurishiki.presenter.activity.UserPresenter;
import com.seki.saezurishiki.view.adapter.BioHeaderPageAdapter;
import com.seki.saezurishiki.view.adapter.DrawerButtonListAdapter;
import com.seki.saezurishiki.view.control.FragmentControl;
import com.seki.saezurishiki.view.fragment.dialog.YesNoSelectDialog;
import com.seki.saezurishiki.view.fragment.editor.EditTweetFragment;

import java.util.HashMap;
import java.util.Map;

public class UserActivity extends    AppCompatActivity
                          implements EditTweetFragment.Callback,
                                     FragmentControl, UserPresenter.View {

    public static final String USER_ID = "userID";

    public static final int SHOW_ACTIVITY = 0x0800;

    private FragmentController mFragmentController;
    private DrawerButtonListAdapter mListAdapter;

    private Setting setting;

    private UserPresenter presenter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);

        this.setting = new Setting();
        final int theme = this.setting.getTheme();
        setTheme(theme);
        setContentView(R.layout.activity_biography);

        mFragmentController = new FragmentController(getSupportFragmentManager());
        mListAdapter = new DrawerButtonListAdapter(this, R.layout.drawer_list_button, theme);

        final long userId = getIntent().getExtras().getLong(USER_ID);
        this.presenter = new UserPresenter(this, ModelContainer.getUserScreenModel(), userId);
    }

    @Override
    public void onResume() {
        super.onResume();
        this.presenter.onResume();
        this.presenter.loadOwner();
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

    @Override
    public void setupActionBar(UserEntity owner) {
        Toolbar toolbar = (Toolbar)findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if ( actionBar == null ) {
            throw  new IllegalStateException("ActionBar is null");
        }

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        this.replaceTitle(owner.getName(), owner.getScreenName());
    }

    @Override
    public void setupBioInformation(UserEntity owner) {
        ViewPager headerPage = (ViewPager)findViewById(R.id.bio_header_page);
        PagerAdapter pagerAdapter = new BioHeaderPageAdapter(getSupportFragmentManager(), owner);
        headerPage.setAdapter(pagerAdapter);
        headerPage.setCurrentItem(1);

        Button replyButton = (Button)findViewById(R.id.bio_reply_button);
        replyButton.setOnClickListener(view -> UserActivity.this.displayFragment(ScreenNav.TWEET_EDITOR, owner));

        Button messageButton = (Button)findViewById(R.id.bio_message_button);
        messageButton.setOnClickListener(view -> UserActivity.this.displayFragment(ScreenNav.MESSAGE_EDITOR, owner));

        Button followButton = (Button)findViewById(R.id.bio_follow_button);
        followButton.setOnClickListener(view -> presenter.onClickFollowButton());

        ListView drawerList = (ListView)findViewById(R.id.bio_drawer_list);
        mListAdapter.setUserItem(owner);
        drawerList.setAdapter(mListAdapter);
        drawerList.setOnItemClickListener(drawerItemClickListener);
    }

    @Override
    public void setRelationshipText(int text) {
        ((TextView)findViewById(R.id.bio_relation)).setText(text);
    }

    @Override
    public void changeDirectMessageButtonState(boolean isMutual) {
        Button sendButton = (Button) findViewById(R.id.bio_message_button);
        final int color = isMutual ? R.color.white_FFFFFF : R.color.gray_808080;
        sendButton.setClickable(isMutual);
        sendButton.setTextColor(ContextCompat.getColor(this, color));
    }

    @Override
    public void disableFollowButton() {
        Button followButton = (Button)findViewById(R.id.bio_follow_button);
        followButton.setClickable(false);
        followButton.setTextColor(ContextCompat.getColor(this, R.color.gray_808080));
    }

    @Override
    public void setFollowButton() {
        Button followButton = (Button)findViewById(R.id.bio_follow_button);
        followButton.setText(R.string.follow_button_label);
    }

    @Override
    public void setRemoveButton() {
        Button followButton = (Button)findViewById(R.id.bio_follow_button);
        followButton.setText(R.string.remove_button_label);
    }

    @Override
    public void updateOptionMenu() {
        invalidateOptionsMenu();
    }

    private final AdapterView.OnItemClickListener drawerItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final ScreenNav screenNav = mListAdapter.getItem(position).screenNav;
            presenter.onClickButtonList(screenNav);
        }
    };

    @Override
    public void displayFragment(ScreenNav screenNav, UserEntity owner) {
        final Map<String, Object> args = new HashMap<>();
        args.put("user", owner);
        requestChangeScreen(screenNav, args);

    }

    private void changeActionBarIndicatorState() {
        if (getSupportActionBar() == null) {
            throw new IllegalStateException("ActionBar is null!");
        }

        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (menu.size() == 0) return true;
        MenuItem block = menu.findItem(R.id.action_block);
        MenuItem destroyBlock = menu.findItem(R.id.action_release_block);

        if (this.presenter.isBlocking()) {
            block.setVisible(false);
            destroyBlock.setVisible(true);
        } else {
            block.setVisible(true);
            destroyBlock.setVisible(false);
        }

        MenuItem mute = menu.findItem(R.id.action_mute);
        MenuItem destroyMute = menu.findItem(R.id.action_destroy_mute);

        mute.setVisible(false);
        destroyMute.setVisible(false);

        MenuItem setting = menu.findItem(R.id.action_settings);
        setting.setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onHomePressed();
                return true;

            case R.id.action_search:
                return true;

            case R.id.action_block:
                this.presenter.onSelectBlock();
                return true;

            case R.id.action_release_block:
                this.presenter.onSelectReleaseBlock();
                return true;

            case R.id.action_mute:
            case R.id.action_destroy_mute:
                throw new IllegalStateException("Mute is not exist!");

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void displayFollowDialog(UserEntity user, final boolean isFollow) {
        final YesNoSelectDialog.Listener<UserEntity> action = userEntity ->  {
            if (isFollow)
                UserActivity.this.remove();
            else
                UserActivity.this.follow();
        };

        final DialogFragment dialogFragment = YesNoSelectDialog.newFollowDialog(user, action, isFollow);
        dialogFragment.show(getSupportFragmentManager(), "YesNoSelectDialog");
    }

    private void follow() {
        this.presenter.follow();
    }

    @Override
    public void showCompleteFollowMessage() {
        CustomToast.show(this, R.string.done_follow, Toast.LENGTH_SHORT);
    }

    private void remove() {
        this.presenter.remove();
    }

    @Override
    public void showCompleteRemoveMessage() {
        CustomToast.show(this, R.string.done_remove, Toast.LENGTH_SHORT);
    }

    @Override
    public void removeEditTweetFragment(Fragment tweetEditor) {
        onBackPressed();
    }

    @Override
    public void showCompletePostTweetMessage() {
        CustomToast.show(UserActivity.this, R.string.reply_complete, Toast.LENGTH_SHORT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_activity, menu);
        return true;
    }

    private void replaceTitle(String title, String subTitle) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) throw new NullPointerException("ActionBar is null!");

        actionBar.setTitle(title);
        actionBar.setSubtitle(subTitle);
    }

    @Override
    public void updateTitle(UserEntity user) {
        if (!mFragmentController.hasFragment()) {
            this.replaceTitle(user.getName(), user.getScreenName());
            return;
        }

        Fragment currentFragment = mFragmentController.getFragment(R.id.biography_container);
        replaceTitle(user.getName(), currentFragment.toString());
    }

    private void changeSubtitle(String subtitle) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) throw new NullPointerException("ActionBar is null!");

        actionBar.setSubtitle(subtitle);
    }

    void onHomePressed() {
        this.presenter.onHomePressed(mFragmentController.hasFragment());
    }

    @Override
    public void onBackPressed() {
        this.presenter.onBackPressed(mFragmentController.hasFragment());
    }

    @Override
    public void finishActivity() {
        finish();
    }

    @Override
    public void removeCurrentScreen() {
        mFragmentController.removeCurrentFragment(R.id.biography_container);
    }

    @Override
    public void removeAllScreen() {
        mFragmentController.removeAllFragment(R.id.biography_container);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void displayFollowRequestDialog(UserEntity user) {
        YesNoSelectDialog.Listener<UserEntity> action = u -> sendFollowRequest();
        DialogFragment dialogFragment = YesNoSelectDialog.newFollowRequestDialog(user, action);
        dialogFragment.show(getSupportFragmentManager(), "YesNoSelectDialog");
    }

    public void sendFollowRequest() {
        this.presenter.sendFollowRequest();
    }

    @Override
    public void showCompleteSendFollowRequestMessage() {
        CustomToast.show(UserActivity.this, R.string.done_follow_request, Toast.LENGTH_SHORT);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void showBlockUserDialog(UserEntity user) {
        YesNoSelectDialog.Listener<UserEntity> action = u -> this.blockUser();
        DialogFragment dialogFragment = YesNoSelectDialog.newBlockUserDialog(user, action);
        dialogFragment.show(getSupportFragmentManager(), "YesNoSelectDialog");
    }

    public void blockUser() {
        this.presenter.block();
    }

    @Override
    public void showCompleteBlockMessage() {
        CustomToast.show(UserActivity.this, R.string.block_complete, Toast.LENGTH_SHORT);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void showReleaseBlockDialog(UserEntity user) {
        YesNoSelectDialog.Listener<UserEntity> action = item -> UserActivity.this.releaseBlock();
        DialogFragment dialog = YesNoSelectDialog.newReleaseBlockDialog(user, action);
        dialog.show(getSupportFragmentManager(), "YesNoSelectDialog");
    }

    private void releaseBlock() {
        this.presenter.destroyBlock();
    }

    @Override
    public void showCompleteDestroyBlockMessage() {
        CustomToast.show(UserActivity.this, R.string.release_block_complete, Toast.LENGTH_SHORT);
    }


    @Override
    public void requestChangeScreen(ScreenNav screenNav, Map<String, Object> args) {
        screenNav.transition(this, getSupportFragmentManager(), R.id.biography_container, args,
                fragment -> {
                    changeSubtitle(fragment.toString());
                    changeActionBarIndicatorState();
                });
    }

}
