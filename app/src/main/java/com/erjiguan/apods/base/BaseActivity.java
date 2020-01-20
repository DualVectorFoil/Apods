package com.erjiguan.apods.base;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.erjiguan.apods.utils.DialogUtil;
import com.squareup.leakcanary.RefWatcher;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import butterknife.ButterKnife;

public abstract class BaseActivity<V extends IBaseView, P extends BasePresenter<V>> extends RxAppCompatActivity implements IBaseView, IBaseActivity {

    protected P mPresenter;

    protected Dialog mDialog;

    @SuppressWarnings("unchecked")

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());
        ButterKnife.bind(this);
        if (mPresenter == null) {
            mPresenter = createPresenter();
        }
        mPresenter.attachView((V) this);
        initToolBar();
        mDialog = DialogUtil.createLoadingDialog(this, "请稍后");
        initView();
        initData(savedInstanceState);
    }

    protected abstract P createPresenter();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = BaseApplication.getRefWatcher(this);
        refWatcher.watch(this);
        if (mPresenter != null) {
            mPresenter.detachView();
        }
    }

    protected void initToolBar() {
        // TODO init tool bar
    }
}
