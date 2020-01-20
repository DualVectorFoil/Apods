package com.erjiguan.apods.base;

public abstract class BaseBean<T extends BaseBean.DataBean> {

    private String mErrorMsg;
    private int mErrorCode;
    private T mData;

    // TODO error code here
    public static final int ERROR_UNKNOWN = 0;

    public String getErrorMsg() {
        return mErrorMsg;
    }

    public void setErrorMsg(String mErrorMsg) {
        this.mErrorMsg = mErrorMsg;
    }

    public int getErrorCode() {
        return mErrorCode;
    }

    public void setErrorCode(int mErrorCode) {
        this.mErrorCode = mErrorCode;
    }

    public T getData() {
        return mData;
    }

    public void setData(T mData) {
        this.mData = mData;
    }

    public static abstract class DataBean {
    }
}
