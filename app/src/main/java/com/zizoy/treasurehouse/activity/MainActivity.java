package com.zizoy.treasurehouse.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.example.treasurehouse.R;
import com.tencent.bugly.crashreport.CrashReport;
import com.zizoy.treasurehouse.api.MApplication;
import com.zizoy.treasurehouse.base.SuperActivity;
import com.zizoy.treasurehouse.util.ClearEditText;
import com.zizoy.treasurehouse.util.ExitUtil;
import com.zizoy.treasurehouse.util.PreferencesUtils;
import com.zizoy.treasurehouse.util.ToastUtil;

/**
 * @Description: 程序主界面
 *
 * @author falcon
 */
public class MainActivity extends SuperActivity {
    private ExitUtil exitUtil;

    private TextView title;
    private LinearLayout cityBtn;
    private LinearLayout phoneBtn;
    private TextView cityTv;
    
    private ClearEditText searchEt;
    private Button searchBtn;

    private ImageView fcBtn; // 房产
    private ImageView jtgjBtn; // 交通工具
    private ImageView sjBtn; // 手机
    private ImageView jdBtn; // 家电
    private ImageView zpBtn; // 招聘
    private ImageView esjyBtn; // 二手交易
    
    private Button addBtn; // 添加发布
    private Button personalBtn; // 个人中心 
    
    private String userId  = null;
    private String searchStr = null;

