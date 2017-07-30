package com.zizoy.treasurehouse.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.treasurehouse.R;
import com.zizoy.treasurehouse.adapter.MyAddAdapter;
import com.zizoy.treasurehouse.api.MApplication;
import com.zizoy.treasurehouse.base.SuperActivity;
import com.zizoy.treasurehouse.bean.AjaxParamsBean;
import com.zizoy.treasurehouse.util.JsonUtil;
import com.zizoy.treasurehouse.util.PreferencesUtils;
import com.zizoy.treasurehouse.util.ToastUtil;
import com.zizoy.xutils.exception.HttpException;
import com.zizoy.xutils.http.RequestParams;
import com.zizoy.xutils.http.ResponseInfo;
import com.zizoy.xutils.http.callback.RequestCallBack;
import com.zizoy.xutils.http.client.HttpRequest;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 程序我的发布界面
 *
 * @author falcon
 */
public class MyAddActivity extends SuperActivity {
	private TextView title;
	private LinearLayout backBtn;
	private LinearLayout gotoBtn;
	private TextView gotoTv;

	private ListView addList;
	private MyAddAdapter mAdapter;
	private LinearLayout addBtn;
	
	private String nowData = null;
	private String userId  = null;
	
	private List<Map<String, String>> dataList;

	// 我的发布接口地址
	private String myPath = MApplication.serverURL + "post/listPostByUserId";
	// 刷新发布接口地址
	private String deletePath = MApplication.serverURL + "post/deletePostByPid";
	// 刷新发布接口地址
	private String refreshPath = MApplication.serverURL + "post/pushpost";
	
	@Override
	protected int getLayoutId() {
		return R.layout.activity_my_add;
	}

	@Override
	protected void onResume() {
		super.onResume();

		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		nowData = sDateFormat.format(new java.util.Date());
	}

	@Override
	protected void initData() {
		super.initData();

		userId = PreferencesUtils.getStringPreference(activity, "ZHKJ", "userId", "");
	}

	@Override
	protected void initView() {
		super.initView();

		title = (TextView) findViewById(R.id.tv_title);
		backBtn = (LinearLayout) findViewById(R.id.btn_back);
		gotoBtn = (LinearLayout) findViewById(R.id.btn_goto);
		gotoTv = (TextView) findViewById(R.id.tv_goto);
		addList = (ListView) findViewById(R.id.addList);
		addBtn = (LinearLayout) findViewById(R.id.ll_add);
	}
	
	@Override
	protected void setListener() {
		super.setListener();
		
		backBtn.setOnClickListener(mBtnClick);
		gotoBtn.setOnClickListener(mBtnClick);
		addBtn.setOnClickListener(mBtnClick);
	}
	
