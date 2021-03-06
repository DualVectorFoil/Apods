package com.erjiguan.apods.mvp.model;

import com.erjiguan.apods.base.OnLoadDataListener;
import com.erjiguan.apods.mvp.model.bean.BluetoothStatusBean;

public interface IMainModel {

    void registerBtStatusListner(OnLoadDataListener<BluetoothStatusBean> listener);

    void unRegisterBtStatusListner(OnLoadDataListener<BluetoothStatusBean> listener);
}
