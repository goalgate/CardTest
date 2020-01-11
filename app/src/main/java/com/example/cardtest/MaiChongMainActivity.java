package com.example.cardtest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.SPUtils;
import com.example.cardtest.Func_IDCard.mvp.presenter.IDCardPresenter;
import com.example.cardtest.Func_IDCard.mvp.view.IIDCardView;
import com.example.cardtest.Switch.mvp.module.ISwitching;
import com.example.cardtest.Switch.mvp.presenter.SwitchPresenter;
import com.example.cardtest.Switch.mvp.view.ISwitchView;
import com.example.cardtest.Tool.AssetsUtils;
import com.example.cardtest.Tool.NetInfo;
import com.example.drv.card.ICardInfo;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class MaiChongMainActivity extends AppCompatActivity implements IIDCardView , ISwitchView {



    IDCardPresenter mIDCardPresenter = IDCardPresenter.getInstance();

    SwitchPresenter mSwitchPresenter = SwitchPresenter.getInstance();

    public static final String STATICIP = "StaticIp";

    public static final String DHCP = "DHCP";

    public static final String ethernet = "ethernet";



    TextView tv_info;
    TextView tv_switch;
    TextView tv_mac;
    ImageView headphoto;
    TextView tv_hute;
    Button btn_getSam;
    Button btn_12VRelay;
    Button btn_reboot;
    Button btn_greenlight;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mc_activity_main);

        tv_info = (TextView) findViewById(R.id.tv_info);
        tv_switch = (TextView) findViewById(R.id.tv_switch);
        tv_mac = (TextView) findViewById(R.id.tv_mac);
        tv_hute = (TextView) findViewById(R.id.tv_hute);
        headphoto = (ImageView) findViewById(R.id.iv_headphoto);
        btn_getSam = (Button) findViewById(R.id.btn_getSam) ;
        btn_getSam.setOnClickListener(mOnClickListener);

        btn_12VRelay = (Button)findViewById(R.id.btn_12V) ;
        btn_12VRelay.setOnClickListener(mOnClickListener);

        btn_reboot =(Button) findViewById(R.id.btn_reboot);
        btn_reboot.setOnClickListener(mOnClickListener);
        btn_greenlight = (Button) findViewById(R.id.btn_greenlight);
        btn_greenlight.setOnClickListener(mOnClickListener);

        mIDCardPresenter.idCardOpen();
        mSwitchPresenter.switch_Open();
        tv_mac.setText("WIFIMac:" + new NetInfo().getWifiMac() + "\n" +
                "ethMac" + new NetInfo().getMac());
    }


    @Override
    protected void onResume() {
        super.onResume();
        mIDCardPresenter.readCard();
        mIDCardPresenter.ReadIC();
        mIDCardPresenter.IDCardPresenterSetView(this);
        mSwitchPresenter.SwitchPresenterSetView(this);
    }



    @Override
    protected void onPause() {
        super.onPause();
        mIDCardPresenter.stopReadCard();
        mIDCardPresenter.StopReadIC();
        mIDCardPresenter.IDCardPresenterSetView(null);
        mSwitchPresenter.SwitchPresenterSetView(null);

    }

    static int Success_IDCount = 0;

    static int Failed_IDCount = 0;

    @Override
    public void onsetCardImg(Bitmap bmp) {
        if (bmp == null) {
            tv_info.setText("警告，没有身份证照片，可能获取身份证延时不足或者没有wltlib文件夹");
            Failed_IDCount++;
        } else {
            Success_IDCount++;
            headphoto.setImageBitmap(bmp);
            headphoto.setVisibility(View.VISIBLE);
            Observable.timer(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                    .subscribe((l) -> headphoto.setVisibility(View.GONE));
        }
        tv_info.setText("成功刷卡: " + Success_IDCount + " 次；失败刷卡: " + Failed_IDCount + " 次");

    }

    @Override
    public void onsetCardInfo(ICardInfo cardInfo) {
        Toast.makeText(this, cardInfo.name(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onsetICCardInfo(ICardInfo cardInfo) {
        Toast.makeText(this, cardInfo.getUid(), Toast.LENGTH_LONG).show();

    }

    @Override
    public void onSetText(String Msg) {
        Toast.makeText(this, Msg, Toast.LENGTH_LONG).show();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIDCardPresenter.idCardClose();
        mSwitchPresenter.Close();

    }

    @Override
    public void onSwitchingText(String value) {
        if(value.endsWith("00")){
            tv_switch.setText("设备处于开门状态");
        }else{
            tv_switch.setText("设备处于关门状态");
        }

    }

    @Override
    public void onTemHum(int temperature, int humidity) {
        tv_hute.setText("温度："+temperature+"，湿度："+humidity);
    }

    boolean status_12V = false;
    View.OnClickListener mOnClickListener = v -> {
        switch (v.getId()) {
            case R.id.btn_getSam:
                mIDCardPresenter.readSam();
                break;

            case R.id.btn_12V:
                status_12V = !status_12V;
                mSwitchPresenter.relay(ISwitching.Relay.relay_12V, ISwitching.Hex.H0,status_12V);
                break;
            case R.id.btn_reboot:
                Intent intent = new Intent();
                intent.setAction("android.intent.action.MCREBOOT");
                sendBroadcast(intent);

                break;
            case R.id.btn_greenlight:
                mSwitchPresenter.greenLight();
                break;
            default:
                break;
        }

    };

}
