package com.seki.saezurishiki.network.twitter;

import android.content.Context;

import com.seki.saezurishiki.cache.DirectMessageCache;
import com.seki.saezurishiki.cache.TweetCache;
import com.seki.saezurishiki.cache.UserCache;
import com.seki.saezurishiki.entity.mapper.EntityMapper;
import com.seki.saezurishiki.file.SharedPreferenceUtil;
import com.seki.saezurishiki.repository.DirectMessageRepository;
import com.seki.saezurishiki.repository.RemoteRepositoryImp;
import com.seki.saezurishiki.repository.TweetRepositoryKt;
import com.seki.saezurishiki.repository.UserRepository;

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
        final EntityMapper mapper = new EntityMapper(account.config.loginUserId);
        RemoteRepositoryImp.onCreate(account.twitter, mapper);
        TweetRepositoryKt.INSTANCE.setup(account.twitter, mapper, new TweetCache());
        UserRepository.INSTANCE.setup(account.twitter, mapper, new UserCache());
        DirectMessageRepository.INSTANCE.setup(account.twitter, mapper, new DirectMessageCache());
        UserStreamManager.onCreate(account);
        return account;
    }

    public static void logout(Context context) {
        UserStreamManager.getInstance().destroy();
        SharedPreferenceUtil.clearLoginUserInfo(context);
    }

    //TODO
    public static long getLoginUserId() {
        return userId;
    }

}
