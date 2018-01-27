package com.fcb.fogcomputingbox;

import android.animation.ObjectAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import butterknife.BindView;
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

    private List<AwardBean.DailysBean> mData;
    private BaseQuickAdapter<AwardBean.DailysBean, BaseViewHolder> mAdapter;
    private TextView mTotalAmount;

    @Override
    public int doSetContentView() {
        return R.layout.activity_look_award;
    }

    @Override
    public void initData() {
        StatusBarUtil.immersive(this);
        StatusBarUtil.setPaddingSmart(this, mLayoutTitle);
        AwardBean bean = getIntent().getParcelableExtra(DATA);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        mData = bean.dailys;

        mAdapter = new BaseQuickAdapter<AwardBean.DailysBean, BaseViewHolder>(R.layout.item_award, mData) {
            @Override
            protected void convert(BaseViewHolder helper, AwardBean.DailysBean item) {
                helper.setText(R.id.tv_name, "+" + item.dailyReward);
                helper.setText(R.id.tv_date, item.dailyDate);
            }
        };
        View view = View.inflate(this, R.layout.view_awart_header, null);
        mTotalAmount = view.findViewById(R.id.tv_total_amount);
        mTotalAmount.setText(bean.reward);
        mAdapter.addHeaderView(view);
        mRecyclerview.setAdapter(mAdapter);
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

}
