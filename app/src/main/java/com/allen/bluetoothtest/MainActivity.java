package com.allen.bluetoothtest;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.allen.bluetoothtest.util.SPUtils;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ListView lv;
    private Button search;
    String address;
    private static final int ACCESS_LOCATION = 101;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv = findViewById(R.id.main_lv);
        search = findViewById(R.id.main_search);

        getPermission();

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "设备不支持蓝牙4.0", Toast.LENGTH_SHORT).show();
            finish();
        }

        MBluetoothManage.getInstance().init(this);
        BluetoothPrinterManage.getInstance().setBluetoothManage(MBluetoothManage.getInstance());
        lv.setAdapter(MBluetoothManage.getInstance().getAdapter());

        MBluetoothManage.getInstance().setMbmListener(new MBluetoothManage.MBMListener() {
            @Override
            public void finish() {
                search.setText("搜索完毕！点击重新搜索");
            }

            @Override
            public void bind(BluetoothDevice device, int state) {
                if (state == MBluetoothManage.MBMListener.BINDING) {
                    setTitle("正在绑定。。。" + device.getName());
                } else if (state == MBluetoothManage.MBMListener.SUCCESS) {
                    address = device.getAddress();
                    SPUtils.put(MainActivity.this,"address",address);
                    setTitle("绑定" + '\"' + device.getName() + '\"' + "成功！");
                } else if (state == MBluetoothManage.MBMListener.FAILD) {
                    setTitle("绑定" + '\"' + device.getName() + '\"' + "失败！");
                }
            }
        });
//        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
//        BluetoothGatt gatt = device.connectGatt(this, false, mGattCallback);
//
//        gatt.setCharacteristicNotification(characteristic, true);
//        gatt.readCharacteristic(characteristic);
//        gatt.wirteCharacteristic(mCurrentcharacteristic);
//        gatt.writeDescriptor(descriptor);
//        gatt.readRemoteRssi();
//        gatt.discoverServices();



    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    private boolean hasAllPermissionGranted(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }


    private void getPermission() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            int permissionCheck = 0;
            permissionCheck = this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            permissionCheck += this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions( // 请求授权
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION},
                        ACCESS_LOCATION);// 自定义常量,任意整型
            } else {
                // 已经获得权限
            }
        }
    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_search:
                TextView v1 = (TextView) v;
                v1.setText("正在搜索。。。");
                MBluetoothManage.getInstance().getAdapter().clear();
                MBluetoothManage.getInstance().checkBleDevice();
//                    RXJAVA();

                break;
            case R.id.main_print:
                Intent intent = new Intent(this, PrintActivity.class);

                startActivity(intent);
                break;
        }
    }


    public void RXJAVA() {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                emitter.onNext("1");
            }
        }).map(new Function<String, Integer>() {
            @Override
            public Integer apply(String s) throws Exception {
                if (s.equals("1")) return 1;
                return 0;
            }
        }).map(new Function<Integer, String>() {
            @Override
            public String apply(Integer integer) throws Exception {
                return "" + (++integer);
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String integer) throws Exception {
                search.setText(integer + "");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        MBluetoothManage.getInstance().onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MBluetoothManage.getInstance().onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case ACCESS_LOCATION:
                if (hasAllPermissionGranted(grantResults)) {
                    Log.d(TAG, "onRequestPermissionsResult: OK");
                } else {
                    Log.d(TAG, "onRequestPermissionsResult: NOT OK");
                }
                break;
        }
    }
}
