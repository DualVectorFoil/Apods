package com.erjiguan.apods.base;

import android.os.Bundle;

import androidx.annotation.Nullable;

public abstract class BaseActivity<T extends IBasePresenter> extends IBaseActivity implements IBaseView {

    protected T mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mPresenter = createPresenter();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.detachView();
            mPresenter = null;
        }
    }

    protected abstract T createPresenter();

    @Override
    public void showNormal() {
    }

    @Override
    public void showError() {
    }

    @Override
    public void showLoading() {
    }

    @Override
    public void showErrorMsg(String errorMsg) {
    }
}