	@Override
	protected void initCodeLogic() {
		super.initCodeLogic();
		
		title.setText("个人中心");
		gotoTv.setText("退出");
		backBtn.setVisibility(View.VISIBLE);
		gotoBtn.setVisibility(View.VISIBLE);
		
		getAddDate(); // 获取已发布数据
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

			case R.id.btn_goto: // 退出被点击
				PreferencesUtils.setStringPreferences(activity, "ZHKJ", "userId", "");
				PreferencesUtils.setStringPreferences(activity, "ZHKJ", "userName", "");

				ToastUtil.showMessage(activity, "用户退出成功");
				activityFinish();
				break;
				
			case R.id.ll_add: // 添加发布被点击
				startActivity(ReleaseActivity.class, null);
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
				break;

			default: break;
			}
		}
	};

	/**
	 * 获取已发布数据
	 */
	private void getAddDate() {
		if (checkNet.checkNet()) {
			RequestParams params = new RequestParams();
			params.addBodyParameter("jsonpCallback", "jsonpCallback");

			AjaxParamsBean bean = new AjaxParamsBean();
			bean.setUid(userId);
			bean.setPageno("1");
			bean.setPagesize("100");
			
			String json = JsonUtil.toJson(bean);
			params.addBodyParameter("jsoninput", json);
			
			httpUtils.send(HttpRequest.HttpMethod.POST, myPath, params,
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
								String jsonObject = responseInfo.result.substring(14, responseInfo.result.length() - 1);
								JSONArray jsonArray = new JSONArray(jsonObject);
								dataList = new ArrayList<>();

								if (jsonArray != null && jsonArray.length() > 0) {
									for (int i = 0; i < jsonArray.length(); i++) {
										Map<String, String> map = new HashMap<>();
										map.put("pid", jsonArray.getJSONObject(i).getString("pid"));
										map.put("title", jsonArray.getJSONObject(i).getString("title"));
										map.put("url", jsonArray.getJSONObject(i).getString("url"));
										map.put("price", jsonArray.getJSONObject(i).getString("price"));
										map.put("address", jsonArray.getJSONObject(i).getString("address"));
										map.put("readernum", jsonArray.getJSONObject(i).getString("readernum"));

										dataList.add(map);
									}
									mAdapter = new MyAddAdapter(activity, dataList);
									addList.setAdapter(mAdapter);
								} else {
									ToastUtil.showMessage(activity, "暂无发布数据！");
									mAdapter = new MyAddAdapter(activity, dataList);
									addList.setAdapter(mAdapter);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						@Override
						public void onFailure(HttpException error, String msg) {
							ToastUtil.showMessage(activity, "网络异常！");
							
							mAdapter = new MyAddAdapter(activity, dataList);
							addList.setAdapter(mAdapter);
						}
					});
		} else {
			dialogUtil.showNetworkDialog(); // 显示提示界面
		}
	}

	/**
	 * 帖子编辑
	 */
	public void putEditData(final String pidStr) {
		Bundle bundle = new Bundle();
		bundle.putString("id", pidStr);
		startActivityForResult(AddEditActivity.class, bundle, 1);
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
	}

	/**
	 * 帖子刷新
	 * @param pidStr
	 */
	public void getRefreshData(final String pidStr) {
		String oldDate = PreferencesUtils.getStringPreference(activity, "ZHKJ", "pidDate", "");
		String refreshDate = PreferencesUtils.getStringPreference(activity, "ZHKJ", "refreshDate", "");

		if (nowData.equals(refreshDate)) {
			String[] pids = oldDate.split(",");
			boolean flage = false;

			for (int i = 0, len = pids.length; i < len; i++) {
				String refreshPid = pids[i].toString();

				if (refreshPid.equals(pidStr)) {
					flage = true;
				}
			}
			if (flage) { // 之前已刷新
				ToastUtil.showMessage(activity, "每条每天只能置顶一次！");
			} else { // 之前未刷新
				putRefreshData(pidStr);
			}
		} else {
			PreferencesUtils.setStringPreferences(activity, "ZHKJ", "pidDate", "");
			putRefreshData(pidStr);
		}
	}

	/**
	 * 提交删除数据
	 */
	public void putDeleteData(final String pidStr) {
		if (checkNet.checkNet()) {
			RequestParams params = new RequestParams();
			params.addBodyParameter("jsonpCallback", "jsonpCallback");
			
			AjaxParamsBean bean = new AjaxParamsBean();
			bean.setPid(pidStr);

			String json = JsonUtil.toJson(bean);
			params.addBodyParameter("jsoninput", json);
			
			httpUtils.send(HttpRequest.HttpMethod.POST, deletePath, params,
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
								String jsonObject = responseInfo.result.substring(15, responseInfo.result.length()-2);

								if ("1".equals(jsonObject)) {
									ToastUtil.showMessage(activity, "该信息删除成功");
									getAddDate();
								} else {
									ToastUtil.showMessage(activity, "该信息删除失败！");
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
	 * 提交刷新数据
	 */
	private void putRefreshData(final String pidStr) {
		if (checkNet.checkNet()) {
			RequestParams params = new RequestParams();
			params.addBodyParameter("jsonpCallback", "jsonpCallback");
			
			final AjaxParamsBean bean = new AjaxParamsBean();
			bean.setPid(pidStr);

			String json = JsonUtil.toJson(bean);
			params.addBodyParameter("jsoninput", json);
			
			httpUtils.send(HttpRequest.HttpMethod.POST, refreshPath, params,
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
								String jsonObject = responseInfo.result.substring(15, responseInfo.result.length()-2);

								if ("1".equals(jsonObject)) {
									ToastUtil.showMessage(activity, "该信息置顶成功");

									String oldDate = PreferencesUtils.getStringPreference(activity, "ZHKJ", "pidDate", "");
									StringBuffer mBuffer = new StringBuffer();

									if ("".equals(oldDate)) {
										mBuffer.append(pidStr);
									} else {
										mBuffer.append(oldDate).append(",").append(pidStr);
									}

									PreferencesUtils.setStringPreferences(activity, "ZHKJ", "pidDate", mBuffer.toString());
									PreferencesUtils.setStringPreferences(activity, "ZHKJ", "refreshDate", nowData);
								} else {
									ToastUtil.showMessage(activity, "该信息置顶失败！");
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
	 * 获取返回参数
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK)
			return;

		switch (requestCode) {
			case 1: // 帖子修改
				getAddDate();
				break;

			default: break;
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