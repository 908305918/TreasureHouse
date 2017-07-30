package com.zizoy.treasurehouse.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;


public class CameraAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<Bitmap> dataList;
	private DisplayMetrics mDisplayMetrics;

	public CameraAdapter(Context context, ArrayList<Bitmap> dataList) {
		this.mContext = context;
		this.dataList = dataList;
		this.mDisplayMetrics = new DisplayMetrics();
		((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
	}

	@Override
	public int getCount() {
		return dataList.size();
	}

	@Override
	public Object getItem(int position) {
		return dataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView addPhotoBtn; // 添加图片按钮
		
		if (convertView == null) {
			addPhotoBtn = new ImageView(mContext);
			addPhotoBtn.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT, dipToPx(75)));
			addPhotoBtn.setAdjustViewBounds(true);
			addPhotoBtn.setScaleType(ImageView.ScaleType.FIT_XY);
		} else {
			addPhotoBtn = (ImageView) convertView;
		}
		if (dataList.size() > 0) {
			addPhotoBtn.setImageBitmap(dataList.get(position));
		}
		return addPhotoBtn;
	}

	public int dipToPx(int dip) {
		return (int) (dip * mDisplayMetrics.density + 0.5f);
	}
}