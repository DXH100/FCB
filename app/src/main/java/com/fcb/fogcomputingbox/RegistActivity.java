package com.fcb.fogcomputingbox;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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
    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.layout_title)
    RelativeLayout mLayoutTitle;

    @Override
    public int doSetContentView() {
        return R.layout.activity_regist;
    }

    @Override
    public void initData() {
        StatusBarUtil.immersive(this);
        StatusBarUtil.setPaddingSmart(this, mLayoutTitle);
    }

    @OnClick({R.id.tv_regist})
    public void onViewClick(View view) {

        switch (view.getId()) {
            case R.id.tv_regist:
                if (TextUtils.isEmpty(mEtAddress.getText().toString().trim())) {
                    showErrToast("钱包地址不能为空");
                    return;
                }
                if (TextUtils.isEmpty(mEtCode.getText().toString().trim())) {
                    showErrToast("注册码不能为空");
                    return;
                }
                doShowNetProgress();
                final OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(Constants.TIMEOUT, TimeUnit.SECONDS)
                        .readTimeout(Constants.TIMEOUT, TimeUnit.SECONDS)
                        .addInterceptor(getLogInterceptor())
                        .build();

                FormBody.Builder builder = new FormBody.Builder();
                builder.add("address", mEtAddress.getText().toString().trim());
                builder.add("registerCode", mEtCode.getText().toString().trim());
//                builder.add("deviceId", DateHelper.getYYMMdd(System.currentTimeMillis() - (1000 * 60 * 60 * 24 * 7)));
                LogUtils.e(MacUtils.getAdresseMAC(this));
                builder.add("macAddress", MacUtils.getAdresseMAC(this));
                FormBody body = builder.build();
                final Request request = new Request.Builder()
                        .url("http://39.107.96.160:8080/api/register")
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
                        LogUtils.e(baseBean.toString());
                        if (baseBean.code.equalsIgnoreCase("E000000")) {
                            requestAward(mEtAddress.getText().toString().trim());
                        } else {
                            showErrToast(baseBean.msg);
                        }
                    }
                });


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
                .url("http://39.107.96.160:8080/api/getReward")
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
                LogUtils.e(baseBean);
                if (TextUtils.equals(baseBean.code,"E000000")) {

                    AwardBean awardBean = gson.fromJson(baseBean.data.toString(), AwardBean.class);
                    Intent intent = new Intent(RegistActivity.this, LookAwardActivity.class);
                    intent.putExtra(LookAwardActivity.DATA, awardBean);
                    startActivity(intent);
                    SPUtils.getInstance().put("address", address);
                    finish();
                } else {
                    showErrToast(baseBean.msg);
                }
            }
        });
    }
}
