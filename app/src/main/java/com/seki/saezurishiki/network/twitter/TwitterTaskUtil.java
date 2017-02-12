package com.seki.saezurishiki.network.twitter;

import android.content.Context;
import android.support.v4.app.LoaderManager;

import com.seki.saezurishiki.network.server.TwitterServer;

import java.io.InputStream;
import java.util.List;

import twitter4j.DirectMessage;
import twitter4j.PagableResponseList;
import twitter4j.Paging;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Relationship;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.UploadedMedia;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;


/**
 * Twitter送受信メソッド提供クラス<br>
 * 受信後のtaskとpaging,idを渡してやると非同期通信する
 * @author seki
 */
public final class TwitterTaskUtil {

    private final Context mContext;
    private final LoaderManager mLoaderManager;
    private final Twitter twitter;
    private final TwitterServer repository;
    private final long loginUserId;

    public TwitterTaskUtil(Context context, LoaderManager loaderManager, TwitterAccount twitterAccount) {
        mContext = context;
        mLoaderManager = loaderManager;
        this.twitter = twitterAccount.twitter;
        this.repository = twitterAccount.getRepository();
        this.loginUserId = twitterAccount.getLoginUserId();
    }


    public void getHomeTimeLine(final Paging paging, AsyncTwitterTask.AfterTask<ResponseList<Status>> afterTask) {
        AsyncTwitterTask.AsyncTask<ResponseList<Status>> task = new AsyncTwitterTask.AsyncTask<ResponseList<Status>>() {
            @Override
            public ResponseList<Status> doInBackground() throws TwitterException {
                ResponseList<Status> result = twitter.getHomeTimeline(paging);
                repository.add(result);
                return result;
            }
        };

        new AsyncTwitterTask<>(mContext, task, afterTask, mLoaderManager).run();
    }


    public void showStatus(final long statusID, AsyncTwitterTask.AfterTask<Status> afterTask) {
        AsyncTwitterTask.AsyncTask<Status> task = new AsyncTwitterTask.AsyncTask<Status>() {
            @Override
            public Status doInBackground() throws TwitterException {
                final Status status = twitter.showStatus(statusID);
                repository.addStatus(status);
                return status;
            }
        };

        new AsyncTwitterTask<>(mContext, task, afterTask, mLoaderManager).run();
    }



    public void destroyStatus(final long statusID, AsyncTwitterTask.AfterTask<Status> afterTask) {
        AsyncTwitterTask.AsyncTask<Status> task = new AsyncTwitterTask.AsyncTask<Status>() {
            @Override
            public Status doInBackground() throws TwitterException {
                return twitter.destroyStatus(statusID);
            }
        };

        new AsyncTwitterTask<>(mContext, task, afterTask, mLoaderManager).run();
    }


    public void createReTweet(final long statusID, AsyncTwitterTask.AfterTask<Status> afterTask) {
        AsyncTwitterTask.AsyncTask<Status> task = new AsyncTwitterTask.AsyncTask<Status>() {
            @Override
            public Status doInBackground() throws TwitterException {
                final Status status = twitter.retweetStatus(statusID);
                repository.addStatus(status);
                return status;
            }
        };

        new AsyncTwitterTask<>(mContext, task, afterTask, mLoaderManager).run();
    }


    public void unFavorite(final long statusID, AsyncTwitterTask.AfterTask<Status> afterTask) {
        AsyncTwitterTask.AsyncTask<Status> task = new AsyncTwitterTask.AsyncTask<Status>() {
            @Override
            public Status doInBackground() throws TwitterException {
                final Status status = twitter.destroyFavorite(statusID);
                repository.addStatus(status);
                return status;
            }
        };

        new AsyncTwitterTask<>(mContext, task, afterTask, mLoaderManager).run();
    }


    public void createFavorite(final long statusID, AsyncTwitterTask.AfterTask<Status> afterTask) {
        AsyncTwitterTask.AsyncTask<Status> task = new AsyncTwitterTask.AsyncTask<Status>() {
            @Override
            public Status doInBackground() throws TwitterException {
                final Status status = twitter.createFavorite(statusID);
                repository.addStatus(status);
                return status;
            }
        };

        new AsyncTwitterTask<>(mContext, task, afterTask, mLoaderManager).run();
    }


    public void showUser(final long userID, AsyncTwitterTask.AfterTask<User> afterTask) {
        AsyncTwitterTask.AsyncTask<User> task = new AsyncTwitterTask.AsyncTask<User>() {
            @Override
            public User doInBackground() throws TwitterException {
                final User user = twitter.showUser(userID);
                repository.add(user);
                return user;
            }
        };

        new AsyncTwitterTask<>(mContext, task, afterTask, mLoaderManager).run();
    }

    public void showUser(final long userID, AsyncTwitterTask.AfterTask<User> afterTask, AsyncTwitterTask.OnCancelTask cancelTask) {
        AsyncTwitterTask.AsyncTask<User> task = new AsyncTwitterTask.AsyncTask<User>() {
            @Override
            public User doInBackground() throws TwitterException {
                final User user = twitter.showUser(userID);
                repository.add(user);
                return user;
            }
        };

        new AsyncTwitterTask<>(mContext, task, afterTask, cancelTask, mLoaderManager).run();
    }


