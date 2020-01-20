package com.erjiguan.apods.ui.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.erjiguan.apods.R;
import com.erjiguan.apods.base.BaseActivity;
import com.erjiguan.apods.mvp.presenter.MainPresenter;
import com.erjiguan.apods.mvp.view.IMainView;

import butterknife.BindView;

public class MainActivity extends BaseActivity<IMainView, MainPresenter> implements IMainView {

    private static final String TAG = "MainActivity";

    @BindView(R.id.pod_status)
    private RelativeLayout mPodStatusLayout;
    @BindView(R.id.interaction_zone)
    private FrameLayout mInteractionZoneLayout;

    private LottieAnimationView mGuideAnimView;
    private LottieAnimationView mConnectAnimView;

    @Override
    protected MainPresenter createPresenter() {
        return new MainPresenter();
    }

    @Override
    public Dialog getLoadDialog() {
        return null;
    }

    @Override
    public void cancelDialog() {
    }

    @Override
    public int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    public int initView() {
        // TODO 初始化一些view，调用presenter来初始化，必要时用回调，因为涉及到状态数据的获取
        return 0;
    }

    @Override
    public void initData(Bundle savedInstanceState) {

    }
}
