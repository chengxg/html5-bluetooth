<p style="color: #ccc; margin-bottom: 30px;">来自于：开发者<a style="background-color: #95ba20; color:#fff; padding:4px 8px;border-radius:5px;margin-left:30px; margin-bottom:0px; font-size:12px;text-decoration:none;" target="_blank" href="//www.apicloud.com/mod_detail/cxgBluetoothSerial">立即使用</a></p>

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

[connectedDevice](#connectedDevice)

[sendData](#sendData)

[readData](#readData)

</div>

# **论坛示例**

模块demo论坛帖示例：https://community.apicloud.com/bbs/thread-147782-1-1.html

# **概述**

**蓝牙串口简介**

蓝牙串口通信的基本支持。

**cxgBluetoothSerial 模块概述**

本模块封装了与经典单片机蓝牙模块 HC-05，HC-06 等蓝牙串口模块的通信接口。

内置心跳检测及定义了一个简单的通信协议。

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
    code: 0,     //数字类型: 错误代码
    //-1(未知错误)
    //0(不支持蓝牙)
    msg:""      //字符串类型；
                //对应错误码的错误信息
}
```

## 示例代码

```js
var bluetoothSerial = api.require("cxgBluetoothSerial");
bluetoothSerial.isEnabledBluetooth({}, function(ret, err) {
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
var bluetoothSerial = api.require("cxgBluetoothSerial");
bluetoothSerial.openBluetooth({}, function(ret, err) {
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
  //蓝牙关闭成功
  //蓝牙已经关闭
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
var bluetoothSerial = api.require("cxgBluetoothSerial");
bluetoothSerial.closeBluetooth({}, function(ret, err) {
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
var bluetoothSerial = api.require("cxgBluetoothSerial");
bluetoothSerial.listenBluetoothStatus({}, function(ret, err) {
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
var bluetoothSerial = api.require("cxgBluetoothSerial");
bluetoothSerial.bondedDevices({}, function(ret, err) {
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
var bluetoothSerial = api.require("cxgBluetoothSerial");
bluetoothSerial.isScanning({}, function(ret, err) {
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
var bluetoothSerial = api.require("cxgBluetoothSerial");
bluetoothSerial.scan({}, function(ret, err) {
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
var bluetoothSerial = api.require("cxgBluetoothSerial");
bluetoothSerial.stopScan({}, function(ret, err) {
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

连接到设备

connect({params}, callback(ret, err))

## params

address：

- 类型：字符串
- 示例: "AB:35:57:57:34:02"
- 描述：蓝牙设备的地址

## callback(ret, err)

ret：

- 类型：JSON 对象
- 内部字段：

```js
{
  state: true; //布尔类型 连接成功
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
  //0(未获取到蓝牙连接地址)
  //1(创建socket失败)
  //2(连接失败)
  msg:"" //字符串类型；
  //对应错误码的错误信息
}
```

## 示例代码

```js
var bluetoothSerial = api.require("cxgBluetoothSerial");
bluetoothSerial.connect(
  {
    address: "AB:35:57:57:34:02"
  },
  function(ret, err) {
    if (!err) {
      alert("连接成功");
    } else {
      alert("code: " + err.code + " msg: " + err.msg);
    }
  }
);
```

## 可用性

Android 系统

可提供1.0.0及更高的版本

<div id="disconnect"></div>

# **disconnect**

断开连接

disconnect({}, callback(ret, err))

## callback(ret, err)

ret：

- 类型：JSON 对象
- 内部字段：

```js
{
  state: true; //布尔类型 断开成功
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
var bluetoothSerial = api.require("cxgBluetoothSerial");
bluetoothSerial.disconnect({}, function(ret, err) {
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

<div id="connectedDevice"></div>

# **connectedDevice**

获取已经连接的设备

connectedDevice({}, callback(ret, err))

## callback(ret, err)

ret：

- 类型：JSON 对象
- 内部字段：

```js
{
  //已经连接的蓝牙设备, 不存在时返回 null
  data: {
    name:"", //设备名称
    address:"" //设备地址
  }
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
var bluetoothSerial = api.require("cxgBluetoothSerial");
bluetoothSerial.connectedDevice({}, function(ret, err) {
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

<div id="sendData"></div>

# **sendData**

发送数据

sendData({params}, callback(ret, err))

## params

data：

- 类型：字符串
- 描述：需要发送的字符串

isHex:

- 类型：布尔
- 描述：是否为HEX格式 1.2.0版本 添加

## callback(ret, err)

ret：

- 类型：JSON 对象
- 内部字段：

```js
{
  state: true; //波尔类型 发送成功
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
  //0(参数解析失败)
  //1(发送失败)
  //2(未获取到socket)
  msg:"" //字符串类型；
  //对应错误码的错误信息
}
```

## 示例代码

```js
var bluetoothSerial = api.require("cxgBluetoothSerial");
bluetoothSerial.sendData(
  {
    data: "hello word",
    isHex:false,//1.2.0版本 添加
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

<div id="readData"></div>

# **readData**

读取数据回调，多次返回

readData({params}, callback(ret, err))

## params

bufferSize：

- 类型：数字类型, 正整数
- 默认值：256
- 描述：数据读取 buffer 的大小

isReturnHex：

- 类型：布尔
- 默认值：false
- 描述：是否返回HEX格式的数据 1.2.0版本 添加

## callback(ret, err)

ret：

- 类型：JSON 对象
- 内部字段：

```js
{
  data: "";
  //字符串 读取到的数据
  //数组 [eventName, dataBody, timeId, checkCode] 解析到的通信指令
  //数组第一项: 事件名称 1-32个字符
  //数组第二项: 数据体 1-1024个字符
  //数组第三项: 数字类型, 发送数据的时间, 0-99999
  //数组第四项: 数字类型, 验证码 0-99999
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
  //0(未连接到蓝牙设备)
  //1(获取输入流失败)
  //2(读取数据失败)
  msg:"" //字符串类型；
  //对应错误码的错误信息
}
```

## 示例代码

```js
var bluetoothSerial = api.require("cxgBluetoothSerial");
bluetoothSerial.readData(
  {
    bufferSize: 256,
    isReturnHex: false
  },
  function(ret, err) {
    if (!err) {
      if (typeof ret.data == "string") {
        alert(ret.data);
      } else {
        alert(JSON.stringify(ret.data));
      }
    } else {
      alert("code: " + err.code + " msg: " + err.msg);
    }
  }
);
```

## 补充说明

在设备连接成功后，手机会每秒自动向设备发送”\0”空字符，来进行心跳检测。

内置一个通信协议
"{eventName,dataBody,timeId,checkCode}"
如果接收到的数据字符串像这种形式，会自动进行解析。
示例:{info,speed=200&deg=90,12345,345}

eventName:事件名称 1-32个字母

dataBody: 不超过1024个字符, 并且不能含有"{},"字符

timeId: 数字类型, 下位机发送数据时的系统时间, 取值范围1-99999 

checkCode: 数字类型, 该数据的校验码, 自行设计, 取值范围1-99999 

## 可用性

Android 系统

可提供1.0.0及更高的版本
