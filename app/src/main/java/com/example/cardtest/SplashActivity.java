package com.example.cardtest;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.SPUtils;
import com.example.cardtest.Tool.AssetsUtils;

public class SplashActivity extends Activity {


    private SPUtils config = SPUtils.getInstance("config");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (config.getBoolean("firstStart", true)) {
            AssetsUtils.getInstance(AppInit.getContext()).copyAssetsToSD("wltlib", "wltlib");
            config.put("firstStart", false);
        }
        ActivityUtils.startActivity(getPackageName(), getPackageName() + AppInit.getInstrumentConfig().getMainActivity());
        this.finish();
    }
}
