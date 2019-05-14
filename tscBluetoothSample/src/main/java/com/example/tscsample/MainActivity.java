package com.example.tscsample;

import android.bluetooth.BluetoothAdapter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.example.tsc.tscdll_test.TSCActivity;

import java.io.UnsupportedEncodingException;


public class MainActivity extends Activity {

    TSCActivity TscDll = new TSCActivity();

    private Button test;
    BluetoothAdapter bluetoothAdapter;
    private String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        test = (Button) findViewById(R.id.button1);

        test.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {


                TscDll.openport("00:19:0E:A0:7A:F6");


                TscDll.setup(50, 25, 4, 12, 0, 3, 0);
                TscDll.clearbuffer();
                TscDll.sendcommand("SET TEAR ON\n");
                TscDll.sendcommand("SET COUNTER @1 1\n");
                //打印二维码没有出现，记得加\n
                TscDll.sendcommand("QRCODE 35,70,M,5,A,0,M1,S2,\"13640B J1338-864-20\"\n");


                Typeface typeface=Typeface.createFromAsset(getAssets(),"font/stzhongs.ttf");

                TscDll.windowsfont(160,60,0,20,typeface,"打印测试Zx123456");
                TscDll.windowsfont(160,90,0,25,typeface,"打印测试Zx123456");
                TscDll.windowsfont(160,120,0,25,typeface,"打印测试Zx123456");
                TscDll.windowsfont(160,150,0,25,typeface,"打印测试Zx123456");

                TscDll.printlabel(1, 1);
                TscDll.closeport(5000);

            }

        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }


}
