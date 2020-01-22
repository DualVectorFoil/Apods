package com.erjiguan.apods.mvp.presenter;

import android.util.Log;

import com.erjiguan.apods.BuildConfig;
import com.erjiguan.apods.base.BasePresenter;
import com.erjiguan.apods.base.OnLoadDataListener;
import com.erjiguan.apods.mvp.model.IMainModel;
import com.erjiguan.apods.mvp.model.bean.BluetoothStatusBean;
import com.erjiguan.apods.mvp.model.impl.MainModelImpl;
import com.erjiguan.apods.mvp.view.IMainView;
import com.erjiguan.apods.ui.activity.MainActivity;

public class MainPresenter extends BasePresenter<IMainView> {

    private IMainModel mMainModel;

    public MainPresenter() {
        this.mMainModel = new MainModelImpl();
    }

    public void handleBtOpen() {
        if (mWeakReferenceView == null) return;

        mMainModel.registerBtStatusListner(new OnLoadDataListener<BluetoothStatusBean>() {
            @Override
            public void onSuccess(BluetoothStatusBean bluetoothStatusBean) {
                getView().onBtStatusUpdated(bluetoothStatusBean);
            }

            @Override
            public void onFailure(String error) {
                if (BuildConfig.DEBUG) {
                    Log.w(MainActivity.TAG, "registerBtStatusListner failed, err: " + error);
                }
            }
        });
    }
}
