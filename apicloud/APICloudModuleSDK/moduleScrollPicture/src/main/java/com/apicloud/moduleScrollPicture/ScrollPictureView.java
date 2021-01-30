package com.apicloud.moduleScrollPicture;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;
import com.uzmap.pkg.uzcore.UZResourcesIDFinder;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

/**
 * 
 * 简单的ScrollPicture，即图片轮播
 *
 */
public class ScrollPictureView extends ViewPager {

	private int mDefaultIndex;
	private boolean mScrollEnabled = true;
	private List<ImageView> mFrames;
	private FramesAdapter mFramesAdapter;
	private MHandler handler = new MHandler();
	
	private class MHandler extends Handler{

		@Override
		public void handleMessage(Message msg) {
			int c = getCurrentItem();
			c++;
			if(c >= mFrames.size()){
				c = 0;
			}
			setCurrentItem(c);
			contiue();
		}
		
		public void contiue(){
			sendEmptyMessageDelayed(0, 3000);
		}
	};
	
	public ScrollPictureView(Context context) {
		super(context);
		mFrames = new ArrayList<ImageView>();
		setAnimationCacheEnabled(false);
		setAlwaysDrawnWithCacheEnabled(false);
		setFocusable(false);
	}
	
	@SuppressWarnings("deprecation")
	public void initialize(){
		mFramesAdapter = new FramesAdapter();
		setAdapter(mFramesAdapter);
		setOnPageChangeListener(mFramesAdapter);
		int size = 4;
		if(0 == size){
			return;
		}
		int limit = size >= 6 ? 6 : size;
		setOffscreenPageLimit(limit);
		for(int i = 0; i < size; ++i){
			ImageView child = createChild(i);
			mFrames.add(child);
		}
		mFramesAdapter.notifyDataSetChanged();
		if(0 != mDefaultIndex){
			setCurFrame(mDefaultIndex, false);
		}else{
			postDelayed(new Runnable() {
				@Override
				public void run() {
					mFramesAdapter.onPageSelected(0);
				}
			}, 10);
		}
		handler.contiue();
	}
	
	//此处使用本地图片资源进行演示，实际使用过程中请根据情况从网络或者本地存储加载
	private ImageView createChild(int index){
		final ImageView frame = new ImageView(getContext());
		frame.setScaleType(ScaleType.FIT_XY);
		int resId = UZResourcesIDFinder.getResDrawableID("mo_scrollpicture_img_switcher_bg" + index);
		frame.setImageResource(resId);
		return frame;
	}
	
	public void start(){
		
		mFramesAdapter.notifyDataSetChanged();
	}
	
	public void setDefaultIndex(int index){
		
		mDefaultIndex = index;
	}
	
	public void setScrollEnabled(boolean flag){
		
		mScrollEnabled = flag;
	}

	public void setCurFrame(int index, boolean smoothScroll){
		
		setCurrentItem(index, smoothScroll);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent e) {
		if(!mScrollEnabled){
			return false;
		}
		//任何时候要求父亲不处理事件，见onPageScrolled
		requestParentDisallowInterceptTouchEvent(true);
		return super.onInterceptTouchEvent(e);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(!mScrollEnabled){
			return false;
		}
		return super.onTouchEvent(event);
	}

    private void requestParentDisallowInterceptTouchEvent(boolean disallowIntercept) {
        final ViewParent parent = getParent();
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(disallowIntercept);
        }
    }
	
	class FramesAdapter extends PagerAdapter implements android.support.v4.view.ViewPager.OnPageChangeListener {
		
		JSONObject mCallbackInfo;
		
		public FramesAdapter() {
			
			mCallbackInfo = new JSONObject();
		}

		@Override
		public int getCount() {
			
		    return mFrames.size();
		}
		
		@Override  
        public void destroyItem(ViewGroup container, int position,  Object object) {
			
//			 ((ViewPager) container).removeView(mFrames.get(position));
        }

		@Override
		public boolean isViewFromObject(View child, Object obj) {
			
			return child == obj;
		}
		
		@Override  
        public Object instantiateItem(ViewGroup container, int position) {
			ImageView child = mFrames.get(position); 
			if(null != child.getParent()){
				return child;
			}
			container.addView(child);
            return child;  
        }
		
		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
	        if(0 == positionOffsetPixels){//滚动到最左或最右时，将事件交回父亲
//	        	requestParentDisallowInterceptTouchEvent(false);
	        }
		}

		@Override
		public void onPageScrollStateChanged(int state) {
			;
		}

		@Override
		public void onPageSelected(final int position) {
			;
		}
		
	}
}
