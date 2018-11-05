package me.apon.lemon.core;

import android.util.Log;

/**
 * Created by yaopeng(aponone@gmail.com) on 2018/11/3.
 */
public class LLog {
    private static boolean debug = false;
    private static final String TAG = "Lemon";

    public static void debug(boolean debug) {
        LLog.debug = debug;
    }

    public static void d(String tag, Object o) {
        if (debug) {
            Log.d(TAG+"-"+tag,  o.toString());
        }
    }

    public static void e(String tag, Object o) {
        if (debug) {
            Log.e(TAG+"-"+tag, o.toString());
        }
    }
}
