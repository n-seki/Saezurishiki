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
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Toast;

import com.seki.saezurishiki.application.SaezurishikiApp;
import com.seki.saezurishiki.R;
import com.seki.saezurishiki.entity.UserEntity;
import com.seki.saezurishiki.view.adapter.DrawerButtonListAdapter;
import com.seki.saezurishiki.control.CustomToast;
import com.seki.saezurishiki.control.FragmentController;
import com.seki.saezurishiki.control.TimeLinePager;
import com.seki.saezurishiki.control.UIControlUtil;
import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.file.CachManager;
import com.seki.saezurishiki.file.EncryptUtil;
import com.seki.saezurishiki.file.Serializer;
import com.seki.saezurishiki.view.fragment.editor.DirectMessageFragment;
import com.seki.saezurishiki.view.fragment.editor.EditTweetFragment;
import com.seki.saezurishiki.view.fragment.other.LicenseFragment;
import com.seki.saezurishiki.view.fragment.list.RecentlyDirectMessageListFragment;
import com.seki.saezurishiki.view.fragment.list.SearchFragment;
import com.seki.saezurishiki.view.fragment.other.SettingFragment;
import com.seki.saezurishiki.view.fragment.dialog.YesNoSelectDialog;
import com.seki.saezurishiki.network.ConnectionReceiver;
import com.seki.saezurishiki.network.twitter.AsyncTwitterTask;
import com.seki.saezurishiki.network.twitter.TwitterAccount;
import com.seki.saezurishiki.network.twitter.TwitterError;
import com.seki.saezurishiki.network.twitter.TwitterTaskResult;
import com.seki.saezurishiki.network.twitter.TwitterWrapper;
import com.seki.saezurishiki.network.twitter.TwitterUtil;
import com.seki.saezurishiki.network.twitter.streamListener.CustomUserStreamListener;
import com.seki.saezurishiki.view.customview.NotificationTabLayout;
import com.seki.saezurishiki.view.customview.TwitterUserDrawerView;
import com.seki.saezurishiki.view.control.FragmentControl;
import com.seki.saezurishiki.view.control.TabManagedView;
import com.seki.saezurishiki.view.control.TabViewControl;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.Contract;

import twitter4j.DirectMessage;
import twitter4j.HashtagEntity;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusUpdate;
import twitter4j.User;

/**
 * ログインユーザーに関する情報を管理するためのActivity<br>
 * 主にFragmentの管理,各コールバックの実装,ActionBar/NavigationDrawerの管理など
 * @author seki
 */
