package com.zizoy.treasurehouse.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.example.treasurehouse.R;
import com.zizoy.treasurehouse.api.MApplication;
import com.zizoy.treasurehouse.util.CheckNet;
import com.zizoy.treasurehouse.util.DialogUtil;
import com.zizoy.treasurehouse.util.ToastUtil;
import com.zizoy.xutils.HttpUtils;

/**
 * @Description: Activity父类
 *
 * @author falcon
 */
public abstract class SuperActivity extends Activity {
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
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(getLayoutId());

		initData(); // 数据初始化
		initView(); // 界面初始化
		initCodeLogic(); // 其他逻辑处理
		setListener(); // 界面UI事件监听
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
		activity = this;
		intent = new Intent();
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
		overridePendingTransition(R.anim.right_in, R.anim.right_out);
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
		overridePendingTransition(R.anim.right_in, R.anim.right_out);
		finishActivity();
	}
	
	/**
     * <p>
     * 发送消息
     * </p>
     * 
     * @author fangzhihua 2014-5-12 上午10:05:30
     * @param mHandler
     * @param msg
     * @param param1
     * @param param2
     */
    protected void sendMessage(Handler mHandler, int msg, int param1, int param2) {
        if (mHandler == null)
            return;
        Message message = mHandler.obtainMessage();
        message.what = msg;
        message.arg1 = param1;
        message.arg2 = param2;
        mHandler.sendMessage(message);
    }
	
	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
			ToastUtil.removeToast();
			dialogUtil.removeDialog();
	}
}