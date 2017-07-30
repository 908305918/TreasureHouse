package com.zizoy.treasurehouse.util;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * @Description: 检查网络连接工具类
 * 
 * @author falcon
 */
public class CheckNet {
	private Context context;

	public CheckNet(Context context) {
		super();
		this.context = context;
	}

	public boolean checkNet() {
		// 网络信息管理
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Activity.CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getActiveNetworkInfo();
		// 判断wifi是否连接成功
		boolean wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
		// 判断Intent是否连接成功
		boolean internet = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
		// 对手机的网络连接进行判断
		if (info != null && info.isAvailable() && wifi | internet) { // 如果网络连接正常
			return true;
		} else {
			return false;
		}
	}
}