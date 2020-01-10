package com.example.cardtest.Config;

import com.example.cardtest.Switch.mvp.module.ISwitching;

public class YS_Config extends BaseConfig {
    @Override
    public String getMainActivity() {
        return ".YiShengMainActivity";
    }

    @Override
    public String cardPort() {
        return "/dev/ttyS3";
    }

    @Override
    public ISwitching switchImpl() {
        return null;
    }
}