    public void showRelationShip(final long targetUserID, AsyncTwitterTask.AfterTask<Relationship> afterTask) {
        AsyncTwitterTask.AsyncTask<Relationship> task = new AsyncTwitterTask.AsyncTask<Relationship>() {
            @Override
            public Relationship doInBackground() throws TwitterException {
                return twitter.showFriendship(loginUserId, targetUserID);
            }
        };

        new AsyncTwitterTask<>(mContext, task, afterTask, mLoaderManager).run();
    }



    public void createRelationShip(final long targetUserID, AsyncTwitterTask.AfterTask<User> afterTask) {
        AsyncTwitterTask.AsyncTask<User> task = new AsyncTwitterTask.AsyncTask<User>() {
            @Override
            public User doInBackground() throws TwitterException {
                return twitter.createFriendship(targetUserID);
            }
        };

        new AsyncTwitterTask<>(mContext, task, afterTask, mLoaderManager).run();
    }



    public void destroyRelationShip(final long targetUserID, AsyncTwitterTask.AfterTask<User> afterTask) {
        AsyncTwitterTask.AsyncTask<User> task = new AsyncTwitterTask.AsyncTask<User>() {
            @Override
            public User doInBackground() throws TwitterException {
                return twitter.destroyFriendship(targetUserID);
            }
        };

        new AsyncTwitterTask<>(mContext, task, afterTask, mLoaderManager).run();
    }


    public AsyncTwitterTask.AsyncTask<ResponseList<Status>> getHomeTimeLineLoader(final Paging paging) {
        return new AsyncTwitterTask.AsyncTask<ResponseList<Status>>() {
            @Override
            public ResponseList<Status> doInBackground() throws TwitterException {
                ResponseList<Status> responseList = twitter.getHomeTimeline(paging);
                repository.add(responseList);
                return responseList;
            }
        };
    }


    public AsyncTwitterTask.AsyncTask<ResponseList<Status>> getMentionTimeLineLoader(final Paging paging) {
        return new AsyncTwitterTask.AsyncTask<ResponseList<Status>>() {
            @Override
            public ResponseList<Status> doInBackground() throws TwitterException {
                final ResponseList<Status> responseList = twitter.getMentionsTimeline(paging);
                repository.add(responseList);
                return responseList;
            }
        };
    }

    public AsyncTwitterTask.AsyncTask<ResponseList<Status>> getFavoriteTimeLineLoader(final long userId, final Paging paging) {
        return new AsyncTwitterTask.AsyncTask<ResponseList<Status>>() {
            @Override
            public ResponseList<Status> doInBackground() throws TwitterException {
                final ResponseList<Status> responseList = twitter.getFavorites(userId, paging);
                repository.add(responseList);
                return responseList;
            }
        };
    }

    public void search(final Query twitterQuery, AsyncTwitterTask.AfterTask<QueryResult> afterTask) {
        AsyncTwitterTask.AsyncTask<QueryResult> task = new AsyncTwitterTask.AsyncTask<QueryResult>() {
            @Override
            public QueryResult doInBackground() throws TwitterException {
                final QueryResult result = twitter.search(twitterQuery);
                repository.add(result.getTweets());
                return result;
            }
        };

        new AsyncTwitterTask<>(mContext, task, afterTask, mLoaderManager).run();
    }


    public void post(final StatusUpdate status, final AsyncTwitterTask.AfterTask<Status> afterTask) {
        AsyncTwitterTask.AsyncTask<Status> task = new AsyncTwitterTask.AsyncTask<Status>() {
            @Override
            public Status doInBackground() throws TwitterException {
                return twitter.updateStatus(status);
            }
        };

        new AsyncTwitterTask<>(mContext, task, afterTask, mLoaderManager).run();
    }

    public void sendDirectMessage(final long userId, final String message, AsyncTwitterTask.AfterTask<DirectMessage> afterTask) {
        AsyncTwitterTask.AsyncTask<DirectMessage> task = new AsyncTwitterTask.AsyncTask<DirectMessage>() {
            @Override
            public DirectMessage doInBackground() throws TwitterException {
                return twitter.sendDirectMessage(userId, message);
            }
        };

        new AsyncTwitterTask<>(mContext, task, afterTask, mLoaderManager).run();
    }


    public void getDirectMessage(AsyncTwitterTask.AfterTask<List<DirectMessage>> afterTask, final Paging paging) {
        final AsyncTwitterTask.AsyncTask<List<DirectMessage>> task = new AsyncTwitterTask.AsyncTask<List<DirectMessage>>() {
            @Override
            public List<DirectMessage> doInBackground() throws TwitterException {
                final List<DirectMessage> messages = twitter.getDirectMessages(paging);
                repository.addDM(messages);
                return messages;
            }
        };

        new AsyncTwitterTask<>(mContext, task, afterTask, mLoaderManager).run();
    }


