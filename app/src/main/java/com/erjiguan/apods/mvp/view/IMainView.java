package com.erjiguan.apods.mvp.view;

import com.erjiguan.apods.base.IBaseView;
import com.erjiguan.apods.mvp.model.bean.BluetoothStatusBean;

public interface IMainView extends IBaseView {

    void onBtStatusUpdated(BluetoothStatusBean statusBean);
}
