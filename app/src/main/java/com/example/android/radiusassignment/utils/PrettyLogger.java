package com.example.android.radiusassignment.utils;

import com.example.android.radiusassignment.BuildConfig;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;


public class PrettyLogger {

    private static final boolean DEBUG = BuildConfig.DEBUG;

    public PrettyLogger() {
    }

    static {
        Logger.addLogAdapter(new AndroidLogAdapter());
    }

    public static void e(String msg) {
        if (DEBUG) Logger.e(msg);
    }

    public static void d(String msg) {
        if (DEBUG) Logger.d(msg);
    }

    public static void i(String msg) {
        if (DEBUG) Logger.i(msg);
    }

    public static void w(String msg) {
        if (DEBUG) Logger.w(msg);
    }

    public static void v(String msg) {
        if (DEBUG) Logger.v(msg);
    }

    public static void wtf(String msg) {
        if (DEBUG) Logger.wtf(msg);
    }

    public static void json(String msg) {
        if (DEBUG) Logger.json(msg);
    }

    public static void xml(String msg) {
        if (DEBUG) Logger.xml(msg);
    }
}