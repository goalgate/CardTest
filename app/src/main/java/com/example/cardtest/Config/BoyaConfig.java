package com.example.cardtest.Config;

import com.example.cardtest.Switch.mvp.module.BoyaSwitchImpl;
import com.example.cardtest.Switch.mvp.module.ISwitching;

public class BoyaConfig extends BaseConfig {
    @Override
    public String getMainActivity() {
        return ".BoyaMainActivity";
    }

    @Override
    public String cardPort() {
        return "/dev/ttyS1";
    }

    @Override
    public ISwitching switchImpl() {
        return new BoyaSwitchImpl();
    }
}
