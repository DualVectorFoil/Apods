package com.erjiguan.apods.contract;

import com.erjiguan.apods.base.IBasePresenter;
import com.erjiguan.apods.base.IBaseView;

public interface IMainContract {

    interface IMainView extends IBaseView {
        void showData();
    }

    interface IMainActivityPresenter extends IBasePresenter<IMainContract.IMainView> {
        void getData();
    }
}
