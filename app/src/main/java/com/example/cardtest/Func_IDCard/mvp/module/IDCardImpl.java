package com.example.cardtest.Func_IDCard.mvp.module;


import android.graphics.Bitmap;
import android.util.Log;

import com.example.cardtest.AppInit;
import com.example.drv.card.BoyaCardAdapter;
import com.example.drv.card.ICardInfo;
import com.example.drv.card.ICardState;
import com.example.drv.card.ReadCard2;
import com.example.log.Lg;

/**
 * Created by zbsz on 2017/6/4.
 */

public class IDCardImpl implements IIDCard {
    private static final String TAG = "信息提示";
    private int cdevfd = -1;
    private static ICardInfo cardInfo = null;
    IIdCardListener mylistener;

    @Override
    public void onOpen(IIdCardListener listener) {
        mylistener = listener;
        try {
            cardInfo = new ReadCard2(115200, AppInit.getInstrumentConfig().cardPort(), m_onCardState);
//            cardInfo = new CardInfo3("/dev/ttyS1", m_onCardState);
//            cardInfo.setDevType("rk3368");
            cdevfd = cardInfo.open();
            if (cdevfd >= 0) {
                Log.e(TAG, "打开身份证读卡器成功");
            } else {
                cdevfd = -1;
                Log.e(TAG, "打开身份证读卡器失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onReadCard() {
        cardInfo.readCard();
    }


    @Override
    public void onReadICCard() {
        cardInfo.ReadIC();
    }

    @Override
    public void onStopReadCard() {
        cardInfo.stopReadCard();
    }

    @Override
    public void onStopReadICCard() {
        cardInfo.stopReadIC();
    }

    @Override
    public void onReadSAM() {
        cardInfo.readSam();
    }

    private ICardState m_onCardState = new ICardState() {
        @Override
        public void onCardState(int itype, int value) {
            if (itype == 4 && value == 1) {
                mylistener.onSetInfo(cardInfo);
                Bitmap bmp = cardInfo.getBmp();
                if (bmp != null) {
                    mylistener.onSetImg(bmp);
                } else {
                    mylistener.onSetImg(null);
                    Lg.e("信息提示", "没有照片");
                }
                cardInfo.clearIsReadOk();
            } else if (itype == 20) {
                mylistener.onSetText("SAM:" + cardInfo.getSam());
            } else if (itype == 14) {
                mylistener.onSetICInfo(cardInfo);
            }

        }

    };

    @Override
    public void onClose() {
        cardInfo.close();
    }
}
