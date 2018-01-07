package com.seki.saezurishiki.view.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Toast;

import com.seki.saezurishiki.R;
import com.seki.saezurishiki.control.CustomToast;
import com.seki.saezurishiki.control.FragmentController;
import com.seki.saezurishiki.control.ScreenNav;
import com.seki.saezurishiki.control.Setting;
import com.seki.saezurishiki.control.UIControlUtil;
import com.seki.saezurishiki.entity.DirectMessageEntity;
import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.entity.UserEntity;
import com.seki.saezurishiki.file.CachManager;
import com.seki.saezurishiki.file.EncryptUtil;
import com.seki.saezurishiki.file.Serializer;
import com.seki.saezurishiki.file.SharedPreferenceUtil;
import com.seki.saezurishiki.model.impl.ModelContainer;
import com.seki.saezurishiki.network.ConnectionReceiver;
import com.seki.saezurishiki.network.twitter.TwitterAccount;
import com.seki.saezurishiki.network.twitter.TwitterUtil;
import com.seki.saezurishiki.presenter.activity.LoginUserPresenter;
import com.seki.saezurishiki.view.adapter.DrawerButtonListAdapter;
import com.seki.saezurishiki.view.adapter.TimeLinePager;
import com.seki.saezurishiki.view.control.FragmentControl;
import com.seki.saezurishiki.view.control.TabManagedView;
import com.seki.saezurishiki.view.control.TabViewControl;
import com.seki.saezurishiki.view.customview.NotificationTabLayout;
import com.seki.saezurishiki.view.customview.TwitterUserDrawerView;
import com.seki.saezurishiki.view.fragment.dialog.YesNoSelectDialog;
import com.seki.saezurishiki.view.fragment.editor.EditTweetFragment;
import com.seki.saezurishiki.view.fragment.list.RecentlyDirectMessageListFragment;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.Contract;

import java.util.HashMap;
import java.util.Map;

import twitter4j.HashtagEntity;

/**
 * ログインユーザーに関する情報を管理するためのActivity<br>
 * 主にFragmentの管理,各コールバックの実装,ActionBar/NavigationDrawerの管理など
 * @author seki
 */
