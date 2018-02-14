package com.fcb.fogcomputingbox;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import butterknife.ButterKnife;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by wangtao on 2017/8/16.
 */

public abstract class BaseActivity extends AppCompatActivity implements Constants, INetWorkResult {

    protected String mClassName = "";

    public TextView mTvTitleRight;
    private TextView mTvTitle;
    private LogInterceptor mLogInterceptor;

    @Override
    public void finish() {
        if (ToastUtil.isShowing()) {
            long time = ToastUtil.getTime();
            if (System.currentTimeMillis() - time < 2000) {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        BaseActivity.super.finish();
                        ToastUtil.resetIsShowing();
                    }
                }, 2000 - (System.currentTimeMillis() - time));
            } else {
                super.finish();
            }
        } else {
            super.finish();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        }

        if (isFullScreen()) {
            ScreenUtils.setFullScreen(this);
        }
        setContentView(doSetContentView());

        ButterKnife.bind(this);

//        initCache();

        initData();

        ScreenUtils.setPortrait(this);

        String className = this.getClass().getName();

        String realClassName = className.substring(className
                .lastIndexOf(".") + 1);

        mClassName = realClassName;
        LogUtils.e(mClassName);
    }

    public abstract int doSetContentView();

    public abstract void initData();


    public boolean isFullScreen() {
        return false;
    }

    public void setPaddingTop() {

        View viewRoot = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);

        StatusBarUtil.setPaddingSmart(this, viewRoot);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doDismissNetProgress();
        NetworkCore.getInstance().cancel(mClassName);
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }

    }

    protected void netError(int msgId, BaseServerObj serverObj) {
        showErrToast(serverObj.Message);
    }

    protected void netSuccess(int msgId, BaseServerObj serverObj) {
    }


    @Override
    public void error(final int msgId, final BaseServerObj serverObj) {

        netError(msgId, serverObj);

    }

    @Override
    public void success(final int msgId, final BaseServerObj serverObj) {

        netSuccess(msgId, serverObj);

    }

    protected void setThemeColor(int colorPrimaryDark) {

        int color = Color.parseColor("#ffffffff");

        int colors = ContextCompat.getColor(this, colorPrimaryDark);

        if (color == colors) {
            setThemeColorWhite();
            return;
        }
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, colorPrimaryDark));
        }
        if (Build.VERSION.SDK_INT >= 23) {
            Window window = getWindow();
            int systemUiVisibility = window.getDecorView().getSystemUiVisibility();
            systemUiVisibility &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            window.getDecorView().setSystemUiVisibility(systemUiVisibility);
        }
    }

    /**
     * 设置状态栏白色，黑字体
     */
    protected void setThemeColorWhite() {
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(0xffffffff);
        }
        if (Build.VERSION.SDK_INT >= 23) {
            Window window = getWindow();
            int systemUiVisibility = window.getDecorView().getSystemUiVisibility();
            systemUiVisibility |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            window.getDecorView().setSystemUiVisibility(systemUiVisibility);
        }
    }


    public void dosetTitle(@StringRes int titleRes) {
        dosetTitle(getString(titleRes));
    }

    /**
     * 只能在设置title之后调用
     *
     * @param titleText
     */
    public void setTitleText(@StringRes int titleText) {
        mTvTitle.setText(titleText);
    }

    public void setTitleText(String titleText) {
        mTvTitle.setText(titleText);

    }

    public void dosetTitle(String titleRes) {
        dosetTitle(titleRes, false);
    }

    public void dosetTitle(@StringRes int titleRes, boolean isClose) {
        dosetTitle(getString(titleRes), isClose);
    }

    public void dosetTitle(String titleRes, boolean isClose) {
        int titleHeight = (int) getResources().getDimension(R.dimen.title_height);

        View viewRoot = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        if (viewRoot == null) {
            LogUtils.i("设置title失败!");
            return;
        }

        View viewTitle = LayoutInflater.from(this).inflate(R.layout.layout_include_title, null);
        mTvTitle = viewTitle.findViewById(R.id.tv_title);
        ImageView ivBack = viewTitle.findViewById(R.id.iv_back);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                KeyboardUtils.hideSoftInput(BaseActivity.this);
                finish();
            }
        });

