package com.erjiguan.apods.view.main;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.erjiguan.apods.R;
import com.erjiguan.apods.base.BaseActivity;
import com.erjiguan.apods.contract.IMainContract;
import com.erjiguan.apods.presenter.MainPresenter;

public class MainActivity extends BaseActivity<MainPresenter> implements IMainContract.IMainView {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO 业务逻辑
    }

    @Override
    protected void initEventAndData() {
        mPresenter.getData();
    }

    @Override
    protected void onViewCreated() {

    }

    @Override
    protected void initToolbar() {

    }

    @Override
    protected int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void showData() {
        // TODO 通过mPresenter更新view，展示数据
    }
}
