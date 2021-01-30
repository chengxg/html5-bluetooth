package com.apicloud.eventdemo;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.uzmap.pkg.uzcore.uzmodule.AppInfo;
import com.uzmap.pkg.uzcore.uzmodule.ApplicationDelegate;

/**
 * ApplicationDelegate是APICloud应用中，系统Application的代理类，多数第三方SDK要求在Application的onCreate中初始化相关代码，
 * 但每个应用只有一个Application，因此我们通过ApplicationDelegate对系统Application进行代理并分发事件。
 * 继承自ApplicationDelegate的类，APICloud引擎在应用初始化之初就会将该类初始化一次（即new一个出来），并保持引用，
 * 在应用整个运行期间，会将生命周期事件通过该对象下的相关函数回调。<br>
 * 该类需要在module.json中配置，其中name字段可以任意配置，因为该字段将被忽略，请参考module.json中EasDelegate的配置
 */
public class EasDelegate extends ApplicationDelegate {

	/**
	 * 继承自ApplicationDelegate的类，APICloud引擎在应用初始化之初就会将该类初始化一次（即new一个出来），并保持引用，
	 * 在应用整个运行期间，会将生命周期事件通过该对象通知出来。<br>
	 * 该类需要在module.json中配置，其中name字段可以任意配置，因为该字段将被忽略，请参考module.json中EasDelegate的配置
	 */
	public EasDelegate() {
		//应用运行期间，该对象只会初始化一个出来
	}

	@Override
	public void onApplicationCreate(Context context, AppInfo info) {
		//TODO 请在这个函数中初始化需要在Application的onCreate中初始化的代码
		//例如很多第三方SDK要求在Application的onCreate中初始化的代码可以放到该函数中
		Log.i("apicloud", "app onApplicationCreate");
	}

	@Override
	public void onActivityResume(Activity activity, AppInfo info) {
		//APP从后台回到前台时，APICloud引擎将通过该函数回调事件
		//TODO 请在这个函数中实现你需要的逻辑，无则忽略
	}

	@Override
	public void onActivityPause(Activity activity, AppInfo info) {
		//APP从前台退到后台时，APICloud引擎将通过该函数回调事件
		//TODO 请在这个函数中实现你需要的逻辑，无则忽略
	}

	@Override
	public void onActivityFinish(Activity activity, AppInfo info) {
		//APP即将结束运行时，APICloud引擎将通过该函数回调事件
		//TODO 请在这个函数中实现你需要的逻辑，无则忽略
	}

	@Override
	public boolean supportMulti() {
		//TODO 如果你的代码需要支持多进程运行，可重写该函数并返回true
		return false;
	}

}