//        mTvTitle.setText(StaticMethod.toUpperCaseFirstOne(titleRes));

//        viewRoot.setBackgroundColor(getResources().getColor(R.color.color_page_bg));

        if (viewRoot instanceof LinearLayout) {
            LinearLayout linearLayout = (LinearLayout) viewRoot;

            viewTitle.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, titleHeight));
//            viewTitle.setPadding(0, BarUtils.getStatusBarHeight(), 0, 0);
            linearLayout.addView(viewTitle, 0);
        }
        if (viewRoot instanceof RelativeLayout) {
            RelativeLayout relativeLayout = (RelativeLayout) viewRoot;

            viewTitle.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, titleHeight));
//            viewTitle.setPadding(0, BarUtils.getStatusBarHeight(), 0, 0);
            viewTitle.setId(R.id.layout_title_id);
            relativeLayout.addView(viewTitle, 0);
            if (relativeLayout.getChildCount() > 1) {
                View child1 = relativeLayout.getChildAt(1);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) child1.getLayoutParams();
                params.addRule(RelativeLayout.BELOW, R.id.layout_title_id);
                child1.setLayoutParams(params);
            }


        }
    }

    public void loadDataPost(String tag, INetWorkResult mINetWorkResult, int msgId, String urlName, HashMap<String, Object> params, Class t) {
        NetworkCore.getInstance().doPost(tag, mINetWorkResult, msgId, urlName, params, t);
//        doShowNetProgress();
    }

    public void loadDataAuthPost(String tag, INetWorkResult mINetWorkResult, int msgId, String urlName, HashMap<String, Object> params, Class t) {
        NetworkCore.getInstance().doAuthorizationPost(tag, mINetWorkResult, msgId, urlName, params, t);
//        doShowNetProgress();
    }

    public void dosetTitleRightTxt(@StringRes int titleRes, @StringRes int rightTxt, final View.OnClickListener listener) {
        dosetTitleRightTxt(titleRes, getString(rightTxt), listener);
    }

    public void dosetTitleRightTxt(@StringRes int titleRes, String rightTxt, final View.OnClickListener listener) {
        dosetTitleRightTxt(getString(titleRes), rightTxt, listener);
    }

    public void dosetTitleRightTxt(String titleRes, String rightTxt, final View.OnClickListener listener) {
        int titleHeight = (int) getResources().getDimension(R.dimen.title_height);
        View viewRoot = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        if (viewRoot == null) {
            LogUtils.i("设置title失败!");
            return;
        }
//        viewRoot.setBackgroundColor(getResources().getColor(R.color.color_page_bg));
        View viewTitle = LayoutInflater.from(this).inflate(R.layout.layout_include_title_txt, null);
        mTvTitle = viewTitle.findViewById(R.id.tv_title);
        ImageView ivBack = viewTitle.findViewById(R.id.iv_back);
        mTvTitleRight = viewTitle.findViewById(R.id.title_right_tv);
        mTvTitleRight.setText(rightTxt);
        if (!TextUtils.isEmpty(rightTxt)) {
            mTvTitleRight.setVisibility(View.VISIBLE);
        }

        mTvTitleRight.setOnClickListener(listener);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                KeyboardUtils.hideSoftInput(BaseActivity.this);
                finish();
            }
        });

//        mTvTitle.setText(StaticMethod.toUpperCaseFirstOne(titleRes));

//        viewRoot.setBackgroundColor(getResources().getColor(R.color.color_page_bg));

        if (viewRoot instanceof LinearLayout) {
            LinearLayout linearLayout = (LinearLayout) viewRoot;
            viewTitle.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, titleHeight));
//            viewTitle.setPadding(0, BarUtils.getStatusBarHeight(), 0, 0);
            linearLayout.addView(viewTitle, 0);
        }
        if (viewRoot instanceof RelativeLayout) {
            RelativeLayout relativeLayout = (RelativeLayout) viewRoot;

            viewTitle.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, titleHeight));
