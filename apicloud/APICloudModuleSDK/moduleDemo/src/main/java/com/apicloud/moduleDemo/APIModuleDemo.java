package com.apicloud.moduleDemo;

import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.uzmap.pkg.uzcore.UZWebView;
import com.uzmap.pkg.uzcore.uzmodule.UZModule;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;

/**
 * 该类映射至Javascript中moduleDemo对象<br><br>
 * <strong>Js Example:</strong><br>
 * var module = api.require('moduleDemo');<br>
 * module.xxx();
 * @author APICloud
 *
 */
public class APIModuleDemo extends UZModule {

	static final int ACTIVITY_REQUEST_CODE_A = 100;
	
	private AlertDialog.Builder mAlert;
	private Vibrator mVibrator;
	private UZModuleContext mJsCallback;
	private MyTextView mMyTextView;
	
	public APIModuleDemo(UZWebView webView) {
		super(webView);
	}
	
	/**
	 * <strong>函数</strong><br><br>
	 * 该函数映射至Javascript中moduleDemo对象的showAlert函数<br><br>
	 * <strong>JS Example：</strong><br>
	 * moduleDemo.showAlert(argument);
	 * 
	 * @param moduleContext  (Required)
	 */
	public void jsmethod_showAlert(final UZModuleContext moduleContext){
		if(null != mAlert){
			return;
		}
		String showMsg = moduleContext.optString("msg");
		mAlert = new AlertDialog.Builder(context());
		mAlert.setTitle("这是标题");
		mAlert.setMessage(showMsg);
		mAlert.setCancelable(false);
		mAlert.setPositiveButton("确定", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				mAlert = null;
				JSONObject ret = new JSONObject();
				try {
					ret.put("buttonIndex", 1);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				moduleContext.success(ret, true);
			}
		});
		mAlert.show();
	}
	
	/**
	 * <strong>函数</strong><br><br>
	 * 该函数映射至Javascript中moduleDemo对象的startActivity函数<br><br>
	 * <strong>JS Example：</strong><br>
	 * moduleDemo.startActivity(argument);
	 * 
	 * @param moduleContext  (Required)
	 */
	public void jsmethod_startActivity(UZModuleContext moduleContext){
		Intent intent = new Intent(context(), DemoActivity.class);
		intent.putExtra("appParam", moduleContext.optString("appParam"));
		startActivity(intent);
	}
	
	/**
	 * <strong>函数</strong><br><br>
	 * 该函数映射至Javascript中moduleDemo对象的startActivityForResult函数<br><br>
	 * <strong>JS Example：</strong><br>
	 * moduleDemo.startActivityForResult(argument);
	 * 
	 * @param moduleContext  (Required)
	 */
	public void jsmethod_startActivityForResult(UZModuleContext moduleContext){
		mJsCallback = moduleContext;
		Intent intent = new Intent(context(), DemoActivity.class);
		intent.putExtra("appParam", moduleContext.optString("appParam"));
		intent.putExtra("needResult", true);
		startActivityForResult(intent, ACTIVITY_REQUEST_CODE_A);
	}
	
	/**
	 * <strong>函数</strong><br><br>
	 * 该函数映射至Javascript中moduleDemo对象的vibrate函数<br><br>
	 * <strong>JS Example：</strong><br>
	 * moduleDemo.vibrate(argument);
	 * 
	 * @param moduleContext  (Required)
	 */
	public void jsmethod_vibrate(UZModuleContext moduleContext){
		try {
			if (null == mVibrator) {
				mVibrator = (Vibrator) context().getSystemService(Context.VIBRATOR_SERVICE);
			}
			mVibrator.vibrate(moduleContext.optLong("milliseconds"));
		} catch (SecurityException e) {
			Toast.makeText(context(), "no vibrate permisson declare", Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * <strong>函数</strong><br><br>
	 * 该函数映射至Javascript中moduleDemo对象的stopVibrate函数<br><br>
	 * <strong>JS Example：</strong><br>
	 * moduleDemo.stopVibrate(argument);
	 * 
	 * @param moduleContext  (Required)
	 */
	public void jsmethod_stopVibrate(UZModuleContext moduleContext){
		if (null != mVibrator) {
			try {
				mVibrator.cancel();
				mVibrator = null;
			} catch (SecurityException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * <strong>函数</strong><br><br>
	 * 该函数映射至Javascript中moduleDemo对象的addView函数<br><br>
	 * <strong>JS Example：</strong><br>
	 * moduleDemo.addView(argument);
	 * 
	 * @param moduleContext  (Required)
	 */
	public void jsmethod_addView(UZModuleContext moduleContext){
		int x = moduleContext.optInt("x");
		int y = moduleContext.optInt("y");
		int w = moduleContext.optInt("w");
		int h = moduleContext.optInt("h");
		if(w == 0){
			w = ViewGroup.LayoutParams.MATCH_PARENT;
		}
		if(h == 0){
			h = ViewGroup.LayoutParams.MATCH_PARENT;
		}
		if(null == mMyTextView){
			mMyTextView = new MyTextView(context());
		}
		int FROM_TYPE = Animation.RELATIVE_TO_PARENT;
		Animation anim = new TranslateAnimation(FROM_TYPE, 1.0f, FROM_TYPE, 0.0f, FROM_TYPE, 0.0f, FROM_TYPE, 0.0f);
		anim.setDuration(500);
		mMyTextView.setAnimation(anim);
		RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(w, h);
		rlp.leftMargin = x;
		rlp.topMargin = y;
		insertViewToCurWindow(mMyTextView, rlp);
	}
	
	/**
	 * <strong>函数</strong><br><br>
	 * 该函数映射至Javascript中moduleDemo对象的removeView函数<br><br>
	 * <strong>JS Example：</strong><br>
	 * moduleDemo.removeView(argument);
	 * 
	 * @param moduleContext  (Required)
	 */
	public void jsmethod_removeView(UZModuleContext moduleContext){
		if(null != mMyTextView){

			removeViewFromCurWindow(mMyTextView);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == Activity.RESULT_OK && requestCode == ACTIVITY_REQUEST_CODE_A){
			String result = data.getStringExtra("result");
			if(null != result && null != mJsCallback){
				try {
					JSONObject ret = new JSONObject(result);
					mJsCallback.success(ret, true);
					mJsCallback = null;
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	protected void onClean() {
		if(null != mAlert){
			mAlert = null;
		}
		if(null != mJsCallback){
			mJsCallback = null;
		}
	}

	class MyTextView extends TextView{

		public MyTextView(Context context) {
			super(context);
			setBackgroundColor(0xFFF0CFD0);
			setText("我是自定义View");
			setTextColor(0xFF000000);
			setGravity(Gravity.CENTER);
		}
	}
}
