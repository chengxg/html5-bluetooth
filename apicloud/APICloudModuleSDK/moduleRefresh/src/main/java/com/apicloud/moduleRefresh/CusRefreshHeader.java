package com.apicloud.moduleRefresh;


import com.uzmap.pkg.uzcore.UZCoreUtil;
import com.uzmap.pkg.uzcore.uzmodule.RefreshHeader;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;
import com.uzmap.pkg.uzkit.UZUtility;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

/**
 * 简单自定义下拉刷新模块demo<br>
 * 下拉刷新模块需要继承自RefreshHeader类，实现相关接口，并且在module.json中配置该类，
 * 引擎会在适当的时机创建该类，并加载为window或者frame的下拉刷新头<br>
 * 
 * @author dexing.li
 * 
 */
@SuppressWarnings("deprecation")
public class CusRefreshHeader extends RefreshHeader{

	static int defaultRefrshHeaderHeight = 45;
	static float DEFAULT_FONT_SIZE = 15.0f;
	private DefaultHeader mHeader;
	
	public CusRefreshHeader() {
		;
	}

	@Override
	public View onCreateView(Context context) {
		if(null == mHeader){
			mHeader = new DefaultHeader(context);
		}
		return mHeader;
	}


	@Override
	public void onDestroyView() {
		if(null != mHeader){
			//TODO
		}
	}

	@Override
	public int getRefreshingThreshold(Context context) {
		Resources res = context.getResources();
		float density = res.getDisplayMetrics().density;
		int threshold = (int) (density * defaultRefrshHeaderHeight);
		return threshold;
	}
	
	@Override
	public int getViewHeight(Context context) {
		Resources res = context.getResources();
		int screenHeight = res.getDisplayMetrics().heightPixels;
		return (screenHeight / 3) * 2;
	}

	@Override
	public void onStateChange(int state) {
		if(null != mHeader){
			mHeader.onStateChange(state);
		}
	}

	@Override
	public void onScrollY(int curScrollY) {
		if(null != mHeader){
			mHeader.onScrollY(curScrollY);
		}
	}

	@Override
	public void onRelease() {
		if(null != mHeader){
			mHeader.onRelease();
		}
	}

	@Override
	public void onRefresh() {
		if(null != mHeader){
			mHeader.onRefresh();
		}
	}

	@Override
	public void onSetRefreshInfo(UZModuleContext moduleContext) {
		if(null != mHeader){
			mHeader.onSetRefreshInfo(moduleContext);
		}
	}

	@Override
	public void onForceRefresh() {
		if(null != mHeader){
			mHeader.onForceRefresh();
		}
	}

	@Override
	public void onSetVisibility(int visibility) {
		if(null != mHeader){
			mHeader.setVisibility(visibility);
		}
	}
	
	class DefaultHeader extends RelativeLayout{

		static final int DOWN = 0;
		static final int UP 	= 1;
		
		private ProgressBar mProgress;
		private ImageView mArrowImage;
		private RotateAnimation mAnimationDown;
	    private RotateAnimation mAnimationUp;
	    private int bgColor = 0xFFCCCCCC;
		
		DefaultHeader(Context context) {
			super(context);
		    setFocusable(false);
		    setBackgroundColor(bgColor);
			RelativeLayout layout = new RelativeLayout(context);
			layout.setFocusable(false);
			int padding = UZCoreUtil.dipToPix(5);
			layout.setPadding(padding, padding, padding, padding);
			LayoutParams llrlp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			llrlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
			layout.setLayoutParams(llrlp);
			addView(layout);
			
			
			
			mProgress = new ProgressBar(context);
			mProgress.setIndeterminate(true);
			int size = UZCoreUtil.dipToPix(25);
			LayoutParams prlp = new LayoutParams(size, size);
			prlp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
			mProgress.setLayoutParams(prlp);
			mProgress.setVisibility(GONE);
			layout.addView(mProgress);
			
			mArrowImage = new ImageView(context);
			prlp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			prlp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
			mArrowImage.setLayoutParams(prlp); 
			layout.addView(mArrowImage);
			
			mAnimationUp = new RotateAnimation(0, -180,
	                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
	                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
			mAnimationUp.setInterpolator(new AccelerateInterpolator());
			mAnimationUp.setDuration(250);
			mAnimationUp.setFillAfter(true);
			
			mAnimationDown = new RotateAnimation(-180, 0,
	                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
	                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
			mAnimationDown.setInterpolator(new AccelerateInterpolator());
			mAnimationDown.setDuration(250);
			mAnimationDown.setFillAfter(true);
		}

		private void setArrowVisibility(int v){
			mArrowImage.setVisibility(v);
			if(v == GONE){
				mArrowImage.clearAnimation();
			}
		}
		
		private void rotateArrowImage(int type){
			mArrowImage.clearAnimation();
			switch (type) {
			case DOWN:
				mArrowImage.startAnimation(mAnimationDown);
				break;
			case UP:
				mArrowImage.startAnimation(mAnimationUp);
				break;
			}
		}
		
		private void setProgressBarVisibility(int v){

			mProgress.setVisibility(v);
		}

		private void setImage(Drawable drawable){
			Resources res = getContext().getResources();
			if(null != drawable){
				mArrowImage.setBackgroundDrawable(drawable);
			}else{
				drawable = res.getDrawable(R.drawable.mo_cus_refresh_default);
				mArrowImage.setBackgroundDrawable(drawable);
			}
		}

		public void onStateChange(int state) {
			if(state == RefreshHeader.STATE_RELEASE){
				setArrowVisibility(VISIBLE);
				setProgressBarVisibility(GONE);
				rotateArrowImage(UP);
			}else{
				setArrowVisibility(VISIBLE);
				setProgressBarVisibility(GONE);
				rotateArrowImage(DOWN);
			}
		}

		public void onRelease() {
			setArrowVisibility(VISIBLE);
		    setProgressBarVisibility(GONE);
		}

		public void onRefresh() {
			setArrowVisibility(GONE);
			setProgressBarVisibility(VISIBLE);
		}

		public void onForceRefresh() {
			setVisibility(View.VISIBLE);
			setImage((Drawable)null);
		}
		
		public void onSetRefreshInfo(UZModuleContext settings) {
			String arrowImage = settings.optString("arrowImage");
			if(!TextUtils.isEmpty(arrowImage)){
				arrowImage = settings.makeRealPath(arrowImage);
				Bitmap bitmap = UZUtility.getLocalImage(arrowImage);
				if(null != bitmap){
					Resources res = getContext().getResources();
					Drawable drawable = new BitmapDrawable(res, bitmap);
					setImage(drawable);
				}
			}
		}

		public void onScrollY(int distance) {
			;
		}
	}
}
