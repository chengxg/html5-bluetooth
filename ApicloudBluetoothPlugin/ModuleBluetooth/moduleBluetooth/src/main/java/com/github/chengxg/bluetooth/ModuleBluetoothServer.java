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

import java.util.HashMap;
import java.util.Map;

public class ModuleBluetoothServer extends UZModule {
    public static Map<String, BluetoothServer> serversMap = new HashMap();

    public ModuleBluetoothServer(UZWebView webView) {
        super(webView);
    }

    private static void removeFromMap(String UUID) {
        BluetoothServer server = serversMap.get(UUID);
        if (server != null) {
            server.stopServerThread();
            serversMap.remove(UUID);
        }
    }

    public void jsmethod_syncServersInfo(final UZModuleContext moduleContext) {
        JSONArray arr = new JSONArray();
        for (Map.Entry<String, BluetoothServer> entry : serversMap.entrySet()) {
            BluetoothServer server = entry.getValue();
            arr.put(server.getServerInfo());
        }
        ResponseUtil.success(moduleContext, arr);
    }

    public void jsmethod_startServer(final UZModuleContext moduleContext) {
        String UUID = moduleContext.optString("Server_UUID");
        if (UUID == null || UUID.isEmpty()) {
            UUID = "00001101-0000-1000-8000-00805F9B34FB";
        }
        BluetoothServer server = serversMap.get(UUID);
        if (server == null) {
            server = new BluetoothServer();
            serversMap.put(UUID, server);
        }
        server.startServer(moduleContext);
    }

    /**
     * 停止服务
     *
     * @param moduleContext
     */
    public void jsmethod_stopServer(final UZModuleContext moduleContext) {
        try {
            String UUID = moduleContext.optString("Server_UUID");
            boolean isRemove = moduleContext.optBoolean("isRemove");
            //移除全部
            if (UUID == null || UUID.isEmpty()) {
                for (Map.Entry<String, BluetoothServer> entry : serversMap.entrySet()) {
                    BluetoothServer server = entry.getValue();
                    server.stopServerThread();
                }
                if (isRemove) {
                    serversMap.clear();
                }
            } else {
                BluetoothServer server = serversMap.get(UUID);
                if (server == null) {
                    ResponseUtil.success(moduleContext, false);
                    return;
                }
                server.stopServerThread();
                if (isRemove) {
                    removeFromMap(UUID);
                }
            }
            ResponseUtil.success(moduleContext, true);
        } catch (Exception e) {
            ResponseUtil.error(moduleContext, -1, e.getMessage());
        }
    }

    /**
     * 断开连接
     */
    public void jsmethod_disconnectServerClient(final UZModuleContext moduleContext) {
        String UUID = moduleContext.optString("Server_UUID");
        BluetoothServer server = serversMap.get(UUID);
        if (server == null) {
            ResponseUtil.success(moduleContext, true);
            return;
        }
        server.disconnect(moduleContext);
    }

    /**
     * 发送数据
     *
     * @param moduleContext moduleContext
     */
    public void jsmethod_sendDataServerClient(final UZModuleContext moduleContext) {
        String UUID = moduleContext.optString("Server_UUID");
        BluetoothServer server = serversMap.get(UUID);
        if (server == null) {
            ResponseUtil.error(moduleContext, 1, "该服务不存在!");
            return;
        }
        server.sendData(moduleContext);
    }

    /**
     * 读取数据
     *
     * @param moduleContext moduleContext
     */
    public void jsmethod_setReadDataCallbackServerClient(final UZModuleContext moduleContext) {
        String UUID = moduleContext.optString("Server_UUID");
        BluetoothServer server = serversMap.get(UUID);
        if (server == null) {
            ResponseUtil.error(moduleContext, 1, "该服务不存在!");
            return;
        }
        server.setReadDataCallback(moduleContext);
    }

}
