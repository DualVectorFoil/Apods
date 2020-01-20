package com.erjiguan.apods.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public abstract class IBaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());
        onViewCreated();
        initToolbar();
        initEventAndData();
    }

    protected abstract void onViewCreated();

    protected abstract void initToolbar();

    protected abstract void initEventAndData();

    protected abstract int getLayout();
}
