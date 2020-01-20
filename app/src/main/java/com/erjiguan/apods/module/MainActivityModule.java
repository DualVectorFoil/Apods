package com.erjiguan.apods.module;

import com.erjiguan.apods.contract.IMainContract;
import com.erjiguan.apods.util.annotation.ActivityScope;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class MainActivityModule {
    @ActivityScope
    @Binds
    abstract IMainContract.IMainActivityPresenter bindPresenter(IMainContract.IMainActivityPresenter presenter);
}
