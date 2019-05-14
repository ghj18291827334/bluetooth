package com.allen.bluetoothtest;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/**
 * Created by zhanglong on 2018/5/21.
 */
public class MBluetoothManage {

    public static final int RESULT = 111;
    private static MBluetoothManage instance;
    BluetoothManager bluetoothManager;
    BluetoothAdapter mBluetoothAdapter;
    Activity tagActivity;
    MAdapter adapter;
    MBMListener mbmListener;
    BluetoothDevice selectDevice;
    BluetoothServerSocket bluetoothServerSocket = null;
    BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        // 这里有9个要实现的方法，看情况要实现那些，用到那些就实现那些
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        }

        ;

        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        }

        ;
    };
    private BluetoothSocket bluetoothSocket = null;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            Log.i("zl", action);
            // 获得已经搜索到的蓝牙设备
            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                //通过此方法获取搜索到的蓝牙设备
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device == null || device.getName() == null) return;

                if (adapter.getData().contains(device)) {

                    return;
                }
                if (!device.getName().equals("")) {
                    adapter.addData(device);
                }


                // 获取搜索到的蓝牙绑定状态,看看是否是已经绑定过的蓝牙
//                if (device.getBondState() ！= BluetoothDevice.BOND_BONDED) {
//                    // 如果没有绑定过则将蓝牙名称和地址显示在TextView上
//                    //78:02:F8:D0:C0:D1
//                    //如果指定地址的蓝牙和搜索到的蓝牙相同,则我们停止扫描
//                    if ("38:A4:ED:77:A2:8C".equals(device.getAddress())) {
//                        mBluetoothAdapter.cancelDiscovery();//这句话是停止扫描蓝牙
//                        //根据蓝牙地址创建蓝牙对象
//                        BluetoothDevice btDev = mBluetoothAdapter.getRemoteDevice(device.getAddress());
//                        //通过反射来配对对应的蓝牙
//                        try {
//                            //这里是调用的方法，此方法使用反射,后面解释
//                            createBond(btDev.getClass(), btDev);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
                // 搜索完成
            } else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                if (tagActivity != null) tagActivity.setTitle("搜索蓝牙设备");
                if (mbmListener != null) mbmListener.finish();
            } else if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                //获取发生改变的蓝牙对象
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //根据不同的状态显示提示
                switch (device.getBondState()) {
                    case BluetoothDevice.BOND_BONDING://正在配对
                        Log.d("zl", "正在配对......");
                        if (mbmListener != null) {
                            mbmListener.bind(device, MBMListener.BINDING);
                            getAdapter().notifyDataSetChanged();
                        }
                        break;
                    case BluetoothDevice.BOND_BONDED://配对结束
                        Log.d("zl", "完成配对");
                        if (mbmListener != null) {
                            mbmListener.bind(device, MBMListener.SUCCESS);
                            selectDevice = device;
                            getAdapter().notifyDataSetChanged();
                        }
//                        Toast.makeText(context, "签到成功", Toast.LENGTH_SHORT).show();
//                        handler.sendEmptyMessageDelayed(1,2000);
                        break;
                    case BluetoothDevice.BOND_NONE://取消配对/未配对
                        Log.d("zl", "取消配对");
                        if (mbmListener != null) {
                            mbmListener.bind(device, MBMListener.FAILD);
                            getAdapter().notifyDataSetChanged();
                        }
                    default:
                        break;
                }
            }
        }
    };
    private Handler handler;

    public static MBluetoothManage getInstance() {
        if (instance == null) instance = new MBluetoothManage();
        return instance;
    }

    public BluetoothSocket getBluetoothSocket() {
        if (bluetoothSocket == null) {
            bluetoothSocket = createSocket(selectDevice);
        }
        return bluetoothSocket;
    }

    public BluetoothServerSocket getBluetoothServerSocket() {
        if (bluetoothServerSocket == null) {
            bluetoothServerSocket = createServerSocket();
        }
        return bluetoothServerSocket;
    }

    public void connect() {
        try {
            getBluetoothSocket().connect();
        } catch (IOException e) {
            e.printStackTrace();
            bluetoothSocket = null;
            Log.i("zl", "连接异常：connect===" + Log.getStackTraceString(e));
        }
    }

    public void close() {
        try {
            bluetoothSocket.close();
            bluetoothSocket = null;
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("zl", "close===" + Log.getStackTraceString(e));
        } finally {
            bluetoothSocket = null;
        }
    }

    public void setMbmListener(MBMListener mbmListener) {
        this.mbmListener = mbmListener;
    }

    public void init(Activity activity) {
        this.tagActivity = activity;
        bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        /**
         * 功能：获取与本机蓝牙所有绑定的远程蓝牙信息，以BluetoothDevice类实例(稍后讲到)返回。
         * 注意：如果蓝牙为开启，该函数会返回一个空集合 。
         */
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                String devName = device.getName();

                /**
                 * 把这段代码注释掉，目的是已经绑定的蓝牙不再重复添加
                 */
//                if (!devName.equals("")) {
//                    selectDevice = device;
//                    getAdapter().addData(device);
//                }

            }

        }


        register();
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0) {
                    if (tagActivity != null) checkBleDevice();
                } else if (msg.what == 1) {
                    BluetoothDevice btDev = mBluetoothAdapter.getRemoteDevice("38:A4:ED:77:A2:8C");
                    try {
                        removeBond(btDev);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (msg.what == 2) {
                    if (tagActivity != null)
                        Toast.makeText(tagActivity, "检查状态", Toast.LENGTH_SHORT).show();
                    handler.sendEmptyMessageDelayed(3, 60000);
                } else if (msg.what == 3) {
                    currentTime();
                }
            }
        };


    }


    private BluetoothSocket createSocket(BluetoothDevice bd) {
        BluetoothSocket bs = null;
        try {
            /**
             * 00001101-0000-1000-8000-00805F9B34FB这个值是android的API上面说明的，用于普通蓝牙适配器和android手机蓝牙模块连接的
             */
            bs = bd.createInsecureRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("zl", "setSocket" + Log.getStackTraceString(e));
        }
        return bs;
    }


    private BluetoothServerSocket createServerSocket() {
        try {
            BluetoothServerSocket eric = mBluetoothAdapter
                    .listenUsingRfcommWithServiceRecord(
                            "eric",
                            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
            return eric;
        } catch (Exception e) {
            Log.i("zl", "setServerSocket===" + Log.getStackTraceString(e));
        }
        return null;
    }

    public MAdapter getAdapter() {
        if (adapter == null) adapter = new MAdapter();
        return adapter;
    }

    public void register() {
        //蓝牙查询,可以在reciever中接受查询到的蓝牙设备
        IntentFilter mFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        tagActivity.registerReceiver(mReceiver, mFilter);
        // 注册搜索完时的receiver
        mFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        tagActivity.registerReceiver(mReceiver, mFilter);
        //蓝牙连接状态发生改变时,接收状态
        mFilter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        tagActivity.registerReceiver(mReceiver, mFilter);
    }

    /**
     * 蓝牙配对
     *
     * @param btDevice
     * @return
     * @throws Exception
     */
    public boolean createBond(BluetoothDevice btDevice) throws Exception {
//        Method createBondMethod = btClass.getMethod("createBond");//获取蓝牙的连接方法
//        Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);
        boolean bond = btDevice.createBond();
        return bond;
//        return returnValue.booleanValue();//返回连接状态
    }


    /**
     * 判断是否支持蓝牙，并打开蓝牙
     * 获取到BluetoothAdapter之后，还需要判断是否支持蓝牙，以及蓝牙是否打开。
     * 如果没打开，需要让用户打开蓝牙：
     */
    public void checkBleDevice() {
        //通过适配器对象调用isEnabled()方法，判断蓝牙是否打开了
        if (mBluetoothAdapter.isEnabled()) {
            //如果蓝牙已经打开,判断此时蓝牙是否正在扫描,如果正在扫描,则先停止当前扫描,然后在重新扫描
            if (mBluetoothAdapter.isDiscovering()) {
                mBluetoothAdapter.cancelDiscovery();
            }
            // 开始搜索蓝牙设备,搜索到的蓝牙设备通过广播返回BluetoothLeScanner
            if (tagActivity != null) tagActivity.setTitle("正在搜索。。。");
            mBluetoothAdapter.startDiscovery();

        } else {
            //如果没有开启蓝牙，调用系统方法,让用户确认开启蓝牙
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableBtIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            tagActivity.startActivityForResult(enableBtIntent, RESULT);
            if (mbmListener != null) mbmListener.finish();
        }
    }

    /**
     * 删除指定的已配对设备
     *
     * @param btDevice
     * @return
     * @throws Exception
     */
    public boolean removeBond(BluetoothDevice btDevice) throws Exception {
        Method removeBondMethod = btDevice.getClass().getMethod("removeBond");//获取移除蓝牙设备的方法
        Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice);//得到操作结果
        return returnValue;
    }

    /**
     * 判断是否到了指定时间，打开蓝牙，扫描指定设备
     */
    public void currentTime() {
//        if(TimeUtils.isCurrentInTimeScope(13,33,13,50)){
////如果时间已经到了,则默认自动打开蓝牙，且使用handler发出一条通知，
//            mBluetoothAdapter.enable();
//            handler.sendEmptyMessageDelayed(0,2000);
//        }else{
        //如果没有到指定时间,我们也发出一条通知,
        handler.sendEmptyMessageDelayed(2, 1000);
//        }
    }

    public void onDestroy() {
        if (tagActivity != null)
            tagActivity.unregisterReceiver(mReceiver);
        mbmListener = null;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Toast.makeText(tagActivity, "request:" + requestCode + "   result:" + resultCode, Toast.LENGTH_SHORT).show();
    }

    public static abstract class MBMListener {
        public static final int BINDING = 0;
        public static final int SUCCESS = 1;
        public static final int FAILD = 2;

        public abstract void finish();

        public abstract void bind(BluetoothDevice device, int state);
    }


    public class MAdapter extends BaseAdapter {
        List<BluetoothDevice> data = new ArrayList<>();

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BluetoothDevice tag = (BluetoothDevice) v.getTag();
                //判断给定地址下的device是否已经配对
                if (tag.getBondState() == BluetoothDevice.BOND_BONDED) {
                    try {
                        removeBond(tag);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return;
                }
                mBluetoothAdapter.cancelDiscovery();//这句话是停止扫描蓝牙
                //根据蓝牙地址创建蓝牙对象
                BluetoothDevice btDev = mBluetoothAdapter.getRemoteDevice(tag.getAddress());
                //通过反射来配对对应的蓝牙

                try {
                    //这里是调用的方法，此方法使用反射,后面解释
                    if (mbmListener != null) {
                        mbmListener.bind(tag, MBMListener.BINDING);
                    }
                    createBond(btDev);
//                    if (createBond(btDev.getClass(), btDev)) {
//                        if (mbmListener != null) {
//                            mbmListener.bind(tag,MBMListener.BINDING);
//                        }
//                    } else {
//                        if (mbmListener != null) {
//                            mbmListener.bind(tag,MBMListener.FAILD);
//                        }
//                    }
//                    ;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        private TextView title;

        public void setTitleAddHeight(float height) {
            AbsListView.LayoutParams layoutParams = (AbsListView.LayoutParams) title.getLayoutParams();
            layoutParams.height = (int) height;
            title.setLayoutParams(layoutParams);
        }

        public void setTitleSubtractHeight(float height) {
            AbsListView.LayoutParams layoutParams = (AbsListView.LayoutParams) title.getLayoutParams();
            layoutParams.height = (int) (layoutParams.height - height);
            title.setLayoutParams(layoutParams);
        }

        public List<BluetoothDevice> getData() {
            return data;
        }

        public void addData(BluetoothDevice device) {
            data.add(device);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return data.size()+1 ;
        }

        @Override
        public BluetoothDevice getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (position == 0) {
                if (title == null) {
                    title = new TextView(parent.getContext());
                    AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
                    layoutParams.height = 0;
                    title.setLayoutParams(layoutParams);
                    title.setGravity(Gravity.CENTER);
                    title.setBackgroundColor(0xff667788);
                    title.setText(Calendar.getInstance().getTimeZone().getDisplayName(Locale.CHINA));
                }

                convertView = title;

                return convertView;
            }
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, null);
                convertView.setOnClickListener(listener);
            }
            TextView textView = (TextView) convertView;
            BluetoothDevice item = getItem(--position);
            if (item.getBondState() == BluetoothDevice.BOND_BONDED) {
                textView.setText(item.getName() + "：" + item.getAddress() + "：已绑定");
            } else {
                textView.setText(item.getName() + "：" + item.getAddress());
            }
            textView.setTag(getItem(position));
            return convertView;
        }

        public void clear() {
            data.clear();
            notifyDataSetChanged();
        }
    }
}
