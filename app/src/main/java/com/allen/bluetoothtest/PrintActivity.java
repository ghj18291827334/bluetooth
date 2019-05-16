package com.allen.bluetoothtest;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.allen.bluetoothtest.util.BufferAppend;
import com.allen.bluetoothtest.util.SPUtils;
import com.allen.bluetoothtest.util.TSCPrint;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by zhanglong on 2018/6/19.
 */

public class PrintActivity extends AppCompatActivity {

    private EditText et_qrcode, et_ItemNo, et_goods, et_specifications, et_barcode;
    TSCPrint tscPrint = new TSCPrint(this);
    private String address;
    private BufferAppend bufferAppend;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.printactivity);
        et_qrcode = findViewById(R.id.et_qrcode);
        et_ItemNo = findViewById(R.id.et_ItemNo);
        et_goods = findViewById(R.id.et_goods);
        et_specifications = findViewById(R.id.et_specifications);
        et_barcode = findViewById(R.id.et_barcode);
        address = SPUtils.get(this, "address", "").toString();

        bufferAppend = new BufferAppend(this);




//        BluetoothPrinterManage.getInstance().acceptMessage();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.printactivity_print:
                if (!address.equals("")) {
                    String info = bufferAppend.StringBufferAppend(et_goods.getText().toString(), 17);
                    if (et_qrcode.getText().toString().equals("") || et_ItemNo.getText().toString().equals("") || et_goods.getText().toString().equals("") || et_specifications.getText().toString().equals("") || et_barcode.getText().toString().equals("")) {
                        tscPrint.printData(address, "13640B J1338-864-20", "A00000054", "13640B J1338-864-20(扭力笔)", "CRH3_350 G22NiMoCr5-6", "13640B J1338-864-20");
                    } else {
                        tscPrint.printData(address, et_qrcode.getText().toString(), et_ItemNo.getText().toString(), info, et_specifications.getText().toString(), et_barcode.getText().toString());
                    }
                } else {
                    Toast.makeText(this, "您还没有连接蓝牙，请连接蓝牙后在打印", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }


    /**
     * 原先点击打印的代码，没有调用
     *
     * @param string
     */
    private void printData(String string) {
        try {

            int vstar = 50; //竖直位置
            int hstar = 1; //水平位置
            int h1 = hstar + 40, h2 = hstar + 165;//距左边距离
            int vstep = 30; //行宽
            int maxWidth = 28;//一行字最大数量
            int p = 0;//水平字间距 默认3
            int l = 30;//垂直字间距 默认0101
            String[] split = string.split("\n");
            ArrayList<String> strings = new ArrayList<String>(Arrays.asList(split));
            for (int i = 0; i < strings.size(); i++) {
                List<String> strList = getStrList(strings.get(i), maxWidth);
                strings.remove(i);
                strings.addAll(i, strList);
            }
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < strings.size(); i++) {
                sb.append("<V>").append(vstar + vstep * i);
                sb.append("<H>").append(h2);
                sb.append("<P>").append(p);
                sb.append("<L>").append(l);
                sb.append("<C9>").append("H");
                sb.append(BluetoothPrinterManage.string2HexString(strings.get(i)));
            }
            byte[] gb18030s = string.getBytes("GB18030");
            int byteTextLen = gb18030s.length;
            String dnmmmm = String.format("%04d", byteTextLen);
            String CommandCode = "<A>" + sb.toString()
                    + "<V>" + (vstar + vstep * strings.size()) + "<H>" + h1 + "<2D30>,L,03,1,0<DN>" + dnmmmm + "," + string
                    + "<Q>1<Z>";
            // +"<V>"+(vstar+vstep*0)+"<H>"+h1+"<P>3<L>0101<C9>H"+drugCode
            // +"<V>"+(vstar+vstep*1)+"<H>"+h2+"<P>3<L>0101<C9>H"+drugDesc
            // +"<V>"+(vstar+vstep*2)+"<H>"+h2+"<P>3<L>0101<C9>H"+drugSpec
            // +"<V>"+(vstar+vstep*3)+"<H>"+h2+"<P>3<L>0101<C9>H"+drugBatNo
            // +"<V>"+(vstar+vstep*4)+"<H>"+h2+"<P>3<L>0101<C9>H"+drugExpDate
            // +"<V>"+(vstar+vstep*5)+"<H>"+h2+"<P>3<L>0101<C9>H"+drugBarcode
            // +"<V>"+(vstar+vstep*6)+"<H>"+h2+"<P>3<L>0101<C9>H"+drugManf
            // +"<V>"+(vstar+vstep*2)+"<H>"+h1+"<2D30>,L,03,1,0<DN>"+dnmmmm+","+text
            // +"<Q>1"
            // +"<Z>";
            //CommandCode= "<A><H>100<V>100<2D30>,L,09,1,0<DN>0096,"+text+"<Q1<Z";
            BluetoothPrinterManage.getInstance().codePrint(CommandCode);
        } catch (Exception e) {
            Log.i("zl", Log.getStackTraceString(e));
        }

    }


    public static List<String> getStrList(String inputString, int length) {
        ArrayList<String> strings = new ArrayList<>();
        StringBuffer sb = new StringBuffer();
        char[] chars = inputString.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            sb.append(chars[i]);
            try {
                if (sb.toString().getBytes("gbk").length > length) {
                    strings.add(sb.toString());
                    sb.delete(0, sb.length());
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        strings.add(sb.toString());
        return strings;
    }

    private void printData() {
        try {
            String text = "XY01392    货位码:0415!丙戊酸钠片!0.2g*100片!141159!2017-04-01!000000093011!湖南湘雅制药有限公司";
            String[] inarr = text.split("!");
            String drugCode = inarr[0];
            String drugDesc = inarr[1];
            String drugSpec = inarr[2];
            String drugBatNo = inarr[3];
            String drugExpDate = inarr[4];
            String drugBarcode = inarr[5];
            String drugManf = inarr[6];
            drugCode = BluetoothPrinterManage.string2HexString(drugCode);
            drugDesc = BluetoothPrinterManage.string2HexString(drugDesc);
            drugSpec = BluetoothPrinterManage.string2HexString(drugSpec);
            drugBatNo = BluetoothPrinterManage.string2HexString(drugBatNo);
            drugExpDate = BluetoothPrinterManage.string2HexString(drugExpDate);
            drugBarcode = BluetoothPrinterManage.string2HexString(drugBarcode);
            drugManf = BluetoothPrinterManage.string2HexString(drugManf);
            int vstar = 50; //竖直位置
            int hstar = 1; //水平位置
            int h1 = hstar + 40, h2 = hstar + 165;
            int vstep = 40; //行宽

            byte[] byteText = text.getBytes("GB18030");
            int byteTextLen = byteText.length;
            String dnmmmm = String.format("%04d", byteTextLen);
            String CommandCode = "<A>"
                    + "<V>" + (vstar + vstep * 0) + "<H>" + h1 + "<P>3<L>0101<C9>H" + drugCode
                    + "<V>" + (vstar + vstep * 1) + "<H>" + h2 + "<P>3<L>0101<C9>H" + drugDesc
                    + "<V>" + (vstar + vstep * 2) + "<H>" + h2 + "<P>3<L>0101<C9>H" + drugSpec
                    + "<V>" + (vstar + vstep * 3) + "<H>" + h2 + "<P>3<L>0101<C9>H" + drugBatNo
                    + "<V>" + (vstar + vstep * 4) + "<H>" + h2 + "<P>3<L>0101<C9>H" + drugExpDate
                    + "<V>" + (vstar + vstep * 5) + "<H>" + h2 + "<P>3<L>0101<C9>H" + drugBarcode
                    + "<V>" + (vstar + vstep * 6) + "<H>" + h2 + "<P>3<L>0101<C9>H" + drugManf
                    + "<V>" + (vstar + vstep * 2) + "<H>" + h1 + "<2D30>,L,03,1,0<DN>" + dnmmmm + "," + text
                    + "<Q>1"
                    + "<Z>";
            //CommandCode= "<A><H>100<V>100<2D30>,L,09,1,0<DN>0096,"+text+"<Q1<Z";
            BluetoothPrinterManage.getInstance().codePrint(CommandCode);
            return;
        } catch (Exception e) {
        }
    }

}
