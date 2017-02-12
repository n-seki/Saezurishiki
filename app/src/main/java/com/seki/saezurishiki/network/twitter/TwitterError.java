package com.seki.saezurishiki.network.twitter;

import android.content.Context;
import android.widget.Toast;

import com.seki.saezurishiki.R;
import com.seki.saezurishiki.control.CustomToast;

import org.jetbrains.annotations.Contract;

import twitter4j.TwitterException;

import static com.seki.saezurishiki.network.TwitterErrorCode.BAD_GATEWAY;
import static com.seki.saezurishiki.network.TwitterErrorCode.NOT_AUTHORIZED_TO_SEE_STATUS;
import static com.seki.saezurishiki.network.TwitterErrorCode.PAGE_NOT_EXIST;
import static com.seki.saezurishiki.network.TwitterErrorCode.RATE_LIMIT_EXCEEDED;
import static com.seki.saezurishiki.network.TwitterErrorCode.STATUS_IS_DUPLICATE;

/**
 * TwitterExceptionからエラーメッセージを表示するクラス<br>
 * @author seki
 */
public class TwitterError {

    public static void showText(Context context, TwitterException twitterException) {
        String text = context.getString(getErrorTextCode(twitterException.getErrorCode()));
        CustomToast.show(context, text, Toast.LENGTH_SHORT);
    }



    @Contract(pure = true)
    public static int getErrorTextCode(int twitterErrorCode) {
        switch( twitterErrorCode ) {
            case PAGE_NOT_EXIST :
                return R.string.page_not_exist;

            case NOT_AUTHORIZED_TO_SEE_STATUS :
                return R.string.not_authorized_to_see_status;

            case STATUS_IS_DUPLICATE :
                return R.string.status_is_duplicate;

            case RATE_LIMIT_EXCEEDED :
                return R.string.rate_limit_exceeded;

            case BAD_GATEWAY :
                return R.string.bad_gateway;

            default:
                return R.string.twitter_bad_something;
        }
    }
}
