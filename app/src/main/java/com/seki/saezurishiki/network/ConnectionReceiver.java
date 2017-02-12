package com.seki.saezurishiki.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * ネットワーク接続監視クラス<br>
 * Observerを実装することでネットワークの接続時、切断時に通知を受け取れます
 * @author seki
 */
public class ConnectionReceiver extends BroadcastReceiver {

    private Observer mObserver;

    public ConnectionReceiver(Observer observer) {
        mObserver = observer;
    }

    public interface Observer {
        void onConnect();
        void onDisconnect();
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();

        if (info == null) {
            mObserver.onDisconnect();
            for (Observer observer : mObserverList) {
                observer.onDisconnect();
            }
        } else {
            mObserver.onConnect();
            for (Observer observer : mObserverList) {
                observer.onConnect();
            }
        }
    }



    //TODO staticで行ってよいか検討する
    private static List<Observer> mObserverList = new ArrayList<>();
    public static void addObserver(Observer observer) {
        mObserverList.add(observer);
    }
}
