
<p style="color: #ccc; margin-bottom: 30px;">来自于：开发者<a style="background-color: #95ba20; color:#fff; padding:4px 8px;border-radius:5px;margin-left:30px; margin-bottom:0px; font-size:12px;text-decoration:none;" target="_blank" href="//www.apicloud.com/mod_detail/cxgBluetooth">立即使用</a></p>

<div class="outline">

[isEnabledBluetooth](#isEnabledBluetooth)

[openBluetooth](#openBluetooth)

[closeBluetooth](#closeBluetooth)

[listenBluetoothStatus](#listenBluetoothStatus)

[bondedDevices](#bondedDevices)

[isScanning](#isScanning)

[scan](#scan)

[stopScan](#stopScan)

[connect](#connect)

[disconnect](#disconnect)

[sendData](#sendData)

[setReadDataCallback](#setReadDataCallback)

[syncClientsInfo](#syncClientsInfo)

[startServer](#startServer)

[stopServer](#stopServer)

[disconnectServerClient](#disconnectServerClient)

[sendDataServerClient](#sendDataServerClient)

[setReadDataCallbackServerClient](#setReadDataCallbackServerClient)

[syncServersInfo](#syncServersInfo)

</div>

# **论坛示例**

模块demo论坛帖示例：https://community.apicloud.com/bbs/thread-171310-1-1.html 

该demo示例使用了所有的接口，比较完善，甚至可以代替蓝牙串口助手。

有关建议,bug及问题讨论等在此发帖。

# **概述**

**cxgBluetooth 模块概述**

蓝牙串口通信的基本支持。  
作为客户端支持同时连接多个不同的蓝牙设备。  
作为客户端支持同时连接同一个设备的不同UUID的服务。  
作为服务端支持同时开启多个不同UUID服务。  
允许同时作为客户端和服务端。  
支持手机与手机之间的通信，支持手机与ESP32、经典蓝牙模块等下位机之间的通信。  
所有发送读取数据均支持返回HEX数据。  
空闲超过1秒自动发送一个心跳包，能够监听蓝牙外设主动断开。  
支持多页面应用，页面刷新状态保存，设备不掉线。  
该模块暂时不支持蓝牙ble。  

## **模块接口**

<div id="isEnabledBluetooth"></div>

# **isEnabledBluetooth**

获取当前的蓝牙状态

isEnabledBluetooth({}, callback(ret, err))

## callback(ret, err)

ret：

- 类型：JSON 对象
- 内部字段：

```js
{
  //当前设备是否开启蓝牙
  state: true; //布尔型；true|false
}
```

err：

- 类型：JSON 对象
- 内部字段：

```js
{
    code: 0,    //数字类型: 错误代码
                //-1(未知错误)
                //0(不支持蓝牙)
    msg:""      //字符串类型；
                //对应错误码的错误信息
}
```

## 示例代码

```js
api.require("cxgBluetoothBase").isEnabledBluetooth({}, function(ret, err) {
  if (!err) {
    alert(ret.state);
  } else {
    alert("code: " + err.code + " msg: " + err.msg);
  }
});
```

## 可用性

Android 系统

可提供1.0.0及更高的版本

<div id="openBluetooth"></div>

# **openBluetooth**

打开蓝牙

openBluetooth({}, callback(ret, err))

## callback(ret, err)

ret：

- 类型：JSON 对象
- 内部字段：

```js
{
  state: true; //布尔类型 蓝牙已经打开
}
```

err：

- 类型：JSON 对象
- 内部字段：

```js
{
  code: 0, //数字类型；
  //错误码：
  //-1(未知错误）
  //0（没有蓝牙）
  //1（未获取到activity）
  //2（请求打开蓝牙）
  msg:"" //字符串类型；
  //对应错误码的错误信息
}
```

## 示例代码

```js
api.require("cxgBluetoothBase").openBluetooth({}, function(ret, err) {
  if (!err) {
    alert(ret.state);
  } else {
    alert("code: " + err.code + " msg: " + err.msg);
  }
});
```

## 可用性

Android 系统

可提供1.0.0及更高的版本

<div id="closeBluetooth"></div>

# **closeBluetooth**

关闭蓝牙

closeBluetooth({}, callback(ret, err))

## callback(ret, err)

ret：

- 类型：JSON 对象
- 内部字段：

```js
{
  state: true; //布尔类型
  //true 蓝牙关闭成功
  //false 蓝牙已经关闭
}
```

err：

- 类型：JSON 对象
- 内部字段：

```js
{
  code: 0, //数字类型；
  //错误码：
  //-1(未知错误）
  msg:"" //字符串类型；
  //对应错误码的错误信息
}
```

## 示例代码

```js
api.require("cxgBluetoothBase").closeBluetooth({}, function(ret, err) {
  if (!err) {
    alert(ret.state);
  } else {
    alert("code: " + err.code + " msg: " + err.msg);
  }
});
```

## 可用性

Android 系统

可提供1.0.0及更高的版本

<div id="listenBluetoothStatus"></div>

# **listenBluetoothStatus**

监听蓝牙的状态变化， 多次返回

listenBluetoothStatus({}, callback(ret, err))

## callback(ret, err)

ret：

- 类型：JSON 对象
- 内部字段：

```js
{
  status: ""; //字符串
  //STATE_TURNING_ON (蓝牙正在打开)
  //STATE_ON (蓝牙已经打开)
  //STATE_TURNING_OFF (蓝牙正在关闭)
  //STATE_OFF (蓝牙已经关闭)
}
```

err：

- 类型：JSON 对象
- 内部字段：

```js
{
  code: 0, //数字类型；
  //错误码：
  //-1(未知错误）
  msg:"" //字符串类型；
  //对应错误码的错误信息
}
```

## 示例代码

```js
api.require("cxgBluetoothBase").listenBluetoothStatus({}, function(ret, err) {
  if (!err) {
    alert(ret.status);
  } else {
    alert("code: " + err.code + " msg: " + err.msg);
  }
});
```

## 可用性

Android 系统

可提供1.0.0及更高的版本

<div id="bondedDevices"></div>

# **bondedDevices**

获取已经配对的设备

bondedDevices({}, callback(ret, err))

## callback(ret, err)

ret：

- 类型：JSON 对象
- 内部字段：

```js
{
  //数组 已经配对的设备
  data: [
    {
      name: "", //字符串 蓝牙名称
      address: "" //字符串 蓝牙地址
    }
  ];
}
```

err：

- 类型：JSON 对象
- 内部字段：

```js
{
  code: 0, //数字类型；
  //错误码：
  //-1(未知错误）
  msg:"" //字符串类型；
  //对应错误码的错误信息
}
```

## 示例代码

```js
api.require("cxgBluetoothBase").bondedDevices({}, function(ret, err) {
  if (!err) {
    alert(JSON.stringify(ret.data));
  } else {
    alert("code: " + err.code + " msg: " + err.msg);
  }
});
```

## 可用性

Android 系统

可提供1.0.0及更高的版本

<div id="isScanning"></div>

# **isScanning**

是否正在搜索设备

isScanning({}, callback(ret, err))

## callback(ret, err)

ret：

- 类型：JSON 对象
- 内部字段：

```js
{
  state: true; //布尔类型 true|false
}
```

err：

- 类型：JSON 对象
- 内部字段：

```js
{
  code: 0, //数字类型；
  //错误码：
  //-1(未知错误）
  msg:"" //字符串类型；
  //对应错误码的错误信息
}
```

## 示例代码

```js
api.require("cxgBluetoothBase").isScanning({}, function(ret, err) {
  if (!err) {
    alert(ret.state);
  } else {
    alert("code: " + err.code + " msg: " + err.msg);
  }
});
```

## 可用性

Android 系统

可提供1.0.0及更高的版本

<div id="scan"></div>

# **scan**

扫描周边设备

scan({}, callback(ret, err))

## callback(ret, err)

ret：

- 类型：JSON 对象
- 内部字段：

```js
{
  //返回搜索到的设备, 会多次返回
  data: {
      name:"", //蓝牙名称
      address:"" //蓝牙地址
  },
  //搜索完成后, data 返回字符串 "ACTION_DISCOVERY_FINISHED"
}
```

err：

- 类型：JSON 对象
- 内部字段：

```js
{
  code: 0, //数字类型；
  //错误码：
  //-1(未知错误)
  //0(当前未打开蓝牙)
  //1(请授予相关权限)
  //2(请手动开启位置信息, 否则不能搜索蓝牙设备！)
  msg:"" //字符串类型；
  //对应错误码的错误信息
}
```

## 示例代码

```js
api.require("cxgBluetoothBase").scan({}, function(ret, err) {
  if (!err) {
    if (ret.data == "ACTION_DISCOVERY_FINISHED") {
      alert("搜索完成");
    } else {
      alert(JSON.stringify(ret.data));
    }
  } else {
    alert("code: " + err.code + " msg: " + err.msg);
  }
});
```

## 可用性

Android 系统

可提供1.0.0及更高的版本

<div id="stopScan"></div>

# **stopScan**

停止扫描周边设备

stopScan({}, callback(ret, err))

## callback(ret, err)

ret：

- 类型：JSON 对象
- 内部字段：

```js
{
  state: true; //布尔类型 只有true
}
```

err：

- 类型：JSON 对象
- 内部字段：

```js
{
  code: 0, //数字类型；
  //错误码：
  //-1(未知错误)
  msg:"" //字符串类型；
  //对应错误码的错误信息
}
```

## 示例代码

```js
api.require("cxgBluetoothBase").stopScan({}, function(ret, err) {
  if (!err) {
    alert(ret.state);
  } else {
    alert("code: " + err.code + " msg: " + err.msg);
  }
});
```

## 可用性

Android 系统

可提供1.0.0及更高的版本

<div id="connect"></div>

# **connect**

作为客户端连接到设备，回调函数多次返回

connect({params}, callback(ret, err))

## params

address：

- 类型：字符串
- 示例: "AB:35:57:57:34:02"
- 描述：蓝牙设备的地址

UUID：

- 类型： 字符串
- 示例:  "00001101-0000-1000-8000-00805F9B34FB"
- 描述： 蓝牙服务UUID

## callback(ret, err)

ret：

- 类型：JSON 对象
- 内部字段：

```js
{
  data: true; //布尔类型true 连接成功
  //布尔类型false 断开连接
}
```

err：

- 类型：JSON 对象
- 内部字段：

```js
{
  code: 0, //数字类型；
  //错误码：
  //-1(未知错误)
  //5(该客户端已连接)
  //11(创建socket失败)
  //12(连接失败)
  msg:"" //字符串类型；
  //对应错误码的错误信息
}
```

## 示例代码

```js
api.require("cxgBluetoothClient").bluetoothSerial.connect(
  {
    address: "AB:35:57:57:34:02",
    UUID:"00001101-0000-1000-8000-00805F9B34FB"
  },
  function(ret, err) {
   //多次返回
    if (!err) {
        if (ret.data === true) {
            alet("连接成功");
        }
        //设备断开
        if (ret.data.disconnectAddress) {
            alet("断开连接成功");
        }
    } else {
        alert("code: " + err.code + " msg: " + err.msg);
    }
  }
);
```

## 补充说明

回调函数多次返回

## 可用性

Android 系统

可提供1.0.0及更高的版本

<div id="disconnect"></div>

# **disconnect**

断开连接

disconnect({params}, callback(ret, err))

## params

address：

- 类型：字符串
- 示例: "AB:35:57:57:34:02"
- 描述：蓝牙设备的地址

UUID：

- 类型： 字符串
- 示例:  "00001101-0000-1000-8000-00805F9B34FB"
- 描述： 蓝牙服务UUID

## callback(ret, err)

ret：

- 类型：JSON 对象
- 内部字段：

```js
{
  data: true; //布尔类型 true:断开成功
  //false, 已经断开
}
```

err：

- 类型：JSON 对象
- 内部字段：

```js
{
  code: 0, //数字类型；
  //错误码：
  //-1(未知错误)
  msg:"" //字符串类型；
  //对应错误码的错误信息
}
```

## 示例代码

```js
api.require("cxgBluetoothClient").disconnect({
  address: "AB:35:57:57:34:02",
  UUID:"00001101-0000-1000-8000-00805F9B34FB"
}, function(ret, err) {
  if (!err) {
    alert("断开连接成功");
  } else {
    alert("code: " + err.code + " msg: " + err.msg);
  }
});
```

## 可用性

Android 系统

可提供1.0.0及更高的版本

<div id="sendData"></div>

# **sendData**

发送数据

sendData({params}, callback(ret, err))

## params

address：

- 类型：字符串
- 示例: "AB:35:57:57:34:02"
- 描述：蓝牙设备的地址

UUID：

- 类型： 字符串
- 示例:  "00001101-0000-1000-8000-00805F9B34FB"
- 描述： 蓝牙服务UUID

data：

- 类型：字符串
- 描述：需要发送的字符串

isHex:

- 类型：布尔
- 描述：是否为HEX格式

charset:

- 类型：字符串
- 描述：v1.1.0添加 发送字符串时的编码格式, 默认UTF-8

## callback(ret, err)

ret：

- 类型：JSON 对象
- 内部字段：

```js
{
  data: true; //波尔类型 发送成功
}
```

err：

- 类型：JSON 对象
- 内部字段：

```js
{
  code: 0, //数字类型；
  //错误码：
  //-1(未知错误)
  //11(参数解析失败)
  //12(发送失败, 已断开连接)
  //13(未获取到socket, 已断开连接)
  msg:"" //字符串类型；
  //对应错误码的错误信息
}
```

## 示例代码

```js
api.require("cxgBluetoothClient").sendData(
  {
    address: "AB:35:57:57:34:02",
    UUID:"00001101-0000-1000-8000-00805F9B34FB",
    data: "hello word",
    isHex:false,
    // data: "0100AABB",
    // isHex:true
  },
  function(ret, err) {
    if (!err) {
      alert("发送成功");
    } else {
      alert("code: " + err.code + " msg: " + err.msg);
    }
  }
);
```

## 可用性

Android 系统

可提供1.0.0及更高的版本

<div id="setReadDataCallback"></div>

# **setReadDataCallback**

设置读取数据回调，多次返回

setReadDataCallback({params}, callback(ret, err))

## params

address：

- 类型：字符串
- 示例: "AB:35:57:57:34:02"
- 描述：蓝牙设备的地址

UUID：

- 类型： 字符串
- 示例:  "00001101-0000-1000-8000-00805F9B34FB"
- 描述： 蓝牙服务UUID

bufferSize：

- 类型：数字类型, 正整数
- 默认值：1024
- 描述：单次数据读取 buffer 的大小

isReturnHex：

- 类型：布尔
- 默认值：false
- 描述：是否返回HEX格式的数据

isNoReturnData：

- 类型：数字类型
- 默认值：false
- 描述：是否不返回数据, 接收了但是不返回前端

returnStrCharset:

- 类型：字符串
- 描述：v1.1.0添加 接收数据时, 解析字符串使用的编码格式, 默认UTF-8

## callback(ret, err)

ret：

- 类型：JSON 对象
- 内部字段：

```js
{
  data: "";
  //字符串 读取到的数据
  //布尔类型true, 表示设置回调成功
}
```

err：

- 类型：JSON 对象
- 内部字段：

```js
{
  code: 0, //数字类型；
  //错误码：
  //-1(未知错误)
  //11(未连接到蓝牙设备)
  //21(读取线程获取输入流失败)
  //22(读取数据失败, 连接已断开)
  msg:"" //字符串类型；
  //对应错误码的错误信息
}
```

## 示例代码

```js
api.require("cxgBluetoothClient").setReadDataCallback(
  {
    address: "AB:35:57:57:34:02",
    UUID:"00001101-0000-1000-8000-00805F9B34FB",
    bufferSize: 1024,
    isReturnHex: false,
    isNoReturnData: false,
  },
  function(ret, err) {
    if (!err) {
      if (ret.data === true) {
          alert("设置成功);
          return;
      }
      alert(ret.data);
    } else {
      alert("code: " + err.code + " msg: " + err.msg);
    }
  }
);
```

## 补充说明

在设备连接成功后，手机会每秒自动向设备发送”\0”空字符，来进行心跳检测。

回调多次返回, 刷新页面时需要重新设置回调。

## 可用性

Android 系统

可提供1.0.0及更高的版本

<div id="syncClientsInfo"></div>

# **syncClientsInfo**

同步已连接的客户端数据, 用于刷新页面初始化

syncClientsInfo({}, callback(ret, err))

## callback(ret, err)

ret：

- 类型：JSON 对象
- 内部字段：

```js
{
  data: [
    {
      name:"",//蓝牙名称
      address:"",//蓝牙地址
      UUID:"",//服务UUID
      isReturnHex:false,//上次设置的读取数据是否返回hex数据
      isServerClient:false,//是否为服务端被动连接的客户端, 这里一定是false
      readBufferSize:1024,//上次设置的读取数据的单次读取大小
    }
  ]
  //客户端信息, 用于页面初始化
}
```

err：

- 类型：JSON 对象
- 内部字段：

```js
{
  code: 0, //数字类型；
  //错误码：
  //-1(未知错误)
  msg:"" //字符串类型；
  //对应错误码的错误信息
}
```

## 示例代码

```js
api.require("cxgBluetoothClient").syncClientsInfo(
  {
  },
  function(ret, err) {
    if (!err) {
      //具体使用方法见demo
      alert(JSON.stringify(ret.data));
    } else {
      alert("code: " + err.code + " msg: " + err.msg);
    }
  }
);
```

## 补充说明

服务端信息, 刷新页面时获取数据来初始化

## 可用性

Android 系统

可提供1.0.0及更高的版本


<div id="startServer"></div>

# **startServer**

开启蓝牙服务, 多次返回

startServer({params}, callback(ret, err))

## params

isNewStartServer:

- 类型： 布尔
- 示例:  false
- 描述： 是否重新启动服务, 设为true, 外设会掉线


Server_UUID：

- 类型： 字符串
- 示例:  "00001101-0000-1000-8000-00805F9B34FB"
- 描述： 蓝牙服务UUID

## callback(ret, err)

ret：

- 类型：JSON 对象
- 内部字段：

```js
{
  data: true; //布尔类型 true:开启成功
  //返回Object {
  // name:"",
  // address:""  
  //} 连接的设备
}
```

err：

- 类型：JSON 对象
- 内部字段：

```js
{
  code: 0, //数字类型；
  //错误码：
  //-1(未知错误)
  //21(创建服务失败)
  msg:"" //字符串类型；
  //对应错误码的错误信息
}
```

## 示例代码

```js
api.require("cxgBluetoothServer").startServer({
  isNewStartServer: false,
  Server_UUID:"00001101-0000-1000-8000-00805F9B34FB"
}, function(ret, err) {
  if (!err) {
    if (ret.data === true) {
      alert("创建服务成功");
    }
    if (typeof ret.data == "object") {
      alert(JSON.stringify(ret.data));
    }
    if (ret.data.disconnectAddress){
      alert("外设断开");
    }
  } else {
    alert("code: " + err.code + " msg: " + err.msg);
  }
});
```

## 补充说明

回调多次返回, 刷新页面时需要重新设置回调。

## 可用性

Android 系统

可提供1.0.0及更高的版本


<div id="stopServer"></div>

# **stopServer**

停止蓝牙服务

stopServer({params}, callback(ret, err))

## params

isRemove:

- 类型： 布尔
- 示例:  false
- 描述： 是否将服务对象移除

Server_UUID：

- 类型： 字符串, 为空表示停止所有服务
- 示例:  "00001101-0000-1000-8000-00805F9B34FB"
- 描述： 蓝牙服务UUID

## callback(ret, err)

ret：

- 类型：JSON 对象
- 内部字段：

```js
{
  data: true; //布尔类型 true:开启成功
  //false:服务不存在
}
```

err：

- 类型：JSON 对象
- 内部字段：

```js
{
  code: 0, //数字类型；
  //错误码：
  //-1(未知错误)
  msg:"" //字符串类型；
  //对应错误码的错误信息
}
```

## 示例代码

```js
api.require("cxgBluetoothServer").stopServer({
  isRemove: false,
  Server_UUID:"00001101-0000-1000-8000-00805F9B34FB"
}, function(ret, err) {
  if (!err) {
      alert("服务关闭");
  } else {
    alert("code: " + err.code + " msg: " + err.msg);
  }
});
```

## 可用性

Android 系统

可提供1.0.0及更高的版本

<div id="disconnectServerClient"></div>

# **disconnectServerClient**

服务端主动断开连接

disconnectServerClient({params}, callback(ret, err))

## params

Server_UUID ：

- 类型： 字符串
- 示例:  "00001101-0000-1000-8000-00805F9B34FB"
- 描述： 蓝牙服务UUID

## callback(ret, err)

ret：

- 类型：JSON 对象
- 内部字段：

```js
{
  data: true; //布尔类型 true:断开成功
  //false, 已经断开
}
```

err：

- 类型：JSON 对象
- 内部字段：

```js
{
  code: 0, //数字类型；
  //错误码：
  //-1(未知错误)
  msg:"" //字符串类型；
  //对应错误码的错误信息
}
```

## 示例代码

```js
api.require("cxgBluetoothClient").disconnectServerClient({
  Server_UUID:"00001101-0000-1000-8000-00805F9B34FB"
}, function(ret, err) {
  if (!err) {
    alert("断开连接成功");
  } else {
    alert("code: " + err.code + " msg: " + err.msg);
  }
});
```

## 可用性

Android 系统

可提供1.0.0及更高的版本

<div id="sendDataServerClient"></div>

# **sendDataServerClient**

发送数据

sendDataServerClient({params}, callback(ret, err))

## params

address：

- 类型：字符串
- 示例: "AB:35:57:57:34:02"
- 描述：蓝牙设备的地址

Server_UUID：

- 类型： 字符串
- 示例:  "00001101-0000-1000-8000-00805F9B34FB"
- 描述： 蓝牙服务UUID

data：

- 类型：字符串
- 描述：需要发送的字符串

isHex:

- 类型：布尔
- 描述：是否为HEX格式

charset:

- 类型：字符串
- 描述：v1.1.0添加 发送字符串时编码格式, 默认UTF-8

## callback(ret, err)

ret：

- 类型：JSON 对象
- 内部字段：

```js
{
  data: true; //波尔类型 发送成功
}
```

err：

- 类型：JSON 对象
- 内部字段：

```js
{
  code: 0, //数字类型；
  //错误码：
  //-1(未知错误)
  //5(蓝牙设备未连接)
  //11(参数解析失败)
  //12(发送失败, 已断开连接)
  //13(未获取到socket, 已断开连接)
  msg:"" //字符串类型；
  //对应错误码的错误信息
}
```

## 示例代码

```js
api.require("cxgBluetoothServer").sendDataServerClient(
  {
    Server_UUID:"00001101-0000-1000-8000-00805F9B34FB",
    data: "hello word",
    isHex:false
  },
  function(ret, err) {
    if (!err) {
      alert("发送成功");
    } else {
      alert("code: " + err.code + " msg: " + err.msg);
    }
  }
);
```

## 可用性

Android 系统

可提供1.0.0及更高的版本

<div id="setReadDataCallbackServerClient"></div>

# **setReadDataCallbackServerClient**

读取数据回调，多次返回

setReadDataCallbackServerClient({params}, callback(ret, err))

## params

Server_UUID：

- 类型： 字符串
- 示例:  "00001101-0000-1000-8000-00805F9B34FB"
- 描述： 蓝牙服务UUID

bufferSize：

- 类型：数字类型, 正整数
- 默认值：1024
- 描述：单次数据读取 buffer 的大小

isReturnHex：

- 类型：布尔
- 默认值：false
- 描述：是否返回HEX格式的数据

isNoReturnData：

- 类型：数字类型
- 默认值：false
- 描述：是否不返回数据, 接收了但是不返回前端

returnStrCharset:

- 类型：字符串
- 描述：v1.1.0添加 接收数据时, 解析字符串使用的编码格式, 默认UTF-8

## callback(ret, err)

ret：

- 类型：JSON 对象
- 内部字段：

```js
{
  data: "";
  //字符串 读取到的数据
  //布尔类型true, 表示设置回调成功
}
```

err：

- 类型：JSON 对象
- 内部字段：

```js
{
  code: 0, //数字类型；
  //错误码：
  //-1(未知错误)
  //11(未连接到蓝牙设备)
  //21(读取线程获取输入流失败)
  //22(读取数据失败, 连接已断开)
  msg:"" //字符串类型；
  //对应错误码的错误信息
}
```

## 示例代码

```js
api.require("cxgBluetoothServer").setReadDataCallbackServerClient(
  {
    address: "AB:35:57:57:34:02",
    UUID:"00001101-0000-1000-8000-00805F9B34FB",
    bufferSize: 1024,
    isReturnHex: false,
    isNoReturnData: false,
  },
  function(ret, err) {
    if (!err) {
      if (ret.data === true) {
          alert("设置成功);
          return;
      }
      alert(ret.data);
    } else {
      alert("code: " + err.code + " msg: " + err.msg);
    }
  }
);
```

## 补充说明

在设备连接成功后，手机在空闲(既没发送数据,又没接收到数据)超过1s时自动向设备发送”\0”空字符，来进行心跳检测。

回调多次返回, 刷新页面时需要重新设置回调。

## 可用性

Android 系统

可提供1.0.0及更高的版本

<div id="syncServersInfo"></div>

# **syncServersInfo**

同步服务端数据,用于刷新页面初始化

syncServersInfo({}, callback(ret, err))

## callback(ret, err)

ret：

- 类型：JSON 对象
- 内部字段：

```js
{
  data: [
    {
      isServerRun:false,
      Server_UUID:"",
      //被连接的客户端信息, 复用客户端对象
      client:{
        name:"",//蓝牙名称
        address:"",//蓝牙地址
        UUID:"",//服务UUID
        isReturnHex:false,//上次设置的读取数据是否返回hex数据
        isServerClient:true,//是否为服务端被动连接的客户端, 这里一定是true
        readBufferSize:1024,//上次设置的读取数据的单次读取大小
      }
    }
  ]
  //服务端信息, 用于页面初始化
}
```

err：

- 类型：JSON 对象
- 内部字段：

```js
{
  code: 0, //数字类型；
  //错误码：
  //-1(未知错误)
  msg:"" //字符串类型；
  //对应错误码的错误信息
}
```

## 示例代码

```js
api.require("cxgBluetoothServer").syncServersInfo(},
  function(ret, err) {
    if (!err) {
      // 具体使用方法见demo
      alert(JSON.stringify(ret.data));
    } else {
      alert("code: " + err.code + " msg: " + err.msg);
    }
  }
);
```

## 补充说明

服务端信息, 刷新页面时获取数据来初始化

## 可用性

Android 系统

可提供1.0.0及更高的版本