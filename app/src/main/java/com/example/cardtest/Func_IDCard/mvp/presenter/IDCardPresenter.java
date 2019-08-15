package com.example.cardtest.Func_IDCard.mvp.presenter;

import android.graphics.Bitmap;

import com.example.cardtest.Func_IDCard.mvp.module.IDCardImpl;
import com.example.cardtest.Func_IDCard.mvp.module.IIDCard;
import com.example.cardtest.Func_IDCard.mvp.view.IIDCardView;
import com.example.drv.card.ICardInfo;


/**
 * Created by zbsz on 2017/6/9.
 */

public class IDCardPresenter {
    private IIDCardView view;

    private static IDCardPresenter instance = null;

    private IDCardPresenter() {
    }

    public static IDCardPresenter getInstance() {
        if (instance == null)
            instance = new IDCardPresenter();
        return instance;
    }

    public void IDCardPresenterSetView(IIDCardView view) {
        this.view = view;
    }

    IIDCard idCardModule = new IDCardImpl();

    public void idCardOpen() {
        idCardModule.onOpen(new IIDCard.IIdCardListener() {
            @Override
            public void onSetImg(Bitmap bmp) {
                view.onsetCardImg(bmp);
            }

            @Override
            public void onSetInfo(ICardInfo cardInfo) {
                view.onsetCardInfo(cardInfo);
            }

            @Override
            public void onSetICInfo(ICardInfo cardInfo) {
                view.onsetICCardInfo(cardInfo);
            }

            @Override
            public void onSetText(String Msg) {
                view.onSetText(Msg);
            }
        });
    }

    public void readCard() {
        idCardModule.onReadCard();
    }

    public void stopReadCard() {
        idCardModule.onStopReadCard();
    }

    public void idCardClose() {
        idCardModule.onClose();
    }

    public void readSam() {
        idCardModule.onReadSAM();
    }

    public void StopReadIC() {
        idCardModule.onStopReadICCard();
    }

    public void ReadIC() {
        idCardModule.onReadICCard();
    }
}