//            viewTitle.setPadding(0, BarUtils.getStatusBarHeight(), 0, 0);
            viewTitle.setId(R.id.layout_title_id);
            relativeLayout.addView(viewTitle, 0);
            if (relativeLayout.getChildCount() > 1) {
                View child1 = relativeLayout.getChildAt(1);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) child1.getLayoutParams();
                params.addRule(RelativeLayout.BELOW, R.id.layout_title_id);
                child1.setLayoutParams(params);
            }


        }
    }


    /**
     * 更新title内容
     *
     * @param titleTxt
     * @param rightTxt
     */
    public void doUpdataTitleContent(@StringRes int titleTxt, String rightTxt) {
        View viewRoot = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        if (viewRoot == null) {
            return;
        }
        if (viewRoot instanceof ViewGroup) {
            ViewGroup linearLayout = (ViewGroup) ((ViewGroup) viewRoot).getChildAt(0);

            for (int index = 0; index < linearLayout.getChildCount(); index++) {
                View item = linearLayout.getChildAt(index);

                if (item.getId() == R.id.tv_title) {
                    TextView title = (TextView) item;
//                    title.setText(StaticMethod.toUpperCaseFirstOne(getString(titleTxt)));
                }
                if (item.getId() == R.id.title_right_tv) {
                    TextView title = (TextView) item;
                    title.setText(rightTxt);
                }
            }
        }

        viewRoot.invalidate();
    }


    public void showSuccessToast(@StringRes final int msgRes) {
        ToastUtil.showSuccessToast(msgRes);
    }

    public void showSuccessToast(final String msgRes) {
        ToastUtil.showSuccessToast(msgRes);

    }

    public void showErrToast(String msg) {
        ToastUtil.showErrToast(msg);
    }

    public void showErrToast(@StringRes int msg) {
        ToastUtil.showErrToast(msg);
    }


    private ProgressNetworkDialog progressNetwork = null;

    public void doShowNetProgress() {
        AppClass.getHandler().post(new Runnable() {
            @Override
            public void run() {
                if (progressNetwork != null) {
                    if (progressNetwork.isShowing()) {
                        return;
                    }
                }
                progressNetwork = new ProgressNetworkDialog(BaseActivity.this);
            }
        });

    }

    public void doDismissNetProgress() {
        AppClass.getHandler().post(new Runnable() {
            @Override
            public void run() {
                if (progressNetwork != null) {
                    if (progressNetwork.isShowing()) {
                        progressNetwork.dismiss();
                    }
                    progressNetwork = null;
                }
            }
        });

    }

    public void startActivity(Class activityClass) {
        startActivity(null, activityClass);
    }



    public void startActivity(HashMap<String, String> data, Class activityClass) {
        Intent intent = new Intent(this, activityClass);
        if (data != null && data.size() != 0) {
            Iterator<String> iterator = data.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                intent.putExtra(key, data.get(key));
            }
        }
        super.startActivity(intent);
    }


    public void startActivity(String key, int value, Class activityClass) {
        Intent intent = new Intent(this, activityClass);
        intent.putExtra(key, value);
        super.startActivity(intent);
    }


//    public void doshowImg(ImageView view, String iconId) {
//        //占位图和错误图
//        PicassoUtils.loadAuthImg(IMAGE_URL + iconId, this, view, R.mipmap.ic_launcher);
//    }

//    public void doshowHead(CircleImageView circleIv, String iconId) {
////        占位图和错误图
//        PicassoUtils.loadAuthImg(IMAGE_URL + iconId, this, circleIv, R.drawable.img_head, R.drawable.img_head);
//    }

