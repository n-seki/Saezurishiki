package com.seki.saezurishiki.view.fragment.dialog.adapter;


import com.seki.saezurishiki.R;
import com.seki.saezurishiki.entity.TweetEntity;

public class DialogSelectAction {

    public static final int SHOW_TWEET   = R.string.do_show_tweet;
    public static final int BIOGRAPHY    = 10;
    public static final int URL          = 20;
    public static final int MEDIA = 30;

    public static final int DELETE = R.string.do_delete;
    public static final int RE_TWEET = R.string.do_retweet;
    public static final int UN_RE_TWEET = R.string.do_un_retweet;
    public static final int FAVORITE = R.string.do_favorite;
    public static final int UN_FAVORITE = R.string.do_un_favorite;

    public final Object item;
    public final Class<?> clazz;
    public final int action;

    public DialogSelectAction(Object item, Class<?> clazz, int action) {
        this.item = item;
        this.clazz = clazz;
        this.action = action;
    }


    public static DialogSelectAction showBiography(TweetEntity tweet) {
        return new DialogSelectAction(tweet.user.getId(), Long.class, BIOGRAPHY);
    }

    public static DialogSelectAction showBiography(long userId) {
        return new DialogSelectAction(userId, Long.class, BIOGRAPHY);
    }

    public static DialogSelectAction showTweet(TweetEntity tweet) {
        return new DialogSelectAction(tweet.getId(), Long.class, SHOW_TWEET);
    }

    public static DialogSelectAction openURL(String url) {
        return new DialogSelectAction(url, String.class, URL);
    }

    public static DialogSelectAction mediaURL(String mediaUrl) {
        return new DialogSelectAction(mediaUrl, String.class, MEDIA);
    }

    public static DialogSelectAction delete(long tweetId) {
        return new DialogSelectAction(tweetId, Long.class, DELETE);
    }

    public static DialogSelectAction retweet(long tweetId) {
        return new DialogSelectAction(tweetId, Long.class, RE_TWEET);
    }

    public static DialogSelectAction unRetweet(long tweetID) {
        return new DialogSelectAction(tweetID, Long.class, UN_RE_TWEET);
    }

    public static DialogSelectAction favorite(long tweetId) {
        return new DialogSelectAction(tweetId, Long.class, FAVORITE);
    }

    public static DialogSelectAction unFavorite(long tweetId) {
        return new DialogSelectAction(tweetId, Long.class, UN_FAVORITE);
    }
}
