package com.fcb.fogcomputingbox;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.SPUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
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

public class LookAwardActivity
        extends BaseActivity
{
    //    @BindView(R.id.iv_back)
    //    ImageView mIvBack;
    @BindView(R.id.tv_title)
    TextView       mTvTitle;
    @BindView(R.id.layout_title)
    RelativeLayout mLayoutTitle;
    @BindView(R.id.recyclerview)
    RecyclerView   mRecyclerview;
    public static final String DATA = "data";
    @BindView(R.id.ll_empty)
    LinearLayout mLlEmpty;
    @BindView(R.id.tv_address)
    TextView     mTvAddress;

    private List<AwardBean.DailysBean>                             mData;
    private BaseQuickAdapter<AwardBean.DailysBean, BaseViewHolder> mAdapter;
    private TextView                                               mTotalAmount;
    private TextView                                               mTvState;
    private boolean                                                mIsAlive;

    @Override
    public int doSetContentView() {
        return R.layout.activity_look_award;
    }

    @Override
    public void initData() {
        StatusBarUtil.immersive(this);
        StatusBarUtil.setPaddingSmart(this, mLayoutTitle);
        EventBus.getDefault()
                .register(this);
        AwardBean bean = getIntent().getParcelableExtra(DATA);
        mIsAlive = getIntent().getBooleanExtra("isAlive", true);

        mRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        if (bean == null) {
            bean = new AwardBean();
            bean.dailys = new ArrayList<>();
        }
        mData = bean.dailys;
        bean.reward = "0.00";
        mAdapter = new BaseQuickAdapter<AwardBean.DailysBean, BaseViewHolder>(R.layout.item_award,
                                                                              mData)
        {
            @Override
            protected void convert(BaseViewHolder helper, AwardBean.DailysBean item) {
                helper.setText(R.id.tv_name, "+" + item.dailyReward);
                helper.setText(R.id.tv_date, item.dailyDate);
            }
        };
        View view = View.inflate(this, R.layout.view_awart_header, null);
        mTotalAmount = view.findViewById(R.id.tv_total_amount);
        mTvState = view.findViewById(R.id.tv_status);

        mTotalAmount.setText(bean.reward);
        mAdapter.addHeaderView(view);
        mRecyclerview.setAdapter(mAdapter);
        mTvAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(MyAddressActivity.class);
            }
        });

        if (mIsAlive) {
            mTvState.setText("在线");
            Drawable drawable = getResources().getDrawable(R.drawable.online);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            mTvState.setCompoundDrawables(drawable, null, null, null);

        } else {
            mTvState.setText("离线");
            Drawable drawable = getResources().getDrawable(R.drawable.offline);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            mTvState.setCompoundDrawables(drawable, null, null, null);
        }
    }

    private void startServices() {
        startService(new Intent(this, LocalService.class));
        startService(new Intent(this, RemoteService.class));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceive(StateBean bean) {
        LogUtils.e("状态:" + bean.isAlive);
        if (bean.isAlive) {
            mTvState.setText("在线");
            Drawable drawable = getResources().getDrawable(R.drawable.online);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            mTvState.setCompoundDrawables(drawable, null, null, null);


            if (!mIsAlive) {
                requestAward(SPUtils.getInstance()
                                    .getString("address"));

            }
            mIsAlive = true;
        } else {
            mTvState.setText("离线");
            Drawable drawable = getResources().getDrawable(R.drawable.offline);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            mTvState.setCompoundDrawables(drawable, null, null, null);
            mIsAlive = false;
        }

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault()
                .unregister(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mFlag=false;
        startServices();
    }

    boolean mFlag = true;

    @Override
    protected void onStart() {
        super.onStart();
        stopServices();
        mFlag = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mFlag) {
                    SystemClock.sleep(Constants.INTERVAL);
                    startRequest();
                    //                    SystemClock.sleep(10000);
                }

            }
        }).start();
    }

    private void stopServices() {
        stopService(new Intent(this, LocalService.class));
        stopService(new Intent(this, RemoteService.class));
    }

    boolean isExtend = false;

    //    @OnClick({R.id.iv_back})
    //    public void onViewClick(View view) {
    //        switch (view.getId()) {
    //            case R.id.iv_back:
    //                finish();
    //                break;
    //        }
    //    }

    public ObjectAnimator createRotateAnimator(final View target, final float from, final float to)
    {
        ObjectAnimator animator = ObjectAnimator.ofFloat(target, "rotation", from, to);
        animator.setDuration(300);
        animator.setInterpolator(new LinearInterpolator());
        return animator;
    }

    private void startRequest() {
        LogUtils.e("startRequest");
        final OkHttpClient client = new OkHttpClient.Builder().connectTimeout(Constants.TIMEOUT,
                                                                              TimeUnit.SECONDS)
                                                              .readTimeout(Constants.TIMEOUT,
                                                                           TimeUnit.SECONDS)
                                                              .build();

        FormBody.Builder builder = new FormBody.Builder();
        TelephonyManager tm      = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        LogUtils.e(MacUtils.getAdresseMAC(this));
        builder.add("address",
                    SPUtils.getInstance()
                           .getString("address"));
        builder.add("macAddress", MacUtils.getAdresseMAC(this));

        FormBody body = builder.build();
        Request request = new Request.Builder().url(Constants.BaseUrl + "/api/saveMining")
                                               .post(body)
                                               .build();

        client.newCall(request)
              .enqueue(new Callback() {
                  @Override
                  public void onFailure(Call call, IOException e) {
                      EventBus.getDefault()
                              .post(new StateBean(false));
                  }

                  @Override
                  public void onResponse(Call call, Response response)
                          throws IOException
                  {
                      BaseBean baseBean = BaseActivity.parseJson(response.body()
                                                                         .string(), new Gson());
                      LogUtils.e(baseBean.toString());
                      if (baseBean != null && baseBean.code.equalsIgnoreCase("E000000")) {
                          EventBus.getDefault()
                                  .post(new StateBean(true));
                      } else {
                          EventBus.getDefault()
                                  .post(new StateBean(false));
                      }

                  }
              });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    private void requestAward(final String address) {
        doShowNetProgress();
        final OkHttpClient client = new OkHttpClient.Builder().connectTimeout(Constants.TIMEOUT,
                                                                              TimeUnit.SECONDS)
                                                              .readTimeout(Constants.TIMEOUT,
                                                                           TimeUnit.SECONDS)
                                                              .build();

        FormBody.Builder builder = new FormBody.Builder();
        builder.add("address", address);
        FormBody body = builder.build();
        final Request request = new Request.Builder().url(Constants.BaseUrl + "/api/getReward")
                                                     .post(body)
                                                     .build();

        client.newCall(request)
              .enqueue(new Callback() {
                  @Override
                  public void onFailure(Call call, IOException e) {
                      doDismissNetProgress();
                      showErrToast("网络连接失败");
                  }

                  @Override
                  public void onResponse(Call call, Response response)
                          throws IOException
                  {
                      doDismissNetProgress();
                      final Gson gson = new Gson();
                      final BaseBean baseBean = parseJson(response.body()
                                                                  .string(), gson);
                      //                BaseBean baseBean = gson.fromJson(response.body().string(), BaseBean.class);
                      if (baseBean == null) {
                          showErrToast("网络错误");
                          return;
                      }
                      LogUtils.e(baseBean);
                      if (TextUtils.equals(baseBean.code, "E000000")) {

                          final AwardBean awardBean = gson.fromJson(baseBean.data.toString(),
                                                                    AwardBean.class);
                          runOnUiThread(new Runnable() {
                              @Override
                              public void run() {
                                  mTotalAmount.setText(awardBean.reward);
                                  mData.clear();
                                  mData.addAll(awardBean.dailys);
                                  mAdapter.notifyDataSetChanged();
                              }
                          });


                      } else {
                          showErrToast(baseBean.msg);
                      }
                  }
              });
    }

}
