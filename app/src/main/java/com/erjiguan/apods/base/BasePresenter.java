package com.erjiguan.apods.base;

import java.lang.ref.WeakReference;

public class BasePresenter<V extends IBaseView> {

    protected WeakReference<V> mWeakReferenceView;

    protected V getView() {
        if (mWeakReferenceView == null) {
            throw new IllegalStateException("View not attached.");
        } else {
            return mWeakReferenceView.get();
        }
    }

    public void attachView(V view) {
        if (mWeakReferenceView != null) {
            mWeakReferenceView = new WeakReference<V>(view);
        }
    }

    public void detachView() {
        mWeakReferenceView.clear();
        mWeakReferenceView = null;
    }
}
