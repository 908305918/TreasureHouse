package com.zizoy.treasurehouse.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.treasurehouse.R;
import com.zizoy.treasurehouse.api.MApplication;
import com.zizoy.treasurehouse.util.CheckNet;
import com.zizoy.treasurehouse.util.DialogUtil;
import com.zizoy.treasurehouse.util.ToastUtil;
import com.zizoy.xutils.HttpUtils;

/**
 * @Description: Fragment父类
 *
 * @author falcon
 */
public abstract class SuperFragment extends Fragment {
	/** 当前界面View **/
	protected View view;
	/** 供之类使用intent **/
	protected Intent intent;
	/** 当前Activity类 **/
	protected Activity activity;
	/** 判断当前网络状态 **/
	protected CheckNet checkNet;
	/** 网络解析工具类 **/
	protected HttpUtils httpUtils;
	/** dialog提示对话框工具类 **/
	protected DialogUtil dialogUtil;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		view = inflater.inflate(getLayoutId(), container, false);

		initData(); // 数据初始化
		initView(); // 界面初始化
		initCodeLogic(); // 其他逻辑处理
		setListener(); // 界面UI事件监听

		return view;
	}

	/**
	 * 设置UI界面布局
	 * 
	 * @return UI
	 */
	protected abstract int getLayoutId();

	/**
	 * 数据初始化
	 */
	protected void initData() {
		intent = new Intent();
		activity = getActivity();
		checkNet = new CheckNet(activity);
		dialogUtil = new DialogUtil(activity);
		httpUtils = MApplication.getHttpUtils();
	}

	/**
	 * 界面初始化
	 */
	protected void initView() {
	}

	/**
	 * 其他逻辑处理
	 */
	protected void initCodeLogic() {
	}

	/**
	 * 界面UI事件监听
	 */
	protected void setListener() {
	}

	/**
	 * 界面跳转
	 * 
	 * @param cls
	 */
	protected void startActivity(Class<? extends Activity> cls, Bundle bundle) {
		intent.setClass(activity, cls);

		if (bundle != null) {
			intent.putExtras(bundle);
		}
		
		startActivity(intent);
	}

	/**
	 * 界面跳转(回调)
	 * 
	 * @param cls
	 */
	protected void startActivityForResult(Class<? extends Activity> cls, Bundle bundle, int requestCode) {
		intent.setClass(activity, cls);

		if (bundle != null) {
			intent.putExtras(bundle);
		}
		
		startActivityForResult(intent, requestCode);
	}

	/**
	 * 销毁当前界面
	 */
	protected void finishActivity() {
		activity.finish();
	}
	
	/**
	 * 销毁当前界面
	 */
	protected void activityFinish() {
		activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
		finishActivity();
	}

	/**
	 * 销毁当前界面(回调)
	 */
	protected void finishActivityForResult(Bundle bundle) {
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		
		activity.setResult(Activity.RESULT_OK, intent);
		finishActivity();
	}

	/**
	 * 销毁当前界面(回调)
	 */
	protected void activityFinishForResult(Bundle bundle) {
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		activity.setResult(Activity.RESULT_OK, intent);
		activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
		finishActivity();
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		ToastUtil.removeToast();
		dialogUtil.removeDialog();
	}
}