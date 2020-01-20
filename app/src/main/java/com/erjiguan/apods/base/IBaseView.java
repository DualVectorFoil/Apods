package com.erjiguan.apods.base;

public interface IBaseView {

    void showNormal();

    void showError();

    void showLoading();

    void showErrorMsg(String errorMsg);
}
