package com.example.cardtest.Config;

import com.example.cardtest.Switch.mvp.module.ISwitching;

public abstract class BaseConfig {

    public abstract String getMainActivity();

    public abstract String cardPort();

    public abstract ISwitching switchImpl();
}