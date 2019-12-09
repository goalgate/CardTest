package com.example.cardtest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.example.Tool.AssetsUtils;
import com.example.Tool.NetInfo;
import com.example.cardtest.Func_IDCard.mvp.presenter.IDCardPresenter;
import com.example.cardtest.Func_IDCard.mvp.view.IIDCardView;
import com.example.cardtest.Switch.mvp.module.SwitchImpl;
import com.example.cardtest.Switch.mvp.presenter.SwitchPresenter;
import com.example.cardtest.Switch.mvp.view.ISwitchView;
import com.example.drv.card.ICardInfo;
import com.example.log.Lg;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.ys.myapi.MyManager;

import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity2 extends AppCompatActivity implements IIDCardView, ISwitchView {
    IDCardPresenter mIDCardPresenter = IDCardPresenter.getInstance();

    SwitchPresenter mSwitchPresenter = SwitchPresenter.getInstance();


    public static final String STATICIP = "StaticIp";

    public static final String DHCP = "DHCP";

    public static final String ethernet = "ethernet";

    private SPUtils config = SPUtils.getInstance("config");


    Button btn_getSam;
    Button btn_12V;
    Button btn_D10;
    Button btn_D5;
    Button btn_relay;
    Button btn_reboot;
    Button btn_static;
    Button btn_dhcp;
    Button btn_eth0;
    Button btn_greenLight;
    Button btn_redLight;
    Button btn_light;
    TextView tv_info;
    TextView tv_switch;
    TextView tv_mac;
    ImageView headphoto;
    MyManager mMyManager;

    SurfaceView mSurfaceView1;
    SurfaceView mSurfaceView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);
        if (config.getBoolean("firstStart", true)) {
            AssetsUtils.getInstance(AppInit.getContext()).copyAssetsToSD("wltlib", "wltlib");
            config.put("firstStart", false);
        }


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
        btn_reboot = (Button) findViewById(R.id.btn_reboot);
        btn_reboot.setOnClickListener(mOnClickListener);
        btn_static = (Button) findViewById(R.id.btn_static);
        btn_static.setOnClickListener(mOnClickListener);
        btn_dhcp = (Button) findViewById(R.id.btn_dhcp);
        btn_dhcp.setOnClickListener(mOnClickListener);
        btn_eth0 = (Button) findViewById(R.id.btn_eth0);
        btn_eth0.setOnClickListener(mOnClickListener);
        btn_greenLight = (Button) findViewById(R.id.btn_greenlight);
        btn_greenLight.setOnClickListener(mOnClickListener);
        btn_redLight = (Button) findViewById(R.id.btn_redlight);
        btn_redLight.setOnClickListener(mOnClickListener);
        btn_light = (Button) findViewById(R.id.btn_light);
        btn_light.setOnClickListener(mOnClickListener);
        tv_info = (TextView) findViewById(R.id.tv_info);
        tv_switch = (TextView) findViewById(R.id.tv_switch);
        tv_mac = (TextView) findViewById(R.id.tv_mac);
        mSurfaceView1 = (SurfaceView) findViewById(R.id.surface1);
        mSurfaceView2 = (SurfaceView) findViewById(R.id.surface2);

        headphoto = (ImageView) findViewById(R.id.iv_headphoto);
        mIDCardPresenter.idCardOpen();
        mSwitchPresenter.switch_Open();


        mMyManager = MyManager.getInstance(this);
        mMyManager.bindAIDLService(this);
        Observable.interval(0, 1800, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((l) -> mSwitchPresenter.readHum());

        tv_mac.setText("WIFIMac:" + new NetInfo().getWifiMac() + "\n" +
                "ethMac" + new NetInfo().getMac());


        CircleOperation();
    }

    private void CircleOperation() {
        Observable.interval(0, 5, TimeUnit.MINUTES).
                subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        mSwitchPresenter.relay(SwitchImpl.Relay.relay_12V, SwitchImpl.Hex.H0, true);
                        Thread.sleep(1000);
                        mSwitchPresenter.relay(SwitchImpl.Relay.relay_12V, SwitchImpl.Hex.H0, false);
                        Thread.sleep(1000);
                        mSwitchPresenter.greenLight();
                        Thread.sleep(1000);
                        mSwitchPresenter.redLight();
                        Thread.sleep(1000);
                        mSwitchPresenter.WhiteLighrOn();
                        Thread.sleep(1000);
                        mSwitchPresenter.WhiteLighrOff();
                        Thread.sleep(1000);
                        mIDCardPresenter.readCard();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIDCardPresenter.readCard();
        mIDCardPresenter.ReadIC();
        mIDCardPresenter.IDCardPresenterSetView(this);
        mSwitchPresenter.SwitchPresenterSetView(this);
    }


    boolean status_12V, status_relay, status_D10, status_D5, eth0, light = false;
    View.OnClickListener mOnClickListener = v -> {
        switch (v.getId()) {
            case R.id.btn_getSam:
                mIDCardPresenter.readSam();
                break;
            case R.id.btn_D10:
                status_D10 = !status_D10;
                mSwitchPresenter.relay(SwitchImpl.Relay.relay_D10, SwitchImpl.Hex.H0, status_D10);
                break;
            case R.id.btn_D5:
                status_D5 = !status_D5;
                mSwitchPresenter.relay(SwitchImpl.Relay.relay_D5, SwitchImpl.Hex.H0, status_D5);
                break;
            case R.id.btn_relay:
                status_relay = !status_relay;
                mSwitchPresenter.relay(SwitchImpl.Relay.relay_relay, SwitchImpl.Hex.H0, status_relay);
                break;
            case R.id.btn_12V:
                status_12V = !status_12V;
                mSwitchPresenter.relay(SwitchImpl.Relay.relay_12V, SwitchImpl.Hex.H0, status_12V);
                break;
            case R.id.btn_reboot:
                Intent intent = new Intent("com.xs.reboot");
                this.sendBroadcast(intent);
                break;
            case R.id.btn_static:
                mMyManager.setStaticEthIPAddress("192.168.1.122", "255.255.255.0", "192.168.1.1", "192.168.1.1", "192" +
                        ".168.1.1");
                Observable.timer(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                        .subscribe((l) -> {
                            if (NetworkUtils.getIPAddress(true) != null) {
                                if (NetworkUtils.getIPAddress(true).endsWith("122")) {
                                    ToastUtils.showLong("IP地址为" + NetworkUtils.getIPAddress(true) + ",正确的期望静态IP地址,静态IP设置成功");
                                    writeFileSdcard(ethernet, STATICIP);
                                } else {
                                    ToastUtils.showLong("IP地址为" + NetworkUtils.getIPAddress(true) + ",错误的期望静态IP地址,正在前往网络设置确认");
                                    NetworkUtils.openWirelessSettings();
                                }
                            } else {
                                ToastUtils.showLong("已尝试设置静态IP为192.168.1.122,正在前往网络设置确认");
                                NetworkUtils.openWirelessSettings();
                            }
                        });
                break;
            case R.id.btn_dhcp:
                mMyManager.setDhcpIpAddress(this);
                Observable.timer(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                        .subscribe((l) -> {
                            if (NetworkUtils.getIPAddress(true) != null) {
                                if (!NetworkUtils.getIPAddress(true).endsWith("122")) {
                                    ToastUtils.showLong("IP地址为" + NetworkUtils.getIPAddress(true) + ",与静态设置的122不同,动态IP设置成功");
                                    writeFileSdcard(ethernet, DHCP);
                                } else {
                                    ToastUtils.showLong("IP地址为" + NetworkUtils.getIPAddress(true) + ",与静态设置的122相同,正在前往网络设置确认");
                                    NetworkUtils.openWirelessSettings();
                                }
                            } else {
                                ToastUtils.showLong("已尝试设置动态IP模式,正在前往网络设置确认");
                                NetworkUtils.openWirelessSettings();
                            }
                        });

                break;
            case R.id.btn_eth0:
                if (eth0) {
                    ToastUtils.showLong("网口已开启，请连接网线查看图标");
                } else {
                    ToastUtils.showLong("网口已被关闭，请连接网线查看图标");
                }
                mMyManager.ethEnabled(eth0);
                eth0 = !eth0;
                break;
            case R.id.btn_greenlight:
                mSwitchPresenter.greenLight();
                break;
            case R.id.btn_redlight:
                mSwitchPresenter.redLight();
                break;
            case R.id.btn_light:
                light = !light;
                if (light) {
                    mSwitchPresenter.WhiteLighrOn();
                } else {
                    mSwitchPresenter.WhiteLighrOff();
                }
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
        tv_info.setText("成功刷卡: " +Success_IDCount+" 次；失败刷卡: "+ Failed_IDCount+" 次");

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
    public void onSwitchingText(String value) {
        tv_switch.setText(value);
    }

    @Override
    public void onTemHum(int temperature, int humidity) {
        Lg.e("sw_temperature", String.valueOf(temperature));
        Lg.e("sw_humidity", String.valueOf(humidity));


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIDCardPresenter.idCardClose();
        mSwitchPresenter.Close();
        mMyManager.unBindAIDLService(this);

    }

    public static void writeFileSdcard(String fileName, String message) {
        String file_pre = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
        try {
            FileOutputStream fout = new FileOutputStream(file_pre + fileName);
            byte[] bytes = message.getBytes();
            fout.write(bytes);
            fout.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
