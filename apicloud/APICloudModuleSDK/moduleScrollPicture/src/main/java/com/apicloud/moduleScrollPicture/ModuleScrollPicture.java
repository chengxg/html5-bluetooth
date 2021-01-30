package com.apicloud.moduleScrollPicture;


import android.widget.RelativeLayout;

import com.uzmap.pkg.uzcore.UZWebView;
import com.uzmap.pkg.uzcore.uzmodule.UZModule;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;

/**
 * 该类映射至Javascript中scrollPicture对象<br>
 * 简单的UI类ScrollPicture模块<br><br>
 * <strong>Js Example:</strong><br>
 * var module = api.require('scrollPicture');<br>
 * module.open();<br>
 * module.close();
 * @author APICloud
 *
 */
public class ModuleScrollPicture extends UZModule {

	ScrollPictureView mInnerGroup;
	
	public ModuleScrollPicture(UZWebView webView) {
		super(webView);
	}
	
	/**
	 * 对应JS中的scrollPicture.open();
	 * @param moduleContext
	 */
	public void jsmethod_open(final UZModuleContext moduleContext){
		if(null == mInnerGroup){
			mInnerGroup = new ScrollPictureView(context());
		}
		if(null == mInnerGroup.getParent()){
			int x = moduleContext.optInt("x");//JS传入的x
			int y = moduleContext.optInt("y");//JS传入的y
			int w = moduleContext.optInt("w");//JS传入的w
			int h = moduleContext.optInt("h");//JS传入的h
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(w, h);
			lp.leftMargin = x;
			lp.topMargin = y;
			//UI类模块都应该实现fixedOn和fixed，标识该UI模块是挂在window还是frame上，是跟随网页滚动还是不跟随滚动
			//fixedOn为frame的name值。
			//通常，fixedOn为空或者不传时，UI模块默认挂在window上，如果有值，则挂在名为fixedOn传入的值的frame上
			String fixedOn = moduleContext.optString("fixedOn");
			//fixed参数标识UI模块是否跟随网页滚动
			boolean fixed = moduleContext.optBoolean("fixed", true);
			insertViewToCurWindow(mInnerGroup, lp, fixedOn, fixed);
		}
		mInnerGroup.initialize();
	}
	
	/**
	 * 对应JS中的scrollPicture.close();
	 * @param moduleContext
	 */
	public void jsmethod_close(final UZModuleContext moduleContext){
		if(null != mInnerGroup){
			removeViewFromCurWindow(mInnerGroup);
		}
	}

}
