package com.fcb.fogcomputingbox;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.SPUtils;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import service.LocalService;
import service.RemoteService;

/**
 * author: denghx
 * date  : 2018/1/22
 * desc  :
 */

public class RegistActivity extends BaseActivity {
    @BindView(R.id.et_address)
    EditText mEtAddress;
    @BindView(R.id.et_code)
    EditText mEtCode;
    @BindView(R.id.tv_regist)
    TextView mTvRegist;
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.layout_title)
    RelativeLayout mLayoutTitle;
    private String mAddress;
    private String code;
    private String address;

    @Override
    public int doSetContentView() {
        return R.layout.activity_regist;
    }

    @Override
    public void initData() {
        StatusBarUtil.immersive(this);
        StatusBarUtil.setPaddingSmart(this, mLayoutTitle);

        mAddress = SPUtils.getInstance().getString("address");
        LogUtils.e(mAddress);
        if (!TextUtils.isEmpty(mAddress)){
            requestAward(mAddress);
        }else {
            doDismissNetProgress();
        }
    }
    private void initService() {
        startService(new Intent(this, LocalService.class));
        startService(new Intent(this, RemoteService.class));
    }
    @OnClick({R.id.tv_regist})
    public void onViewClick(View view) {

        switch (view.getId()) {
            case R.id.tv_regist:
                startActivity(LookAwardActivity.class);
//                if (TextUtils.isEmpty(mEtAddress.getText().toString().trim())) {
//                    showErrToast("钱包地址不能为空");
//                    return;
//                }
//                if (TextUtils.isEmpty(mEtCode.getText().toString().trim())) {
//                    showErrToast("注册码不能为空");
//                    return;
//                }
//                doShowNetProgress();
//                final OkHttpClient client = new OkHttpClient.Builder()
//                        .connectTimeout(Constants.TIMEOUT, TimeUnit.SECONDS)
//                        .readTimeout(Constants.TIMEOUT, TimeUnit.SECONDS)
//                        .addInterceptor(getLogInterceptor())
//                        .build();
//
//                FormBody.Builder builder = new FormBody.Builder();
//                address = mEtAddress.getText().toString().trim();
//                builder.add("address", address);
//                code = mEtCode.getText().toString().trim();
//                builder.add("registerCode", code);
////                builder.add("deviceId", DateHelper.getYYMMdd(System.currentTimeMillis() - (1000 * 60 * 60 * 24 * 7)));
//                LogUtils.e(MacUtils.getAdresseMAC(this));
//                builder.add("macAddress", MacUtils.getAdresseMAC(this));
//                FormBody body = builder.build();
//                final Request request = new Request.Builder()
//                        .url(Constants.BaseUrl+"/api/register")
//                        .post(body)
//                        .build();
//
//                client.newCall(request).enqueue(new Callback() {
//                    @Override
//                    public void onFailure(Call call, IOException e) {
//                        doDismissNetProgress();
//                        showErrToast("网络连接失败");
//                    }
//
//                    @Override
//                    public void onResponse(Call call, Response response) throws IOException {
//                        doDismissNetProgress();
//                        Gson gson = new Gson();
//                        BaseBean baseBean = parseJson(response.body().string(), gson);
//                        if (baseBean==null){
//                            showErrToast("网络错误");
//                            return;
//                        }
//                        LogUtils.e(baseBean.toString());
//                        if (baseBean.code.equalsIgnoreCase("E000000")) {
//                            requestAward(mEtAddress.getText().toString().trim());
//                            SPUtils.getInstance().put("address", address);
//                            SPUtils.getInstance().put("code", code);
//                        } else {
//                            showErrToast(baseBean.msg);
//                        }
//                    }
//                });


                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    private void requestAward(final String address) {
        doShowNetProgress();
        final OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(Constants.TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(Constants.TIMEOUT, TimeUnit.SECONDS)
                .build();

        FormBody.Builder builder = new FormBody.Builder();
        builder.add("address", address);
        FormBody body = builder.build();
        final Request request = new Request.Builder()
                .url(Constants.BaseUrl+"/api/getReward")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                doDismissNetProgress();
                showErrToast("网络连接失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                doDismissNetProgress();
                Gson gson = new Gson();
                BaseBean baseBean = parseJson(response.body().string(), gson);
//                BaseBean baseBean = gson.fromJson(response.body().string(), BaseBean.class);
                if (baseBean==null){
                    showErrToast("网络错误");
                    return;
                }
                LogUtils.e(baseBean);
                if (TextUtils.equals(baseBean.code,"E000000")) {

                    AwardBean awardBean = gson.fromJson(baseBean.data.toString(), AwardBean.class);
                    Intent intent = new Intent(RegistActivity.this, LookAwardActivity.class);
                    intent.putExtra(LookAwardActivity.DATA, awardBean);
                    startActivity(intent);


                    initService();
                    finish();
                } else {
                    showErrToast(baseBean.msg);
                }
            }
        });
    }
}
