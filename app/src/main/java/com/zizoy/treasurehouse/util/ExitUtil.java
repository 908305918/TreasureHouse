package com.zizoy.treasurehouse.util;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.zizoy.treasurehouse.api.MApplication;

import java.util.Timer;
import java.util.TimerTask;

import cn.sharesdk.framework.ShareSDK;

/**
 * @Description: 按两次返回退出
 * 
 * @author falcon
 */
public class ExitUtil {
	private Boolean isQuit = false;
	private Timer timer = new Timer();
	private Activity activity;

	public ExitUtil(Activity activity) {
		super();
		this.activity = activity;
	}

	/**
	 * 退出执行方法
	 */
	public void exit() {
		if (isQuit == false) {
			isQuit = true;
			Toast.makeText(activity, "再按一次退出程序", Toast.LENGTH_SHORT).show();
			TimerTask task = null;
			task = new TimerTask() {
				@Override
				public void run() {
					isQuit = false;
				}
			};
			timer.schedule(task, 2000);
		} else {
			Intent startMain = new Intent(Intent.ACTION_MAIN);
			startMain.addCategory(Intent.CATEGORY_HOME);
			startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			activity.startActivity(startMain);
			activity.finish();
			System.exit(0);
		}
	}
}