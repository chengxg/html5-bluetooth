package com.chengxg.plugin;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.*;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

import com.uzmap.pkg.uzcore.UZWebView;
import com.uzmap.pkg.uzcore.uzmodule.UZModule;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;

/**
 * 蓝牙串口
 */
public class BluetoothSerial extends UZModule{
    private static final int REQUEST_ENABLE_BT = 0, REQUEST_CONNECT = 1;
    private static UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");// 蓝牙连接需要UUID
    private static final BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();// 获取蓝牙适配器

    private Activity activity;//当前活动的 activity
    private static BroadcastReceiver btFindReceiver = null;//蓝牙搜索 Receiver
    private static BroadcastReceiver btStatusReceiver = null;//蓝牙状态 Receiver
    private static BluetoothDevice device = null;//连接的设备
    private static BluetoothSocket btSocket = null;// 蓝牙连接 Socket
    private static InputStream btInStream = null;
    private static OutputStream btOutStream = null;
    private static Thread readDataThread;//读取数据线程
    private static boolean readThreadState = false; //读取数据线程状态位
    private static Thread connDeviceThread;//连接设备线程
    private static StringBuffer receiveDataStr = new StringBuffer();//接收到的数据字符成

    public BluetoothSerial(UZWebView webView){
        super(webView);
        this.activity = this.activity();
    }

    /**
     * 解析指令回调接口
     */
    public interface ResolveCommandCallback{
        public void resolve(String eventName, String dataBody, String timeId, String checkCode);
    }