public class LoginUserActivity extends    AppCompatActivity
                               implements ViewPager.OnPageChangeListener,
                                          EditTweetFragment.Callback,
                                          ConnectionReceiver.Observer,
                                          RecentlyDirectMessageListFragment.CallBack,
                                          TabViewControl,
                                          FragmentControl,
                                          LoginUserPresenter.View {

    private ConnectionReceiver mReceiver;

    private HashtagEntity[] mHashTagEntities;

    private SearchView mSearchView;

    private DrawerLayout mDrawerLayout;

    private int mDisplayPosition = -1;

    private FragmentController mFragmentController;

    private ActionBarDrawerToggle mDrawerToggle;

    private UserEntity mLoginUser;

    private ViewPager mViewPager;

    private final boolean DEBUG = false;

    private int mTabPosition = 0;

    private TwitterUserDrawerView userDrawerView;

    private LoginUserPresenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);

        if (DEBUG) {
            this.setExceptionHandle();
            Picasso.with(this).setIndicatorsEnabled(true);
            String result = EncryptUtil.encrypt("test", this);
            EncryptUtil.decrypt(result, this);
        }

        TwitterUtil.clearAllPreference(this);

        if (!TwitterUtil.hasAccessToken(this)) {
            this.startOauthActivity();
            return;
        }

        Setting.init(this);
        final Setting setting = new Setting();
        final int theme = setting.getTheme();
        setTheme(theme);
        setContentView(R.layout.activity_home);

        new LoginUserPresenter(ModelContainer.getLoginUserScreen(), this);
        this.presenter.loadUser();
        this.setupActionBar();
        this.setupNavigationDrawer(theme);
        this.setupTweetButton(theme);
        this.setupTimeLine(theme);

        Log.d("LoginUserActivity", "onCreate");

        mFragmentController = new FragmentController(getSupportFragmentManager());
    }

    /**
     * Twitter認証Activityを表示します
     * 当Activityはfinishします
     */
    private void startOauthActivity() {
        Intent intent = new Intent(this, TwitterOauthActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mReceiver == null) {
            IntentFilter filter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
            mReceiver = new ConnectionReceiver(this);
            registerReceiver(mReceiver, filter);
        }
    }

    private void setExceptionHandle() {
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            Toast.makeText(LoginUserActivity.this, "問題が発生しました" + throwable.getMessage(), Toast.LENGTH_LONG).show();
            LoginUserActivity.this.applicationFinalizer();
            LoginUserActivity.this.finish();
        });
    }


    private void setupNavigationDrawer(int theme) {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.setDrawerIndicatorEnabled(true);

        this.userDrawerView = (TwitterUserDrawerView)findViewById(R.id.drawer);
        DrawerButtonListAdapter adapter = new DrawerButtonListAdapter(this, R.layout.drawer_list_button, theme);
        this.userDrawerView.setOnListButtonClickListener(this.drawerItemClickListener);
        this.userDrawerView.setAdapter(adapter);
    }


    private void setupTweetButton(int theme) {
        FloatingActionButton editTweetButton = (FloatingActionButton) findViewById(R.id.edit_tweet_button);
        editTweetButton.setOnClickListener(v -> {
            final Map<String, Object> args = new HashMap<>();
            if (mHashTagEntities != null) {
                args.put("hashTag", mHashTagEntities);
            }

            LoginUserActivity.this.addFragment(ScreenNav.TWEET_EDITOR, args);
        });
        editTweetButton.setBackgroundTintList(UIControlUtil.buttonTint(this, theme));
    }

    private void setupActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            this.replaceTitle(R.string.actionbar_home);
        }
    }


    private final AdapterView.OnItemClickListener drawerItemClickListener = (parent, view, position, id) -> {
        mDrawerLayout.closeDrawer(GravityCompat.START);
        final ScreenNav screenNav = LoginUserActivity.this.userDrawerView.getButtonAtPosition(position).screenNav;
        final Map<String, Object> args = new HashMap<>();
        args.put("user", mLoginUser);
        screenNav.transition(this, getSupportFragmentManager(), R.id.home_container, args,
                fragment -> {
                    replaceTitle(fragment.toString());
                    changeActionBarIndicatorState();
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                });
    };


    private void setupTimeLine(int theme) {
        TimeLinePager pagerAdapter = new TimeLinePager(getSupportFragmentManager(), TwitterAccount.getLoginUserId());

        mViewPager = (ViewPager) LoginUserActivity.this.findViewById(R.id.pager);
        mViewPager.addOnPageChangeListener(LoginUserActivity.this);
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setOffscreenPageLimit(2);

        NotificationTabLayout tabLayout = (NotificationTabLayout) LoginUserActivity.this.findViewById(R.id.fragmentTab);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setSelectedTabIndicatorColor(UIControlUtil.textColor(this));

        tabLayout.setup(theme);
    }



    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("display-fragment", mDisplayPosition);
        outState.putSerializable("hash-tag-item", mHashTagEntities);
        outState.putInt("tab-position", mTabPosition);
    }


    @Override
    public void onRestoreInstanceState(@NonNull Bundle inState) {
        super.onRestoreInstanceState(inState);
        mDisplayPosition = inState.getInt("display-fragment");
        mHashTagEntities = (HashtagEntity[])inState.getSerializable("hash-tag-item");
        mTabPosition = inState.getInt("tab-position");
    }


    @Override
    public void onResume() {
        super.onResume();
        this.presenter.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
        this.presenter.onPause();
        CustomToast.cancelToast();
    }


    @Override
    public void onDestroy() {
        this.applicationFinalizer();
        super.onDestroy();
    }


    void applicationFinalizer() {
        ModelContainer.destroy();

        this.presenter.onDestroy();

        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }

        CachManager.deleteCache(this);
        Serializer.saveUser(this, mLoginUser);
    }



    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageSelected(int position) {
        this.changeTitle(position);

        if (position == TimeLinePager.POSITION_MESSAGE) {
            ((FloatingActionButton)LoginUserActivity.this.findViewById(R.id.edit_tweet_button)).hide();
        } else {
            if ((LoginUserActivity.this.findViewById(R.id.edit_tweet_button)).getVisibility() != View.VISIBLE) {
                ((FloatingActionButton)LoginUserActivity.this.findViewById(R.id.edit_tweet_button)).show();
            }
        }

        mTabPosition = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {}



    private void changeActionBarIndicatorState() {
        if (getSupportActionBar() == null) {
            throw new IllegalStateException("ActionBar is null!");
        }

        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerToggle.setDrawerIndicatorEnabled(false);
    }


    private void addFragment(ScreenNav screenNav, Map<String, Object> args) {
        screenNav.transition(this, getSupportFragmentManager(), R.id.home_container, args,
                fragment -> {
                    replaceTitle(fragment.toString());
                    changeActionBarIndicatorState();
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                });
    }


    @Override
    public void removeEditTweetFragment(Fragment tweetEditor) {
        onBackPressed();
    }



    @Override
    public void onConnect() {
        this.presenter.connectNetwork();
    }


    @Override
    public void onDisconnect() {
        this.presenter.disconnectNetwork();
    }

    private void replaceTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) throw new NullPointerException("ActionBar is null!");
        actionBar.setTitle(title);
    }

    private void replaceTitle(int title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) throw new NullPointerException("ActionBar is null!");
        actionBar.setTitle(title);
    }

    @Override
    public void showReceiveReplyMessage(TweetEntity reply) {
        CustomToast.show(this, getString(R.string.reply_from) + reply.user.getName() + "\n" + reply.text, Toast.LENGTH_LONG);
    }

    @Override
    public void onCompletePost() {
        this.userDrawerView.incrementCount(FragmentController.FRAGMENT_ID_TWEET);
    }

    @Override
    public void showReceiveDeletionMessage(TweetEntity deletedTweet) {
        CustomToast.show(this, getString(R.string.delete) + deletedTweet.user.getName() + "\n" + deletedTweet.text, Toast.LENGTH_LONG);
    }

    @Override
    public void onCompleteDeleteTweet() {
        this.userDrawerView.decrementCount(FragmentController.FRAGMENT_ID_TWEET);
    }

    @Override
    public void showFavoritedMessage(TweetEntity tweet, UserEntity user) {
        CustomToast.show(this, getString(R.string.favorite_by) + "\n" + user.getName() + "\n" + tweet.text, Toast.LENGTH_LONG);
    }

    @Override
    public void onCompleteFavorite() {
        CustomToast.show(this, R.string.favorite_complete, Toast.LENGTH_LONG);
        this.userDrawerView.incrementCount(FragmentController.FRAGMENT_ID_FAVORITE);
    }

    @Override
    public void showUnFavoritedMessage(TweetEntity tweet, UserEntity user) {
        CustomToast.show(this, getString(R.string.unfavorite_by) + "\n" + user.getName() + "\n" + tweet.text, Toast.LENGTH_LONG);
    }

    @Override
    public void onCompleteUnFavorite() {
        CustomToast.show(this, R.string.unfavorite_complete, Toast.LENGTH_LONG);
        this.userDrawerView.decrementCount(FragmentController.FRAGMENT_ID_FAVORITE);
    }

    @Override
    public void showReceivedDirectMessage(DirectMessageEntity directMessage) {
        CustomToast.show(this, R.string.message_by + directMessage.sender.getName() + "\n" + directMessage.text, Toast.LENGTH_LONG);
    }

    @Override
    public void showStartUserStreamMessage() {
        CustomToast.show(this, R.string.user_stream_connect, Toast.LENGTH_SHORT);
    }

    @Override
    public void showStopUserStreamMessage() {
        CustomToast.show(this, R.string.user_stream_disconnect, Toast.LENGTH_SHORT);
    }


    @Override
    public void displayDirectMessageEditor(long userId) {
        final Map<String, Object> args = new HashMap<>();
        args.put("userId", userId);
        this.addFragment(ScreenNav.MESSAGE_EDITOR, args);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView =
                (SearchView)searchItem.getActionView();
        mSearchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName())
        );

        mSearchView.setIconifiedByDefault(true);
        mSearchView.setOnQueryTextListener(QUERY_LISTENER);

        searchItem.setOnActionExpandListener(SEARCH_VIEW_EX_LISTENER);

        return true;
    }


    private final MenuItem.OnActionExpandListener SEARCH_VIEW_EX_LISTENER = new MenuItem.OnActionExpandListener() {
        @Override
        public boolean onMenuItemActionExpand(MenuItem item) {
            ((FloatingActionButton)LoginUserActivity.this.findViewById(R.id.edit_tweet_button)).hide();
            return true;
        }

        @Override
        public boolean onMenuItemActionCollapse(MenuItem item) {
            ((FloatingActionButton)LoginUserActivity.this.findViewById(R.id.edit_tweet_button)).show();
            return true;
        }
    };

    private final SearchView.OnQueryTextListener QUERY_LISTENER = new SearchView.OnQueryTextListener() {

        @Override
        public boolean onQueryTextSubmit(String query) {
            final Map<String, Object> args = new HashMap<>();
            args.put("user", mLoginUser);
            args.put("query", query);
            LoginUserActivity.this.addFragment(ScreenNav.SEARCH, args);

//            ActionBar actionBar = getSupportActionBar();
//            if ( actionBar != null ) {
//                //actionBar.collapseActionView();
//            }

            mSearchView.clearFocus();

            return false;
        }

        @Contract(value = "_ -> false", pure = true)
        @Override
        public boolean onQueryTextChange(String newText) {
            return false;
        }
    };



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                this.addFragment(ScreenNav.SETTING, null);
                return true;

            case R.id.action_search:
                return true;

            case android.R.id.home:
                if (mFragmentController.hasFragment()) {
                    onHomePressed();
                    return true;
                }

                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;

            case R.id.action_logout:
                this.showLogoutDialog();
                return true;

            case R.id.action_about_Kawasemi:
                this.addFragment(ScreenNav.LICENSE, null);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("unchecked")
    private void showLogoutDialog() {
        DialogFragment dialogFragment =
                new YesNoSelectDialog.Builder<UserEntity>()
                        .setItem(mLoginUser)
                        .setSummary("ログアウトしますか？")
                        .setPositiveAction((YesNoSelectDialog.Listener<UserEntity>) item -> LoginUserActivity.this.logout())
                        .setNegativeAction((YesNoSelectDialog.Listener<UserEntity>) item -> {
                            //do nothing
                        })
                        .build();

        dialogFragment.show(getSupportFragmentManager(), "YesNoSelectDialog");
    }



    @Override
    public void onBackPressed() {
        //Navigation Drawerが開いていたら閉じる
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            return;
        }

        //Fragment表示中だったらremoveしてActionBarのtitle変える
        if (mFragmentController.hasFragment()) {
            mFragmentController.removeCurrentFragment(R.id.home_container);
            changeTitle();
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

            if (!mFragmentController.hasFragment()) {
                mDrawerToggle.setDrawerIndicatorEnabled(true);
                mDisplayPosition = -1;

                if (this.mViewPager.getCurrentItem() != TimeLinePager.POSITION_MESSAGE) {
                    ((FloatingActionButton)LoginUserActivity.this.findViewById(R.id.edit_tweet_button)).show();
                }
            }

            return;
        }

        //ViewPagerの1ページ目を表示
        if (mViewPager.getCurrentItem() != 0) {
            mViewPager.setCurrentItem(0, true);
            return;
        }

        super.onBackPressed();
    }


    void onHomePressed() {
        mFragmentController.removeAllFragment(R.id.home_container);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDisplayPosition = -1;
        InputMethodManager mgr = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null) {
            mgr.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        changeTitle();

        if (this.mViewPager.getCurrentItem() != TimeLinePager.POSITION_MESSAGE) {
            ((FloatingActionButton)LoginUserActivity.this.findViewById(R.id.edit_tweet_button)).show();
        }
    }


    @Override
    public void onCompletePostTweet(TweetEntity tweet) {
        Toast.makeText(this, R.string.tweet_complete, Toast.LENGTH_SHORT).show();

        HashtagEntity[] hashTagEntity = tweet.hashtagEntities;

        if (hashTagEntity == null || hashTagEntity.length == 0) {
            mHashTagEntities = null;
            return;
        }

        this.mHashTagEntities = hashTagEntity;
    }


    /**
     * ActionBarのタイトルを変更する
     * Fragmentが表示されている場合には,そのタイトルを{@code toString}で取得しする
     * 表示していない場合には{@code ViewPager}より表示中のitem positionを取得して,
     * そのFragmentのタイトルを表示する
     */
    protected void changeTitle() {
        if (mFragmentController.hasFragment()) {
            Fragment currentFragment = mFragmentController.getFragment(R.id.home_container);
            replaceTitle(currentFragment.toString());
            return;
        }


        this.changeTitle(mViewPager.getCurrentItem());
    }


    private void changeTitle(int tabPosition) {
        if (tabPosition == TimeLinePager.POSITION_HOME) {
            this.replaceTitle(R.string.actionbar_home);
        } else if (tabPosition == TimeLinePager.POSITION_REPLY) {
            this.replaceTitle(R.string.actionbar_reply);
        } else if (tabPosition == TimeLinePager.POSITION_MESSAGE) {
            this.replaceTitle(R.string.actionbar_message);
        } else {
            throw new IllegalStateException("onPageSelected position is " + tabPosition);
        }
    }


    public void logout() {
        Intent intent = new Intent(this, TwitterOauthActivity.class);
        startActivity(intent);
        this.presenter.logout();
        SharedPreferenceUtil.clearLoginUserInfo(this);
        finish();
    }

    @Override
    public boolean isCurrentSelect(@NonNull TabManagedView view) {
        return this.mViewPager.getCurrentItem() == view.tabPosition();
    }

    @Override
    public void requestChangeTabState(@NonNull TabManagedView view) {
        if (!view.getRequestTabState().hasUnreadItem() && !isCurrentSelect(view)) {
            return;
        }
        ((NotificationTabLayout)findViewById(R.id.fragmentTab)).onRequestChangeTab(
                view.tabPosition(), view.getRequestTabState());
    }


    @Override
    public void requestChangeScreen(ScreenNav screenNav, Map<String, Object> args) {
        this.addFragment(screenNav, args);
    }


    @Override
    public void onLoadLoginUser(UserEntity user) {
        this.mLoginUser = user;
        this.userDrawerView.updateUser(user);
    }

    @Override
    public void setPresenter(LoginUserPresenter presenter) {
        this.presenter = presenter;
    }
}