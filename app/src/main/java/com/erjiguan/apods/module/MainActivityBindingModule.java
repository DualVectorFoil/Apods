package com.erjiguan.apods.module;

import com.erjiguan.apods.util.annotation.ActivityScope;
import com.erjiguan.apods.view.activity.MainActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class MainActivityBindingModule {

    @ActivityScope
    @ContributesAndroidInjector(modules = MainActivityModule.class)
    abstract MainActivity contributeMainActivity();
}
