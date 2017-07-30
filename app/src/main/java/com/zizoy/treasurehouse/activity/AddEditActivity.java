package com.zizoy.treasurehouse.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.treasurehouse.R;
import com.zizoy.treasurehouse.adapter.BannerAdapter;
import com.zizoy.treasurehouse.api.MApplication;
import com.zizoy.treasurehouse.base.SuperActivity;
import com.zizoy.treasurehouse.bean.AjaxParamsBean;
import com.zizoy.treasurehouse.util.CircleFlowIndicator;
import com.zizoy.treasurehouse.util.ClearEditText;
import com.zizoy.treasurehouse.util.JsonUtil;
import com.zizoy.treasurehouse.util.ToastUtil;
import com.zizoy.treasurehouse.util.ViewFlow;
import com.zizoy.xutils.exception.HttpException;
import com.zizoy.xutils.http.RequestParams;
import com.zizoy.xutils.http.ResponseInfo;
import com.zizoy.xutils.http.callback.RequestCallBack;
import com.zizoy.xutils.http.client.HttpRequest;

/**
 * @Description: 程序关于界面
 *
 * @author falcon
 */
public class AddEditActivity extends SuperActivity {
    private TextView title;
    private LinearLayout backBtn;
    private LinearLayout gotoBtn;
    private TextView gotoTv;

    private ViewFlow mGallery;
	private FrameLayout photoLayout;
    private CircleFlowIndicator mIndicator;
    private ClearEditText name; // 标题
    private ClearEditText price; // 价格
    private ClearEditText user; // 姓名
    private ClearEditText phone; // 电话
    private ClearEditText address; // 地址
    private EditText note; // 内容

    private String idStr = null;
    private String nameStr = null;
    private String priceStr = null;
    private String userStr = null;
    private String phoneStr = null;
    private String noteStr = null;
    private String addressStr = null;

    private List<Map<String, String>> photosData;

