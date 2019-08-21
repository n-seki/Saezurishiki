package com.seki.saezurishiki.network.twitter;

import android.content.Context;
import androidx.loader.app.LoaderManager;

import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;


/**
 * Twitter送受信メソッド提供クラス<br>
 * 受信後のtaskとpaging,idを渡してやると非同期通信する
 * @author seki
 */
public final class TwitterWrapper {

    private TwitterWrapper() { }

    public static void getOAuthAccessToken(final Context context, final LoaderManager loaderManager, final RequestToken token, final String verifier, AsyncTwitterTask.AfterTask<AccessToken> afterTask) {
        final AsyncTwitterTask.AsyncTask<AccessToken> TASK =
                () -> TwitterUtil.getUnauthorizedTwitter(context).getOAuthAccessToken(token, verifier);

        new AsyncTwitterTask<>(context, TASK, afterTask, loaderManager).run();
    }
}
