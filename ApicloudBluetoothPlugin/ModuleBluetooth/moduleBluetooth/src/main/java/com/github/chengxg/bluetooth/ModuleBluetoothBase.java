/*
 * apicloud上的串口蓝牙插件 cxgBluetooth
 * github: https://github.com/chengxg/html5-bluetooth
 * @Author: chengxg
 * @Date: 2022-01-16
 * version 1.2.0
 *
 *  The MIT License (MIT)
 *
 *  Copyright (c) 2021-2022 by Chengxg
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package com.github.chengxg.bluetooth;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.webkit.WebView;

import com.uzmap.pkg.uzcore.UZWebView;
import com.uzmap.pkg.uzcore.uzmodule.UZModule;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ModuleBluetoothBase extends UZModule {
    public static final int REQUEST_ENABLE_BT = 0, REQUEST_CONNECT = 1;
    public static final BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();// 获取蓝牙适配器

    public static Activity activity;//当前活动的 activity
    public static BroadcastReceiver btFindReceiver = null;//蓝牙搜索 Receiver
    public static BroadcastReceiver btStatusReceiver = null;//蓝牙状态 Receiver

    public ModuleBluetoothBase(UZWebView webView) {
        super(webView);
        this.activity = this.activity();
    }

    /**
     * 取消蓝牙搜索
     */
    public static void cancelDiscovery() {
        if (btAdapter.isDiscovering()) {
            btAdapter.cancelDiscovery();
        }
        if (btFindReceiver != null) {
            activity.unregisterReceiver(btFindReceiver);
            btFindReceiver = null;
        }
    }

    /**
     * 是否打开蓝牙
     */
    public void jsmethod_isEnabledBluetooth(final UZModuleContext moduleContext) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            WebView.setWebContentsDebuggingEnabled(true);
