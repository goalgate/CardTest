package com.example.cardtest;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.cardtest.Func_IDCard.mvp.presenter.IDCardPresenter;
import com.example.cardtest.Func_IDCard.mvp.view.IIDCardView;
import com.example.cardtest.Switch.mvp.module.SwitchImpl;
import com.example.cardtest.Switch.mvp.presenter.SwitchPresenter;
import com.example.cardtest.Switch.mvp.view.ISwitchView;
import com.example.drv.card.ICardInfo;
import com.example.log.Lg;
import com.ys.myapi.MyManager;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class MainActivity extends AppCompatActivity implements IIDCardView, ISwitchView {
    IDCardPresenter mIDCardPresenter = IDCardPresenter.getInstance();

    SwitchPresenter mSwitchPresenter = SwitchPresenter.getInstance();

    Button btn_getSam;
    Button btn_12V;
    Button btn_D10;
    Button btn_D5;
    Button btn_relay;

    MyManager mMyManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_getSam = (Button) findViewById(R.id.btn_getSam);
        btn_getSam.setOnClickListener(mOnClickListener);
        btn_D10 = (Button) findViewById(R.id.btn_D10);
        btn_D10.setOnClickListener(mOnClickListener);
        btn_12V = (Button) findViewById(R.id.btn_12V);
        btn_12V.setOnClickListener(mOnClickListener);
        btn_D5 = (Button) findViewById(R.id.btn_D5);
        btn_D5.setOnClickListener(mOnClickListener);
        btn_relay = (Button) findViewById(R.id.btn_relay);
        btn_relay.setOnClickListener(mOnClickListener);
        mIDCardPresenter.idCardOpen();
        mSwitchPresenter.switch_Open();
        mMyManager =MyManager.getInstance(this);
        mMyManager.bindAIDLService(this);
        Observable.interval(0, 5, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((l)->mSwitchPresenter.readHum());
//                .subscribe(new Consumer<Long>() {
//                    @Override
//                    public void accept(@NonNull Long aLong) throws Exception {
//                        mSwitchPresenter.readHum();
//                    }
//                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIDCardPresenter.readCard();
        mIDCardPresenter.ReadIC();
        mIDCardPresenter.IDCardPresenterSetView(this);
        mSwitchPresenter.SwitchPresenterSetView(this);
    }


    boolean status_12V, status_relay, status_D10, status_D5 = false;
    View.OnClickListener mOnClickListener = v -> {
        switch (v.getId()) {
            case R.id.btn_getSam:
//                Intent intent = new Intent("com.xs.reboot");
//                this.sendBroadcast(intent);
//                status_D10 =!status_D10;
//                mMyManager.ethEnabled(status_D10;
                //                mMyManager.setStaticEthIPAddress("192.168.1.122","192.168.1.1","255.255.255.0","192.168.1.1","192.168.1.1");
                break;
            case R.id.btn_D10:
                status_D10 =!status_D10;
                mSwitchPresenter.relay(SwitchImpl.Relay.relay_D10, SwitchImpl.Hex.H0, status_D10);
                break;
            case R.id.btn_D5:
                status_D5 =!status_D5;
                mSwitchPresenter.relay(SwitchImpl.Relay.relay_D5, SwitchImpl.Hex.H0, status_D5);

                //                if(status_D5){
//                    mSwitchPresenter.relay(SwitchImpl.Relay.relay_D5, SwitchImpl.Hex.H0, true);
//                }else {
//                    mSwitchPresenter.relay(SwitchImpl.Relay.relay_D5, SwitchImpl.Hex.H0, false);
//                    Observable.timer(120, TimeUnit.SECONDS)
//                            .observeOn(AndroidSchedulers.mainThread())
//                            .subscribe((l)->{
//                                mSwitchPresenter.relay(SwitchImpl.Relay.relay_D5, SwitchImpl.Hex.H0, true);
//                            });
//                }

                break;
            case R.id.btn_relay:
                status_relay =!status_relay;

                mSwitchPresenter.relay(SwitchImpl.Relay.relay_relay, SwitchImpl.Hex.H0, status_relay);
                break;
            case R.id.btn_12V:
                status_12V =!status_12V;
                mSwitchPresenter.relay(SwitchImpl.Relay.relay_12V, SwitchImpl.Hex.H0, status_12V);
                break;
            default:
                break;


        }

    };

    @Override
    protected void onPause() {
        super.onPause();
        mIDCardPresenter.stopReadCard();
        mIDCardPresenter.StopReadIC();
    }


    @Override
    public void onsetCardImg(Bitmap bmp) {
        Lg.e("uid", "sddssd");

    }

    @Override
    public void onsetCardInfo(ICardInfo cardInfo) {

    }

    @Override
    public void onsetICCardInfo(ICardInfo cardInfo) {
        Lg.e("uid", cardInfo.getUid());

    }

    @Override
    public void onSetText(String Msg) {
        Toast.makeText(this, Msg, Toast.LENGTH_LONG).show();

    }

    @Override
    public void onSwitchingText(String value) {
        Lg.e("switchValue", value);
    }

    @Override
    public void onTemHum(int temperature, int humidity) {
        Lg.e("switchValue_temperature", String.valueOf(temperature));
        Lg.e("switchValue_humidity", String.valueOf(humidity));


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIDCardPresenter.idCardClose();
        mSwitchPresenter.Close();
    }
}
