package com.dming.testopengl;

import android.util.Log;



/**
 * 简单日志类，方便
 * Created by DMing on 2017/9/19.
 */

public class DLog {

    private static String TAG = "DMUI";

    public static void d(String msg){
        if (BuildConfig.DEBUG) Log.d(TAG,msg);
    }

    public static void i(String msg){
        if (BuildConfig.DEBUG) Log.i(TAG,msg);
    }

    public static void e(String msg){
        if (BuildConfig.DEBUG) Log.e(TAG,msg);
    }

    @SuppressWarnings("unused")
    public static void d(String tagNull, String msg) {
        d(msg);
    }

    @SuppressWarnings("unused")
    public static void i(String tagNull, String msg) {
        i(msg);
    }

    @SuppressWarnings("unused")
    public static void e(String tagNull, String msg) {
        e(msg);
    }
}