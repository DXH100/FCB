package com.fcb.fogcomputingbox;

import android.animation.ObjectAnimator;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * author: denghx
 * date  : 2018/1/22
 * desc  :
 */

public class LookAwardActivity extends BaseActivity {
    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.layout_title)
    RelativeLayout mLayoutTitle;
    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerview;
    public static final String DATA = "data";
    @BindView(R.id.ll_empty)
    LinearLayout mLlEmpty;
    @BindView(R.id.tv_address)
    TextView mTvAddress;

    private List<AwardBean.DailysBean> mData;
    private BaseQuickAdapter<AwardBean.DailysBean, BaseViewHolder> mAdapter;
    private TextView mTotalAmount;
    private TextView mTvState;

    @Override
    public int doSetContentView() {
        return R.layout.activity_look_award;
    }

    @Override
    public void initData() {
        StatusBarUtil.immersive(this);
        StatusBarUtil.setPaddingSmart(this, mLayoutTitle);
        EventBus.getDefault().register(this);
        AwardBean bean = getIntent().getParcelableExtra(DATA);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        if (bean == null) {
            bean = new AwardBean();
            bean.dailys = new ArrayList<>();
        }
        mData = bean.dailys;
        bean.reward = "2000";
        mData.add(new AwardBean.DailysBean("2018-2-1", "121252"));
        mData.add(new AwardBean.DailysBean("2018-2-1", "121252"));
        mData.add(new AwardBean.DailysBean("2018-2-1", "121252"));
        mData.add(new AwardBean.DailysBean("2018-2-1", "121252"));
        mAdapter = new BaseQuickAdapter<AwardBean.DailysBean, BaseViewHolder>(R.layout.item_award, mData) {
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
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceive(StateBean bean) {
        LogUtils.e("状态:" + bean);
        if (bean.isAlive) {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    boolean isExtend = false;

    @OnClick({R.id.iv_back})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }

    public ObjectAnimator createRotateAnimator(final View target, final float from, final float to) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(target, "rotation", from, to);
        animator.setDuration(300);
        animator.setInterpolator(new LinearInterpolator());
        return animator;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
