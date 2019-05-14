package com.allen.bluetoothtest;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

/**
 * Created by zhanglong on 2018/6/19.
 */

public class BluetoothPrinterManage {
    private static BluetoothPrinterManage instance = new BluetoothPrinterManage();
    ExecutorService executorService = Executors.newCachedThreadPool();
    private MBluetoothManage bluetoothManage;

    private BluetoothPrinterManage() {

    }

    public static BluetoothPrinterManage getInstance() {
        return instance;
    }

    public static String string2HexString(String strPart) {
        byte[] b;
        StringBuffer result = new StringBuffer();
        String hex;
        try {

            b = strPart.getBytes("GB18030"); //GB18030
            for (int i = 0; i < b.length; i++) {
                hex = Integer.toHexString(b[i] & 0xFF);
                if (hex.length() == 1) {
                    hex = '0' + hex;
                }
                result.append(hex.toUpperCase());
            }
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return result.toString();
        }
        return result.toString();
    }

    public void setBluetoothManage(MBluetoothManage bluetoothManage) {
        this.bluetoothManage = bluetoothManage;
    }

    public Future<?> codePrint(final String CommandCode) {

        Runnable thread = new Runnable() {
            public void run() {
                try {
                    String PrintCode = CommandCode;
                    Log.d("ligao", "CodePrint CommandCode:" + PrintCode);
                    bluetoothManage.connect();
                    OutputStream outputStream = bluetoothManage.getBluetoothSocket().getOutputStream();
                    PrintCode = PrintCode.replace('<', '\u001b'); //< 替换成 ESC
                    PrintCode = PrintCode.replace(">", "");
                    Log.d("ligao", "CodePrint CommandCode2:" + PrintCode);
                    byte[] b = PrintCode.getBytes("GB18030"); //GB18030
                    BufferedSink buffer = Okio.buffer(Okio.sink(outputStream));
                    buffer.write(b);
                    buffer.flush();
                    buffer.close();
                } catch (final Exception e) {
                    e.printStackTrace();
                    Log.i("zl", "打印异常" + Log.getStackTraceString(e));
                } finally {
                    bluetoothManage.close();
                }

            }
        };


        Future<?> submit = executorService.submit(thread);
        return submit;
    }

    public void acceptMessage() {
        Runnable server = new Runnable() {

            @Override
            public void run() {
                while (true) {
                    try {
                        BluetoothSocket accept = MBluetoothManage.getInstance().getBluetoothServerSocket().accept();
                        InputStream inputStream = accept.getInputStream();
                        BufferedSource buffer = Okio.buffer(Okio.source(inputStream));
                        String s = buffer.readUtf8();
                        Log.i("zl", "收到消息:" + s);
                        accept.close();
                    } catch (IOException e) {
                        Log.i("zl", "acceptMessage===" + Log.getStackTraceString(e));
                        break;
                    }

                }
            }
        };
        executorService.submit(server);
    }

}
