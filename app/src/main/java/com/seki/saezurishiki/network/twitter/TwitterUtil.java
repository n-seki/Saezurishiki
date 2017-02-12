package com.seki.saezurishiki.network.twitter;

import android.content.Context;
import android.content.SharedPreferences;

import com.seki.saezurishiki.R;
import com.seki.saezurishiki.file.EncryptUtil;

import java.util.HashSet;
import java.util.Set;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Twitter管理クラス<br>
 * AccessTokenの管理などを行う
 * @author seki
 */
public final class TwitterUtil {

    private TwitterUtil(){}

    private final static String PREFERENCE_USER_ID = "All-User-ID";
    private final static String USERS_ACCESS_TOKEN = "users-access-token";
    private final static String ACCESS_TOKEN = "Access-Token";
    private final static String ACCESS_TOKEN_SECRET = "Access-Token-Secret";
    private final static String USER_ID = "user-id";
    private static final String LOGIN_USER_ID = "login-user-id";
    private final static String ENCRYPT_DATA = "encrypt-data";

    /**
     * Oauth認証がなされていないTwitterインスタンスを返す.
     * ただし,ConsumerKeyとConsumerSecretは設定してある.
     * 基本的にOauth認証をするために使用するインスタンス.
     * @param context context
     * @return Twitter
     */
    public static Twitter getUnauthorizedTwitter(Context context) {
        return new TwitterFactory(createConfiguration(context)).getInstance();
    }


    /**
     * ログインユーザーのAccessTokenを生成して返します.
     * @param context context
     * @return AccessToken
     */
    static synchronized AccessToken createLoginUserAccessToken(Context context) {
        String loginUserID = String.valueOf(createLoginUserId(context));
        SharedPreferences preferences = context.getSharedPreferences(USERS_ACCESS_TOKEN, Context.MODE_PRIVATE);
        String accessToken = preferences.getString(ACCESS_TOKEN + loginUserID, "");
        String tokenSecret = preferences.getString(ACCESS_TOKEN_SECRET + loginUserID, "");

        final String decryptToken = EncryptUtil.decrypt(accessToken, context);
        final String decryptSecret = EncryptUtil.decrypt(tokenSecret, context);
        return new AccessToken(decryptToken, decryptSecret);

    }


    /**
     * Twitter,TwitterStreamインスタンス生成に必要なConfigurationインスタンスを生成します.
     * 当メソッドで生成されるConfigurationにはConsumerKey,ConsumerSecretが設定されています.
     * コール回数は限られているため,Configurationインスタンスはフィールドで保持せず,
     * 毎回新しいインスタンスを生成します.
     * @param context context
     * @return Configuration
     */
    static Configuration createConfiguration(Context context) {
//        final String key = EncryptUtil.decrypt(context.getString(R.string.oauth_consumer_key), context);
//        final String secret = EncryptUtil.decrypt(context.getString(R.string.oauth_consumer_secret), context);
        return new ConfigurationBuilder().setDispatcherImpl("twitter4j.DispatcherImpl")
                .setOAuthConsumerKey(context.getString(R.string.oauth_consumer_key))
                .setOAuthConsumerSecret(context.getString(R.string.oauth_consumer_secret))
                .build();

    }


