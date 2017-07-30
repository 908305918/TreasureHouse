package com.zizoy.treasurehouse.util;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * @Description: ScrollView工具类
 *
 * @author falcon
 */
public class ScrollViewUtil extends ScrollView {
	private ScrollViewListener scrollViewListener = null;

	public ScrollViewUtil(Context context) {
		super(context);
	}

	public ScrollViewUtil(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ScrollViewUtil(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setScrollViewListener(ScrollViewListener scrollViewListener) {
		this.scrollViewListener = scrollViewListener;
	}

	@Override
	protected void onScrollChanged(int x, int y, int oldx, int oldy) {
		super.onScrollChanged(x, y, oldx, oldy);
		if (scrollViewListener != null) {
			scrollViewListener.onScrollChanged(this, x, y, oldx, oldy);
		}
	}

	public interface ScrollViewListener {
		void onScrollChanged(ScrollViewUtil scrollView, int x, int y, int oldx, int oldy);
	}
}