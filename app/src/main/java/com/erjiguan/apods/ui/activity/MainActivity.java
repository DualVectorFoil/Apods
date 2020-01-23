package com.erjiguan.apods.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import com.airbnb.lottie.LottieAnimationView;
import com.erjiguan.apods.R;
import com.erjiguan.apods.base.BaseActivity;
import com.erjiguan.apods.mvp.model.bean.BluetoothStatusBean;
import com.erjiguan.apods.mvp.presenter.MainPresenter;
import com.erjiguan.apods.mvp.view.IMainView;

import butterknife.BindView;

public class MainActivity extends BaseActivity<IMainView, MainPresenter> implements IMainView, View.OnClickListener, View.OnLongClickListener {

    public static final String TAG = "MainActivity";

    @BindView(R.id.pod_status)
    protected RelativeLayout mPodStatusLayout;
    @BindView(R.id.interaction_zone)
    protected FrameLayout mInteractionZoneLayout;

    private LottieAnimationView mGuideAnimView;
    private LottieAnimationView mConnectAnimView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter.init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.handleDestroy();
    }

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
    public void initView() {
        mGuideAnimView = new LottieAnimationView(this);
        mGuideAnimView.setRepeatCount(-1);
        mGuideAnimView.setAnimation("bluetooth_direct.json");
        mGuideAnimView.setBackgroundColor(Color.WHITE);

        mConnectAnimView = new LottieAnimationView(this);
        mConnectAnimView.setRepeatCount(-1);
        mConnectAnimView.setAnimation("bluetooth_connecting.json");
        mConnectAnimView.setBackgroundColor(Color.WHITE);
        mConnectAnimView.setSpeed(0f);
        mConnectAnimView.setOnClickListener(this);
        mConnectAnimView.setOnLongClickListener(this);

        return;
    }

    @Override
    public void initData(Bundle savedInstanceState) {

    }

    @Override
    public void onBtStatusUpdated(BluetoothStatusBean statusBean) {
        // TODO 更新view，最好是有一个根据变化字段更新只对应view的能力
    }

    @Override
    public void onClick(View v) {
        mPresenter.handleBtOpen();
    }

    @Override
    public boolean onLongClick(View view) {
        Intent i = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
        startActivity(i);
        return true;
    }

    @Override
    public void showViewWhenBtConnected() {

    }

    @Override
    public void showViewWhenBtNotConnected() {

    }

    @Override
    public void playBtSearchAnimation() {
        mConnectAnimView.setSpeed(1.5f);
        mConnectAnimView.playAnimation();
    }

    @Override
    public void stopBtSearchAnimation() {
        mConnectAnimView.setSpeed(0f);
        mConnectAnimView.cancelAnimation();
    }

    @Override
    public void finishSelf() {
        finish();
    }
}
