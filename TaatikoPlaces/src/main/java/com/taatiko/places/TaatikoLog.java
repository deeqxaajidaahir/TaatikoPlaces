package com.taatiko.places;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class TaatikoLog {
//    private static final boolean isDebug = BuildConfig.DEBUG;
    private static final boolean isDebug = false;
    public static final void Log(String tag, String message) {
        if (isDebug) {
            android.util.Log.i(tag, message + "");
        }
    }

    public static final void handleException(String tag, Exception e) {
        if (isDebug) {
            if (e != null) {
                android.util.Log.d(tag, e.toString());
            }
        }
    }
    public static final void handleThrowable(String tag, Throwable t) {
        if (isDebug) {
            if (t != null) {
                android.util.Log.d(tag, t + "");
            }
        }
    }

    public static boolean isInternetConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService
                (Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();
        return isConnected;
    }

}