    /**
     * アプリが認証したユーザー情報を保持しているか判定する.
     * 内部的には,ログインユーザーIDがSharedPreferenceに保存されているか否かの結果を返す.
     * 注意としては,SharedPreferenceの状態がなんらかの理由で破壊され,LoginUserIDとして不正な値が設定されていた場合にも
     * 当メソッドは{@code true}を返却します.
     * @param context context
     * @return AccessTokenを保持していればtrue,そうでなければfalse
     */
    public static boolean hasAccessToken(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCE_USER_ID, Context.MODE_PRIVATE);
        return preferences.contains(LOGIN_USER_ID);
    }


    /**
     * ユーザーのAccessToken情報をSharedPreferenceに保存する.
     * このメソッドはOauth認証成功時に呼び出される.認証後は認証完了したユーザーでアプリを起動するため,
     * 認証したユーザーをログインユーザーとして処理,保存する.
     * AccessTokenをフィールドに保持し,I/O処理を減らす.SharedPreferenceのkeyは以下の通り
     *
     * ファイル名:user-access-token(USERS_ACCESS_TOKEN)
     * (Access-Token + userID, accessToken)
     * (Access-Token-Secret + userId, accessTokenSecret)
     * @param context context
     * @param accessToken AccessToken
     */
    public static void storeAccessToken(Context context, AccessToken accessToken) {
        storeAccessTokenEncrypt(context, accessToken);
        storeLoginUserID(context, accessToken.getUserId(), accessToken.getScreenName());
        storeUserName(context, accessToken.getScreenName());
    }

    private static void storeAccessTokenEncrypt(Context context, AccessToken accessToken) {
        SharedPreferences preferences = context.getSharedPreferences(USERS_ACCESS_TOKEN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        final String encryptToken = EncryptUtil.encrypt(accessToken.getToken(), context);
        final String encryptTokenSecret = EncryptUtil.encrypt(accessToken.getTokenSecret(), context);
        editor.putString(ACCESS_TOKEN + String.valueOf(accessToken.getUserId()), encryptToken);
        editor.putString(ACCESS_TOKEN_SECRET + String.valueOf(accessToken.getUserId()), encryptTokenSecret);
        editor.putBoolean(ENCRYPT_DATA, true);
        editor.apply(); //async
    }


    /**
     * 認証成功したユーザーのIDと名前をSharedPreferenceに保存する.
     * ユーザー情報を保存するSharedPreferenceは以下のようになっている
     *
     * ファイル名:All-User-ID(PREFERENCE_USER_ID)
     * key = login-user-id : value = ログインユーザーのID
     * key = user-id + userName : value = ユーザーのID
     * @param context Context
     * @param userID long
     * @param userName String
     */
    private static void storeLoginUserID(Context context, long userID, String userName) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCE_USER_ID, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        if (!preferences.contains(USER_ID + userName)) {
            editor.putLong(USER_ID + userName, userID);
        }

        if (preferences.getLong(LOGIN_USER_ID, -1) != userID) {
            editor.putLong(LOGIN_USER_ID, userID);
        }

        editor.apply(); //async
    }


    private static final String ALL_USER_NAME = "all-user-name";
    private static final String NAMES = "all_names";



    /**
     * ユーザー名をユーザー名一覧SharedPreferenceに保存する.
     * SharedPreferenceは以下のようになっており,ユーザー名をSet<String>として保存している.
     *
     * ファイル名:all-user-name
     * key = names(NAMES) : value = 全ユーザー名
     * @param context Context
     * @param userName String
     */
    private static void storeUserName(Context context, String userName) {
        SharedPreferences preferences = context.getSharedPreferences(ALL_USER_NAME, Context.MODE_PRIVATE);
        Set<String> names = preferences.getStringSet(NAMES, new HashSet<String>());
        names.add(userName);
        preferences.edit().putStringSet(NAMES, new HashSet<>(names)).apply(); //async
    }

    /**
     * ユーザーID格納SharedPreferenceにログインユーザーIDとして保存している,ユーザーIDを返却する.
     * このメソッド呼び出し時点でユーザーの認証,情報の格納は完了しているため,
     * ログインユーザーIDが格納されていない場合は例外をスローする.
     * 既にフィールドとして保持している場合には、その値を返却する
     * @param context Context
     * @return loginUserID
     */
    static long createLoginUserId(Context context) {
        SharedPreferences usersInfo = context.getSharedPreferences(PREFERENCE_USER_ID, Context.MODE_PRIVATE);

        final long loginUserId = usersInfo.getLong(LOGIN_USER_ID, -1);
        if ( loginUserId == -1 ) {
            throw new IllegalStateException("SharedPreference don't store Login User ID!");
        }

        return loginUserId;
    }


    public static void clearAllPreference(Context context) {
        //暗号化されていない場合にはpreferenceを消去する
        if (!context.getSharedPreferences(USERS_ACCESS_TOKEN, Context.MODE_PRIVATE).getBoolean(ENCRYPT_DATA, false)) {
            context.getSharedPreferences(ALL_USER_NAME, Context.MODE_PRIVATE).edit().clear().apply();
            context.getSharedPreferences(PREFERENCE_USER_ID, Context.MODE_PRIVATE).edit().clear().apply();
            context.getSharedPreferences(USERS_ACCESS_TOKEN, Context.MODE_PRIVATE).edit().clear().apply();
        }
    }


}