//        }
        try {
            if (btAdapter != null) {
                ResponseUtil.successState(moduleContext, btAdapter.isEnabled());
            } else {
                ResponseUtil.error(moduleContext, 0, "不支持蓝牙");
            }
        } catch (Exception e) {
            ResponseUtil.error(moduleContext, -1, e.getMessage());
        }
    }

    /**
     * 打开蓝牙
     */
    public void jsmethod_openBluetooth(final UZModuleContext moduleContext) {
        try {
            if (btAdapter == null) {
                ResponseUtil.error(moduleContext, 0, "没有蓝牙");
                return;
            }
            if (!btAdapter.isEnabled()) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                if (activity == null) {
                    ResponseUtil.error(moduleContext, 1, "未获取到activity");
                } else {
                    ResponseUtil.error(moduleContext, 2, "请求打开蓝牙");
                    startActivityForResult(intent, REQUEST_ENABLE_BT);
                }
            } else {
                ResponseUtil.successState(moduleContext, true);
            }
        } catch (Exception e) {
            ResponseUtil.error(moduleContext, -1, e.getMessage());
        }
    }

    /**
     * 关闭蓝牙
     */
    public void jsmethod_closeBluetooth(final UZModuleContext moduleContext) {
        try {
            if (btFindReceiver != null) {
                try {
                    activity.unregisterReceiver(btFindReceiver);
                } catch (Exception ignored) {

                }
                btFindReceiver = null;
            }
            cancelDiscovery();
            //断开所有连接
            ModuleBluetoothClient.disconnectAll();

            if (btAdapter != null && btAdapter.isEnabled()) {
                btAdapter.disable();
                ResponseUtil.successState(moduleContext, false);
            } else {
                ResponseUtil.successState(moduleContext, true);
            }
        } catch (Exception e) {
            ResponseUtil.error(moduleContext, -1, e.getMessage());
        }
    }

    /**
     * 监听蓝牙的状态变化
     */
    public void jsmethod_listenBluetoothStatus(final UZModuleContext moduleContext) {
        try {
            if (btStatusReceiver != null) {
                try {
                    activity.unregisterReceiver(btStatusReceiver);
                } catch (Exception ignored) {

                }
                btStatusReceiver = null;
            }

            btStatusReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String retState = "";
                    switch (intent.getAction()) {
                        case BluetoothAdapter.ACTION_STATE_CHANGED:
                            int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                            switch (blueState) {
                                case BluetoothAdapter.STATE_TURNING_ON:
                                    retState = "STATE_TURNING_ON";
                                    break;
                                case BluetoothAdapter.STATE_ON:
                                    retState = "STATE_ON";
                                    break;
                                case BluetoothAdapter.STATE_TURNING_OFF:
                                    retState = "STATE_TURNING_OFF";
                                    break;
                                case BluetoothAdapter.STATE_OFF:
                                    retState = "STATE_OFF";
                                    break;
                            }
                            break;
                    }
                    JSONObject ret = new JSONObject();
                    try {
                        ret.put("status", retState);
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                    moduleContext.success(ret, false);
                }
            };
            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            activity.registerReceiver(btStatusReceiver, filter);
        } catch (Exception e) {
            ResponseUtil.error(moduleContext, -1, e.getMessage());
        }
    }

    /**
     * 获取已经配对的设备
     */
    public void jsmethod_bondedDevices(final UZModuleContext moduleContext) {
        try {
            Set<BluetoothDevice> connetedDevicesSet = btAdapter.getBondedDevices();
            JSONArray connetedDeviceList = new JSONArray();
            for (BluetoothDevice device : connetedDevicesSet) {
                Map<String, String> map = new HashMap<>();
                map.put("name", device.getName());
                map.put("address", device.getAddress());

                JSONObject obj = new JSONObject(map);
                connetedDeviceList.put(obj);
            }
            ResponseUtil.success(moduleContext, connetedDeviceList);
        } catch (Exception e) {
            ResponseUtil.error(moduleContext, -1, e.getMessage());
        }
    }

    /**
     * 是否正在进行搜索
     */
    public void jsmethod_isScanning(final UZModuleContext moduleContext) {
        try {
            ResponseUtil.successState(moduleContext, btAdapter.isDiscovering());
        } catch (Exception e) {
            ResponseUtil.error(moduleContext, -1, e.getMessage());
        }
    }

    /**
     * 发现新的设备
     */
    public void jsmethod_scan(final UZModuleContext moduleContext) {
        try {
            if (!btAdapter.isEnabled()) {
                ResponseUtil.error(moduleContext, 0, "当前未打开蓝牙");
                return;
            }
            if (btFindReceiver != null) {
                try {
                    activity.unregisterReceiver(btFindReceiver);
                } catch (Exception ignored) {

                }
                btFindReceiver = null;
                cancelDiscovery();
            }

            //6.0 以后的如果需要利用本机查找周围的wifi和蓝牙设备, 申请权限
            if (Build.VERSION.SDK_INT >= 6.0) {
                if (
                        ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                                ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                ) {
                    //请求权限
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 2);
                    ResponseUtil.error(moduleContext, 1, "请授予相关权限");
                    return;
                }
            }
            btFindReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (BluetoothDevice.ACTION_FOUND.equals(action)) {// 找到设备
                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                        JSONObject map = new JSONObject();
                        try {
                            map.put("name", device.getName());
                            map.put("address", device.getAddress());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ResponseUtil.successKeep(moduleContext, map);
                    }
                    if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) { // 搜索完成
                        cancelDiscovery();
                        ResponseUtil.success(moduleContext, "ACTION_DISCOVERY_FINISHED");
                    }
                }
            };
            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            activity.registerReceiver(btFindReceiver, filter);
            btAdapter.startDiscovery(); //开启搜索
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                //android 10必须打开位置开关才能搜索蓝牙
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //延时
                        try {
                            Thread.sleep(50);
                        } catch (Exception e) {

                        }
                        //看一下是否真的在搜索
                        //如果不是需要提醒打开GPS
                        boolean isFind = btAdapter.isDiscovering();
                        if (!isFind) {
                            ResponseUtil.error(moduleContext, 2, "请手动开启位置信息, 否则不能搜索蓝牙设备！");
                        } else {
                            ResponseUtil.successKeep(moduleContext, true);
                        }
                    }
                }).start();
            }
        } catch (Exception e) {
            ResponseUtil.error(moduleContext, -1, e.getMessage());
        }
    }

    /**
     * 停止搜索
     */
    public void jsmethod_stopScan(final UZModuleContext moduleContext) {
        try {
            if (btFindReceiver != null) {
                try {
                    activity.unregisterReceiver(btFindReceiver);
                } catch (Exception ignored) {

                }
                btFindReceiver = null;
                cancelDiscovery();
            }
            ResponseUtil.successState(moduleContext, true);
        } catch (Exception e) {
            ResponseUtil.error(moduleContext, -1, e.getMessage());
        }
    }

}
