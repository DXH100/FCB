package com.fcb.fogcomputingbox;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class ToastUtil {


    private static String oldMsg;
    protected static Toast toast = null;
    private static long oneTime = 0;
    private static long twoTime = 0;
    private static Toast toast1;
    private static long start;
    private static long stop;
    private static Toast toast2;
    private static boolean mIsShowing;
    private static long mTime;

    public static void showToast(Context context, String s) {
        if (TextUtils.isEmpty(s)) {
            return;
        }
        if (toast == null) {
            toast = Toast.makeText(context, s, Toast.LENGTH_SHORT);
            toast.show();
            oneTime = System.currentTimeMillis();
        } else {
            twoTime = System.currentTimeMillis();
            if (s.equals(oldMsg)) {
                if (twoTime - oneTime > Toast.LENGTH_SHORT) {
                    toast.show();
                }
            } else {
                oldMsg = s;
                toast.setText(s);
                toast.show();

            }
        }
        oneTime = twoTime;
        mIsShowing = true;
        mTime = System.currentTimeMillis();
    }
    public static  boolean isShowing(){

        return mIsShowing;
    }

    public static void showToast(Context context, @NonNull int resId) {
        showToast(context, context.getString(resId));

    }


    public static void showErrToast(final String msgRes) {

        mTime = System.currentTimeMillis();
        if(TextUtils.isEmpty(msgRes))return;
        AppClass.getHandler().post(new Runnable() {
            @Override
            public void run() {
                if (toast1 == null) {
                    toast1 = Toast.makeText(UIUtils.getContext(), msgRes, Toast.LENGTH_SHORT);
                    start = System.currentTimeMillis();
                    setUI(toast1, msgRes, false);
                    toast1.show();
                } else {
                    stop = System.currentTimeMillis();
                    if (msgRes.equals(oldMsg)) {
                        if (stop - start > Toast.LENGTH_SHORT) {
                            toast1.show();
                        }
                    } else {
                        oldMsg = msgRes;
                        setUI(toast1, msgRes, false);
                        toast1.show();
                    }
                }
                start = stop;
                mIsShowing = true;
            }
        });


    }

    private static void setUI(Toast toast, String msgRes, boolean isSuccess) {
        View view = View.inflate(UIUtils.getContext(), R.layout.toast_layout, null);
        ImageView ivMessage = (ImageView) view.findViewById(R.id.iv_message);
        TextView message = (TextView) view.findViewById(R.id.message);
        if (isSuccess) {

            ivMessage.setImageResource(R.drawable.ok);
        } else {
            ivMessage.setImageResource(R.drawable.no);

        }
        message.setText(msgRes == null ? "" : msgRes);
        toast.setView(view);
        toast.setGravity(Gravity.CENTER, 0, UIUtils.px2Dp(25));
    }

    public static void showSuccessToast(final String msgRes) {

        mTime = System.currentTimeMillis();
        if(TextUtils.isEmpty(msgRes))return;
        new Handler().post(new Runnable() {
            @Override
            public void run() {

                if (toast2 == null) {
                    toast2 = Toast.makeText(UIUtils.getContext(), msgRes, Toast.LENGTH_SHORT);
                    start = System.currentTimeMillis();
                    setUI(toast2, msgRes, true);
                    toast2.show();
                } else {
                    stop = System.currentTimeMillis();
                    if (msgRes.equals(oldMsg)) {
                        if (stop - start > Toast.LENGTH_SHORT) {
                            toast2.show();
                        }
                    } else {
                        oldMsg = msgRes;
                        setUI(toast2, msgRes, true);
                        toast2.show();
                    }
                }
                start = stop;
                mIsShowing = true;
            }
        });


    }

    public static void showSuccessToast(@StringRes final int msgRes) {

        mTime = System.currentTimeMillis();
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                String string = UIUtils.getString(msgRes);
                if (toast2 == null) {
                    toast2 = Toast.makeText(UIUtils.getContext(), msgRes, Toast.LENGTH_SHORT);
                    start = System.currentTimeMillis();
                    setUI(toast2, string, true);
                    toast2.show();
                } else {
                    stop = System.currentTimeMillis();
                    if (string.equals(oldMsg)) {
                        if (stop - start > Toast.LENGTH_SHORT) {
                            toast2.show();
                        }
                    } else {
                        oldMsg = string;
                        setUI(toast2, string, true);
                        toast2.show();
                    }
                }
                start = stop;
                mIsShowing = true;
            }
        });

    }

    public static void showErrToast(final int msg) {

        mTime = System.currentTimeMillis();
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                String string = UIUtils.getString(msg);
                if (toast1 == null) {
                    toast1 = Toast.makeText(UIUtils.getContext(), string, Toast.LENGTH_SHORT);
                    start = System.currentTimeMillis();
                    setUI(toast1, string, false);
                    toast1.show();
                } else {
                    stop = System.currentTimeMillis();
                    if (string.equals(oldMsg)) {
                        if (stop - start > Toast.LENGTH_SHORT) {
                            toast1.show();
                        }
                    } else {
                        oldMsg = string;
                        setUI(toast1, string, false);
                        toast1.show();
                    }
                }
                start = stop;
                mIsShowing = true;
            }
        });

    }

    public static void resetIsShowing() {
        mIsShowing = false;
    }

    public static long getTime() {
        return mTime;
    }
}
 