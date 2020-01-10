package com.example.cardtest.Config;

import com.example.cardtest.Switch.mvp.module.ISwitching;
import com.example.cardtest.Switch.mvp.module.MaiChongSwitchImpl;

public class MC_Config extends BaseConfig {
    @Override
    public String getMainActivity() {
        return ".MaiChongMainActivity";
    }

    @Override
    public String cardPort() {
        return "/dev/ttyS1";
    }

    @Override
    public ISwitching switchImpl() {
        return new MaiChongSwitchImpl();
    }
}
