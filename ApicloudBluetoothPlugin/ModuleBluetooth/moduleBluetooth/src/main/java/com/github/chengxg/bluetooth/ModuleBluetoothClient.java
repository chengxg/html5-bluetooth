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

import com.uzmap.pkg.uzcore.UZWebView;
import com.uzmap.pkg.uzcore.uzmodule.UZModule;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class ModuleBluetoothClient extends UZModule {
    public static ArrayList<BluetoothClient> clientList = new ArrayList<>();

    public ModuleBluetoothClient(UZWebView webView) {
        super(webView);
    }

    private static BluetoothClient getDeviceByAddressAndUUID(String address, String UUID) {
        if (address == null || address.isEmpty()) {
            return null;
        }
        if (UUID == null || UUID.isEmpty()) {
            return null;
        }
        for (BluetoothClient client : clientList) {
            if (client != null && address.equals(client.address) && UUID.equals(client.UUID)) {
                return client;
            }
        }
        return null;
    }

    private static void removeClientFromMap(String address, String UUID) {
        if (address == null || address.isEmpty()) {
            return;
        }
        if (UUID == null || UUID.isEmpty()) {
            return;
        }
        Iterator<BluetoothClient> it = clientList.iterator();
        while (it.hasNext()) {
            BluetoothClient client = it.next();
            if (client != null && address.equals(client.address) && UUID.equals(client.UUID)) {
                client.isRemove = true;
                break;
            }
        }
    }

    public static void disconnectAll() {
        for (BluetoothClient client : clientList) {
            if (client != null) {
                client.disconnect();
            }
        }

        Iterator<BluetoothClient> it = clientList.iterator();
        while (it.hasNext()) {
            BluetoothClient client = it.next();
            if (client != null && client.isRemove) {
                it.remove();
            }
        }
    }

    private BluetoothClient checkAndGetClient(final UZModuleContext moduleContext, boolean isReturnNoCLient) {
        String address = moduleContext.optString("address");
        String Client_UUID = moduleContext.optString("UUID");
        if (address == null || address.isEmpty()) {
            ResponseUtil.error(moduleContext, 1, "未获取到蓝牙连接地址");
            return null;
        }
        if (Client_UUID == null || Client_UUID.isEmpty()) {
            ResponseUtil.error(moduleContext, 2, "未获取到连接UUID");
            return null;
        }
        BluetoothClient device = getDeviceByAddressAndUUID(address, Client_UUID);
        if (device == null) {
            if (isReturnNoCLient) {
                ResponseUtil.error(moduleContext, 3, "蓝牙客户端未连接");
            }
            return null;
        }
        return device;
    }

    /**
     * 连接设备
     */
    public void jsmethod_connect(final UZModuleContext moduleContext) {
        try {
            String Client_UUID = moduleContext.optString("UUID");
            String address = moduleContext.optString("address");
            BluetoothClient client = checkAndGetClient(moduleContext, false);
            if (client != null && client.btSocket != null) {
                client.isRemove = false;
                ResponseUtil.error(moduleContext, 5, "该客户端已连接");
                return;
            }
            if (client == null) {
                client = new BluetoothClient();
                client.address = address;
                client.UUID = Client_UUID;
                client.isServerClient = false;
                client.callback = new BluetoothClient.Callback() {
                    @Override
                    public void removeDevice(BluetoothClient device) {
                        removeClientFromMap(device.address, device.UUID);
                    }
                };
                clientList.add(client);
            }
            ModuleBluetoothBase.cancelDiscovery();
            client.connect(moduleContext);
        } catch (Exception e) {
            e.printStackTrace();
            ResponseUtil.error(moduleContext, -1, e.getMessage());
        }
    }

    /**
     * 断开连接
     */
    public void jsmethod_disconnect(final UZModuleContext moduleContext) {
        try {
            BluetoothClient client = checkAndGetClient(moduleContext, false);
            if (client == null) {
                ResponseUtil.success(moduleContext, false);
                return;
            }
            client.disconnect();
            ResponseUtil.success(moduleContext, true);
        } catch (Exception e) {
            ResponseUtil.error(moduleContext, -1, e.getMessage());
        }
    }

    /**
     * 获取已经连接的设备
     */
    public void jsmethod_syncClientsInfo(final UZModuleContext moduleContext) {
        try {
            JSONArray clientsArr = new JSONArray();
            for (BluetoothClient client : clientList) {
                if (client == null || client.device == null || client.isRemove) {
                    continue;
                }
                JSONObject clientJSON = client.getJSONParams();
                clientsArr.put(clientJSON);
            }
            ResponseUtil.success(moduleContext, clientsArr);
        } catch (Exception e) {
            ResponseUtil.error(moduleContext, -1, e.getMessage());
        }
    }

    /**
     * 发送数据
     */
    public void jsmethod_sendData(final UZModuleContext moduleContext) {
        try {
            BluetoothClient client = checkAndGetClient(moduleContext, true);
            if (client == null) {
                return;
            }
            client.sendData(moduleContext);
        } catch (Exception e) {
            ResponseUtil.error(moduleContext, -1, e.getMessage());
        }
    }

    /**
     * 读取数据
     *
     * @param moduleContext moduleContext
     */
    public void jsmethod_setReadDataCallback(final UZModuleContext moduleContext) {
        try {
            BluetoothClient client = checkAndGetClient(moduleContext, true);
            if (client == null) {
                return;
            }
            client.setReadDataCallback(moduleContext);
        } catch (Exception e) {
            ResponseUtil.error(moduleContext, -1, e.getMessage());
        }
    }


}
