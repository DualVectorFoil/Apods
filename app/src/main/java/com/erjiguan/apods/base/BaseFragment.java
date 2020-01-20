package com.erjiguan.apods.base;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.erjiguan.apods.utils.DialogUtil;
import com.squareup.leakcanary.RefWatcher;
import com.trello.rxlifecycle2.components.support.RxFragment;

import butterknife.ButterKnife;

public abstract class BaseFragment<V extends IBaseView, P extends BasePresenter<V>> extends RxFragment implements IBaseView, IFragment {

    protected P mPresenter;

    protected View mRootView;
    protected Dialog mDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(getLayout(), container, false);
        ButterKnife.bind(this, v);
        if (mRootView == null) {
            mRootView = inflater.inflate(getLayout(), null);
        }

        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }

        if (mRootView instanceof ViewGroup) {
            // TODO 初始化触摸关闭软键盘
        }

        mPresenter.attachView((V) this);

        mDialog = DialogUtil.createLoadingDialog(getActivity(), "请稍后...");

        initToolBar();
        initView();
        initData(savedInstanceState);
        return mRootView;
    }

    protected abstract P createPresenter();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Context context = getActivity();
        if (context != null) {
            RefWatcher watcher = BaseApplication.getRefWatcher(getActivity());
            watcher.watch(this);
        }
        if (mPresenter != null) {
            mPresenter.detachView();
        }
    }

    protected void initToolBar() {
        // TODO init tool bar
    }
}
