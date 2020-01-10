package com.example.cardtest.Switch.mvp.presenter;


import com.example.cardtest.AppInit;
import com.example.cardtest.Switch.mvp.module.ISwitching;
import com.example.cardtest.Switch.mvp.module.BoyaSwitchImpl;
import com.example.cardtest.Switch.mvp.view.ISwitchView;

/**
 * Created by zbsz on 2017/8/23.
 */

public class SwitchPresenter {

    private ISwitchView view;

    private SwitchPresenter() {
    }

    private static SwitchPresenter instance = null;

    public static SwitchPresenter getInstance() {
        if (instance == null)
            instance = new SwitchPresenter();
        return instance;
    }

    public void SwitchPresenterSetView(ISwitchView view) {
        this.view = view;
    }

    ISwitching switchingModule = AppInit.getInstrumentConfig().switchImpl();

    public void switch_Open() {
        switchingModule.onOpen(new ISwitching.ISwitchingListener() {
            @Override
            public void onSwitchingText(String value) {
                if (view != null) {
                    view.onSwitchingText(value);
                }
            }

            @Override
            public void onTemHum(int temperature, int humidity) {
                if (view != null) {
                    view.onTemHum(temperature, humidity);
                }
            }
        });
    }

    public void readHum() {
        switchingModule.onReadHum();
    }

    public void OutD8(boolean isOn) {
        switchingModule.onOutD8(isOn);
    }

    public void OutD9(boolean isOn) {
        switchingModule.onOutD9(isOn);
    }

    public void buzz(ISwitching.Hex hex) {
        switchingModule.onBuzz(hex);
    }

    public void buzzOff() {
        switchingModule.onBuzzOff();
    }

    public void doorOpen() {
        switchingModule.onDoorOpen();
    }

    public void greenLight() {
        switchingModule.onGreenLightBlink();
    }

    public void redLight() {
        switchingModule.onRedLightBlink();
    }

    public void WhiteLighrOn() {
        switchingModule.onWhiteLighrOn();
    }

    public void WhiteLighrOff() {
        switchingModule.onWhiteLighrOff();
    }

    public void Close(){
        switchingModule.onClose();
    }

    public void relay(ISwitching.Relay relay, ISwitching.Hex hex, boolean status) {
        switch (relay) {
            case relay_D5:
                switchingModule.onD5Relay(hex, status);
                break;
            case relay_D10:
                switchingModule.onD10Relay(hex, status);

                break;
            case relay_12V:
                switchingModule.on12VRelay(hex, status);
                break;
            case relay_relay:
                switchingModule.onRelay(hex, status);
                break;
            default:
                break;

        }
    }
}
