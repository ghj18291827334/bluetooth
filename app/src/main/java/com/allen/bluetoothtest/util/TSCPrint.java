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


        TscDll.setup(50, 25, 4, 15, 0, 3, 0);
        TscDll.clearbuffer();
        TscDll.sendcommand("SET TEAR ON\n");
        TscDll.sendcommand("SET COUNTER @1 1\n");
        /**
         * 打印二维码
         */
        //打印二维码没有出现，记得加\n
//        TscDll.sendcommand("QRCODE 17,70,M,5,A,0,M1,S2,\""+QRCODE+"\"\n");
        Typeface typeface=Typeface.createFromAsset(context.getAssets(),"font/stzhongs.ttf");
//        TscDll.windowsfont(142,60,0,22,typeface,""+ItemNo+"");
//        TscDll.windowsfont(142,100,0,22,typeface,""+goods+"");
//        TscDll.windowsfont(142,160,0,22,typeface,""+specifications+"");
//        TscDll.windowsfont(17,20,0,22,typeface,""+barcode+"");

        /**
         * 打印条码
         */
       TscDll.barcode(70, 20, "128", 50, 1, 0, 1, 8, QRCODE);
        TscDll.windowsfont(142,92,0,22,typeface,""+ItemNo+"");
        TscDll.windowsfont(17,122,0,22,typeface,""+goods+"");
        TscDll.windowsfont(17,152,0,22,typeface,""+specifications+"");

        TscDll.printlabel(1, 1);
        TscDll.closeport(5000);

    }
}
