package com.fcb.fogcomputingbox;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.blankj.utilcode.util.Utils;


/**
 * Created by Administrator on 2017/8/21.
 */

public class AppClass extends Application {

    public static Context mContext;

    public static boolean mHasShowUpdateDialog = false;

    public static AppClass instance;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();

        instance = this;
        Utils.init(this);

//        if (Constants.DEBUG_TOGGLE) {
//        if (true) {
//        LogCrashHandler.getInstance().init(this);
//        }

        mHandler = new Handler();

    }






    /**
     * 得到上下文对象
     *
     * @return
     */
    public static Context getContext() {
        return mContext;
    }

    public static Handler mHandler;

    public static Handler getHandler() {
        return mHandler;
    }

}
