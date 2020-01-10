package com.example.cardtest.Switch.mvp.module;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.log.Lg;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;

import android_serialport_api.SerialPort;

/**
 * 当前类注释:
 *
 * @author wzw
 * @date 2019/8/13 09:28
 */
public class MaiChongSwitchImpl implements ISwitching {


    private byte[] dt_12VRelayOn = {(byte) 0xEA, (byte) 0x01, (byte) 0x03};

    private byte[] dt_12VRelayOff = {(byte) 0xEA, (byte) 0x01, (byte) 0x04};

    private byte[] dt_greenLightBlink = {0x02, 0x02, (byte) 0x0B, 0x00, (byte) 0xA2, 0x00, 0x02, 0x02, 0x03, 0x00,
            (byte) 0x0B, 0x01, 0x01, (byte) 0xD1, (byte) 0x92, (byte) 0x93, 0x63};
    private byte[] dt_redLightBlink = {0x02, 0x02, (byte) 0x0B, 0x00, (byte) 0xA2, 0x00, 0x02, 0x02, 0x03, 0x00,
            (byte) 0x0B, 0x02, 0x02, (byte) 0x91, (byte) 0x63, (byte) 0x93, 0x63};
    private byte[] dt_whiteLightOn = {0x02, 0x02, (byte) 0x0B, 0x00, (byte) 0xA2, 0x00, 0x02, 0x02, 0x03, 0x00, 0x0A,
            0x01, 0x01, (byte) 0x80, 0x52, (byte) 0x93, 0x63};
    private byte[] dt_whiteLightOff = {0x02, 0x02, (byte) 0x0B, 0x00, (byte) 0xA2, 0x00, 0x02, 0x02, 0x03, 0x00, 0x0A
            , 0x01, 0x00, (byte) 0x41, (byte) 0x92, (byte) 0x93, 0x63};

    ISwitchingListener listener;

    private int light_devfd = -1;

    private int switch_devfd = -1;

    private SerialPort light_port;

    private SerialPort switch_port;

    private InputStream light_InputStream;

    private OutputStream light_OutputStream;

    private InputStream switch_InputStream;

    private OutputStream switch_OutputStream;

    private ReadThread mReadThread;

    private int temperature = 0;  //温度

    private int humidity = 0;   //湿度

    boolean thread_continuous = false;

    @Override
    public void onOpen(ISwitchingListener listener) {
        this.listener = listener;
        switch_devOpen(115200, "/dev/ttyS0");
        light_devOpen(115200, "/dev/ttyS1");

    }

    private int light_devOpen(int sp, String devName_) {
        try {
            light_port = new SerialPort(new File(devName_), sp, 0);
            light_InputStream = light_port.getInputStream();
            light_OutputStream = light_port.getOutputStream();
            Lg.e("light_dev", "open  SerialPort ok");
            light_devfd = 1;
        } catch (Exception e) {
            Lg.e("light_dev", e.toString());
        }
        //        Lg.e("SDs", CRC16.getCRC3(dt_Success2,((dt_Success2[1]<<8)+dt_Success2[0])+2));
        return light_devfd;
    }

    private int switch_devOpen(int sp, String devName_) {
        try {
            switch_port = new SerialPort(new File(devName_), sp, 0);
            switch_InputStream = switch_port.getInputStream();
            switch_OutputStream = switch_port.getOutputStream();
            Lg.e("switch_dev", "open  SerialPort ok");
            switch_devfd = 1;
        } catch (Exception e) {
            Lg.e("switch_dev", e.toString());
        }
        if (mReadThread == null) {
            mReadThread = new ReadThread();
            thread_continuous = true;
            mReadThread.start();
        }
        return switch_devfd;
    }

    private byte[] readerbuffer = new byte[20];
    byte[] by_copy;
    String testStrTemp;

    class ReadThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (thread_continuous) {
                try {
                    sleep(100);

                    int size = switch_InputStream.read(readerbuffer);

                    by_copy = new byte[size];

                    System.arraycopy(readerbuffer, 0, by_copy, 0, size);

                    testStrTemp = "";

                    for (int i = 0; i < size; i++) {
                        testStrTemp += byteToHex(by_copy[i]);
                    }

                    if (testStrTemp.startsWith("EA03")) {
                        mhandler.sendEmptyMessage(0x123);
                    } else if (testStrTemp.startsWith("EA04")) {
                        int hu_high = Integer.parseInt(testStrTemp.substring(4, 6), 16);
                        int hu_low = Integer.parseInt(testStrTemp.substring(6, 8), 16);
                        int te_high = Integer.parseInt(testStrTemp.substring(8, 10), 16);
                        int te_low = Integer.parseInt(testStrTemp.substring(10, 12), 16);
                        int check = Integer.parseInt(testStrTemp.substring(12, 14), 16);
                        if (hu_high + hu_low + te_high + te_low == check) {
                            humidity = Integer.parseInt(testStrTemp.substring(4, 8), 16) / 10;
                            temperature = Integer.parseInt(testStrTemp.substring(8, 12), 16) / 10;
                            mhandler.sendEmptyMessage(0x234);
                        }

                    }
                } catch (Exception e) {
                    Lg.e("switch_dev", e.toString());
                }
            }

        }
    }

    @Override
    public void onReadHum() {

    }

    @Override
    public void onOutD8(boolean status) {

    }

    @Override
    public void onOutD9(boolean status) {

    }

    @Override
    public void onBuzz(Hex hex) {

    }

    @Override
    public void onBuzzOff() {

    }

    @Override
    public void on12VRelay(Hex hex, boolean status) {
        if (status) {
            sendData(dt_12VRelayOn);
        } else {
            sendData(dt_12VRelayOff);

        }
    }

    @Override
    public void onRelay(Hex hex, boolean status) {

    }

    @Override
    public void onD10Relay(Hex hex, boolean status) {

    }

    @Override
    public void onD5Relay(Hex hex, boolean status) {

    }

    @Override
    public void onDoorOpen() {

    }

    @Override
    public void onRedLightBlink() {
        onWhiteLighrOff();
        try {
            light_OutputStream.write(dt_redLightBlink);
        } catch (Exception ex) {
            Lg.e("M121_sendData", ex.toString());
        }
    }

    @Override
    public void onGreenLightBlink() {
        onWhiteLighrOff();
        try {
            light_OutputStream.write(dt_greenLightBlink);
        } catch (Exception ex) {
            Lg.e("M121_sendData", ex.toString());
        }
    }

    @Override
    public void onWhiteLighrOn() {
        try {
            light_OutputStream.write(dt_whiteLightOn);
        } catch (Exception ex) {
            Lg.e("M121_sendData", ex.toString());
        }
    }

    @Override
    public void onWhiteLighrOff() {
        try {
            light_OutputStream.write(dt_whiteLightOff);
        } catch (Exception ex) {
            Lg.e("M121_sendData", ex.toString());
        }
    }

    @Override
    public void onClose() {
        thread_continuous = false;
    }

    private void sendData(byte[] bs) {
        try {
            switch_OutputStream.write(bs);
        } catch (Exception ex) {
            Lg.e("M121_sendData", ex.toString());
        }
    }

    Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x123) {
                listener.onSwitchingText(testStrTemp);
            } else if (msg.what == 0x234) {
                listener.onTemHum(temperature, humidity);
            }
        }
    };

    public String byteToHex(byte b) {
        String s = "";
        s = Integer.toHexString(0xFF & b).trim();
        if (s.length() < 2) {
            s = "0" + s;
        }

        return s.toUpperCase();
    }

}
