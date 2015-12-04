package com.example.shareholders.common;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class NoScrollViewPager extends ViewPager {

	private boolean isEnbleScroll;

	public NoScrollViewPager(Context context) {
		super(context);
		isEnbleScroll = true;
	}

	public NoScrollViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		isEnbleScroll = true;
	}

	public void setScrollEnble(boolean isEnbleScroll) {
		this.isEnbleScroll = isEnbleScroll;
	}

	// @Override
	// public void scrollTo(int x, int y){
	// if (isEnbleScroll){
	// super.scrollTo(x, y);
	// }
	// }

	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		/* return false;//super.onTouchEvent(arg0); */
		if (isEnbleScroll)
			return super.onTouchEvent(arg0);
		else
			return false;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		if (isEnbleScroll)
			return super.onInterceptTouchEvent(arg0);
		else
			return false;
	}
}
