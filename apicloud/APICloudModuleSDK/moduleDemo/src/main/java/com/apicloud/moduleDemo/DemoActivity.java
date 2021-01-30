package com.apicloud.moduleDemo;

import org.json.JSONException;
import org.json.JSONObject;
import com.apicloud.sdk.moduledemo.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 原生activity
 *
 */
public class DemoActivity extends Activity {
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mo_demo_main_activity);
		Intent data = getIntent();
		String appParam = data.getStringExtra("appParam");
		if(null != appParam){
			TextView text = (TextView) findViewById(R.id.text);
			text.setText("JS传入的参数为：" + appParam);
		}
		final boolean needResult = data.getBooleanExtra("needResult", false);
		Button btn = (Button) findViewById(R.id.btn);
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(needResult){
					Intent resultData = new Intent();
					JSONObject json = new JSONObject();
					try {
						json.put("key1", "value1");
						json.put("key2", "value2");
						json.put("key3", "value3");
					} catch (JSONException e) {
						e.printStackTrace();
					}
					resultData.putExtra("result", json.toString());
					setResult(RESULT_OK, resultData);
				};
				finish();
			}
		});
	}
}