	// 详情接口地址
	private String detailPath = MApplication.serverURL + "post/getPostDetail";
    // 信息修改接口地址
    private String editPath = MApplication.serverURL + "post/editPost1";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_edit;
    }

    @Override
	protected void initData() {
		super.initData();

		Bundle bundle = getIntent().getExtras();

		if (bundle != null) {
			idStr = bundle.getString("id");
		}
    }

    @Override
    protected void initView() {
        super.initView();

        title = (TextView) findViewById(R.id.tv_title);
        backBtn = (LinearLayout) findViewById(R.id.btn_back);
        gotoBtn = (LinearLayout) findViewById(R.id.btn_goto);
        gotoTv = (TextView) findViewById(R.id.tv_goto);
        
        mGallery = (ViewFlow) findViewById(R.id.gwzpGallery);
		photoLayout = (FrameLayout) findViewById(R.id.ll_photo);
		mIndicator = (CircleFlowIndicator) findViewById(R.id.gwzpFlowindic);
        name = (ClearEditText) findViewById(R.id.et_name);
        price = (ClearEditText) findViewById(R.id.et_price);
        user = (ClearEditText) findViewById(R.id.et_user);
        phone = (ClearEditText) findViewById(R.id.et_phone);
        address = (ClearEditText) findViewById(R.id.et_address);
        note = (EditText) findViewById(R.id.et_note);
    }

    @Override
    protected void initCodeLogic() {
        super.initCodeLogic();

        title.setText("信息编辑");
        gotoTv.setText("提交");
        backBtn.setVisibility(View.VISIBLE);
        gotoBtn.setVisibility(View.VISIBLE);
        
        getDetailDate(); // 获取详情数据
    }

    @Override
    protected void setListener() {
        super.setListener();

        backBtn.setOnClickListener(mBtnClick);
        gotoBtn.setOnClickListener(mBtnClick);
    }

    /**
     * 按钮点击触发事件
     */
    private OnClickListener mBtnClick = new OnClickListener() {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_back: // 界面返回被点击
                    activityFinish();
                    break;

                case R.id.btn_goto: // 信息修改被点击
                    nameStr = name.getText().toString().trim();
                    priceStr = price.getText().toString().trim();
                    userStr = user.getText().toString().trim();
                    phoneStr = phone.getText().toString().trim();
                    noteStr = note.getText().toString().trim();
                    addressStr = address.getText().toString().trim();

                    if ("".equals(nameStr) || nameStr == null) {
                        ToastUtil.showMessage(activity, "标题不能为空！");
                        name.requestFocus();
                    } else if ("".equals(priceStr) || priceStr == null) {
                        ToastUtil.showMessage(activity, "价格不能为空！");
                        price.requestFocus();
                    } else if ("".equals(userStr) || userStr == null) {
                        ToastUtil.showMessage(activity, "姓名不能为空！");
                        user.requestFocus();
                    } else if ("".equals(phoneStr) || phoneStr == null) {
                        ToastUtil.showMessage(activity, "手机号不能为空！");
                        phone.requestFocus();
                    } else if ("".equals(noteStr) || noteStr == null) {
						ToastUtil.showMessage(activity, "内容不能为空！");
						note.requestFocus();
					} else if (noteStr.length() < 10) {
						ToastUtil.showMessage(activity, "内容至少输入10字符！");
					} else {
                        putAddData();
                    }
                    break;
                    
                default: break;
            }
        }
    };
    
    /**
	 * 获取详情数据
	 */
	private void getDetailDate() {
		if (checkNet.checkNet()) {
			RequestParams params = new RequestParams();
			params.addBodyParameter("jsonpCallback", "jsonpCallback");
			params.addBodyParameter("id", "2");

			AjaxParamsBean bean = new AjaxParamsBean();
			bean.setPid(idStr);
			
			String json = JsonUtil.toJson(bean);
			params.addBodyParameter("jsoninput", json);
			
			httpUtils.send(HttpRequest.HttpMethod.POST, detailPath, params,
					new RequestCallBack<String>() {
						@Override
						public void onStart() {
						}

						@Override
						public void onLoading(long total, long current, boolean isUploading) {
						}

						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							try {
								JSONArray jsonArray = new JSONArray(responseInfo.result.substring(14, responseInfo.result.length() - 1));
								JSONObject jsonObject = jsonArray.getJSONObject(0);
								
								name.setText(jsonObject.getString("title"));
								price.setText(jsonObject.getString("price"));
								note.setText(jsonObject.getString("content"));
								address.setText(jsonObject.getString("address"));
								user.setText(jsonObject.getString("contact"));
								phone.setText(jsonObject.getString("phone"));

								JSONArray photoArray = jsonObject.getJSONArray("postattachList");
								photosData = new ArrayList<>();

								if (photoArray != null && photoArray.length() > 0) {
									for (int i = 0; i < photoArray.length(); i++) {
										Map<String, String> map = new HashMap<>();
										
										String photoPath = MApplication.serverURL + "upload/";
										String suffix = photoArray.getJSONObject(i).getJSONObject("attachment").getString("suffix");
										String photoUrl = photoArray.getJSONObject(i).getJSONObject("attachment").getString("name") + "." + suffix;
										photoPath = photoPath + photoUrl;
										map.put("url", photoPath);
										
										photosData.add(map);
									}
									mGallery.setAdapter(new BannerAdapter(activity, photosData));
	                                mGallery.setmSideBuffer(photosData.size());
	                                mGallery.setFlowIndicator(mIndicator);
	                                mGallery.setTimeSpan(4500);
	                                mGallery.setSelection(3 * 1000); // 设置初始位置
	                                mGallery.startAutoFlowTimer(); // 启动自动播放
	                                photoLayout.setVisibility(View.VISIBLE);
								} else {
									ToastUtil.showMessage(activity, "暂无图片数据！");
									photoLayout.setVisibility(View.GONE);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						@Override
						public void onFailure(HttpException error, String msg) {
							ToastUtil.showMessage(activity, "网络异常！");
						}
					});
		} else {
			dialogUtil.showNetworkDialog(); // 显示提示界面
		}
	}

	/**
	 * 提交发布数据
	 */
	private void putAddData() {
		if (checkNet.checkNet()) {
			RequestParams params = new RequestParams();
			params.addBodyParameter("pid", idStr);
			params.addBodyParameter("title", nameStr);
			params.addBodyParameter("price", priceStr);
			params.addBodyParameter("contact", userStr);
			params.addBodyParameter("phone", phoneStr);
			params.addBodyParameter("address", addressStr);
			params.addBodyParameter("content", noteStr);

			httpUtils.send(HttpRequest.HttpMethod.POST, editPath, params,
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
								JSONObject jsonObject = new JSONObject(responseInfo.result);

								if ("ok".equals(jsonObject.getString("msg")) && "1".equals(jsonObject.getString("stat"))) {
									ToastUtil.showMessage(activity, "该信息修改成功");
									activityFinishForResult(null);
								} else {
									ToastUtil.showMessage(activity, "该信息修改失败！");
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