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

import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ResponseUtil {
    public static void success(final UZModuleContext moduleContext, String msg) {
        if (moduleContext == null) {
            return;
        }
        JSONObject ret = new JSONObject();
        try {
            ret.put("data", msg);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        moduleContext.success(ret, true);
    }

    public static void successKeep(final UZModuleContext moduleContext, String msg) {
        if (moduleContext == null) {
            return;
        }
        JSONObject ret = new JSONObject();
        try {
            ret.put("data", msg);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        moduleContext.success(ret, false);
    }

    public static void successState(final UZModuleContext moduleContext, boolean state) {
        if (moduleContext == null) {
            return;
        }
        JSONObject ret = new JSONObject();
        try {
            ret.put("state", state);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        moduleContext.success(ret, true);
    }

    public static void successStatus(final UZModuleContext moduleContext, boolean status) {
        if (moduleContext == null) {
            return;
        }
        JSONObject ret = new JSONObject();
        try {
            ret.put("status", status);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        moduleContext.success(ret, true);
    }

    public static void success(final UZModuleContext moduleContext, boolean option) {
        if (moduleContext == null) {
            return;
        }
        JSONObject ret = new JSONObject();
        try {
            ret.put("data", option);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        moduleContext.success(ret, true);
    }

    public static void successKeep(final UZModuleContext moduleContext, boolean data) {
        if (moduleContext == null) {
            return;
        }
        JSONObject ret = new JSONObject();
        try {
            ret.put("data", data);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        moduleContext.success(ret, false);
    }


    public static void success(final UZModuleContext moduleContext, JSONObject data) {
        if (moduleContext == null) {
            return;
        }
        JSONObject ret = new JSONObject();
        try {
            ret.put("data", data);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        moduleContext.success(ret, true);
    }

    public static void successKeep(final UZModuleContext moduleContext, JSONObject data) {
        if (moduleContext == null) {
            return;
        }
        JSONObject ret = new JSONObject();
        try {
            ret.put("data", data);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        moduleContext.success(ret, false);
    }

    public static void success(final UZModuleContext moduleContext, JSONArray data) {
        if (moduleContext == null) {
            return;
        }
        JSONObject ret = new JSONObject();
        try {
            ret.put("data", data);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        moduleContext.success(ret, true);
    }

    public static void successKeep(final UZModuleContext moduleContext, JSONArray data) {
        if (moduleContext == null) {
            return;
        }
        JSONObject ret = new JSONObject();
        try {
            ret.put("data", data);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        moduleContext.success(ret, false);
    }

    public static void error(final UZModuleContext moduleContext, int code, String msg) {
        if (moduleContext == null) {
            return;
        }
        JSONObject ret = new JSONObject();
        try {
            ret.put("msg", msg);
            ret.put("code", code);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        moduleContext.error(ret, true);
    }

    public static void errorKeep(final UZModuleContext moduleContext, int code, String msg) {
        if (moduleContext == null) {
            return;
        }
        JSONObject ret = new JSONObject();
        try {
            ret.put("msg", msg);
            ret.put("code", code);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        moduleContext.error(ret, false);
    }


    public static byte hexStrToByte(String hexbytein) {
        return (byte) Integer.parseInt(hexbytein, 16);
    }

    public static byte[] Str2Hex(String hexStrIn) {
        int hexlen = hexStrIn.length() / 2;
        byte[] result;
        result = new byte[hexlen];
        for (int i = 0; i < hexlen; i++) {
            result[i] = hexStrToByte(hexStrIn.substring(i * 2, i * 2 + 2));
        }
        return result;
    }

    public static StringBuffer Hex2Str(byte[] hexByteIn) {
        int len = hexByteIn.length;
        StringBuffer restult = new StringBuffer();
        for (int i = 0; i < len; i++) {
            restult.append(" ");
            restult.append(String.format("%02x", hexByteIn[i]));
        }
        return restult;
    }


    public static byte[] subBytes(byte[] src, int begin, int count) {
        byte[] bs = new byte[count];
        System.arraycopy(src, begin, bs, 0, count);
        return bs;
    }

    public static byte[] subBytes(byte[] src, int begin, int count, int firstByte) {
        byte[] bs = new byte[count + 1];
        bs[0] = (byte) firstByte;
        System.arraycopy(src, begin, bs, 1, count);
        return bs;
    }

    public static int indexOfChars(char[] src, char b) {
        int len = src.length;
        for (int i = 0; i < len; i++) {
            if (src[i] == b) {
                return i;
            }
        }
        return -1;
    }

    public static String charsToString(char[] chars) {
        int index = indexOfChars(chars, '\0');
        if (index > -1) {
            return new String(chars, 0, index);
        }
        return "";
    }

    /**
     * 清空char 数组
     *
     * @param chars chars
     */
    public static void clearCharArray(char[] chars) {
        int len = chars.length;
        for (int i = 0; i < len; i++) {
            chars[i] = '\0';
        }
    }

}
