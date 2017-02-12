package com.seki.saezurishiki.view.activity;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

import com.seki.saezurishiki.application.SaezurishikiApp;
import com.seki.saezurishiki.R;
import com.seki.saezurishiki.view.adapter.BioHeaderPageAdapter;
import com.seki.saezurishiki.view.adapter.DrawerButtonListAdapter;
import com.seki.saezurishiki.control.CustomToast;
import com.seki.saezurishiki.control.FragmentController;
import com.seki.saezurishiki.control.RelationshipModel;
import com.seki.saezurishiki.view.fragment.editor.EditTweetFragment;
import com.seki.saezurishiki.view.fragment.dialog.YesNoSelectDialog;
import com.seki.saezurishiki.network.twitter.AsyncTwitterTask;
import com.seki.saezurishiki.network.twitter.TwitterAccount;
import com.seki.saezurishiki.network.twitter.TwitterError;
import com.seki.saezurishiki.network.twitter.TwitterTaskResult;
import com.seki.saezurishiki.network.twitter.TwitterTaskUtil;
import com.seki.saezurishiki.network.twitter.streamListener.UserStreamUserListener;
import com.seki.saezurishiki.view.control.FragmentControl;

import java.io.Serializable;

import twitter4j.Relationship;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.User;

/**
 * ユーザー情報管理Activity<br>
 * ログインユーザーによって選択されたUserに関するFragmentなどを管理する
 * ログインユーザーとのrelation等も扱う
* @author seki
*/
public class UserActivity extends    AppCompatActivity
                          implements EditTweetFragment.Callback,
                                     UserStreamUserListener,
                                     FragmentControl {


    public static final String USER = "User";
    public static final String USER_ID = "userID";

    public static final int SHOW_ACTIVITY = 0x0800;

    private RelationshipModel relation;

    private TwitterTaskUtil mTwitterTask;
    private User mUser;

    private FragmentController mFragmentController;
    private DrawerButtonListAdapter mListAdapter;

    private TwitterAccount twitterAccount;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);

        SaezurishikiApp app = (SaezurishikiApp)getApplication();
        this.twitterAccount = app.getTwitterAccount();
        this.twitterAccount.addStreamListener(this);

        final int theme = this.twitterAccount.setting.getTheme();
        setTheme(theme);
        setContentView(R.layout.activity_biography);

        this.relation = new RelationshipModel();
        mFragmentController = new FragmentController(getSupportFragmentManager());
        mTwitterTask = new TwitterTaskUtil(this, getSupportLoaderManager(), this.twitterAccount);
        mListAdapter = new DrawerButtonListAdapter(this, R.layout.drawer_list_button, theme);
        this.loadUser();

    }

    @Override
    public void onDestroy() {
        this.twitterAccount.removeListener(this);
        super.onDestroy();
    }


    private void setupActionBar() {
        Toolbar toolbar = (Toolbar)findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if ( actionBar == null ) {
            throw  new IllegalStateException("ActionBar is null");
        }

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        this.replaceTitle(mUser.getName(), mUser.getScreenName());
    }

    private void setupUserInformation() {
        this.setupActionBar();
        this.showRelationship();
        this.displayBiographyInfo(mUser);
    }


    protected void loadUser() {
        Serializable temp = getIntent().getExtras().getSerializable(USER);

        if (temp instanceof User) {
            mUser = (User)temp;
            this.setupUserInformation();
        } else {
            this.asyncLoadUser();
        }
    }


    private final AdapterView.OnItemClickListener drawerItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if ( mListAdapter.getItem(position).getAction() == FragmentController.FRAGMENT_ID_DIRECT_MESSAGE_EDITOR ) {
                if (!UserActivity.this.relation.isMutualFollow()) {
                    return;
                }
            }
            UserActivity.this.displayFragment(mListAdapter.getItem(position).getAction());
        }
    };



    private void asyncLoadUser() {
        final long userID = getIntent().getExtras().getLong(USER_ID);
        mTwitterTask.showUser(userID, new AsyncTwitterTask.AfterTask<User>() {
            @Override
            public void onLoadFinish(TwitterTaskResult<User> result) {
                UserActivity.this.onLoadUser(result, userID);
            }
        });
    }


    void onLoadUser(TwitterTaskResult<User> result, final long userId) {
        if (result.isException()) {
            TwitterError.showText(this, result.getException());
            User user = this.twitterAccount.getRepository().getUser(userId);
            if (user == null) {
                UserActivity.this.finish();
                return;
            }

            mUser = user;
            this.setupUserInformation();
            return;
        }

        mUser = result.getResult();

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                UserActivity.this.setupUserInformation();
            }
        });
    }



    public void displayBiographyInfo(User user) {
        ViewPager headerPage = (ViewPager)findViewById(R.id.bio_header_page);
        PagerAdapter pagerAdapter = new BioHeaderPageAdapter(getSupportFragmentManager(), mUser);
        headerPage.setAdapter(pagerAdapter);
        headerPage.setCurrentItem(1);

        Button replyButton = (Button)findViewById(R.id.bio_reply_button);
        replyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserActivity.this.displayFragment(FragmentController.FRAGMENT_ID_TWEET_EDITOR);
            }
        });

        Button messageButton = (Button)findViewById(R.id.bio_message_button);
        messageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserActivity.this.displayFragment(FragmentController.FRAGMENT_ID_DIRECT_MESSAGE_EDITOR);
            }
        });

        Button followButton = (Button)findViewById(R.id.bio_follow_button);
        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserActivity.this.showFollowDialog();
            }
        });

        ListView drawerList = (ListView)findViewById(R.id.bio_drawer_list);
        mListAdapter.setUserItem(user);
        drawerList.setAdapter(mListAdapter);
        drawerList.setOnItemClickListener(drawerItemClickListener);
    }




    private void displayFragment(int position) {
        Fragment fragment = mFragmentController.createFragment(position, mUser);
        if (position != FragmentController.FRAGMENT_ID_DIRECT_MESSAGE_EDITOR && position != FragmentController.FRAGMENT_ID_TWEET_EDITOR) {
            this.replaceFragment(fragment);
        } else {
            this.addFragment(fragment);
        }
    }


    private void replaceFragment(Fragment fragment) {
        mFragmentController.replace(fragment, R.id.biography_container);
        changeSubtitle(fragment.toString());
        changeActionBarIndicatorState();
    }


    private void addFragment(Fragment fragment) {
        mFragmentController.add(fragment, R.id.biography_container);
        changeSubtitle(fragment.toString());
        changeActionBarIndicatorState();
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

        if (this.relation.isBlocking()) {
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
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home :
                onHomePressed();
                return true;

            case R.id.action_search :
                return true;

//            case R.id.action_follow :
//                this.showFollowDialog();
//                return true;

            case R.id.action_block :
                this.showBlockUserDialog();
                return true;

            case R.id.action_release_block:
                this.showReleaseBlockDialog();
                return true;

            case R.id.action_mute:
            case R.id.action_destroy_mute:
                throw new IllegalStateException("Mute is not exist!");

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showRelationship() {
        mTwitterTask.showRelationShip(mUser.getId(), new AsyncTwitterTask.AfterTask<Relationship>() {
            @Override
            public void onLoadFinish(TwitterTaskResult<Relationship> result) {
                UserActivity.this.onShowRelationship(result);
            }
        });
    }


    void onShowRelationship(TwitterTaskResult<Relationship> result) {
        if ( result.isException() ) {
            TwitterError.showText(UserActivity.this, result.getException());
            return;
        }

        UserActivity.this.changeUserInformation(result.getResult());
    }

    private void changeUserInformation(Relationship relation) {
        changeRelationStatus(relation);
        changeUserInformation();
    }


    private void changeUserInformation() {
        changeRelationStatusText();
        changeDirectMessageButtonState();
        changeFollowButtonState();
        invalidateOptionsMenu();
    }

    private void changeFollowButtonState() {
        Button followButton = (Button)findViewById(R.id.bio_follow_button);
        if (mUser.getId() == this.twitterAccount.getLoginUserId() ) {
            followButton.setClickable(false);
            followButton.setTextColor(ContextCompat.getColor(this, R.color.gray_808080));
            return;
        }

        if (this.relation.isFollowByLoginUser()) {
            followButton.setText(R.string.remove_button_label);
        } else {
            followButton.setText(R.string.follow_button_label);
        }

    }

    private void changeDirectMessageButtonState() {
        Button sendButton = (Button) findViewById(R.id.bio_message_button);
        if (this.relation.isMutualFollow()) {
            sendButton.setClickable(true);
            sendButton.setTextColor(ContextCompat.getColor(this, R.color.white_FFFFFF));
        } else {
            sendButton.setClickable(false);
            sendButton.setTextColor(ContextCompat.getColor(this, R.color.gray_808080));
        }
    }



    private void follow() {
        if (this.twitterAccount.getLoginUserId() == mUser.getId() ) {
            return;
        }

        mTwitterTask.createRelationShip(mUser.getId(), new AsyncTwitterTask.AfterTask<User>() {
            @Override
            public void onLoadFinish(TwitterTaskResult<User> result) {
                UserActivity.this.onFollow(result);
            }
        });
    }


    void onFollow(TwitterTaskResult<User> result) {
        if ( result.isException() ) {
            TwitterError.showText(UserActivity.this, result.getException());
            return;
        }

        CustomToast.show(UserActivity.this, R.string.done_follow, Toast.LENGTH_SHORT);
        this.relation.onFollowedByLoginUser();
        changeUserInformation();
    }


    private void remove() {
        mTwitterTask.destroyRelationShip(mUser.getId(), new AsyncTwitterTask.AfterTask<User>() {
            @Override
            public void onLoadFinish(TwitterTaskResult<User> result) {
                UserActivity.this.onRemove(result);
            }
        });
    }


    void onRemove(TwitterTaskResult<User> result) {
        if ( result.isException() ) {
            TwitterError.showText(UserActivity.this, result.getException());
            return;
        }
        CustomToast.show(UserActivity.this, R.string.done_remove, Toast.LENGTH_SHORT);
        this.relation.onRemovedByLoginUser();
        changeUserInformation();
    }



    private void changeRelationStatusText() {
        if (mUser.getId() == this.twitterAccount.getLoginUserId()) {
            ((TextView)findViewById(R.id.bio_relation)).setText(R.string.showing_loginUser);
            return;
        }
        ((TextView)findViewById(R.id.bio_relation)).setText(this.relation.toStringResource());
    }


    private void changeRelationStatus(Relationship relationship) {
        this.relation.update(relationship);
    }


    @SuppressWarnings("unchecked")
    private void displayFollowDialog() {

        final boolean follow = this.relation.isFollowByLoginUser();

        DialogFragment dialogFragment =
                new YesNoSelectDialog.Builder<User>()
                        .setItem(mUser)
                        .setSummary(mUser.getScreenName() + (follow ? "をリムーブしますか？" : "をフォローしますか？"))
                        .setPositiveAction(new YesNoSelectDialog.Listener<User>() {
                            @Override
                            public void onItemClick(User item) {
                                if (follow)
                                    UserActivity.this.remove();
                                else
                                    UserActivity.this.follow();
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
    public void removeEditTweetFragment(Fragment tweetEditor) {
        onBackPressed();
    }

    @Override
    public void postTweet(StatusUpdate status) {
        mTwitterTask.post(status, new AsyncTwitterTask.AfterTask<Status>() {
            @Override
            public void onLoadFinish(TwitterTaskResult<Status> result) {
                UserActivity.this.onPostTweet(result);
            }
        });
    }


    void onPostTweet(TwitterTaskResult<Status> result) {
        if ( result.isException() ) {
            TwitterError.showText(UserActivity.this, result.getException());
            return;
        }

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


    private void changeTitle() {
        if (!mFragmentController.hasFragment()) {
            this.replaceTitle(mUser.getName(), mUser.getScreenName());
            return;
        }

        Fragment currentFragment = mFragmentController.getFragment(R.id.biography_container);
        replaceTitle(mUser.getName(), currentFragment.toString());
    }

    private void changeSubtitle(String subtitle) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) throw new NullPointerException("ActionBar is null!");

        actionBar.setSubtitle(subtitle);
    }


    void onHomePressed() {
        if (!mFragmentController.hasFragment()) {
            finish();
            return;
        }
        mFragmentController.removeAllFragment(R.id.biography_container);
        changeTitle();
    }


    @Override
    public void onBackPressed() {
        if (!mFragmentController.hasFragment()) {
            finish();
            return;
        }

        mFragmentController.removeCurrentFragment(R.id.biography_container);
        changeTitle();

        if (!mFragmentController.hasFragment()) {
            changeSubtitle(mUser.getScreenName());
        }
    }


    @SuppressWarnings("unchecked")
    public void displayFollowRequestDialog() {
        DialogFragment dialogFragment =
                new YesNoSelectDialog.Builder<User>()
                        .setItem(mUser)
                        .setTitle(R.string.follow_request)
                        .setSummary(mUser.getScreenName() + "にフォローリクエストを送信しますか？")
                        .setPositiveAction(new YesNoSelectDialog.Listener<User>() {
                            @Override
                            public void onItemClick(User item) {
                                UserActivity.this.sendFollowRequest();
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


    public void sendFollowRequest() {
        mTwitterTask.follow(mUser, new AsyncTwitterTask.AfterTask<User>() {
            @Override
            public void onLoadFinish(TwitterTaskResult<User> result) {
                UserActivity.this.onSendFollowRequest(result);
            }
        });
    }


    void onSendFollowRequest(TwitterTaskResult<User> result) {
        if (result.isException()) {
            TwitterError.showText(UserActivity.this, result.getException());
            return;
        }

        CustomToast.show(UserActivity.this, R.string.done_follow_request, Toast.LENGTH_SHORT);
    }



    @SuppressWarnings("unchecked")
    public void showBlockUserDialog() {
        DialogFragment dialogFragment =
                new YesNoSelectDialog.Builder<User>()
                        .setItem(mUser)
                        .setTitle(R.string.action_block)
                        .setSummary(mUser.getScreenName() + "をブロックしますか？")
                        .setPositiveAction(new YesNoSelectDialog.Listener<User>() {
                            @Override
                            public void onItemClick(User item) {
                                UserActivity.this.blockUser();
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


    public void blockUser() {
        mTwitterTask.block(mUser, new AsyncTwitterTask.AfterTask<User>() {
            @Override
            public void onLoadFinish(TwitterTaskResult<User> result) {
                UserActivity.this.onBlock(result);
            }
        });
    }


    void onBlock(TwitterTaskResult<User> result) {
        if (result.isException()) {
            TwitterError.showText(UserActivity.this, result.getException());
            return;
        }

        CustomToast.show(UserActivity.this, R.string.block_complete, Toast.LENGTH_SHORT);
        this.relation.onBlock();
        this.changeUserInformation();
    }


    @SuppressWarnings("unchecked")
    private void showReleaseBlockDialog() {
        DialogFragment dialogFragment =
                new YesNoSelectDialog.Builder<User>()
                        .setItem(mUser)
                        .setTitle(R.string.action_destroy_block)
                        .setSummary(mUser.getScreenName() + "のブロックを解除しますか？")
                        .setPositiveAction(new YesNoSelectDialog.Listener<User>() {
                            @Override
                            public void onItemClick(User item) {
                                UserActivity.this.releaseBlock();
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


    private void releaseBlock() {
        mTwitterTask.destroyBlock(mUser, new AsyncTwitterTask.AfterTask<User>() {
            @Override
            public void onLoadFinish(TwitterTaskResult<User> result) {
                UserActivity.this.onReleaseBlock(result);
            }
        });
    }


    void onReleaseBlock(TwitterTaskResult<User> result) {
        if (result.isException()) {
            TwitterError.showText(UserActivity.this, result.getException());
            return;
        }

        CustomToast.show(UserActivity.this, R.string.release_block_complete, Toast.LENGTH_SHORT);
        this.relation.onReleaseBlock();
        this.changeUserInformation();
    }


    protected void showFollowDialog() {
        if (mUser.isProtected() && !this.relation.isFollowByLoginUser()) {
            this.displayFollowRequestDialog();
            return;
        }

        this.displayFollowDialog();
    }

    @Override
    public void onRemoveFragment(Fragment f) {
        onBackPressed();
    }

    @Override
    public void requestShowUser(long userId) {
        //同一ユーザーの表示はしない
        if (userId == this.mUser.getId()) return;

        Intent intent = new Intent(this, UserActivity.class);
        intent.putExtra(USER_ID, userId);
        startActivity(intent);
    }

    @Override
    public void requestShowFragment(Fragment fragment) {
        this.addFragment(fragment);
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
