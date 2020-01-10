package com.example.cardtest;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.example.cardtest.Tool.AssetsUtils;
import com.example.cardtest.Tool.NetInfo;
import com.example.cardtest.Func_IDCard.mvp.presenter.IDCardPresenter;
import com.example.cardtest.Func_IDCard.mvp.view.IIDCardView;
import com.example.drv.card.ICardInfo;
import com.example.yfaceapi.GPIOManager;
import com.example.yfaceapi.GpioUtils;
import com.ys.rkapi.MyManager;

import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class YiShengMainActivity extends AppCompatActivity implements IIDCardView {
    IDCardPresenter mIDCardPresenter = IDCardPresenter.getInstance();

    public static final String STATICIP = "StaticIp";

    public static final String DHCP = "DHCP";

    public static final String ethernet = "ethernet";

    Button btn_getSam;
    Button btn_relay;
    Button btn_IR;
    Button btn_Voice;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ys_activity_main);
        btn_getSam = (Button) findViewById(R.id.btn_getSam);
        btn_getSam.setOnClickListener(mOnClickListener);
        btn_IR = (Button) findViewById(R.id.btn_IR);
        btn_IR.setOnClickListener(mOnClickListener);
        btn_relay = (Button) findViewById(R.id.btn_relay);
        btn_relay.setOnClickListener(mOnClickListener);
        btn_Voice = (Button) findViewById(R.id.btn_Voice);
        btn_Voice.setOnClickListener(mOnClickListener);
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

        headphoto = (ImageView) findViewById(R.id.iv_headphoto);
        mIDCardPresenter.idCardOpen();


        mMyManager = MyManager.getInstance(this);
        mMyManager.bindAIDLService(this);

        tv_mac.setText("WIFIMac:" + new NetInfo().getWifiMac() + "\n" +
                "ethMac" + new NetInfo().getMac());
        SwitchOperation();
    }


    private void SwitchOperation(){
        int index = 163;
        GpioUtils.upgradeRootPermissionForGpio(index);
        String status = GpioUtils.getGpioDirection(index);
        if ("".equals(status)){
            ToastUtils.showLong("无效的GPIO");
            return;
        }
        if(GpioUtils.setGpioDirection(index, 1)){
            new Thread(()->{
                while (true) {
                    try {
                        runOnUiThread(()->{
                            String text;
                            if (GpioUtils.getGpioValue(index).equals("0")){
                                text ="当前处于开门状态";
                            }else{
                                text ="当前处于关门状态";
                            }
                            runOnUiThread(()->tv_switch.setText(text));
                        });
                        Thread.sleep(500);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }

                }
            }).start();
        }else{
            ToastUtils.showLong("无法设置该IO口为输入口");
            return;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        mIDCardPresenter.readCard();
        mIDCardPresenter.ReadIC();
        mIDCardPresenter.IDCardPresenterSetView(this);
    }


    boolean status_12V, status_relay, status_D10, status_D5, eth0, green,red,light = false;
    View.OnClickListener mOnClickListener = v -> {
        switch (v.getId()) {
            case R.id.btn_getSam:
                mIDCardPresenter.readSam();
                break;
            case R.id.btn_D10:
                status_D10 = !status_D10;

                if(status_D10){
                    GPIOManager.getInstance(YiShengMainActivity.this).pullUpInfraredLed();
                }else {
                    GPIOManager.getInstance(YiShengMainActivity.this).pullDownInfraredLed();

                }
                break;
            case R.id.btn_D5:
                status_D5 = !status_D5;
                if(status_D5){
                    GPIOManager.getInstance(YiShengMainActivity.this).pullUpVoice();
                }else {
                    GPIOManager.getInstance(YiShengMainActivity.this).pullDownVoice();

                }

                break;
            case R.id.btn_12V:
                status_12V = !status_12V;
                if(status_12V){
                    GPIOManager.getInstance(YiShengMainActivity.this).pullUpRelay();
                }else {
                    GPIOManager.getInstance(YiShengMainActivity.this).pullDownRelay();
                }                break;
            case R.id.btn_reboot:
                mMyManager.reboot();
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
                green = !green;
                if (green){
                    GPIOManager.getInstance(YiShengMainActivity.this).pullUpGreenLight();
                }else {
                    GPIOManager.getInstance(YiShengMainActivity.this).pullDownGreenLight();
                }
                break;
            case R.id.btn_redlight:
                red = !red;
                if (red){
                    GPIOManager.getInstance(YiShengMainActivity.this).pullUpRedLight();
                }else {
                    GPIOManager.getInstance(YiShengMainActivity.this).pullDownRedLight();
                }
                break;
            case R.id.btn_light:
                light = !light;
                if (light) {
                    GPIOManager.getInstance(YiShengMainActivity.this).pullUpWhiteLight();

                } else {
                    GPIOManager.getInstance(YiShengMainActivity.this).pullDownWhiteLight();
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