//
//    public boolean isShowUpdateDialog;
//    DownloadReceiver mReceiver;
//    UpdateDialog mDialog;
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onGetVersion(final VersionBean bean) {
//        LogUtils.e(bean.getUrl());
//        if (!TextUtils.equals(BuildConfig.VERSION_NAME, bean.getVersion())) {
//
//            IntentFilter filter = new1 IntentFilter();
//            filter.addAction("android.intent.action.DOWNLOAD_COMPLETE");
//            mReceiver = new1 DownloadReceiver();
//            registerReceiver(mReceiver, filter);
//
//
//            mDialog = new1 UpdateDialog(this, bean.getDescription());
//            mDialog.setOnBtnDownloadClick(new1 UpdateDialog.BtnDownloadListener() {
//                @Override
//                public void onDownload() {
//                    if (TextUtils.isEmpty(bean.getUrl())) {
//                        showErrToast(R.string.empty_apkurl);
//                    } else {
//                        downloadApk(bean.getUrl());
//                    }
//
//                }
//            });
//            mDialog.setOnTvClick(new1 UpdateDialog.TvListener() {
//                @Override
//                public void onTvClick() {
//                    try {
//                        Intent i = new1 Intent(Intent.ACTION_VIEW);
//                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        i.setData(Uri.parse("market://details?id=" + BuildConfig.APPLICATION_ID));
//                        startActivity(i);
//                    } catch (Exception e) {
//                        LogUtils.e(e);
//                        showErrToast(R.string.no_appstore);
//                        e.printStackTrace();
//                    }
//                }
//            });
//            if (!isFinishing()) {
//                mDialog.show();
//            }
//            mDialog.isShowIvClose(!bean.isForceToUpdate());
//            mDialog.setOnKeyListener(new1 DialogInterface.OnKeyListener() {
//                @Override
//                public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
//                    return true;
//                }
//            });
//            mDialog.setCancelable(false);
//            mDialog.setCanceledOnTouchOutside(false);
//
//            AppClass.mHasShowUpdateDialog = true;
//
//            SPUtils.getInstance().put("isShowUpdateDialog", true);
//        }
//    }
//
//    DownloadManager mManager;
//    private long mDownloadID;
//
//    private void downloadApk(String url) {
//        File file = new1 File(Constants.DOWNLOAD_PATH);
//        LogUtils.e(file.exists() + "");
//        if (!file.exists()) {
//            file.mkdir();
//        } else {
//            FileUtil.deleteFile(file, "giiiwallet_merchant.apk");
//        }
//        mManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
//        DownloadManager.Request request = null;
//        try {
//            showDownloadDialog();
//            request = new1 DownloadManager.Request(Uri.parse(url));
//            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//            request.setTitle(getResources().getString(R.string.download));
//            request.setDescription(getResources().getString(R.string.apk_download));
//            request.setDestinationInExternalFilesDir(BaseActivity.this, Environment.DIRECTORY_DOWNLOADS, "giiiwallet_merchant.apk");
//            mDownloadID = mManager.enqueue(request);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    ProgressDialog mPd;
//
//    private void showDownloadDialog() {
//        mPd = new1 ProgressDialog(this);
//        mPd.setMessage(getResources().getString(R.string.downloading));
//        mPd.setCancelable(false);
//        mPd.show();
//    }
//
//
//    public class DownloadReceiver extends BroadcastReceiver {
//        final static String MINETYPE = "application/vnd.android.package-archive";
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            Uri filepath = mManager.getUriForDownloadedFile(mDownloadID);
//
//            if (intent.getAction() == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
//                LogUtils.e(mPd == null);
//                if (mPd != null) mPd.dismiss();
//                Intent activityIntent = new1 Intent(Intent.ACTION_VIEW);
//
//
//                try {
//                    mDialog.dismiss();
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                        activityIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                        if (filepath.toString().startsWith("content")) {
//                            Uri contentUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider", new1 File(getDataColumn(filepath)));
//                            activityIntent.setDataAndType(contentUri, MINETYPE);
//                        }
//
//                    } else {
//                        if (filepath.toString().startsWith("content")) {
//                            activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//                            filepath = Uri.fromFile(new1 File(getDataColumn(filepath)));
//                        }
//                        LogUtils.e(filepath);
//                        activityIntent.setDataAndType(filepath, MINETYPE);
//                    }
//                    context.startActivity(activityIntent);
//                    finish();
//                } catch (Exception ex) {
//                    LogUtils.e(ex);
//                }
//            } else if (intent.getAction().equals(DownloadManager.ACTION_NOTIFICATION_CLICKED)) {
//                try {
//                    Intent dm = new1 Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
//                    dm.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    context.startActivity(dm);
//                } catch (ActivityNotFoundException ex) {
//                }
//            }
//
//        }
//
//    }
//
//    public String getDataColumn(Uri uri) {
//        Cursor cursor = null;
//        final String column = "_data";
//        final String[] projection = {column};
//        try {
//            cursor = getContentResolver().query(uri, projection, null, null,
//                    null);
//            if (cursor != null && cursor.moveToFirst()) {
//                final int column_index = cursor.getColumnIndexOrThrow(column);
//                return cursor.getString(column_index);
//            }
//        } finally {
//            if (cursor != null)
//                cursor.close();
//        }
//        return null;
//    }

    private boolean isShowingEyes = false;

//    public void dosetTitleEyes(@StringRes int titleRes, final EyeShowListener listener) {
//        dosetTitleEyes(getString(titleRes), listener);
//    }

//    public void dosetTitleEyes(String titleRes, final EyeShowListener listener) {
//        dosetTitleIcon(titleRes, R.drawable.icon_eye_close, new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ImageView ivEyes = (ImageView) view;
//                if (isShowingEyes) {
//                    isShowingEyes = false;
//                    ivEyes.setImageResource(R.drawable.icon_eye_close);
//                } else {
//                    isShowingEyes = true;
//                    ivEyes.setImageResource(R.drawable.icon_eye_open);
//                }
//                listener.showOrHidEyes(isShowingEyes);
//            }
//        });


//    }

    public void dosetTitleIcon(@StringRes int titleRes, @DrawableRes int icon, final View.OnClickListener listener) {
        dosetTitleIcon(getString(titleRes), icon, listener);
    }

    public void dosetTitleIcon(String titleRes, @DrawableRes int icon, final View.OnClickListener listener) {
        int titleHeight = (int) getResources().getDimension(R.dimen.title_height);


        View viewRoot = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        if (viewRoot == null) {
            LogUtils.i("设置title失败!");
            return;
        }
//        viewRoot.setBackgroundColor(getResources().getColor(R.color.color_page_bg));
        View viewTitle = LayoutInflater.from(this).inflate(R.layout.layout_include_title, null);
        TextView tvTitle = viewTitle.findViewById(R.id.tv_title);
        ImageView ivBack = viewTitle.findViewById(R.id.iv_back);
        if (listener != null) {
            final ImageView ivEyes = viewTitle.findViewById(R.id.title_eye_iv);
            ivEyes.setImageResource(icon);
            ivEyes.setClickable(true);
            ivEyes.setVisibility(View.VISIBLE);
            ivEyes.setOnClickListener(listener);
        }
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                KeyboardUtils.hideSoftInput(BaseActivity.this);
                finish();
            }
        });