    //---------------------------返回结果处理方法 开始--------------------------------------
    private void success(final UZModuleContext moduleContext, String msg){
        JSONObject ret = new JSONObject();
        try {
            ret.put("data", msg);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        moduleContext.success(ret, true);
    }

    private void successKeep(final UZModuleContext moduleContext, String msg){
        JSONObject ret = new JSONObject();
        try {
            ret.put("data", msg);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        moduleContext.success(ret, false);
    }

    private void successState(final UZModuleContext moduleContext, boolean state){
        JSONObject ret = new JSONObject();
        try {
            ret.put("state", state);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        moduleContext.success(ret, true);
    }

    private void successStatus(final UZModuleContext moduleContext, boolean status){
        JSONObject ret = new JSONObject();
        try {
            ret.put("status", status);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        moduleContext.success(ret, true);
    }

    private void success(final UZModuleContext moduleContext, boolean option){
        JSONObject ret = new JSONObject();
        try {
            ret.put("data", option);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        moduleContext.success(ret, true);
    }

    private void success(final UZModuleContext moduleContext, JSONObject data){
        JSONObject ret = new JSONObject();
        try {
            ret.put("data", data);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        moduleContext.success(ret, true);
    }

    private void successKeep(final UZModuleContext moduleContext, JSONObject data){
        JSONObject ret = new JSONObject();
        try {
            ret.put("data", data);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        moduleContext.success(ret, false);
    }

    private void success(final UZModuleContext moduleContext, JSONArray data){
        JSONObject ret = new JSONObject();
        try {
            ret.put("data", data);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        moduleContext.success(ret, true);
    }

    private void successKeep(final UZModuleContext moduleContext, JSONArray data){
        JSONObject ret = new JSONObject();
        try {
            ret.put("data", data);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        moduleContext.success(ret, false);
    }

    private void error(final UZModuleContext moduleContext, int code, String msg){
        JSONObject ret = new JSONObject();
        try {
            ret.put("msg", msg);
            ret.put("code", code);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        moduleContext.error(ret, true);
    }

    private void errorKeep(final UZModuleContext moduleContext, int code, String msg){
        JSONObject ret = new JSONObject();
        try {
            ret.put("msg", msg);
            ret.put("code", code);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        moduleContext.error(ret, false);
    }
    //---------------------------返回结果处理方法 结束--------------------------------------

    /**
     * 关闭 btSocket
     */
    private void closeBtSocket(){
        try {
            btSocket.close();
        } catch (Exception ignored) {

        } finally {
            btSocket = null;
            device = null;
        }
    }

    /**
     * 取消蓝牙搜索
     */
    private void cancelDiscovery(){
        if(btAdapter.isDiscovering()){
            btAdapter.cancelDiscovery();
        }
        if(btFindReceiver != null){
            activity.unregisterReceiver(btFindReceiver);
            btFindReceiver = null;
        }
    }

    /**
     * 是否已经打开了蓝牙
     */
    public void jsmethod_isEnabledBluetooth(final UZModuleContext moduleContext){
        try {
            if(btAdapter != null){
                successState(moduleContext, btAdapter.isEnabled());
            } else {
                error(moduleContext, 0, "不支持蓝牙");
            }
        } catch (Exception e) {
            error(moduleContext, - 1, e.getMessage());
        }
    }

    /**
     * 打开蓝牙
     */
    public void jsmethod_openBluetooth(final UZModuleContext moduleContext){
        try {
            if(btAdapter == null){
                error(moduleContext, 0, "没有蓝牙");
                return;
            }
            if(! btAdapter.isEnabled()){
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                if(activity == null){
                    error(moduleContext, 1, "未获取到activity");
                } else {
                    error(moduleContext, 2, "请求打开蓝牙");
                    startActivityForResult(intent, REQUEST_ENABLE_BT);
                }
            } else {
                successState(moduleContext, true);
            }
        } catch (Exception e) {
            error(moduleContext, - 1, e.getMessage());
        }
    }

    /**
     * 关闭蓝牙
     */
    public void jsmethod_closeBluetooth(final UZModuleContext moduleContext){
        try {
            if(btFindReceiver != null){
                try {
                    activity.unregisterReceiver(btFindReceiver);
                } catch (Exception ignored) {

                }
                btFindReceiver = null;
            }
            cancelDiscovery();
            readThreadState = false;
            closeBtSocket();

            if(btAdapter != null && btAdapter.isEnabled()){
                btAdapter.disable();
                successState(moduleContext, false);
            } else {
                successState(moduleContext, true);
            }

            connDeviceThread = null;
        } catch (Exception e) {
            error(moduleContext, - 1, e.getMessage());
        }
    }

    /**
     * 监听蓝牙的状态变化
     */
    public void jsmethod_listenBluetoothStatus(final UZModuleContext moduleContext){
        try {

            if(btStatusReceiver != null){
                try {
                    activity.unregisterReceiver(btStatusReceiver);
                } catch (Exception ignored) {

                }
                btStatusReceiver = null;
            }

            btStatusReceiver = new BroadcastReceiver(){
                @Override
                public void onReceive(Context context, Intent intent){
                    String retState = "";
                    switch(Objects.requireNonNull(intent.getAction())) {
                        case BluetoothAdapter.ACTION_STATE_CHANGED:
                            int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                            switch(blueState) {
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
            error(moduleContext, - 1, e.getMessage());
        }
    }

    /**
     * 获取已经配对的设备
     */
    public void jsmethod_bondedDevices(final UZModuleContext moduleContext){
        try {
            Set<BluetoothDevice> connetedDevicesSet = btAdapter.getBondedDevices();
            JSONArray connetedDeviceList = new JSONArray();
            JSONObject ret = new JSONObject();
            for(BluetoothDevice device : connetedDevicesSet){
                Map<String, String> map = new HashMap<>();
                map.put("name", device.getName());
                map.put("address", device.getAddress());

                JSONObject obj = new JSONObject(map);
                connetedDeviceList.put(obj);
            }
            success(moduleContext, connetedDeviceList);
        } catch (Exception e) {
            error(moduleContext, - 1, e.getMessage());
        }
    }

    /**
     * 是否正在进行搜索
     */
    public void jsmethod_isScanning(final UZModuleContext moduleContext){
        try {
            successState(moduleContext, btAdapter.isDiscovering());
        } catch (Exception e) {
            error(moduleContext, - 1, e.getMessage());
        }
    }

    /**
     * 发现新的设备
     */
    public void jsmethod_scan(final UZModuleContext moduleContext){
        try {
            if(! btAdapter.isEnabled()){
                error(moduleContext, 0, "当前未打开蓝牙");
                return;
            }
            if(btFindReceiver != null){
                try {
                    activity.unregisterReceiver(btFindReceiver);
                } catch (Exception ignored) {

                }
                btFindReceiver = null;
                cancelDiscovery();
            }
            //6.0 以后的如果需要利用本机查找周围的wifi和蓝牙设备, 申请权限
            if(Build.VERSION.SDK_INT >= 6.0){
                if(PermissionChecker.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                    //请求权限
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 2);
                    if(ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_COARSE_LOCATION)){

                    }
                    error(moduleContext, 1, "请授予相关权限");
                    return;
                }
            }
            btFindReceiver = new BroadcastReceiver(){
                @Override
                public void onReceive(Context context, Intent intent){
                    String action = intent.getAction();
                    if(BluetoothDevice.ACTION_FOUND.equals(action)){// 找到设备
                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                        JSONObject map = new JSONObject();
                        try {
                            map.put("name", device.getName());
                            map.put("address", device.getAddress());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        successKeep(moduleContext, map);
                    }
                    if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){ // 搜索完成
                        cancelDiscovery();
                        success(moduleContext, "ACTION_DISCOVERY_FINISHED");
                    }
                }
            };
            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            activity.registerReceiver(btFindReceiver, filter);
            btAdapter.startDiscovery(); //开启搜索
        } catch (Exception e) {
            error(moduleContext, - 1, e.getMessage());
        }
    }

    /**
     * 停止搜索
     */
    public void jsmethod_stopScan(final UZModuleContext moduleContext){
        try {
            if(btFindReceiver != null){
                try {
                    activity.unregisterReceiver(btFindReceiver);
                } catch (Exception ignored) {

                }
                btFindReceiver = null;
                cancelDiscovery();
            }
            successState(moduleContext, true);
        } catch (Exception e) {
            error(moduleContext, - 1, e.getMessage());
        }
    }

    /**
     * 连接到设备
     */
    public void jsmethod_connect(final UZModuleContext moduleContext){
        try {
            final String address = moduleContext.optString("address");
            if(address == null || address.isEmpty()){
                error(moduleContext, 0, "未获取到蓝牙连接地址");
                return;
            }
            cancelDiscovery();
            if(btSocket != null){
                closeBtSocket();
                btSocket = null;
            }

            if(connDeviceThread != null){
                connDeviceThread.interrupt();
                connDeviceThread = null;
                readThreadState = false;
            }

            connDeviceThread = new Thread(new Runnable(){
                @Override
                public void run(){
                    try {
                        device = btAdapter.getRemoteDevice(address);
                        btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
                    } catch (Exception e) {
                        error(moduleContext, 1, "创建socket失败");
                        return;
                    }
                    try {
                        btSocket.connect();
                        btOutStream = btSocket.getOutputStream();
                        successState(moduleContext, true);
                    } catch (Exception e) {
                        closeBtSocket();
                        error(moduleContext, 2, "连接失败");
                    }
                    connDeviceThread = null;
                }
            });
            connDeviceThread.start();
        } catch (Exception e) {
            error(moduleContext, - 1, e.getMessage());
        }
    }

    /**
     * 断开连接到设备
     */
    public void jsmethod_disconnect(final UZModuleContext moduleContext){
        try {
            if(btSocket != null){
                closeBtSocket();
                btSocket = null;
            }
            if(connDeviceThread != null){
                connDeviceThread.interrupt();
                connDeviceThread = null;
                readThreadState = false;
            }
            device = null;
            successState(moduleContext, true);
        } catch (Exception e) {
            error(moduleContext, - 1, e.getMessage());
        }
    }

    /**
     * 获取已经连接的设备
     */
    public void jsmethod_connectedDevice(final UZModuleContext moduleContext){
        try {
            if(device != null){
                JSONObject data = new JSONObject();
                data.put("name", device.getName());
                data.put("address", device.getAddress());
                success(moduleContext, data);
            } else {
                JSONObject ret = new JSONObject();
                try {
                    ret.put("data", null);
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                moduleContext.success(ret, true);
            }
        } catch (Exception e) {
            error(moduleContext, - 1, e.getMessage());
        }
    }

    /**
     * 发送数据
     */
    public void jsmethod_sendData(final UZModuleContext moduleContext){
        try {
            byte[] buffer = null;
            String data = moduleContext.optString("data");//发送的数据

            try {
                buffer = data.getBytes();
            } catch (Exception e) {
                error(moduleContext, 0, "参数解析失败");
                return;
            }

            if(btSocket != null){
                try {
                    btOutStream.write(buffer);
                    successState(moduleContext, true);
                } catch (IOException e) {
                    error(moduleContext, 1, "发送失败");
                }
            } else {
                error(moduleContext, 2, "未获取到socket");
            }
        } catch (Exception e) {
            error(moduleContext, - 1, e.getMessage());
        }
    }

    /**
     * 读取数据
     *
     * @param moduleContext moduleContext
     */
    public void jsmethod_readData(final UZModuleContext moduleContext){
        try {

            int setBufferSize = moduleContext.optInt("bufferSize");
            if(setBufferSize == 0){
                setBufferSize = 256;
            }
            final int bufferSize = setBufferSize;
            try {
                if(readDataThread != null){
                    readThreadState = false;
                    readDataThread.interrupt();
                }
            } catch (Exception ignored) {

            }
            if(btSocket == null){
                error(moduleContext, 0, "未连接到蓝牙设备");
                return;
            }

            readDataThread = new Thread(new Runnable(){
                @Override
                public void run(){
                    try {
                        btInStream = btSocket.getInputStream();
                    } catch (IOException e) {
                        error(moduleContext, 1, "获取输入流失败");
                    }
                    readThreadState = true;
                    long t = System.nanoTime();
                    Thread curr = Thread.currentThread();

                    while(readThreadState){
                        try {
                            if(curr.isInterrupted()){
                                break;
                            }

                            long ct = System.nanoTime();
                            //心跳检测
                            if(ct - t > 1000000000){
                                btOutStream.write(0x00);
                                t = ct;
                            }

                            if(btInStream.available() != 0){
                                //Thread.sleep(5);
                                byte[] buffer = new byte[bufferSize];
                                int len = btInStream.read(buffer);
                                if(len == 0){
                                    continue;
                                }
                                byte[] subBuffer = subBytes(buffer, 0, len);
                                String dataStr = new String(subBuffer);
                                successKeep(moduleContext, dataStr);

                                dataStr = dataStr.replace("\r", "").replace("\n", "");
                                receiveDataStr.append(dataStr);

                                try {
                                    parseCommand(new ResolveCommandCallback(){
                                        public void resolve(String eventName, String dataBody, String timeId, String checkCode){
                                            //发送
                                            if(eventName != null && ! eventName.isEmpty()){
                                                JSONArray eventData = new JSONArray();
                                                eventData.put(eventName);
                                                eventData.put(dataBody);
                                                eventData.put(timeId);
                                                eventData.put(checkCode);

                                                successKeep(moduleContext, eventData);
                                            }
                                        }
                                    });
                                } catch (Exception ignored) {

                                }

                                int strLen = receiveDataStr.length();
                                if(strLen > 3000){
                                    receiveDataStr.delete(0, strLen - 1500);
                                }
                            }
                        } catch (Exception e) {
                            readThreadState = false;
                            closeBtSocket();
                            error(moduleContext, 2, "读取数据失败");
                        }
                    }
                }
            });
            readDataThread.start();
        } catch (Exception e) {
            error(moduleContext, - 1, e.getMessage());
        }
    }

    private byte[] subBytes(byte[] src, int begin, int count){
        byte[] bs = new byte[count];
        System.arraycopy(src, begin, bs, 0, count);
        return bs;
    }

    private int indexOfChars(char[] src, char b){
        int len = src.length;
        for(int i = 0; i < len; i++){
            if(src[i] == b){
                return i;
            }
        }
        return - 1;
    }

    private String charsToString(char[] chars){
        int index = indexOfChars(chars, '\0');
        if(index > - 1){
            return new String(chars, 0, index);
        }
        return "";
    }

    /**
     * 解析命令
     */
    private void parseCommand(ResolveCommandCallback callback){
        boolean isStart = false;
        boolean isEnd = false;
        int endIndex = 0;
        byte splitIndex = 0;
        int charIndex = 0;
        int bodyLen = 1024; //body体长度

        char eventName[] = new char[32];  //指令名
        char dataBody[] = new char[bodyLen]; //数据
        char timeId[] = new char[6];   //时间
        char checkCode[] = new char[6]; //验证码

        int len = receiveDataStr.length();
        if(len == 0){
            return;
        }

        for(int i = 0; i < len; i++){
            char c = receiveDataStr.charAt(i);
            if(c == '{'){
                isStart = true;
                continue;
            }
            if(! isStart){
                continue;
            }
            if(c == '}'){
                if(eventName[0] > 0){
                    callback.resolve(charsToString(eventName), charsToString(dataBody), charsToString(timeId), charsToString(checkCode));
                }
                endIndex = i;
                isStart = false;
                splitIndex = 0;
                charIndex = 0;
                clearCharArray(eventName);
                clearCharArray(dataBody);
                clearCharArray(timeId);
                clearCharArray(checkCode);
                continue;
            }
            if(c == ','){
                splitIndex++;
                charIndex = 0;
                continue;
            }
            if(splitIndex == 0){
                if(charIndex >= 31){
                    continue;
                }
                eventName[charIndex] = c;
                charIndex++;
            }
            if(splitIndex == 1){
                if(charIndex >= bodyLen - 1){
                    continue;
                }
                dataBody[charIndex] = c;
                charIndex++;
                continue;
            }
            if(splitIndex == 2){
                if(charIndex >= 5){
                    continue;
                }
                timeId[charIndex] = c;
                charIndex++;
                continue;
            }
            if(splitIndex == 3){
                if(charIndex >= 5){
                    continue;
                }
                checkCode[charIndex] = c;
                charIndex++;
            }
        }

        //截取缓存的字符串
        if(endIndex > 0){
            if(endIndex < len - 1){
                receiveDataStr.delete(0, endIndex + 1);
            } else {
                receiveDataStr.delete(0, len);
            }
        }
    }

    /**
     * 清空char 数组
     *
     * @param chars chars
     */
    private void clearCharArray(char[] chars){
        int len = chars.length;
        for(int i = 0; i < len; i++){
            chars[i] = '\0';
        }
    }

}
