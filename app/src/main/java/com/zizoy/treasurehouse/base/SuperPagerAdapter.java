package com.zizoy.treasurehouse.base;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * @Description: ViewPagerAdapter父类
 * 
 * @author falcon
 */
public class SuperPagerAdapter extends PagerAdapter {
	protected List<View> listViews = null;

	public SuperPagerAdapter(List<View> listViews) {
		this.listViews = listViews;
	}

	@Override
	public void destroyItem(View container, int position, Object object) {
		((ViewPager) container).removeView(listViews.get(position));
	}

	@Override
	public int getCount() {
		return listViews.size();
	}

	@Override
	public Object instantiateItem(View container, int position) {
		((ViewPager) container).addView(listViews.get(position), 0);
		return listViews.get(position);
	}

	@Override
	public boolean isViewFromObject(View paramView, Object paramObject) {
		return paramView == paramObject;
	}

	public List<View> getListViews() {
		return listViews;
	}

	public void setListViews(List<View> listViews) {
		this.listViews = listViews;
	}
}