//        tvTitle.setText(StaticMethod.toUpperCaseFirstOne(titleRes));

        if (viewRoot instanceof LinearLayout) {
            LinearLayout linearLayout = (LinearLayout) viewRoot;
            viewTitle.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, titleHeight));
//            viewTitle.setPadding(0, BarUtils.getStatusBarHeight(), 0, 0);
            linearLayout.addView(viewTitle, 0);
        }
        if (viewRoot instanceof RelativeLayout) {
            RelativeLayout relativeLayout = (RelativeLayout) viewRoot;

            viewTitle.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, titleHeight));
//            viewTitle.setPadding(0, BarUtils.getStatusBarHeight(), 0, 0);
            viewTitle.setId(R.id.layout_title_id);

            relativeLayout.addView(viewTitle, 0);
            if (relativeLayout.getChildCount() > 1) {
                View child1 = relativeLayout.getChildAt(1);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) child1.getLayoutParams();
                params.addRule(RelativeLayout.BELOW, R.id.layout_title_id);
                child1.setLayoutParams(params);
            }


        }
    }




    private File mSaveFile;

//    public void startCameraActivity() {
//        if (mSaveFile == null) {
//            mSaveFile = new FileStorage().createIconFile();
//        }
//
//        if (mSaveFile != null) {
//            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//            ContentValues contentValues = new ContentValues(1);
//            contentValues.put(MediaStore.Images.Media.DATA, mSaveFile.getPath());
//            Uri uri = this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//            startActivityForResult(intent,
//                    Constants.FROM_CAMERA_REQUEST);
//        }
//    }

