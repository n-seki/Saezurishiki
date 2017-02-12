package com.seki.saezurishiki.network.twitter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;

import twitter4j.TwitterException;

/**
 * AsyncTaskLoaderとLoaderCallbackのwrapper的クラス<br>
 * @author seki
 */
public class AsyncTwitterTask<T> extends AsyncTaskLoader<TwitterTaskResult<T>>
                              implements LoaderManager.LoaderCallbacks<TwitterTaskResult<T>> {

    private final LoaderManager mLoaderManager;
    private final AsyncTask<T> mTask;
    private final AfterTask<T> mAfterTask;
    private final OnCancelTask mCancelTask;


    public interface AsyncTask<R> {
        R doInBackground() throws TwitterException;
    }

    public interface AfterTask<R> {
        void onLoadFinish(TwitterTaskResult<R> result);
    }

    public interface OnCancelTask {
        void onLoadCancel();
    }


    public AsyncTwitterTask(Context context, AsyncTask<T> task, AfterTask<T> afterTask, LoaderManager loaderManager) {
        this(context, task, afterTask, null, loaderManager);

    }

    public AsyncTwitterTask(Context context, AsyncTask<T> task, AfterTask<T> afterTask, OnCancelTask cancelTask, LoaderManager loaderManager) {
        super(context);
        mTask = task;
        mAfterTask = afterTask;
        mCancelTask = cancelTask;
        mLoaderManager = loaderManager;
    }

    public void run() {
        mLoaderManager.restartLoader(hashCode() + (int)(Math.random()*100), null, this);
    }


    @Override
    public Loader<TwitterTaskResult<T>> onCreateLoader(int id, Bundle args) {
        super.forceLoad(); //別にforceLoadでなくても良い気がする
        return this;
    }

    @Override
    public void onLoadFinished(Loader<TwitterTaskResult<T>> loader, TwitterTaskResult<T> result) {
        mAfterTask.onLoadFinish(result);
    }

    @Override
    public void onLoaderReset(Loader<TwitterTaskResult<T>> loader) {
        //do nothing
    }

    @Override
    public void onStopLoading() {
        cancelLoad();
    }

//    //task実行中にcancelされたときに呼ばれる
//    //データの破棄をしたいような気がする
//    @Override
//    public boolean onCancelLoad() {
//        final boolean isCancel = this.cancelLoad();
//        if (isCancel) {
//            if (this.mCancelTask != null) {
//                mCancelTask.onLoadCancel();
//            }
//        }
//
//        return isCancel;
//    }

//    @Override
//    public void onForceLoad() {
//        super.onForceLoad(); //ここからforceLoadが呼び出される
//        //実装的には現在のtaskをcancelして新しいtaskを生成して実行、という感じか
//    }


    @Override
    public TwitterTaskResult<T> loadInBackground() {
        TwitterTaskResult<T> result = new TwitterTaskResult<>();

        try {
            T t = mTask.doInBackground();
            result.setResult(t);
        } catch( TwitterException e) {
            e.printStackTrace();
            result.setException(e);
        }

        return result;
    }

}
