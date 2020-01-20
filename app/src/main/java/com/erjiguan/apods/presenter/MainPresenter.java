package com.erjiguan.apods.presenter;

import android.util.Log;

import com.erjiguan.apods.base.RxBasePresenter;
import com.erjiguan.apods.contract.IMainContract;

public class MainPresenter extends RxBasePresenter<IMainContract.IMainView> implements IMainContract.IMainActivityPresenter {

    // TODO ???
    private Object mModel;

    public MainPresenter(Object model) {
        super(model);
        this.mModel = model;
    }

    @Override
    public void attachView(IMainContract.IMainView view) {
        super.attachView(view);
    }

    @Override
    public void getData() {
        // TODO mModel to get data from somewhere
        mView.showData();
    }

    // TODO 可以在这里定义一些操作mModel的方法，在MainActivity中去使用，达到从activity通过presenter操作model的一条通路
    // TODO 注册回调方法的话，就可以在model变化的时候，反过来去更新activity的view变化了
}