    @Override
    public void onResume() {
        super.onResume();

        userId = PreferencesUtils.getStringPreference(activity, "ZHKJ", "userId", "");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData() {
        super.initData();

        exitUtil = new ExitUtil(activity);
    }

    @Override
    protected void initView() {
        super.initView();

        title = (TextView) findViewById(R.id.tv_title);
        cityBtn = (LinearLayout) findViewById(R.id.btn_city);
        phoneBtn = (LinearLayout) findViewById(R.id.btn_phone);
        cityTv = (TextView) findViewById(R.id.tv_city);
        
        searchEt = (ClearEditText) findViewById(R.id.et_search);
        searchBtn = (Button) findViewById(R.id.btn_Search);
        fcBtn = (ImageView) findViewById(R.id.btn_fc);
        jtgjBtn = (ImageView) findViewById(R.id.btn_jtgj);
        sjBtn = (ImageView) findViewById(R.id.btn_sj);
        jdBtn = (ImageView) findViewById(R.id.btn_jd);
        zpBtn = (ImageView) findViewById(R.id.btn_zp);
        esjyBtn = (ImageView) findViewById(R.id.btn_esjy);
        addBtn = (Button) findViewById(R.id.btn_add);
        personalBtn = (Button) findViewById(R.id.btn_personal);
    }

    @Override
    protected void initCodeLogic() {
        super.initCodeLogic();

        title.setText("置换空间");
        cityBtn.setVisibility(View.VISIBLE);
        phoneBtn.setVisibility(View.VISIBLE);

        getCityLocate(); // 获取当前定位城市
    }

    @Override
    protected void setListener() {
        super.setListener();

		cityBtn.setOnClickListener(mBtnClick);
		phoneBtn.setOnClickListener(mBtnClick);

		searchBtn.setOnClickListener(mBtnClick);
		fcBtn.setOnClickListener(mBtnClick);
		jtgjBtn.setOnClickListener(mBtnClick);
		sjBtn.setOnClickListener(mBtnClick);
		jdBtn.setOnClickListener(mBtnClick);
		zpBtn.setOnClickListener(mBtnClick);
		esjyBtn.setOnClickListener(mBtnClick);
		addBtn.setOnClickListener(mBtnClick);
		personalBtn.setOnClickListener(mBtnClick);
        searchEt.setOnEditorActionListener(searchListener);
	}

    /**
     * 按钮点击触发事件
     */
    private OnClickListener mBtnClick = new OnClickListener() {

        @Override
        public void onClick(View view) {
        	Bundle bundle = new Bundle();
        	bundle.putString("city", cityTv.getText().toString().trim());
        	
            switch (view.getId()) {
                case R.id.btn_city: // 城市选择被点击
                    startActivityForResult(CityChoiceActivity.class, null, 1);
                    overridePendingTransition(R.anim.left_in, R.anim.left_out);
                    break;
                    
                case R.id.btn_phone: // 联系电话被点击
                	callPhone("18614005553");
                    break;
                    
                case R.id.btn_Search: // 关键字搜索被点击
                	searchStr = searchEt.getText().toString().trim();
                	
                	if ("".equals(searchStr) || searchStr == null) {
                		ToastUtil.showMessage(activity, "搜索关键字不能为空！");
						searchEt.requestFocus();
					} else {
						bundle.putString("search", searchStr);
						startActivity(SearchActivity.class, bundle);
						overridePendingTransition(R.anim.left_in, R.anim.left_out);
					}
                	break;
                    
                case R.id.btn_fc: // 房产被点击
                	bundle.putString("type", "房产");
                	startActivity(ShowTypeActivity.class, bundle);
                	overridePendingTransition(R.anim.left_in, R.anim.left_out);
                	break;
                	
                case R.id.btn_jtgj: // 交通工具被点击
                	bundle.putString("type", "交通工具");
                	startActivity(ShowTypeActivity.class, bundle);
                	overridePendingTransition(R.anim.left_in, R.anim.left_out);
                	break;
                	
                case R.id.btn_sj: // 手机被点击
                	bundle.putString("type", "手机");
                	startActivity(ShowTypeActivity.class, bundle);
                	overridePendingTransition(R.anim.left_in, R.anim.left_out);
                	break;
                	
                case R.id.btn_jd: // 家电被点击
                	bundle.putString("type", "家电");
                	startActivity(ShowTypeActivity.class, bundle);
                	overridePendingTransition(R.anim.left_in, R.anim.left_out);
                	break;
                	
                case R.id.btn_zp: // 招聘被点击
                	bundle.putString("type", "招聘");
                	startActivity(ShowTypeActivity.class, bundle);
                	overridePendingTransition(R.anim.left_in, R.anim.left_out);
                	break;
                	
                case R.id.btn_esjy: // 二手交易被点击
                	bundle.putString("type", "二手交易");
                	startActivity(ShowTypeActivity.class, bundle);
                	overridePendingTransition(R.anim.left_in, R.anim.left_out);
                	break;
                	
                case R.id.btn_add: // 添加发布被点击
                    if ("".equals(userId)) {
                        ToastUtil.showMessage(activity, "请先登录！");
                        startActivity(PersonalActivity.class, null);
                    } else {
                        startActivity(ReleaseActivity.class, null);
                    }
                    overridePendingTransition(R.anim.left_in, R.anim.left_out);
                    break;

                case R.id.btn_personal: // 个人中心被点击
                    if ("".equals(userId)) {
                        startActivity(PersonalActivity.class, bundle);
                    } else {
                        startActivity(MyAddActivity.class, bundle);
                    }
                    overridePendingTransition(R.anim.left_in, R.anim.left_out);
                    break;

                default: break;
            }
        }
    };

    /**
     * 键盘搜索按钮监听
     */
    private OnEditorActionListener searchListener = new OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if ((actionId == 0 || actionId == 3) && event != null) {
                searchStr = searchEt.getText().toString().trim();

                if ("".equals(searchStr) || searchStr == null) {
                    ToastUtil.showMessage(activity, "搜索关键字不能为空！");
                    searchEt.requestFocus();
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("search", searchStr);
                    startActivity(SearchActivity.class, bundle);
                    overridePendingTransition(R.anim.left_in, R.anim.left_out);
                }

                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
            return false;
        }
    };

    /**
     * 获取当前定位城市
     */
    private void getCityLocate() {
        if ("".equals(MApplication.cityStr) || MApplication.cityStr == null) {
            MApplication.cityStr = "北京";
            MApplication.addressStr = "暂无定位地址信息";
        }
        cityTv.setText(MApplication.cityStr.replace("市", ""));
    }

    /**
     * 打电话
     * @param phoneStr
     */
    private void callPhone(final String phoneStr) {
        intent = new Intent();
        intent.setAction("android.intent.action.DIAL");
        intent.setData(Uri.parse("tel:" + phoneStr));
        activity.startActivity(intent);
    }

    /**
     * 获取返回参数
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;

        switch (requestCode) {
            case 1: // 城市选择
                cityTv.setText(data.getExtras().getString("cityName"));
                break;

            default: break;
        }
    }

    /**
     * 按两次返回键退出
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitUtil.exit();
        }
        return true;
    }
}