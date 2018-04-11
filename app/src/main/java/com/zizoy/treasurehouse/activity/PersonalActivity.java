package com.zizoy.treasurehouse.activity;

import android.content.Context;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.treasurehouse.R;
import com.mob.MobSDK;
import com.zizoy.treasurehouse.api.MApplication;
import com.zizoy.treasurehouse.base.SuperActivity;
import com.zizoy.treasurehouse.bean.AjaxParamsBean;
import com.zizoy.treasurehouse.bean.LoginGetBean;
import com.zizoy.treasurehouse.util.JsonUtil;
import com.zizoy.treasurehouse.util.PreferencesUtils;
import com.zizoy.treasurehouse.util.ToastUtil;
import com.zizoy.xutils.exception.HttpException;
import com.zizoy.xutils.http.RequestParams;
import com.zizoy.xutils.http.ResponseInfo;
import com.zizoy.xutils.http.callback.RequestCallBack;
import com.zizoy.xutils.http.client.HttpRequest;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qq.QQ;

/**
 * @author falcon
 * @Description: 程序警报界面
 */
public class PersonalActivity extends SuperActivity implements Callback {
    private TextView title;
    private LinearLayout backBtn;
    private ImageView qqBtn;

    private Handler qqHandler;
    private static final int MSG_AUTH_CANCEL = 1;
    private static final int MSG_AUTH_ERROR = 2;
    private static final int MSG_AUTH_COMPLETE = 3;

    private String from;

    private String loginPath = MApplication.serverURL + "user/loginUser";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_personal;
    }

    @Override
    protected void initData() {
        super.initData();

        qqHandler = new Handler(Looper.getMainLooper(), this);
    }

    @Override
    protected void initView() {
        super.initView();

        title = (TextView) findViewById(R.id.tv_title);
        backBtn = (LinearLayout) findViewById(R.id.btn_back);
        qqBtn = (ImageView) findViewById(R.id.img_qq);

        from = getIntent().getStringExtra("from");
    }

    @Override
    protected void setListener() {
        super.setListener();

        backBtn.setOnClickListener(mBtnClick);
        qqBtn.setOnClickListener(mBtnClick);
    }

    @Override
    protected void initCodeLogic() {
        super.initCodeLogic();

        title.setText("个人中心");
        backBtn.setVisibility(View.VISIBLE);
    }

    /**
     * 控件点击触发事件
     */
    private OnClickListener mBtnClick = new OnClickListener() {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_back: // 界面返回被点击
                    activityFinish();
                    break;

                case R.id.img_qq: // QQ登陆被点击
                    loginShareSDK(activity, QQ.NAME);
                    break;

                default: break;
            }
        }
    };

    /**
     * 登录QQ第三方
     *
     * @param context
     * @param platformName
     */
    private void loginShareSDK(Context context, String platformName) {
        //初始化SDK
        if (!(context instanceof MApplication)) {
            MobSDK.init(context.getApplicationContext());
        }
        Platform plat = ShareSDK.getPlatform(platformName);
        if (plat == null) {
            return;
        }

        if (plat.isAuthValid()) {
            plat.removeAccount(true);
        }

        //使用SSO授权，通过客户单授权
        plat.SSOSetting(false);
        plat.setPlatformActionListener(qqLoginListener);
        plat.showUser(null);
    }

    /**
     * QQ登录监听事件
     */
    private PlatformActionListener qqLoginListener = new PlatformActionListener() {
        @Override
        public void onComplete(Platform plat, int action, HashMap<String, Object> res) {
            if (action == Platform.ACTION_USER_INFOR) {
                Message msg = new Message();
                msg.what = MSG_AUTH_COMPLETE;
                msg.arg2 = action;
                msg.obj = new Object[]{plat.getName(), res};
                qqHandler.sendMessage(msg);
            }
        }

        @Override
        public void onError(Platform plat, int action, Throwable t) {
            if (action == Platform.ACTION_USER_INFOR) {
                Message msg = new Message();
                msg.what = MSG_AUTH_ERROR;
                msg.arg2 = action;
                msg.obj = t;
                qqHandler.sendMessage(msg);
            }
            t.printStackTrace();
        }

        @Override
        public void onCancel(Platform plat, int action) {
            if (action == Platform.ACTION_USER_INFOR) {
                Message msg = new Message();
                msg.what = MSG_AUTH_CANCEL;
                msg.arg2 = action;
                msg.obj = plat;
                qqHandler.sendMessage(msg);
            }
        }
    };

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_AUTH_CANCEL: { // 登录取消
                Toast.makeText(activity, "登录取消！", Toast.LENGTH_SHORT).show();
            }
            break;

            case MSG_AUTH_ERROR: { // 登录失败
                Toast.makeText(activity, "登录失败！", Toast.LENGTH_SHORT).show();
            }
            break;

            case MSG_AUTH_COMPLETE: { // 登录成功
                Object[] objs = (Object[]) msg.obj;
                String plat = (String) objs[0];

                Platform platform = ShareSDK.getPlatform(plat);
                if (platform != null) {
                    String userName = platform.getDb().getUserName();
                    String userId = platform.getDb().getUserId();

                    putLoginData(userId, userName);
                }
            }
            break;
        }
        return false;
    }

    /**
     * 提交登录数据
     */
    private void putLoginData(final String userId, final String userName) {
        if (checkNet.checkNet()) {
            RequestParams params = new RequestParams();
            params.addBodyParameter("jsonpCallback", "jsonpCallback");

            AjaxParamsBean bean = new AjaxParamsBean();
            bean.setUserid(userId);
            bean.setUsername(userName);
            bean.setWeixin(userName);
            bean.setPhone("");

            String json = JsonUtil.toJson(bean);
            params.addBodyParameter("jsoninput", json);

            httpUtils.send(HttpRequest.HttpMethod.POST, loginPath, params,
                    new RequestCallBack<String>() {
                        @Override
                        public void onStart() {
                            dialogUtil.showSubmitDialog(); // 显示加载Dialog
                        }

                        @Override
                        public void onLoading(long total, long current, boolean isUploading) {
                        }

                        @Override
                        public void onSuccess(ResponseInfo<String> responseInfo) {
                            dialogUtil.closeDialog();// 加载成功关闭Dialog
                            try {
                                String jsonObject = responseInfo.result.substring(14, responseInfo.result.length() - 1);

                                LoginGetBean login = JsonUtil.fromJson(jsonObject, LoginGetBean.class);

                                PreferencesUtils.setStringPreferences(activity, "ZHKJ", "userId", login.getUid() + "");
                                PreferencesUtils.setStringPreferences(activity, "ZHKJ", "userName", login.getUsername());

                                ToastUtil.showMessage(activity, "该用户登陆成功");
                                if(!"message".equals(from)){
                                    startActivity(MyAddActivity.class, null);
                                }
                                overridePendingTransition(R.anim.left_in, R.anim.left_out);
                                finishActivity();
                            } catch (Exception e) {
                                e.printStackTrace();
                                ToastUtil.showMessage(activity, "该用户登陆失败！");
                            }
                        }

                        @Override
                        public void onFailure(HttpException error, String msg) {
                            dialogUtil.closeDialog(); // 加载失败关闭Dialog并提示
                            ToastUtil.showMessage(activity, "网络异常！");
                        }
                    });
        } else {
            dialogUtil.showNetworkDialog(); // 显示提示界面
        }
    }

    /**
     * 按系统键返回
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            activityFinish();
        }
        return true;
    }
}