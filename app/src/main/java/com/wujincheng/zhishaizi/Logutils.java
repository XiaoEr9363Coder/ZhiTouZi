package com.wujincheng.zhishaizi;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

public class Logutils {


    private static void sprint(String TAG, String msg) {
        showLog(msg);
    }
    public static void showLog(String log){
        if(mLogListener != null){
            mLogListener.onLog(log);
        }
    }
    public static void d(String tag, String msg) {
        sprint(tag, msg);
    }

    public static void e(String tag, String msg) {
        sprint(tag, msg);
    }

    interface LogListener{
        void onLog(String log);
    }

    private static LogListener mLogListener;


    public static void setLogListener(LogListener logListener){
        mLogListener = logListener;
    }
}
