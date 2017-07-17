package com.seki.saezurishiki.network.twitter;

import android.content.Context;

import com.seki.saezurishiki.entity.mapper.EntityMapper;
import com.seki.saezurishiki.file.SharedPreferenceUtil;
import com.seki.saezurishiki.repository.RemoteRepositoryImp;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;

public class TwitterAccount {
    final TwitterUtil.AccountConfig config;
    private static long userId;
    public final Twitter twitter;

    private TwitterAccount(TwitterUtil.AccountConfig accountConfig) {
        this.config = accountConfig;
        this.twitter = new TwitterFactory(accountConfig.configuration).getInstance(accountConfig.token);
        userId = config.loginUserId;
    }

    public static TwitterAccount onCreate(Context context) {
        TwitterAccount account = new TwitterAccount(new TwitterUtil.AccountConfig(context));
        RemoteRepositoryImp.onCreate(account.twitter, new EntityMapper(account.config.loginUserId));
        UserStreamManager.onCreate(account);
        return account;
    }

    public void logout(Context context) {
        UserStreamManager.getInstance().destroy();
        SharedPreferenceUtil.clearLoginUserInfo(context);
    }

    //TODO
    public static long getLoginUserId() {
        return userId;
    }

}