public class LoginUserActivity extends    AppCompatActivity
                               implements ViewPager.OnPageChangeListener,
                                          EditTweetFragment.Callback,
                                          ConnectionReceiver.Observer,
                                          CustomUserStreamListener,
                                          RecentlyDirectMessageListFragment.CallBack,
                                          TabViewControl,
                                          FragmentControl {

    private ConnectionReceiver mReceiver;

    private HashtagEntity[] mHashTagEntities;

    private SearchView mSearchView;

    private DrawerLayout mDrawerLayout;

    private int mDisplayPosition = -1;

    private TwitterWrapper mTwitterTask;

    private FragmentController mFragmentController;

    private ActionBarDrawerToggle mDrawerToggle;

    private User mLoginUser;

    private ViewPager mViewPager;

    private final boolean DEBUG = false;

    private int mTabPosition = 0;

    private TwitterUserDrawerView userDrawerView;


    private TwitterAccount twitterAccount;

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

        SaezurishikiApp saezurishikiApp = (SaezurishikiApp)getApplication();
        saezurishikiApp.createTwitterAccount();
        this.twitterAccount = saezurishikiApp.getTwitterAccount();

        final int theme = this.twitterAccount.setting.getTheme();
        setTheme(theme);
        setContentView(R.layout.activity_home);

        IntentFilter filter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        mReceiver = new ConnectionReceiver(this);
        registerReceiver(mReceiver, filter);

        mTwitterTask = new TwitterWrapper(this, getSupportLoaderManager(), this.twitterAccount);

        this.loadUser();
        this.setupActionBar();
        this.setupNavigationDrawer(theme);
        this.setupTweetButton(theme);
        this.setupTimeLine(theme);

        mFragmentController = new FragmentController(getSupportFragmentManager());

        twitterAccount.startUserStream();
        twitterAccount.addStreamListener(this);
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

    private void setExceptionHandle() {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable throwable) {
                Toast.makeText(LoginUserActivity.this, "問題が発生しました" + throwable.getMessage(), Toast.LENGTH_LONG).show();
                LoginUserActivity.this.applicationFinalizer();
                LoginUserActivity.this.finish();
            }
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
        editTweetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = mHashTagEntities == null ? EditTweetFragment.newNormalEditor()
                        : EditTweetFragment.newEditorWithHashTag(mHashTagEntities);

                LoginUserActivity.this.addFragment(R.id.home_container, fragment);
            }
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


    private final AdapterView.OnItemClickListener drawerItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final int action = LoginUserActivity.this.userDrawerView.getButtonAtPosition(position).getAction();
            if (action == UserActivity.SHOW_ACTIVITY) {
                LoginUserActivity.this.mDrawerLayout.closeDrawer(GravityCompat.START);
                LoginUserActivity.this.displayBiography(mLoginUser.getId());
                return;
            }
            LoginUserActivity.this.displayFragment(action, mLoginUser);
        }
    };


    private void setupTimeLine(int theme) {
        TimeLinePager pagerAdapter = new TimeLinePager(getSupportFragmentManager(), new UserEntity(mLoginUser));

        mViewPager = (ViewPager) LoginUserActivity.this.findViewById(R.id.pager);
        mViewPager.addOnPageChangeListener(LoginUserActivity.this);
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setOffscreenPageLimit(2);

        NotificationTabLayout tabLayout = (NotificationTabLayout) LoginUserActivity.this.findViewById(R.id.fragmentTab);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setSelectedTabIndicatorColor(UIControlUtil.textColor(this));

        tabLayout.setup(theme);
    }



    private void displayFragment(int position, User user) {
        if (mDisplayPosition == position) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            return;
        }

        addFragment(R.id.home_container, mFragmentController.createFragment(position, user));
        mDrawerLayout.closeDrawer(GravityCompat.START);
        mDisplayPosition = position;
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
        outState.putSerializable("hash-tag-entity", mHashTagEntities);
        outState.putInt("tab-position", mTabPosition);
    }


    @Override
    public void onRestoreInstanceState(@NonNull Bundle inState) {
        super.onRestoreInstanceState(inState);
        mDisplayPosition = inState.getInt("display-fragment");
        mHashTagEntities = (HashtagEntity[])inState.getSerializable("hash-tag-entity");
        mTabPosition = inState.getInt("tab-position");
    }


    @Override
    public void onPause() {
        super.onPause();
        CustomToast.cancelToast();
    }


    @Override
    public void onDestroy() {
        this.applicationFinalizer();
        super.onDestroy();
    }


    void applicationFinalizer() {
        if (this.twitterAccount != null) {
            this.twitterAccount.removeListener(this);
            this.twitterAccount.onActivityDestroyed(this);
        }

        if ( mReceiver != null) {
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


    private void addFragment(int containerViewId, Fragment fragment) {
        mFragmentController.add(fragment, containerViewId);
        replaceTitle(fragment.toString());
        changeActionBarIndicatorState();
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }


    @Override
    public void removeEditTweetFragment(Fragment tweetEditor) {
        onBackPressed();
    }



    @Override
    public void onConnect() {
        CustomToast.show(this, R.string.connect, Toast.LENGTH_SHORT);
        this.loadUser();

        twitterAccount.startUserStream();
    }


    @Override
    public void onDisconnect() {
        twitterAccount.stopUserStream();
        CustomToast.show(this, R.string.disconnect, Toast.LENGTH_SHORT);
    }


    protected void loadUser() {
        AsyncTwitterTask.AfterTask<User> afterTask = new AsyncTwitterTask.AfterTask<User>() {
            @Override
            public void onLoadFinish(TwitterTaskResult<User> result) {
                LoginUserActivity.this.onLoadUser(result);
            }
        };

        AsyncTwitterTask.OnCancelTask cancelTask = new AsyncTwitterTask.OnCancelTask() {
            @Override
            public void onLoadCancel() {
                User user = Serializer.loadUser(LoginUserActivity.this);
                if (user == null) return;
                mLoginUser = user;
                userDrawerView.updateUser(mLoginUser);
            }
        };

        mTwitterTask.showUser(this.twitterAccount.getLoginUserId(), afterTask, cancelTask);
    }


    void onLoadUser(TwitterTaskResult<User> result) {
        if (result.isException()) {
            TwitterError.showText(this, result.getException());
            User user = Serializer.loadUser(this);
            if (user == null) return;
            mLoginUser = user;
        } else {
            mLoginUser = result.getResult();
        }

        this.userDrawerView.updateUser(mLoginUser);
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
    public void onStatus(Status status) {
        if (status.getInReplyToUserId() == this.twitterAccount.getLoginUserId()) {
            CustomToast.show(this, getString(R.string.reply_from) + status.getUser().getName() + "\n" + status.getText(), Toast.LENGTH_LONG);
        }

        if (status.getUser().getId() == this.twitterAccount.getLoginUserId()) {
            this.userDrawerView.incrementCount(FragmentController.FRAGMENT_ID_TWEET);
        }
    }

    @Override
    public void onDeletionNotice(StatusDeletionNotice deletionNotice) {
        if (this.twitterAccount.getRepository().hasStatus(deletionNotice.getStatusId())) {
            TweetEntity target = this.twitterAccount.getRepository().getStatus(deletionNotice.getStatusId());
            CustomToast.show(this, getString(R.string.delete) + target.user.getName() + "\n" + target.text, Toast.LENGTH_LONG);
        }

        if (deletionNotice.getUserId() == this.twitterAccount.getLoginUserId()) {
            this.userDrawerView.decrementCount(FragmentController.FRAGMENT_ID_TWEET);
        }
    }

    @Override
    public void onFavorite(User sourceUser, User targetUser, Status favoriteStatus) {
        if ( sourceUser.getId() == this.twitterAccount.getLoginUserId()) {
            CustomToast.show(this, R.string.favorite_complete, Toast.LENGTH_LONG);
            this.userDrawerView.incrementCount(FragmentController.FRAGMENT_ID_FAVORITE);
            return;
        }

        //自分がお気に入りされた
        if ( targetUser.getId() == this.twitterAccount.getLoginUserId()) {
            CustomToast.show(this, getString(R.string.favorite_by) + "\n" + sourceUser.getName() + "\n" + favoriteStatus.getText(), Toast.LENGTH_LONG);
        }
    }

    @Override
    public void onUnFavorite(User sourceUser, User targetUser, Status unFavoriteStatus) {
        //自分がお気に入りから外した
        if (sourceUser.getId() == this.twitterAccount.getLoginUserId()) {
            CustomToast.show(this, R.string.unfavorite_complete, Toast.LENGTH_LONG);
            this.userDrawerView.decrementCount(FragmentController.FRAGMENT_ID_FAVORITE);
            return;
        }

        //自分がお気に入りを外された
        if (targetUser.getId() == this.twitterAccount.getLoginUserId()) {
            CustomToast.show(this, getString(R.string.unfavorite_by) + "\n" + sourceUser.getName() + "\n" + unFavoriteStatus.getText(), Toast.LENGTH_LONG);
        }
    }


    @Override
    public void onDirectMessage(DirectMessage directMessage) {
        if (directMessage.getSenderId() != this.twitterAccount.getLoginUserId()) {
            CustomToast.show(this, R.string.message_by + directMessage.getSender().getName() + "\n" + directMessage.getText(), Toast.LENGTH_LONG);
        }
    }


    @Override
    public void displayDirectMessageEditor(long messageId) {
        Fragment directMessageEditor = DirectMessageFragment.getInstance(this.twitterAccount.getRepository().getDM(messageId).getSender());
        this.addFragment(R.id.home_container, directMessageEditor);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView =
                (SearchView) MenuItemCompat.getActionView(searchItem);
        mSearchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName())
        );

        mSearchView.setIconifiedByDefault(true);
        mSearchView.setOnQueryTextListener(QUERY_LISTENER);

        MenuItemCompat.setOnActionExpandListener(searchItem, SEARCH_VIEW_EX_LISTENER);

        return true;
    }


    private final MenuItemCompat.OnActionExpandListener SEARCH_VIEW_EX_LISTENER = new MenuItemCompat.OnActionExpandListener() {
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
            Fragment searcher = SearchFragment.getInstance(query);
            LoginUserActivity.this.addFragment(R.id.home_container, searcher);

            ActionBar actionBar = getSupportActionBar();
            if ( actionBar != null ) {
                actionBar.collapseActionView();
            }

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
                Fragment fragment = SettingFragment.getInstance();
                addFragment(R.id.home_container, fragment);
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
                this.addFragment(R.id.home_container, LicenseFragment.newInstance());
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("unchecked")
    private void showLogoutDialog() {
        DialogFragment dialogFragment =
                new YesNoSelectDialog.Builder<User>()
                        .setItem(mLoginUser)
                        .setSummary("ログアウトしますか？")
                        .setPositiveAction(new YesNoSelectDialog.Listener<User>() {
                            @Override
                            public void onItemClick(User item) {
                                LoginUserActivity.this.logout();
                            }
                        })
                        .setNegativeAction(new YesNoSelectDialog.Listener<User>() {
                            @Override
                            public void onItemClick(User item) {
                                //do nothing
                            }
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
    }



    public void displayBiography(long userID) {
        Intent intent = new Intent(this, UserActivity.class);
        intent.putExtra(UserActivity.USER_ID, userID);
        startActivity(intent);
    }



    public void postTweet(StatusUpdate status) {
        mTwitterTask.post(status, new AsyncTwitterTask.AfterTask<Status>() {
            @Override
            public void onLoadFinish(TwitterTaskResult<Status> result) {
                LoginUserActivity.this.onPostTweet(result);
            }
        });
    }


    void onPostTweet(TwitterTaskResult<Status> result) {
        if (result.isException()) {
            CustomToast.show(this, R.string.tweet_fail, Toast.LENGTH_SHORT);
            CustomToast.show(this, result.getException().getMessage(), Toast.LENGTH_LONG);
            return;
        }

        Toast.makeText(this, R.string.tweet_complete, Toast.LENGTH_SHORT).show();

        HashtagEntity[] hashTagEntity = result.getResult().getHashtagEntities();

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
        twitterAccount.logout();
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
    public void onRemoveFragment(@NonNull Fragment f) {
        onBackPressed();
    }

    @Override
    public void requestShowUser(long userId) {
        Intent intent = new Intent(this, UserActivity.class);
        intent.putExtra(UserActivity.USER_ID, userId);
        startActivity(intent);
    }

    @Override
    public void requestShowFragment(@NonNull Fragment fragment) {
        this.addFragment(R.id.home_container, fragment);
    }

    @Override
    public void onFollow(User source, User followedUser) {
    }

    @Override
    public void onRemove(User source, User removedUser) {
    }

    @Override
    public void onBlock(User source, User blockedUser) {
    }

    @Override
    public void onUnblock(User source, User unblockedUser) {
    }
}