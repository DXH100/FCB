package com.fcb.fogcomputingbox;

import android.content.Intent;
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
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import service.LocalService;
import service.RemoteService;

public class MainActivity extends BaseActivity {


    @BindView(R.id.tv_look)
    TextView mTvLook;
    @BindView(R.id.et_address)
    EditText mEtAddress;
    @BindView(R.id.tv_regist)
    TextView mTvRegist;
    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.layout_title)
    RelativeLayout mLayoutTitle;
    private String mAddress;

    @Override
    public int doSetContentView() {
        return R.layout.activity_main;
    }

    @Override
    public void initData() {
        StatusBarUtil.immersive(this);
        StatusBarUtil.setPaddingSmart(this, mLayoutTitle);
        initService();
        doShowNetProgress();
        mAddress = SPUtils.getInstance().getString("address");
        LogUtils.e(mAddress);
        if (!TextUtils.isEmpty(mAddress)){
            requestAward(mAddress);
        }else {
            LogUtils.e(111);
            doDismissNetProgress();
        }
    }
    private void initService() {
        startService(new Intent(this, LocalService.class));
        startService(new Intent(this, RemoteService.class));
    }

    @OnClick({R.id.tv_look, R.id.tv_regist, R.id.iv_back})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.tv_look:
                if (TextUtils.isEmpty(mEtAddress.getText().toString().trim())) {
                    showErrToast("钱包地址不能为空");
                    return;
                }
                requestAward(mEtAddress.getText().toString().trim());


                break;
            case R.id.tv_regist:


                startActivity(RegistActivity.class);
                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }

    private void requestAward(String address) {
        doShowNetProgress();
        final OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(Constants.TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(Constants.TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(getLogInterceptor())
                .build();

        FormBody.Builder builder = new FormBody.Builder();
        long time = System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 7;
        long time1 = System.currentTimeMillis();
        LogUtils.e(DateHelper.getYYMMdd(time));
        LogUtils.e(DateHelper.getYYMMdd(time1));
        builder.add("address",address);
//        builder.add("startDate",DateHelper.getYYMMdd(time));
//        builder.add("endDate",DateHelper.getYYMMdd(time1));

        FormBody body = builder.build();
        Request request = new Request.Builder()
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
                BaseBean baseBean = parseJson(response.body().string(),gson);
                LogUtils.e(baseBean.code.equalsIgnoreCase("E000000"));
                LogUtils.e(baseBean.code.toString());
                LogUtils.e("E000000");
                if (baseBean.code.equalsIgnoreCase("E000000")) {

                    AwardBean awardBean = gson.fromJson(baseBean.data.toString(), AwardBean.class);
                    Intent intent = new Intent(MainActivity.this,LookAwardActivity.class);
                    intent.putExtra(LookAwardActivity.DATA,awardBean);
                    startActivity(intent);
                    if (!TextUtils.isEmpty(mEtAddress.getText().toString())){
                        SPUtils.getInstance().put("address",mEtAddress.getText().toString());
                    }

                } else {
                    showErrToast(baseBean.msg);
                }
            }
        });
    }
}
