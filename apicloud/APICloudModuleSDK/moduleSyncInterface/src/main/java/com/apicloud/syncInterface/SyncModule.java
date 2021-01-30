package com.apicloud.syncInterface;

import org.json.JSONArray;
import org.json.JSONObject;
import com.uzmap.pkg.uzcore.UZWebView;
import com.uzmap.pkg.uzcore.uzmodule.ModuleResult;
import com.uzmap.pkg.uzcore.uzmodule.UZModule;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;



public class SyncModule extends UZModule {

	/**
	 * 同步接口的基本使用方式为：在原先定义jsmethod的基础上以_sync后缀结尾，并返回经ModuleResult包装后的结果。
	 * @param webView
	 */
	public SyncModule(UZWebView webView) {
		super(webView);
	}

	//同步接口，返回int型数据，对应到js的number
	public ModuleResult jsmethod_int_sync(final UZModuleContext context){
		
		return new ModuleResult(10);
	}
	
	//同步接口，返回float型数据，对应到js的number
	public ModuleResult jsmethod_float_sync(final UZModuleContext context){
		
		return new ModuleResult(10.21f);
	}
	
	//同步接口，返回String型数据，对应到js的字符串
	public ModuleResult jsmethod_string_sync(final UZModuleContext context){
		
		return new ModuleResult("我是一个字符串");
	}
	
	//同步接口，返回boolean型数据，对应到js的true或者false
	public ModuleResult jsmethod_boolean_sync(final UZModuleContext context){
		
		return new ModuleResult(true);
	}
	
	//同步接口，返回JSON对象数据，对应到js的json对象
	public ModuleResult jsmethod_json_sync(final UZModuleContext context){
		JSONObject jsonObject = new JSONObject();
		try{
			jsonObject.put("key", "aaaa");
			jsonObject.put("key1", "bbbb");
			jsonObject.put("key2", "cccc");
		}catch(Exception e){
			;
		}
		return new ModuleResult(jsonObject);
	}
	
	//同步接口，返回JSON数组数据，对应到js的json对象数组
	public ModuleResult jsmethod_jsonay_sync(final UZModuleContext context) {
		JSONArray jsonArray = new JSONArray();
		try{
			jsonArray.put("aaaa");
			jsonArray.put("bbbb");
			jsonArray.put("cccc");
		}catch(Exception e){
			;
		}
		return new ModuleResult(jsonArray);
	}
}
