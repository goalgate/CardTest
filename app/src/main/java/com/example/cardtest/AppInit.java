package com.example.cardtest;

import android.app.Application;
import android.content.Context;
import com.example.cardtest.Config.BaseConfig;
import com.blankj.utilcode.util.Utils;
import com.example.cardtest.Config.BoyaConfig;
import com.example.cardtest.Config.MC_Config;


public class AppInit extends Application {

    protected static AppInit instance;

    protected static BaseConfig InstrumentConfig;

    public static AppInit getInstance() {
        return instance;
    }

    public static BaseConfig getInstrumentConfig() {
        return InstrumentConfig;
    }


    public static Context getContext() {
        return getInstance().getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        InstrumentConfig = new BoyaConfig();

        Utils.init(getContext());

    }
}
