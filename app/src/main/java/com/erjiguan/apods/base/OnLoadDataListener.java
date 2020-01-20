package com.erjiguan.apods.base;

public interface OnLoadDataListener<T> {

    void onSuccess(T t);

    void onFailure(String error);
}
