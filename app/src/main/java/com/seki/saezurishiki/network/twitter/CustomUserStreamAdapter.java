package com.seki.saezurishiki.network.twitter;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;

import com.seki.saezurishiki.repository.RemoteRepositoryImp;
import com.seki.saezurishiki.network.twitter.streamListener.CustomUserStreamListener;
import com.seki.saezurishiki.network.twitter.streamListener.DirectMessageUserStreamListener;
import com.seki.saezurishiki.network.twitter.streamListener.StatusUserStreamListener;
import com.seki.saezurishiki.network.twitter.streamListener.UserStreamUserListener;
import com.seki.saezurishiki.repository.TweetRepositoryKt;

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

    private static Handler mOnStatusHandler;
    private Handler mOnDeletionHandler;
    private Handler mOnFavoriteHandler;
    private Handler mOnUnFavoriteHandler;
    private Handler mOnDirectMessageHandler;
    private Handler onFollowHander;
    private Handler onUnFollowHander;

    private final List<StatusUserStreamListener> statusListeners;
    private final List<DirectMessageUserStreamListener> directMessageListeners;
    private final List<UserStreamUserListener> userStreamUserListeners;

    private final RemoteRepositoryImp repository;
    private final long loginUserId;

    @SuppressLint("HandlerLeak")
    CustomUserStreamAdapter(RemoteRepositoryImp repository) {
        this.statusListeners = new ArrayList<>();
        this.directMessageListeners = new ArrayList<>();
        this.userStreamUserListeners = new ArrayList<>();

        this.repository  = repository;
        this.loginUserId = TwitterAccount.getLoginUserId();

        mOnStatusHandler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                final Status newStatus = (Status) message.obj;
                for (StatusUserStreamListener listener : statusListeners) {
                    listener.onStatus(newStatus);
                }
            }
        };

        if ( mOnDeletionHandler == null ) {
            mOnDeletionHandler = new Handler() {
                @Override
                public void handleMessage(Message message) {
                    final StatusDeletionNotice deletionNotice = (StatusDeletionNotice)message.obj;
                    for (StatusUserStreamListener listener : statusListeners) {
                        listener.onDeletionNotice(deletionNotice);
                    }
                }
            };
        }

        mOnFavoriteHandler = new Handler() {
            @Override
            @SuppressWarnings("unchecked")
            public void handleMessage(Message message) {
                Tuple3 tuple = (Tuple3<User, User, Status>)message.obj;
                final User source = (User)tuple.e1;
                final User target = (User)tuple.e2;
                final Status favoriteStatus = (Status)tuple.e3;
                for (StatusUserStreamListener listener : statusListeners) {
                    listener.onFavorite(source, target, favoriteStatus);
                }
            }
        };


        mOnUnFavoriteHandler = new Handler() {
            @Override
            @SuppressWarnings("unchecked")
            public void handleMessage(Message message) {
                Tuple3 tuple = (Tuple3<User,User, Status>) message.obj;
                final User source = (User)tuple.e1;
                final User target = (User)tuple.e2;
                final Status unFavoriteStatus = (Status)tuple.e3;
                for (StatusUserStreamListener listener : statusListeners) {
                    listener.onUnFavorite(source, target, unFavoriteStatus);
                }
            }
        };


        mOnDirectMessageHandler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                DirectMessage directMessage = (DirectMessage)message.obj;
                for (DirectMessageUserStreamListener listener : directMessageListeners) {
                    listener.onDirectMessage(directMessage);
                }
            }
        };

        onFollowHander = new Handler() {
            @Override
            @SuppressWarnings("unchecked")
            public void handleMessage(Message message) {
                Tuple2<User, User> tuple = (Tuple2<User, User>) message.obj;
                final User source = tuple.e1;
                final User followedUser = tuple.e2;
                for (UserStreamUserListener listener : userStreamUserListeners) {
                    listener.onFollow(source, followedUser);
                }
            }
        };

        onUnFollowHander = new Handler() {
            @Override
            @SuppressWarnings("unchecked")
            public void handleMessage(Message message) {
                Tuple2<User, User> tuple = (Tuple2<User, User>) message.obj;
                final User source = tuple.e1;
                final User unfollowedUser = tuple.e2;
                for (UserStreamUserListener listener : userStreamUserListeners) {
                    listener.onRemove(source, unfollowedUser);
                }
            }
        };
    }


    @Override
    public void onStatus(Status status) {
        super.onStatus(status);
        super.onStatus(status);
        TweetRepositoryKt.INSTANCE.mappingAdd(status);
        Message message = Message.obtain();
        message.obj = status;
        mOnStatusHandler.sendMessage(message);
    }


    @Override
    public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
        super.onDeletionNotice(statusDeletionNotice);
        TweetRepositoryKt.INSTANCE.addStatusDeletionNotice(statusDeletionNotice);
        Message message = Message.obtain();
        message.obj = statusDeletionNotice;
        mOnDeletionHandler.sendMessage(message);
    }


    @Override
    public void onFavorite(User source, User target, Status favoriteStatus) {
        super.onFavorite(source, target, favoriteStatus);
        if (source.getId() != this.loginUserId) {
            TweetRepositoryKt.INSTANCE.mappingAdd(favoriteStatus);
        }
        Message message = Message.obtain();
        message.obj = new Tuple3<>(source, target, favoriteStatus);
        mOnFavoriteHandler.sendMessage(message);
    }


    @Override
    public void onUnfavorite(User source, User target, Status unFavoriteStatus) {
        super.onUnfavorite(source, target, unFavoriteStatus);
        if (source.getId() != this.loginUserId) {
            TweetRepositoryKt.INSTANCE.mappingAdd(unFavoriteStatus);
        }
        Message message = Message.obtain();
        message.obj = new Tuple3<>(source, target, unFavoriteStatus);
        mOnUnFavoriteHandler.sendMessage(message);
    }


    @Override
    public void onDirectMessage(DirectMessage directMessage) {
        super.onDirectMessage(directMessage);
        if (directMessage.getSenderId() == this.loginUserId) {
            this.repository.addSentDM(directMessage);
        } else {
            this.repository.addDM(directMessage);
        }
        Message message = Message.obtain();
        message.obj = directMessage;
        mOnDirectMessageHandler.sendMessage(message);
    }

    @Override
    public void onFollow(User source, User followedUser) {
        super.onFollow(source, followedUser);
        Message message = Message.obtain();
        message.obj = new Tuple2<>(source, followedUser);
        this.onFollowHander.sendMessage(message);
    }

    @Override
    public void onUnfollow(User source, User unfollowedUser) {
        super.onUnfollow(source, unfollowedUser);
        Message message = Message.obtain();
        message.obj = new Tuple2<>(source, unfollowedUser);
        this.onUnFollowHander.sendMessage(message);
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
        mOnDeletionHandler = null;
        mOnDirectMessageHandler = null;
        mOnFavoriteHandler = null;
        mOnStatusHandler = null;
        mOnUnFavoriteHandler = null;
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


    private class Tuple3<E1, E2, E3> {
        private final E1 e1;
        private final E2 e2;
        private final E3 e3;

        Tuple3(E1 e1, E2 e2, E3 e3) {
            this.e1 = e1;
            this.e2 = e2;
            this.e3 = e3;
        }
    }


    private class Tuple2<E1, E2> {
        private final E1 e1;
        private final E2 e2;

        Tuple2(E1 e1, E2 e2) {
            this.e1 = e1;
            this.e2 = e2;
        }
    }




}
