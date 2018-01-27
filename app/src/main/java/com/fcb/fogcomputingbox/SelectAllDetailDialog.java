package com.fcb.fogcomputingbox;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.blankj.utilcode.util.BarUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * author: denghx
 * date  : 2018/1/15
 * desc  :
 */

public class SelectAllDetailDialog extends Dialog {
    Context mContext;
    @BindView(R.id.tv_7day)
    TextView mTv7day;
    @BindView(R.id.tv_all)
    TextView mTvAll;
    private BaseQuickAdapter<String, BaseViewHolder> mAdapter;

    public SelectAllDetailDialog(Context context) {
        super(context, R.style.top_dialog);
        mContext = context;
        Activity activity = (Activity) context;
        WindowManager manager = activity.getWindowManager();
        Window dialogWindow = getWindow();
        dialogWindow.setGravity(Gravity.CENTER | Gravity.TOP);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_select_all_detail);
        ButterKnife.bind(this);
        init();


    }

    public interface onTvClickListener {
        void onTvClick(int num);

    }

    onTvClickListener listener;

    public void setOnTvClickListener(onTvClickListener listener) {
        this.listener = listener;
    }

    public void init() {

        mTv7day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener!=null){
                    listener.onTvClick(0);
                }
                dismiss();
            }
        });

        mTvAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener!=null){
                    listener.onTvClick(1);
                }
                dismiss();
            }
        });

        setWidows();
    }


    @SuppressWarnings("deprecation")
    private void setWidows() {
        Display display = getWindow().getWindowManager().getDefaultDisplay();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = (int) (display.getWidth() * 5 / 5); //
        lp.y = -BarUtils.getStatusBarHeight() - UIUtils.dp2Px(R.dimen.title_height);
        getWindow().setAttributes(lp);
        setCanceledOnTouchOutside(true);
        setCancelable(true);
    }

}
