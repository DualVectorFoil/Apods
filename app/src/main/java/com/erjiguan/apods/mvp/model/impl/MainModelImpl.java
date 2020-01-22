package com.erjiguan.apods.mvp.model.impl;

import com.erjiguan.apods.base.OnLoadDataListener;
import com.erjiguan.apods.mvp.model.IMainModel;
import com.erjiguan.apods.mvp.model.bean.BluetoothStatusBean;

public class MainModelImpl implements IMainModel {

    @Override
    public void registerBtStatusListner(final OnLoadDataListener<BluetoothStatusBean> listener) {
        // TODO 打开蓝牙（应该在presenter里面）、注册蓝牙监听、在监听里调用listener的方法进行响应，listner会回调到app去更新view，javabean，能不能判断是哪个成员变量改变了，仅去更新他对应的view
    }
}
