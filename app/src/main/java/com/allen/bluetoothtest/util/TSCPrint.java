package com.allen.bluetoothtest.util;

import android.content.Context;
import android.graphics.Typeface;

import com.example.tsc.tscdll_test.TSCActivity;

public class TSCPrint {
    TSCActivity TscDll = new TSCActivity();
    Context context;

    public TSCPrint(Context context) {
        this.context = context;
    }

    /**
     * 打印方法
     * @param address 蓝牙地址
     * @param QRCODE  二维码
     * @param ItemNo  品号
     * @param goods   品名
     * @param specifications 规格
     * @param barcode 条码
     */
    public void printData(String address,String QRCODE,String ItemNo,String goods,String specifications,String barcode){
//        TscDll.openport("00:19:0E:A0:7A:F6");
        TscDll.openport(address);


        TscDll.setup(50, 25, 4, 12, 0, 3, 0);
        TscDll.clearbuffer();
        TscDll.sendcommand("SET TEAR ON\n");
        TscDll.sendcommand("SET COUNTER @1 1\n");
        //打印二维码没有出现，记得加\n
        TscDll.sendcommand("QRCODE 35,70,M,5,A,0,M1,S2,\""+QRCODE+"\"\n");
        Typeface typeface=Typeface.createFromAsset(context.getAssets(),"font/stzhongs.ttf");
        TscDll.windowsfont(160,60,0,25,typeface,"品号:"+ItemNo+"");
        TscDll.windowsfont(160,90,0,25,typeface,"品名:"+goods+"");
        TscDll.windowsfont(160,120,0,25,typeface,"规格:"+specifications+"");
        TscDll.windowsfont(160,150,0,25,typeface,"条码:"+barcode+"");

        TscDll.printlabel(1, 1);
        TscDll.closeport(5000);

    }
}
