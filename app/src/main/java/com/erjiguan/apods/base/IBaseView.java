package com.erjiguan.apods.base;

import android.app.Dialog;

public interface IBaseView {

    Dialog getLoadDialog();

    void cancelDialog();
}
