package com.seki.saezurishiki.view.fragment.dialog.adapter;

import com.seki.saezurishiki.entity.TweetEntity;

public class DialogSelectAction<T> {

    public static final int SHOW_TWEET   = 0;
    public static final int BIOGRAPHY    = 10;
    public static final int URL          = 20;
    public static final int MEDIA = 30;

    public static final int DELETE = 40;
    public static final int RE_TWEET = 50;
    public static final int UN_RE_TWEET = 60;
    public static final int FAVORITE = 70;
    public static final int UN_FAVORITE = 80;

    public final T targetItem;
    public final Object item;
    public final int action;

    DialogSelectAction(T targetItem, Object item, int action) {
        this.targetItem = targetItem;
        this.item = item;
        this.action = action;
    }


    public static DialogSelectAction showBiography(TweetEntity tweet) {
        return new DialogSelectAction<>(tweet, tweet.user.getId(), BIOGRAPHY);
    }

    public static DialogSelectAction showBiography(TweetEntity tweet, long userId) {
        return new DialogSelectAction<>(tweet, userId, BIOGRAPHY);
    }

    public static DialogSelectAction showTweet(TweetEntity tweet) {
        return new DialogSelectAction<>(tweet, tweet.getId(), SHOW_TWEET);
    }

    public static DialogSelectAction openURL(TweetEntity tweet, String url) {
        return new DialogSelectAction<>(tweet, url, URL);
    }

    public static DialogSelectAction mediaURL(TweetEntity tweet, String mediaUrl) {
        return new DialogSelectAction<>(tweet, mediaUrl, MEDIA);
    }

    public static DialogSelectAction delete(TweetEntity tweet) {
        return new DialogSelectAction<>(tweet, tweet.getId(), DELETE);
    }

    public static DialogSelectAction retweet(TweetEntity tweet) {
        return new DialogSelectAction<>(tweet, tweet.getId(), RE_TWEET);
    }

    public static DialogSelectAction unRetweet(TweetEntity tweet) {
        return new DialogSelectAction<>(tweet, tweet.getId(), UN_RE_TWEET);
    }

    public static DialogSelectAction favorite(TweetEntity tweet) {
        return new DialogSelectAction<>(tweet, tweet.getId(), FAVORITE);
    }

    public static DialogSelectAction unFavorite(TweetEntity tweet) {
        return new DialogSelectAction<>(tweet, tweet.getId(), UN_FAVORITE);
    }
}
