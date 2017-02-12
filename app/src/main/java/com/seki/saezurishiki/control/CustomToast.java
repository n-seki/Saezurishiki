package com.seki.saezurishiki.control;

import android.content.Context;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CustomToast {

    private static List<Toast> allToast = new ArrayList<>();
    private static final Object LOCK = new Object();

    public static void show(Context context, int messageId, int duration) {
        synchronized (LOCK) {
            cancelToast();
            Toast toast = Toast.makeText(context, messageId, duration);
            toast.show();
            allToast.add(toast);
        }
    }


    public static void show(Context context, String message, int duration) {
        synchronized (LOCK) {
            cancelToast();
            Toast toast = Toast.makeText(context, message, duration);
            toast.show();
            allToast.add(toast);
        }
    }

    public static void cancelToast() {
        Iterator<Toast> itr = allToast.iterator();
        while(itr.hasNext()) {
            itr.next().cancel();
            itr.remove();
        }
    }



}
