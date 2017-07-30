package com.zizoy.treasurehouse.base;

import java.util.List;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * @Description: BaseAdapter父类
 * 
 * @author falcon
 * 
 * @param <T> 数据模型
 */
public abstract class SuperBaseAdapter<T> extends BaseAdapter {
	/** 本地context **/
	protected Context context;
	/** 数据模型集合 **/
	protected List<T> listData;

	public SuperBaseAdapter(Context context, List<T> listData) {
		this.context = context;
		this.listData = listData;
	}

	@Override
	public int getCount() {
		return listData == null ? 0 : listData.size();
	}

	@Override
	public Object getItem(int position) {
		return listData == null ? null : listData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/** 获取集合数据 **/
	public List<T> getListData() {
		return listData;
	}

	/** 设置集合数据 **/
	public void setListData(List<T> listData) {
		this.listData = listData;
		notifyDataSetChanged();
	}

	/** 填充集合数据 **/
	public void addAll(List<T> listData) {
		if (listData != null) {
			this.listData.addAll(listData);
			notifyDataSetChanged();
		}
	}

	/** 清除集合数据 **/
	public void clear() {
		this.listData.clear();
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {  
		// 实例化ViewHolder
		ViewHolder viewHolder = ViewHolder.get(context, convertView, parent, getLayoutId(), position);
		// 绑定数据
		bindViewDatas(viewHolder, listData.get(position), position);

		return viewHolder.getConvertView();
	}

	/**
	 * 设置Item界面布局
	 * 
	 * @return UI
	 */
	protected abstract int getLayoutId();

	/**
	 * 绑定数据
	 *
	 * @param viewHolder
	 * @param data
	 * @param position
     */
	protected abstract void bindViewDatas(ViewHolder viewHolder, T data, int position);

	/**
	 * 视图ViewHolder
	 */
	public final static class ViewHolder {
		private final SparseArray<View> mViews;
		private View mConvertView;

		private ViewHolder(Context context, ViewGroup parent, int layoutId, int position) {
			this.mViews = new SparseArray<View>();
			mConvertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
			// setTag
			mConvertView.setTag(this);
		}

		/**
		 * 拿到ViewHolder对象
		 * 
		 * @param context
		 * @param convertView
		 * @param parent
		 * @param layoutId
		 * @param position
		 * @return
		 */
		public static ViewHolder get(Context context, View convertView, ViewGroup parent, int layoutId, int position) {
			if (convertView == null) {
				return new ViewHolder(context, parent, layoutId, position);
			}
			
			return (ViewHolder) convertView.getTag();
		}

		/**
		 * 通过控件的Id获取对于的控件，如果没有则加入views
		 * 
		 * @param viewId
		 * @return
		 */
		public <T extends View> T getView(int viewId) {
			View childView = mViews.get(viewId);
			
			if (childView == null) {
				childView = mConvertView.findViewById(viewId);
				mViews.put(viewId, childView);
			}
			return (T) childView;
		}

		/**
		 * 返回当前View
		 */
		public View getConvertView() {
			return mConvertView;
		}
	}
}