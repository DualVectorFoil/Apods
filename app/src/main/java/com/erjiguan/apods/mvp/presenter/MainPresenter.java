package com.erjiguan.apods.mvp.presenter;

import com.erjiguan.apods.base.BasePresenter;
import com.erjiguan.apods.base.OnLoadDataListener;
import com.erjiguan.apods.mvp.model.IMainModel;
import com.erjiguan.apods.mvp.model.bean.BluetoothStatusBean;
import com.erjiguan.apods.mvp.model.impl.MainModelImpl;
import com.erjiguan.apods.mvp.view.IMainView;

public class MainPresenter extends BasePresenter<IMainView> {

    private IMainModel mMainModel;

    public MainPresenter() {
        this.mMainModel = new MainModelImpl();
    }

    public void handleBtOpen() {
        if (mWeakReferenceView == null) return;

        mMainModel.handleBtOpen(new OnLoadDataListener<BluetoothStatusBean>() {
            @Override
            public void onSuccess(BluetoothStatusBean bluetoothStatusBean) {

            }

            @Override
            public void onFailure(String error) {

            }
        });
    }
}