//    public String getCameraPath() {
//        return mSaveFile.getPath();
//    }
//
//    public void startGalleryActivity() {
//        Intent intent01 = new Intent(Intent.ACTION_PICK, null);
//        intent01.setDataAndType(
//                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                Constants.IMAGE_UNSPECIFIED);
//        startActivityForResult(intent01,
//                Constants.FROM_GALLERY_REQUEST);
//    }
public static BaseBean parseJson(String resJsonString, Gson gson) {
    //在基类里面统一的完成json解析S
    //type-->就是BaseProtocol子类中定义的具体类型
//    BaseBean resultBean = new BaseBean();
//            Class<ID> entityClass = (Class<ID>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
//            resultBean = ResultBean.fromJson(resJsonString, entityClass);
    BaseBean baseBean = new BaseBean();
    JsonParser parser = new JsonParser();
    JsonObject jsonObject = null;
    try {
        jsonObject = parser.parse(resJsonString).getAsJsonObject();
    } catch (JsonSyntaxException e) {
        e.printStackTrace();
        return null;

    }
    String code = null;
    try {
        code = jsonObject.get("code").getAsString();
        baseBean.code=code;
    } catch (Exception e) {
        e.printStackTrace();
    }
    Object data = null;
    try {
        data = jsonObject.get("data").getAsJsonObject();
        baseBean.data=data;
    } catch (Exception e) {
        e.printStackTrace();
    }
    String msg = null;
    try {
        msg = jsonObject.get("msg").getAsString();
        baseBean.msg=msg;
    } catch (Exception e) {
        e.printStackTrace();
    }





//    try {
//        JSONObject obj = new JSONObject(resJsonString);
//        resultBean.code=obj.getString("code");
//        try {
//            resultBean.data=obj.getString("data");
//        } catch (JSONException e) {
//        }
//        try {
//            resultBean.msg=obj.getString("msg");
//        } catch (JSONException e) {
//        }
//    } catch (JSONException e) {
//        LogUtils.e(e.getMessage());
//        e.printStackTrace();
//    }
    return baseBean;
}
    public LogInterceptor getLogInterceptor(){
        if (mLogInterceptor==null){
            mLogInterceptor = new LogInterceptor(this);
        }
        return mLogInterceptor;
    }
    public static class LogInterceptor implements Interceptor {

        private final Context context;
        private long mLast;

        public LogInterceptor(Context context) {
            this.context = context;
        }



        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            String method = request.method();

            Response response = null;
            MediaType mediaType = null;
            String content = null;
            LogUtils.e(request.url());
            StringBuilder sb = new StringBuilder();
            if (request.body() instanceof FormBody) {
                FormBody body = (FormBody) request.body();
                for (int i = 0; i < body.size(); i++) {
                    sb.append(body.encodedName(i) + "=" + body.encodedValue(i) + ",");
                }
                sb.delete(sb.length() - 1, sb.length());
                LogUtils.e("| RequestParams:{" + sb.toString() + "}");
            }
            Headers headers = request.headers();
            try {
                long t1 = System.nanoTime();
                response = chain.proceed(chain.request());
                Response copy = response.newBuilder().build();
                long t2 = System.nanoTime();
                LogUtils.e("responseCode = " + copy.code()+" " +String.format(Locale.getDefault(), "Received response for %s in %.1fms",
                        copy.request().url(), (t2 - t1) / 1e6d));
                Headers headers1 = copy.headers();
                mediaType = copy.body().contentType();
                content = copy.body().string();
                LogUtils.e("response body:" + content);
            } catch (IOException e) {

                LogUtils.e(e);
                e.printStackTrace();
                throw e;
            }
            return response.newBuilder()
                    .body(okhttp3.ResponseBody.create(mediaType, content))
                    .build();
        }

    }
}
