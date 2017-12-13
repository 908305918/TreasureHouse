package com.zizoy.treasurehouse.activity;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.content.Context;
import android.telephony.TelephonyManager;
import com.example.treasurehouse.R;
import com.zizoy.treasurehouse.api.MApplication;
import com.zizoy.treasurehouse.base.SuperActivity;
import com.zizoy.treasurehouse.service.UpdateService;
import com.zizoy.treasurehouse.util.ToastUtil;
import com.zizoy.treasurehouse.util.VersionUtil;
import com.zizoy.xutils.exception.HttpException;
import com.zizoy.xutils.http.RequestParams;
import com.zizoy.xutils.http.ResponseInfo;
import com.zizoy.xutils.http.callback.RequestCallBack;
import com.zizoy.xutils.http.client.HttpRequest;

import org.json.JSONObject;

/**
 * 启动欢迎界面
 *
 * @author falcon
 */
public class WelcomeActivity extends SuperActivity {
    private Dialog nDialog;
    private Dialog mDialog;

    private int olderVerCode; // 程序当前版本号
    private String versionName; // 程序当前程序版本名称
    private String apkUrl; // APK下载地址

    // 软件更新接口地址
    private String updatePath = MApplication.serverURL + "post/checkVersionCodeByMobile";

    private UpdateService.DownloadBinder mBinder;

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinder = (UpdateService.DownloadBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         * 解决按home键应用重新启动问题
         */
        if (!isTaskRoot()) {
            finish();
            return;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_welcome;
    }

    @Override
    protected void initData() {
        super.initData();

        // 获得程序当前的版本号
        olderVerCode = VersionUtil.getVerCode(activity);
        versionName = VersionUtil.getVerName(activity);

        Log.e("zizoy", "得到的本程序的版本号为：" + olderVerCode + " 程序版本名称为：" + versionName);
    }

    @Override
    protected void initView() {
        super.initView();
    }

    @Override
    protected void initCodeLogic() {
        super.initCodeLogic();

        goUpdate(); // 软件更新
        statistics();
        // 开启百度定位
        ((MApplication) getApplication()).startLocate();

        Intent intent = new Intent(activity, UpdateService.class);
        startService(intent);
        bindService(intent, mConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void setListener() {
        super.setListener();
    }

    /**
     * 按钮点击触发事件
     */
    private OnClickListener mBtnClick = new OnClickListener() {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.updateEnterBtn: // 下载更新被点击
                    mDialog.dismiss();

                    if (mBinder == null) {
                        return;
                    } else {
                        mBinder.startDownLoad(apkUrl);
                        startAnimation();
                    }
                    break;

                case R.id.updateCancelBtn: // 下载取消被点击
                    mDialog.cancel();

                    startAnimation(); // 执行程序开始欢迎动画
                    break;

                case R.id.dialogEnterBtn:
                    nDialog.dismiss();

                    Intent intent = new Intent("android.settings.WIRELESS_SETTINGS");
                    startActivity(intent);
                    break;
                case R.id.dialogCancelBtn:
                    nDialog.cancel();

                    startAnimation(); // 执行程序开始欢迎动画
                    break;

                default:
                    break;
            }
        }
    };

    /**
     * 程序开始欢迎动画
     */
    private void startAnimation() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                startActivity(MainActivity.class, null);
                finishActivity();
            }
        }, 3000);
    }

    /**
     * 显示网络连接提醒
     */
    private void showNetworkDialog() {
        nDialog = new Dialog(activity, R.style.mDialog);
        nDialog.setContentView(R.layout.view_network_dialog);
        Button dialogEnterBtn = (Button) nDialog.findViewById(R.id.dialogEnterBtn);
        Button dialogCancelBtn = (Button) nDialog.findViewById(R.id.dialogCancelBtn);

        dialogEnterBtn.setOnClickListener(mBtnClick);
        dialogCancelBtn.setOnClickListener(mBtnClick);
        nDialog.setCancelable(false);
        nDialog.show();
    }

    /**
     * 更新程序执行方法
     *
     * @param flag    更新判断
     * @param content 更新内容
     * @param apkurl  更新软件下载地址
     */
    private void showUpdateAPK(boolean flag, String content, String apkurl) {
        mDialog = new Dialog(activity, R.style.mDialog);
        mDialog.setContentView(R.layout.view_update_dialog);

        TextView updateMessage = (TextView) mDialog.findViewById(R.id.updateMessage);
        Button updateEnterBtn = (Button) mDialog.findViewById(R.id.updateEnterBtn);
        Button updateCancelBtn = (Button) mDialog.findViewById(R.id.updateCancelBtn);
        mDialog.setCancelable(false);

        if (flag) { // 如果为true选择更新
            updateEnterBtn.setVisibility(View.VISIBLE);
            updateCancelBtn.setVisibility(View.VISIBLE);
        } else { // 如果为false强行更新
            updateEnterBtn.setVisibility(View.VISIBLE);
            updateCancelBtn.setVisibility(View.GONE);
        }
        updateMessage.setText(content);
        apkUrl = apkurl;
        mDialog.show();

        updateEnterBtn.setOnClickListener(mBtnClick);
        updateCancelBtn.setOnClickListener(mBtnClick);
    }

    /**
     * 检查软件更新
     */
    private void goUpdate() {
        if (checkNet.checkNet()) {
            RequestParams params = new RequestParams();

            httpUtils.send(HttpRequest.HttpMethod.POST, updatePath, params,
                    new RequestCallBack<String>() {
                        @Override
                        public void onStart() {
                            dialogUtil.showUpdateDialog(); // 显示加载Dialog
                        }

                        @Override
                        public void onLoading(long total, long current, boolean isUploading) {
                        }

                        @Override
                        public void onSuccess(ResponseInfo<String> responseInfo) {
                            dialogUtil.closeDialog();// 加载成功关闭Dialog
                            try {
                                JSONObject jsonObject = new JSONObject(responseInfo.result);

                                if ("ok".equals(jsonObject.getString("msg")) && "1".equals(jsonObject.getString("stat"))) {

                                    // 如果版本名称小于服务器version_code并版本号小于服务器min_version_code（强行更新）
                                    if (!versionName.equals(jsonObject.getString("version_code")) && olderVerCode < Integer.parseInt(jsonObject.getString("min_version_code"))) {
                                        showUpdateAPK(false, jsonObject.getString("update_note"), jsonObject.getString("android_url"));

                                    } else if (olderVerCode < Integer.parseInt(jsonObject.getString("version_id"))) { // 如果版本号小于服务器version_id（提示用户更新）
                                        showUpdateAPK(true, jsonObject.getString("update_note"), jsonObject.getString("android_url"));

                                    } else if (!versionName.equals(jsonObject.getString("version_code"))) { // 如果版本名称小于服务器version_code（提示用户更新）
                                        showUpdateAPK(true, jsonObject.getString("update_note"), jsonObject.getString("android_url"));

                                    } else { // 判断当前无版本更新（正常执行）
                                        startAnimation(); // 执行程序开始欢迎动画
                                    }
                                } else {
                                    ToastUtil.showMessage(activity, "软件更新失败！");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(HttpException error, String msg) {
                            dialogUtil.closeDialog(); // 加载失败关闭Dialog并提示
                            ToastUtil.showMessage(activity, "网络异常！");
                        }
                    });
        } else {
            showNetworkDialog(); // 显示提示窗口
        }
    }

    /**
     * 统计
     */
    private void statistics() {
      String url = MApplication.serverURL + "/count/activationCount";
      TelephonyManager tm = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE));
      RequestParams params = new RequestParams();
      params.addBodyParameter("type", "0");
      params.addBodyParameter("machineFlag", tm.getDeviceId());
      MApplication.getHttpUtils().send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
          @Override
          public void onStart() {
              super.onStart();
          }

          @Override
          public void onSuccess(ResponseInfo<String> responseInfo) {

          }

          @Override
          public void onFailure(HttpException e, String s) {

          }
      });
    }
}
