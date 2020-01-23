package com.erjiguan.apods.base;

import android.os.Bundle;

public interface IBaseActivity {

    int getLayout();

    // Should init tool bar here.
    void initView();

    void initData(Bundle savedInstanceState);
}
