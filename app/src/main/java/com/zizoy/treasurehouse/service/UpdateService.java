package com.zizoy.treasurehouse.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.treasurehouse.R;
import com.zizoy.treasurehouse.api.MApplication;
import com.zizoy.treasurehouse.util.ToastUtil;
import com.zizoy.xutils.HttpUtils;
import com.zizoy.xutils.exception.HttpException;
import com.zizoy.xutils.http.ResponseInfo;
import com.zizoy.xutils.http.callback.RequestCallBack;

import java.io.File;

/**
 * 软件更新服务
 */
public class UpdateService extends Service {
	/** 网络解析工具类 **/
	protected HttpUtils httpUtils;
	/** 下载APK存储位置 **/
	private String apkPath = MApplication.filePath + "update.apk";

	private UpdateService activity;
    private int notificationId = 101;
    private DownloadBinder mBinder = new DownloadBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        
        activity = this;
        httpUtils = MApplication.getHttpUtils();
    }
    
    public class DownloadBinder extends Binder {
    	/**
         *  开始下载APP
         * */
        public void startDownLoad(String url) {
    		httpUtils.download(url, apkPath, new RequestCallBack<File>() {

    			@Override
    			public void onStart() {
    				Log.e("zizoy", "----文件开始下载----onSuccess--");
                    startForeground(notificationId, getNotification("APP下载中...", 0));
    			}
    			
    			@Override
    			public void onLoading(long totalSize, long currentSize, boolean isUploading) {
    				super.onLoading(totalSize, currentSize, isUploading);

					Log.e("zizoy", "downloadProgress -- " + totalSize + "  " + currentSize);

					float progress = (float) currentSize / (float) totalSize;

                    getNotificationManager().notify(notificationId, getNotification("APP下载中...", (Math.round(progress * 10000) * 1.0f / 100)));
    			}

    			@Override
    			public void onSuccess(ResponseInfo<File> responseInfo) {
    				Log.e("zizoy", "----文件下载完成----onSuccess--");
                    getNotificationManager().notify(notificationId, getNotification("APP已下载完成", 100));
                    getNotificationManager().cancel(notificationId); //关闭通知
                    stopForeground(true);

                    Intent intent = new Intent(Intent.ACTION_VIEW);

                    if (Build.VERSION.SDK_INT >= 24) { // 系统版本是否在7.0以上
                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        Uri contentUri = FileProvider.getUriForFile(activity, "com.example.treasurehouse.fileprovider", responseInfo.result);
                        intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
                    } else { // 系统版本是否在7.0以下
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        Uri uri = Uri.fromFile(responseInfo.result);
                        intent.setDataAndType(uri, "application/vnd.android.package-archive");
                    }
                    startActivity(intent);
    			}
    			@Override
    			public void onFailure(HttpException error, String msg) {
    				ToastUtil.showMessage(activity, "网络异常，下载失败！");
    			}
    		});
    	}
    }

    /**
     * 获取系统状态栏信息服务
     */
    private NotificationManager getNotificationManager() {
        return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    /**
     * 自定义显示通知栏
     *
     * @param title
     * @param progress
     */
    private Notification getNotification(final String title, final float progress) {
        RemoteViews contentViews = new RemoteViews(getPackageName(), R.layout.view_notify_load);
        contentViews.setImageViewResource(R.id.iv_pushIco, R.mipmap.push_icon);
        contentViews.setTextViewText(R.id.tv_pushTitle, title);
        contentViews.setTextViewText(R.id.tv_pushResult, progress + "%");
        contentViews.setProgressBar(R.id.pb_pushPlan, 100, (int)progress, false);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(activity);
        builder.setSmallIcon(R.mipmap.push_icon);
        // 自定义布局
        builder.setContent(contentViews);
        builder.setOngoing(false);
        if (progress >= 100) {
            builder.setAutoCancel(true);
        }
        return builder.build();
    }
}