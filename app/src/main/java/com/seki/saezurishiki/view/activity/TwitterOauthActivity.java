package com.seki.saezurishiki.view.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.view.View;
import android.widget.Toast;

import com.seki.saezurishiki.R;
import com.seki.saezurishiki.network.twitter.AsyncTwitterTask;
import com.seki.saezurishiki.network.twitter.TwitterTaskResult;
import com.seki.saezurishiki.network.twitter.TwitterTaskUtil;
import com.seki.saezurishiki.network.twitter.TwitterUtil;

import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

/**
 * Twitter認証用Activity<BR>
 * アカウント認証を行うためにAccessTokenの取得などを行い,
 * 承認が成功すると,承認ユーザー情報でLoginUserActivityを開始する
 * @author seki
 */
public class TwitterOauthActivity extends AppCompatActivity {

    private RequestToken mRequestToken;
    private String mCallback;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_pre_login);

        findViewById(R.id.start_oauth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TwitterOauthActivity.this.startAuthorize();
            }
        });

        mCallback = getString(R.string.callback_url);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    }

    public void startAuthorize() {
        final AsyncTwitterTask.AsyncTask<String> TASK = new AsyncTwitterTask.AsyncTask<String>() {
            @Override
            public String doInBackground() throws TwitterException {
                mRequestToken = TwitterUtil.getUnauthorizedTwitter(TwitterOauthActivity.this).getOAuthRequestToken(mCallback);
                return mRequestToken.getAuthorizationURL();
            }
        };

        final AsyncTwitterTask.AfterTask<String> AFTER_TASK = new AsyncTwitterTask.AfterTask<String>() {
            @Override
            public void onLoadFinish(TwitterTaskResult<String> result) {
                if (result.isException()) {
                    return;
                }

                if (result.getResult() != null) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(result.getResult()));
                    TwitterOauthActivity.this.startActivity(intent);
                }
            }
        };

        new AsyncTwitterTask<>(this, TASK, AFTER_TASK, getSupportLoaderManager()).run();

    }


    @Override
    public void onNewIntent(Intent intent) {
        if ( intent == null
                || intent.getData() == null
                || !intent.getData().toString().startsWith(mCallback)) {
            return;
        }
        String verifier = intent.getData().getQueryParameter("oauth_verifier");
        this.oauthAccess(verifier);
    }



    public void oauthAccess(final String verifier) {
        final AsyncTwitterTask.AfterTask<AccessToken> AFTER_TASK = new AsyncTwitterTask.AfterTask<AccessToken>() {
            @Override
            public void onLoadFinish(TwitterTaskResult<AccessToken> result) {
                if (result.isException()) {
                    return;
                }

                if (result.getResult() != null) {
                    Toast.makeText(TwitterOauthActivity.this, R.string.oauth_done, Toast.LENGTH_SHORT).show();
                    TwitterOauthActivity.this.successOauth(result.getResult());
                }
            }
        };

        TwitterTaskUtil.getOAuthAccessToken(this, getSupportLoaderManager(), mRequestToken, verifier, AFTER_TASK);
    }



    private void successOauth(AccessToken accessToken) {
        TwitterUtil.storeAccessToken(this, accessToken);
        Intent intent = new Intent(this, LoginUserActivity.class);
        startActivity(intent);
        finish();
    }

}
