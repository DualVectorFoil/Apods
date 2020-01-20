package com.erjiguan.apods.base;

public class RxBasePresenter<T extends IBaseView> implements IBasePresenter<T> {

    // TODO 手动注入WeakReference
    protected T mView;

    private Object mModel;

    public RxBasePresenter(Object model) {
        this.mModel = model;
    }

    @Override
    public void attachView(T view) {
        this.mView = view;
    }

    @Override
    public void detachView() {
        this.mView = null;
    }
}
