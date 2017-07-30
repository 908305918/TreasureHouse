package com.zizoy.treasurehouse.util;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.treasurehouse.R;

/**
 * dialog提示对话框工具类
 * 
 * @author falcon
 * 
 */
public class DialogUtil {
	private Context context;
	private Dialog mDialog;
	private Dialog networkDialog;
	private int show;
	private int close;

	public DialogUtil(Context context) {
		this.context = context;
	}

	/**
	 * 加载对话框
	 */
	public void showLoginDialog() {
		showDialog(context, "正在登陆中...", false);
	}

	/**
	 * 加载对话框
	 */
	public void showLogoutDialog() {
		showDialog(context, "正在退出登录中...", false);
	}

	/**
	 * 加载对话框
	 */
	public void showLoadDialog() {
		showDialog(context, "数据加载中，请稍后...", false);
	}

	/**
	 * 提交对话框
	 */
	public void showSubmitDialog() {
		showDialog(context, "提交数据中，请稍后...", false);
	}

	/**
	 * 检查更新对话框
	 */
	public void showUpdateDialog() {
		showDialog(context, "检查更新中，请稍后...", false);
	}

	/**
	 * 下载对话框
	 */
	public void showDownloadDialog() {
		showDialog(context, "正在下载，请稍候...", false);
	}

	/**
	 * 显示对话框
	 */
	private void showDialog(Context context, String msg, boolean cancelable) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.view_loading_dialog, new LinearLayout(context),false);
		LinearLayout layout = (LinearLayout) view.findViewById(R.id.dialogView);

		ImageView dialogShowImg = (ImageView) view.findViewById(R.id.dialogShowImg);
		TextView dialogShowText = (TextView) view.findViewById(R.id.dialogShowText);
		// 加载动画
		Animation dialogAnimation = AnimationUtils.loadAnimation(context, R.anim.loading_animation);
		// 使用ImageView显示动画
		dialogShowImg.startAnimation(dialogAnimation);
		// 设置加载信息
		dialogShowText.setText(msg);
		// 创建自定义样式dialog
		mDialog = new Dialog(context, R.style.loadingDialog);
		// 不可以用“返回键”取消
		mDialog.setCancelable(cancelable);
		// 设置布局
		mDialog.setContentView(layout, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));

		if (mDialog != null) {
			if (mDialog.isShowing()) {
				closeDialog();
			}
		}

		mDialog.show();
		new Thread() {
			public void run() {
				Looper.prepare();
				try {
					Thread.sleep(240000);
					if (show != close) {
						if (mDialog.isShowing()) {
							closeDialog();
						}
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}.start();
		show++;
	}

	/**
	 * 显示网络连接提醒
	 */
	public void showNetworkDialog() {
		networkDialog = new Dialog(context, R.style.mDialog);
		networkDialog.setContentView(R.layout.view_network_dialog);
		Button dialogEnterBtn = (Button) networkDialog.findViewById(R.id.dialogEnterBtn); // 确定按钮
		Button dialogCancelBtn = (Button) networkDialog.findViewById(R.id.dialogCancelBtn); // 取消按钮
		dialogEnterBtn.setOnClickListener(mBtnClick);
		dialogCancelBtn.setOnClickListener(mBtnClick);
		networkDialog.show();
	}

	/**
	 * 控件点击触发事件
	 */
	private OnClickListener mBtnClick = new OnClickListener() {
		@Override
		public void onClick(View view) {
			switch (view.getId()) {
				case R.id.dialogEnterBtn: // 确定被点击
					Intent intent = new Intent("android.settings.WIRELESS_SETTINGS");
					context.startActivity(intent);
					networkDialog.dismiss();
					break;

				case R.id.dialogCancelBtn: // 取消被点击
					networkDialog.cancel();
					break;

				default:
					break;
			}
		}
	};

	/**
	 * 显示对话框
     */
	public boolean isShow() {
		return  mDialog != null && mDialog.isShowing();
	}

	/**
	 * 关闭对话框
	 */
	public void closeDialog() {
		if (mDialog != null) {
			if (mDialog.isShowing()) {
				mDialog.cancel();
				close++;
			}
		}
	}

	/**
	 * 移除对话框
	 */
	public  void removeDialog() {
		context = null;
		mDialog = null;
	}
}