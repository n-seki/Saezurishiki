package com.seki.saezurishiki.network.twitter;

import com.seki.saezurishiki.network.twitter.streamListener.CustomUserStreamListener;
import com.seki.saezurishiki.network.twitter.streamListener.DirectMessageUserStreamListener;
import com.seki.saezurishiki.network.twitter.streamListener.StatusUserStreamListener;
import com.seki.saezurishiki.network.twitter.streamListener.UserStreamUserListener;
import com.seki.saezurishiki.repository.RemoteRepositoryImp;
import com.seki.saezurishiki.repository.TweetRepository;

import java.util.ArrayList;
import java.util.List;

import twitter4j.DirectMessage;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.User;
import twitter4j.UserStreamAdapter;

/**
 * カスタマイズUserStreamAdapter<br>
 * このAdapterのインスタンスだけがTwitterStreamのListenerとして登録される
 * @author seki
 */
class CustomUserStreamAdapter extends UserStreamAdapter {

    private final List<StatusUserStreamListener> statusListeners;
    private final List<DirectMessageUserStreamListener> directMessageListeners;
    private final List<UserStreamUserListener> userStreamUserListeners;

    private final RemoteRepositoryImp repository;
    private final long loginUserId;

    CustomUserStreamAdapter(RemoteRepositoryImp repository) {
        this.statusListeners = new ArrayList<>();
        this.directMessageListeners = new ArrayList<>();
        this.userStreamUserListeners = new ArrayList<>();

        this.repository  = repository;
        this.loginUserId = TwitterAccount.getLoginUserId();
    }

    @Override
    public void onStatus(Status status) {
        super.onStatus(status);
        TweetRepository.INSTANCE.mappingAdd(status);
        for (StatusUserStreamListener listener : statusListeners) {
            listener.onStatus(status);
        }
    }

    @Override
    public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
        super.onDeletionNotice(statusDeletionNotice);
        TweetRepository.INSTANCE.addStatusDeletionNotice(statusDeletionNotice);
        for (StatusUserStreamListener listener : statusListeners) {
            listener.onDeletionNotice(statusDeletionNotice);
        }
    }

    @Override
    public void onFavorite(User source, User target, Status favoriteStatus) {
        super.onFavorite(source, target, favoriteStatus);
        if (source.getId() != this.loginUserId) {
            TweetRepository.INSTANCE.mappingAdd(favoriteStatus);
        }
        for (StatusUserStreamListener listener : statusListeners) {
            listener.onFavorite(source, target, favoriteStatus);
        }
    }

    @Override
    public void onUnfavorite(User source, User target, Status unFavoriteStatus) {
        super.onUnfavorite(source, target, unFavoriteStatus);
        if (source.getId() != this.loginUserId) {
            TweetRepository.INSTANCE.mappingAdd(unFavoriteStatus);
        }
        for (StatusUserStreamListener listener : statusListeners) {
            listener.onUnFavorite(source, target, unFavoriteStatus);
        }
    }

    @Override
    public void onDirectMessage(DirectMessage directMessage) {
        //nop
    }

    @Override
    public void onFollow(User source, User followedUser) {
        super.onFollow(source, followedUser);
        for (UserStreamUserListener listener : userStreamUserListeners) {
            listener.onFollow(source, followedUser);
        }
    }

    @Override
    public void onUnfollow(User source, User unFollowedUser) {
        super.onUnfollow(source, unFollowedUser);
        for (UserStreamUserListener listener : userStreamUserListeners) {
            listener.onFollow(source, unFollowedUser);
        }
    }

    void addListener(CustomUserStreamListener listener) {
        this.statusListeners.add(listener);
        this.directMessageListeners.add(listener);
        this.userStreamUserListeners.add(listener);
    }

    void addListener(StatusUserStreamListener listener) {
        this.statusListeners.add(listener);
    }

    void addListener(DirectMessageUserStreamListener listener) {
        this.directMessageListeners.add(listener);
    }

    void addListener(UserStreamUserListener listener) {
        this.userStreamUserListeners.add(listener);
    }

    void clearListener() {
    }

    void removeListener(StatusUserStreamListener listener) {
        this.statusListeners.remove(listener);
    }

    void removeListener(DirectMessageUserStreamListener listener) {
        this.directMessageListeners.remove(listener);
    }

    void removeListener(UserStreamUserListener listener) {
        this.userStreamUserListeners.remove(listener);
    }

    void removeListener(CustomUserStreamListener listener) {
        this.statusListeners.remove(listener);
        this.directMessageListeners.remove(listener);
        this.userStreamUserListeners.remove(listener);
    }
}
