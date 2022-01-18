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

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStream;
import java.util.UUID;

public class BluetoothServer {
    public static String bluetoothName = "";
    public static final BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();// 获取蓝牙适配器
    public BluetoothServer.ServerThread serverThread;//服务线程
    public BluetoothClient bluetoothClient;
    public UZModuleContext serverModuleContext;
    public String Server_UUID = "00001101-0000-1000-8000-00805F9B34FB";

    private void setServerModuleContext(UZModuleContext moduleContext) {
        serverModuleContext = moduleContext;
    }

    public void stopServerThread() {
        if (bluetoothClient != null) {
            try {
                bluetoothClient.disconnect();
                bluetoothClient = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (serverThread != null) {
            serverThread.serverThreadState = false;
            if (serverThread.serverSocket != null) {
                try {
                    serverThread.serverSocket.close();
                } catch (Exception e) {

                }
            }
            serverThread.interrupt();
            serverThread = null;
        }
        setServerModuleContext(null);
    }

    private class ServerThread extends Thread {
        public BluetoothServerSocket serverSocket;
        public BluetoothSocket btSocket;
        public boolean serverThreadState = false;

        @Override
        public void run() {
            try {
                if (bluetoothName != null && !bluetoothName.isEmpty()) {
                    btAdapter.setName(bluetoothName);
                }
                serverSocket = btAdapter.listenUsingRfcommWithServiceRecord("", UUID.fromString(Server_UUID));
            } catch (Exception e) {
                e.printStackTrace();
                ResponseUtil.error(serverModuleContext, 21, "创建服务失败: " + e.getMessage());
                return;
            }
            serverThreadState = true;

            if (bluetoothClient != null) {
                try {
                    bluetoothClient.disconnect();
                    bluetoothClient = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            Thread curr = Thread.currentThread();

            while (serverThreadState) {
                try {
                    btSocket = serverSocket.accept();
                    if (curr.isInterrupted() || !serverThreadState) {
                        serverSocket.close();
                        btSocket.close();
                        break;
                    }
                    BluetoothDevice device = btSocket.getRemoteDevice();
                    if (bluetoothClient != null) {
                        try {
                            bluetoothClient.disconnect();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (btSocket.isConnected()) {
                        OutputStream btOutStream = btSocket.getOutputStream();
                        bluetoothClient = new BluetoothClient();
                        bluetoothClient.isServerClient = true;
                        bluetoothClient.UUID = Server_UUID;
                        bluetoothClient.device = device;
                        bluetoothClient.address = device.getAddress();
                        bluetoothClient.btSocket = btSocket;
                        bluetoothClient.btOutStream = btOutStream;
                        bluetoothClient.asyncCallbackModuleContext = serverModuleContext;
                        bluetoothClient.callback = new BluetoothClient.Callback() {
                            @Override
                            public void removeDevice(BluetoothClient device) {
                                bluetoothClient = null;
                            }
                        };
                        JSONObject map = new JSONObject();
                        try {
                            map.put("name", device.getName());
                            map.put("address", device.getAddress());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ResponseUtil.successKeep(serverModuleContext, map);
                    } else {
                        btSocket.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (curr.isInterrupted() || !serverThreadState) {
                        try {
                            serverSocket.close();
                        } catch (Exception ig) {
                        }
                        break;
                    } else {
                        ResponseUtil.errorKeep(serverModuleContext, -1, "连接失败: " + e.getMessage());
                    }
                }
            }
        }
    }

    public JSONObject getServerInfo() {
        try {
            JSONObject obj = new JSONObject();
            obj.put("isServerRun", serverThread != null);
            obj.put("Server_UUID", Server_UUID);
            if (bluetoothClient != null) {
                JSONObject info = bluetoothClient.getJSONParams();
                obj.put("client", info);
            } else {
                obj.put("client", null);
            }
            return obj;
        } catch (Exception e) {

        }
        return null;
    }

    public void startServer(final UZModuleContext moduleContext) {
        try {
            boolean isNewStartServer = moduleContext.optBoolean("isNewStartServer");
            String UUID = moduleContext.optString("Server_UUID");
            if (UUID == null || UUID.isEmpty()) {
                UUID = "00001101-0000-1000-8000-00805F9B34FB";
            }
            if (isNewStartServer) {
                stopServerThread();
            }

            if (isNewStartServer || serverThread == null) {
                serverThread = new BluetoothServer.ServerThread();
                Server_UUID = UUID;
                Thread thread = new Thread(serverThread);
                thread.start();
            }
            setServerModuleContext(moduleContext);
            ResponseUtil.successKeep(moduleContext, true);
        } catch (Exception e) {
            e.printStackTrace();
            ResponseUtil.error(moduleContext, -1, e.getMessage());
        }
    }

    /**
     * 断开连接
     */
    public void disconnect(final UZModuleContext moduleContext) {
        if (bluetoothClient == null || bluetoothClient.btSocket == null) {
            ResponseUtil.success(moduleContext, false);
            return;
        }
        try {
            bluetoothClient.disconnect();
            ResponseUtil.success(moduleContext, true);
        } catch (Exception e) {
            ResponseUtil.error(moduleContext, -1, e.getMessage());
        }
    }

    /**
     * 发送数据
     *
     * @param moduleContext moduleContext
     */
    public void sendData(final UZModuleContext moduleContext) {
        if (bluetoothClient == null) {
            ResponseUtil.error(moduleContext, 5, "蓝牙设备未连接");
            return;
        }
        try {
            bluetoothClient.sendData(moduleContext);
        } catch (Exception e) {
            ResponseUtil.error(moduleContext, -1, e.getMessage());
        }
    }

    /**
     * 读取数据
     *
     * @param moduleContext moduleContext
     */
    public void setReadDataCallback(final UZModuleContext moduleContext) {
        if (bluetoothClient == null) {
            ResponseUtil.error(moduleContext, 11, "蓝牙设备未连接");
            return;
        }
        try {
            bluetoothClient.setReadDataCallback(moduleContext);
        } catch (Exception e) {
            ResponseUtil.error(moduleContext, -1, e.getMessage());
        }
    }
}