package com.fcb.fogcomputingbox;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by wangtao on 2017/4/12.
 */

public class ProgressNetworkDialog extends Dialog {

    public ProgressNetworkDialog(Context context) {
        super(context, R.style.dialog_all_style_white);

        Activity a = (Activity) context;
        if (a == null || a.isFinishing()) {
            return;
        }

        Activity activity = (Activity) context;
        WindowManager manager = activity.getWindowManager();
        Point point = new Point();
        manager.getDefaultDisplay().getSize(point);
        Window dialogWindow = getWindow();
        dialogWindow.setGravity(Gravity.CENTER | Gravity.CENTER);
        show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.view_dialog_progress_network);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }


    @Override
    public void dismiss() {
        super.dismiss();
//        iv.clearAnimation();

    }
}
