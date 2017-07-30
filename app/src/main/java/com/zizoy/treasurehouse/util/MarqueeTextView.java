package com.zizoy.treasurehouse.util;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * @Description: 文字跑马灯工具类
 * 
 * @author falcon
 */
public class MarqueeTextView extends TextView {

	public MarqueeTextView(Context context) {
		super(context);
	}

	public MarqueeTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean isFocused() {
		return true;
	}
}