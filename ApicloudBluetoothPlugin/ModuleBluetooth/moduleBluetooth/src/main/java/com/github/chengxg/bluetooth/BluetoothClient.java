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

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class BluetoothClient {
    public boolean isRemove = false;//是否移除
    public boolean isServerClient = false;//是否是服务端
    public String UUID = "00001101-0000-1000-8000-00805F9B34FB";
    public String address;
    public int readBufferSize = 1024;
    public BluetoothDevice device = null;//连接的设备
    public BluetoothSocket btSocket = null;// 蓝牙连接 Socket
    public OutputStream btOutStream = null;//输出流
    public boolean isReturnHex = false;//是否返回hex数据
    public boolean isNoReturnData = false;//是否返回数据
    public String returnStrCharset = "UTF-8";//字符串, UTF-8
    public ReadDataThread readDataThread;//读取数据线程
    public Thread connDeviceThread;//连接设备线程
    public UZModuleContext asyncCallbackModuleContext;
    public BluetoothClient.Callback callback;
    private long lastInteractiveTime = 0;//上次数据交互时间

    public interface Callback {
        public void removeDevice(BluetoothClient device);
    }

    public JSONObject getJSONParams() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", device.getName());
        map.put("address", device.getAddress());
        map.put("UUID", UUID);
        map.put("isReturnHex", isReturnHex);
        map.put("isNoReturnData", isNoReturnData);
        map.put("returnStrCharset", returnStrCharset);
        map.put("isServerClient", isServerClient);
        map.put("isConnected", btSocket != null);
        map.put("isConnecting", connDeviceThread != null);
        map.put("readBufferSize", readBufferSize);
        JSONObject obj = new JSONObject(map);
        return obj;
    }

    private void closeBtSocket() {
        try {
            btSocket.close();
            if (readDataThread != null) {
                readDataThread.interrupt();
                readDataThread.readThreadState = false;
                readDataThread = null;
            }
            if (callback != null) {
                callback.removeDevice(this);
            }
        } catch (Exception ignored) {

        } finally {
            btSocket = null;
            device = null;
        }
    }

    /**
     * 断开连接到设备
     */
    public void disconnect() {
        if (btSocket != null) {
            try {
                btSocket.close();
            } catch (Exception ignored) {

            } finally {
                btSocket = null;
            }
        }

        if (connDeviceThread != null) {
            connDeviceThread.interrupt();
            connDeviceThread = null;
        }
        if (readDataThread != null) {
            readDataThread.interrupt();
            readDataThread.readThreadState = false;
            readDataThread = null;
        }
        if (device != null) {
            JSONObject map = new JSONObject();
            try {
                map.put("disconnectAddress", device.getAddress());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (!isServerClient) {
                ResponseUtil.success(asyncCallbackModuleContext, map);
            } else {
                ResponseUtil.successKeep(asyncCallbackModuleContext, map);
            }
        }
        if (callback != null) {
            try {
                callback.removeDevice(this);
            } catch (Exception e) {

            }
        }
        device = null;
        asyncCallbackModuleContext = null;
    }

    public void connect(final UZModuleContext moduleContext) {
        if (btSocket != null) {
            closeBtSocket();
        }

        if (connDeviceThread != null) {
            connDeviceThread.interrupt();
            connDeviceThread = null;
            if (readDataThread != null) {
                readDataThread.readThreadState = false;
            }
        }

        if (asyncCallbackModuleContext != null) {
            ResponseUtil.success(asyncCallbackModuleContext, "");
            asyncCallbackModuleContext = null;
        }

        connDeviceThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    device = ModuleBluetoothBase.btAdapter.getRemoteDevice(address);
                    btSocket = device.createRfcommSocketToServiceRecord(java.util.UUID.fromString(UUID));
                } catch (Exception e) {
                    ResponseUtil.error(moduleContext, 11, "创建socket失败");
                    return;
                }
                try {
                    btSocket.connect();
                    btOutStream = btSocket.getOutputStream();
                    readData(null);
                    isRemove = false;
                    ResponseUtil.successKeep(moduleContext, true);
                    asyncCallbackModuleContext = moduleContext;
                } catch (Exception e) {
                    closeBtSocket();
                    ResponseUtil.error(moduleContext, 12, "连接失败");
                }
                connDeviceThread = null;
            }
        });
        connDeviceThread.start();
    }

    /**
     * 发送数据
     */
    public void sendData(final UZModuleContext moduleContext) {
        try {
            byte[] buffer = null;
            String data = moduleContext.optString("data");//发送的数据
            boolean isHex = moduleContext.optBoolean("isHex");//是否发送HEX
            String charset = moduleContext.optString("charset");//字符集
            try {
                if (isHex) {
                    buffer = ResponseUtil.Str2Hex(data);
                } else {
                    if (charset != null && !charset.trim().toUpperCase().equals("UTF-8")) {
                        buffer = data.getBytes(charset);
                    } else {
                        buffer = data.getBytes("UTF-8");
                    }
                }
            } catch (Exception e) {
                ResponseUtil.error(moduleContext, 11, "参数解析失败");
                return;
            }

            if (btSocket != null) {
                try {
                    btOutStream.write(buffer);
                    lastInteractiveTime = System.currentTimeMillis();
                    ResponseUtil.successState(moduleContext, true);
                } catch (IOException e) {
                    ResponseUtil.error(moduleContext, 12, "发送失败, 已断开连接");
                    disconnect();
                }
            } else {
                ResponseUtil.error(moduleContext, 13, "未获取到socket, 已断开连接");
                disconnect();
            }
        } catch (Exception e) {
            ResponseUtil.error(moduleContext, -1, e.getMessage());
        }
    }

    public void setReadDataCallback(final UZModuleContext moduleContext) {
        int setBufferSize = moduleContext.optInt("bufferSize");
        isReturnHex = moduleContext.optBoolean("isReturnHex");
        isNoReturnData = moduleContext.optBoolean("isNoReturnData");
        returnStrCharset = moduleContext.optString("returnStrCharset");
        if (returnStrCharset == null || returnStrCharset.trim().isEmpty()) {
            returnStrCharset = "UTF-8";
        } else {
            returnStrCharset = returnStrCharset.trim().toUpperCase();
        }
        if (setBufferSize <= 0) {
            setBufferSize = 1024;
        }
        readBufferSize = setBufferSize;
        readData(moduleContext);
    }

    /**
     * 读取数据线程
     */
    private class ReadDataThread extends Thread {
        public boolean readThreadState = false;
        public UZModuleContext moduleContext;
        public InputStream btInStream = null;

        @Override
        public void run() {
            try {
                btInStream = btSocket.getInputStream();
            } catch (IOException e) {
                ResponseUtil.error(moduleContext, 21, "读取线程获取输入流失败");
                return;
            }
            readThreadState = true;
            Thread curr = Thread.currentThread();
            ResponseUtil.successKeep(moduleContext, true);
            long lastReadTime = 0;

            while (readThreadState) {
                try {
                    if (curr.isInterrupted()) {
                        break;
                    }
                    long ct = System.currentTimeMillis();
                    //心跳检测
                    if (ct - lastInteractiveTime > 1000) {
                        btOutStream.write(0x00);
                        lastInteractiveTime = ct;
                    }
                    if (btInStream.available() > 0) {
                        byte[] buffer = new byte[readBufferSize];
                        int len = btInStream.read(buffer);
                        //检测是否为心跳检测包
                        if (ct - lastReadTime > 100 && len == 1 && buffer[0] == 0) {
                            lastInteractiveTime = ct;
                            lastReadTime = ct;
                            continue;
                        }
                        lastReadTime = ct;
                        lastInteractiveTime = ct;

                        if (moduleContext != null && !isNoReturnData) {
                            byte[] subBuffer = ResponseUtil.subBytes(buffer, 0, len);
                            if (isReturnHex) {
                                StringBuffer dataStr = ResponseUtil.Hex2Str(subBuffer);
                                ResponseUtil.successKeep(moduleContext, dataStr.toString());
                            } else {
                                String dataStr = "";
                                if (returnStrCharset != null && !returnStrCharset.equals("UTF-8")) {
                                    dataStr = new String(new String(subBuffer, returnStrCharset).getBytes("UTF-8"), "UTF-8");
                                } else {
                                    dataStr = new String(subBuffer);
                                }
                                ResponseUtil.successKeep(moduleContext, dataStr);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    readThreadState = false;
                    ResponseUtil.error(moduleContext, 22, "读取数据失败, 连接已断开");
                    disconnect();
                }
            }
        }

    }

    /**
     * 读取数据
     */
    public boolean readData(final UZModuleContext moduleContext) {
        try {
            if (readDataThread != null) {
                readDataThread.readThreadState = false;
                readDataThread.interrupt();
            }
            if (btSocket == null) {
                ResponseUtil.error(moduleContext, 11, "未连接到蓝牙设备");
                return false;
            }
            readDataThread = new ReadDataThread();
            readDataThread.moduleContext = moduleContext;
            Thread thread = new Thread(readDataThread);
            thread.start();
        } catch (Exception e) {
            ResponseUtil.error(moduleContext, -1, e.getMessage());
            return false;
        }
        return true;
    }

}