    public void getSentDirectMessage(AsyncTwitterTask.AfterTask<List<DirectMessage>> afterTask) {
        final AsyncTwitterTask.AsyncTask<List<DirectMessage>> task = new AsyncTwitterTask.AsyncTask<List<DirectMessage>>() {
            @Override
            public List<DirectMessage> doInBackground() throws TwitterException {
                final List<DirectMessage> messages = twitter.getSentDirectMessages(new Paging().count(100));
                repository.addSentDM(messages);
                return messages;
            }
        };

        new AsyncTwitterTask<>(mContext, task, afterTask, mLoaderManager).run();
    }


    public static void getOAuthAccessToken(final Context context, final LoaderManager loaderManager, final RequestToken token, final String verifier, AsyncTwitterTask.AfterTask<AccessToken> afterTask) {
        final AsyncTwitterTask.AsyncTask<AccessToken> TASK = new AsyncTwitterTask.AsyncTask<AccessToken>() {
            @Override
            public AccessToken doInBackground() throws TwitterException {
                return TwitterUtil.getUnauthorizedTwitter(context).getOAuthAccessToken(token, verifier);
            }
        };

        new AsyncTwitterTask<>(context, TASK, afterTask, loaderManager).run();
    }


    public void follow(final User user, AsyncTwitterTask.AfterTask<User> afterTask) {
        final AsyncTwitterTask.AsyncTask<User> task = new AsyncTwitterTask.AsyncTask<User>() {
            @Override
            public User doInBackground() throws TwitterException {
                return twitter.createFriendship(user.getId());
            }
        };

        new AsyncTwitterTask<>(mContext, task, afterTask, mLoaderManager).run();
    }


    public void block(final User user, AsyncTwitterTask.AfterTask<User> afterTask) {
        final AsyncTwitterTask.AsyncTask<User> task = new AsyncTwitterTask.AsyncTask<User>() {
            @Override
            public User doInBackground() throws TwitterException {
                return twitter.createBlock(user.getId());
            }
        };

        new AsyncTwitterTask<>(mContext, task, afterTask, mLoaderManager).run();
    }


    public void destroyBlock(final User user, AsyncTwitterTask.AfterTask<User> afterTask) {
        final AsyncTwitterTask.AsyncTask<User> task = new AsyncTwitterTask.AsyncTask<User>() {
            @Override
            public User doInBackground() throws TwitterException {
                return twitter.destroyBlock(user.getId());
            }
        };

        new AsyncTwitterTask<>(mContext, task, afterTask, mLoaderManager).run();
    }

    public void getMentionTimeLine(final Paging paging, AsyncTwitterTask.AfterTask<ResponseList<Status>> afterTask) {
        final AsyncTwitterTask.AsyncTask<ResponseList<Status>> task = new AsyncTwitterTask.AsyncTask<ResponseList<Status>>() {
            @Override
            public ResponseList<Status> doInBackground() throws TwitterException {
                ResponseList<Status> responseList = twitter.getMentionsTimeline(paging);
                repository.add(responseList);
                return responseList;
            }
        };
        new AsyncTwitterTask<>(mContext, task, afterTask, mLoaderManager).run();
    }


    public void uploadImage(final String fileName, final InputStream media, AsyncTwitterTask.AfterTask<UploadedMedia> afterTask) {
        final AsyncTwitterTask.AsyncTask<UploadedMedia> task = new AsyncTwitterTask.AsyncTask<UploadedMedia>() {
            @Override
            public UploadedMedia doInBackground() throws TwitterException {
                return twitter.uploadMedia(fileName, media);
            }
        };

        new AsyncTwitterTask<>(mContext, task, afterTask, mLoaderManager).run();
    }

    public AsyncTwitterTask.AsyncTask<PagableResponseList<User>> getFollowersListTask(final long mUserId, final long nextCursor) {
        return new AsyncTwitterTask.AsyncTask<PagableResponseList<User>>() {
            @Override
            public PagableResponseList<User> doInBackground() throws TwitterException {
                return twitter.getFollowersList(mUserId, nextCursor);
            }
        };
    }

    public AsyncTwitterTask.AsyncTask<PagableResponseList<User>> getFriendsListTask(final long mUserId, final long nextCursor) {
        return new AsyncTwitterTask.AsyncTask<PagableResponseList<User>>() {
            @Override
            public PagableResponseList<User> doInBackground() throws TwitterException {
                return twitter.getFriendsList(mUserId, nextCursor);
            }
        };
    }

    public AsyncTwitterTask.AsyncTask<ResponseList<Status>> getUserTweetListTask(final long mUserId, final Paging paging) {
        return new AsyncTwitterTask.AsyncTask<ResponseList<Status>>() {
            @Override
            public ResponseList<Status> doInBackground() throws TwitterException {
                final ResponseList<Status> responseList = twitter.getUserTimeline(mUserId, paging);
                repository.add(responseList);
                return responseList;
            }
        };

    }
}
