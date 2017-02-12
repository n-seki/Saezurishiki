package com.seki.saezurishiki.network.twitter;

import twitter4j.TwitterException;

/**
 * AsyncTwitterTaskの結果格納<br>
 * AsyncTwitterTaskの結果は全てこのクラスでラップされる
 * @author seki
 */
public class TwitterTaskResult<T> {

    private T mResult;
    private TwitterException mException;

    public T getResult() {
        return mResult;
    }

    public void setResult(T result) {
        mResult = result;
    }

    public TwitterException getException() {
        return mException;
    }

    public void setException(TwitterException exception) {
        mException = exception;
    }

    public boolean isException() {
        return (mException != null);
    }

}
