package com.erjiguan.apods.module;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    @Provides
    @Singleton
    public Object providerData() {
        return new Object();
    }
}
