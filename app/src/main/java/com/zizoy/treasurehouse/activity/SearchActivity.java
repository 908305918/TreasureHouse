package com.zizoy.treasurehouse.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.example.treasurehouse.R;
import com.zizoy.treasurehouse.adapter.ShowTypeAdapter;
import com.zizoy.treasurehouse.api.MApplication;
import com.zizoy.treasurehouse.base.SuperActivity;
import com.zizoy.treasurehouse.bean.AjaxParamsBean;
import com.zizoy.treasurehouse.util.ClearEditText;
import com.zizoy.treasurehouse.util.JsonUtil;
import com.zizoy.treasurehouse.util.ToastUtil;
import com.zizoy.xutils.exception.HttpException;
import com.zizoy.xutils.http.RequestParams;
import com.zizoy.xutils.http.ResponseInfo;
import com.zizoy.xutils.http.callback.RequestCallBack;
import com.zizoy.xutils.http.client.HttpRequest;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 程序信息搜索界面
 *
 * @author falcon
 */
public class SearchActivity extends SuperActivity {
	private  TextView title;
	private LinearLayout backBtn;
	
	private Button searchBtn;
	private ClearEditText searchEt;
	
	private ListView searchList;
	private ShowTypeAdapter mAdapter;

	private String cityStr = null;
	private String searchStr = null;

	private int curPage = 1; // 当前页数
	private int totalPage = 0; // 总数量
	private int pageSize = 50; // 每页显示条数
	private int listType; // 刷新/加载更多标识

	private List<Map<String, String>> dataList;
	
	// 搜索接口地址
	private String searchPath = MApplication.serverURL + "post/listPostByXYZ";

	@Override
	protected int getLayoutId() {
		return R.layout.activity_search;
	}
	
	@Override
	protected void initData() {
		super.initData();
		
		Bundle bundle = getIntent().getExtras();

		if (bundle != null) {
			cityStr = bundle.getString("city");
			searchStr = bundle.getString("search");
		}
	}
	
	@Override
	protected void initView() {
		super.initView();
		
		title = (TextView) findViewById(R.id.tv_title);
		backBtn = (LinearLayout) findViewById(R.id.btn_back);
		searchBtn = (Button) findViewById(R.id.btn_Search);
		searchEt = (ClearEditText) findViewById(R.id.et_search);
		searchList = (ListView) findViewById(R.id.searchList);
	}
	
	@Override
	protected void setListener() {
		super.setListener();
		
		backBtn.setOnClickListener(mBtnClick);
		searchBtn.setOnClickListener(mBtnClick);
		searchList.setOnItemClickListener(searchsClick);
		searchEt.setOnEditorActionListener(searchListener);
	}
	
	@Override
	protected void initCodeLogic() {
		super.initCodeLogic();
		
		title.setText("信息搜索");
		searchEt.setText(searchStr);
		backBtn.setVisibility(View.VISIBLE);
		
		getSearchDate(searchStr); // 获取搜索数据
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
				
			case R.id.btn_Search: // 关键字搜索被点击
            	searchStr = searchEt.getText().toString().trim();
            	
            	if ("".equals(searchStr) || searchStr == null) {
            		ToastUtil.showMessage(activity, "搜索关键字不能为空！");
					searchEt.requestFocus();
				} else {
					getSearchDate(searchEt.getText().toString().trim());
				}
            	break;
				
			default: break;
			}
		}
	};
	
	/**
	 * 房间列表点击触发事件
	 */
	private OnItemClickListener searchsClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Bundle bundle = new Bundle();
			Map<String, String> map = mAdapter.getListData().get((int)id);
			bundle.putString("id", map.get("pid"));

			startActivityForResult(ShowDetailActivity.class, bundle, 1);
			overridePendingTransition(R.anim.left_in, R.anim.left_out);
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
					getSearchDate(searchEt.getText().toString().trim());
				}

				((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
						.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
			return false;
		}
	};
	
	/**
	 * 获取搜索数据
	 */
	private void getSearchDate(final String searchStr) {
		if (checkNet.checkNet()) {
			RequestParams params = new RequestParams();
			params.addBodyParameter("jsonpCallback", "jsonpCallback");
			
			AjaxParamsBean bean = new AjaxParamsBean();
			bean.setKey(searchStr);
			bean.setPageno("1");
			bean.setPagesize("100");
			bean.setLongitude(MApplication.lon + "");
			bean.setLatitude(MApplication.lat + "");
            bean.setDistrict("");
            bean.setStreet("");

			String json = JsonUtil.toJson(bean);
			params.addBodyParameter("jsoninput", json);
			
			httpUtils.send(HttpRequest.HttpMethod.POST, searchPath, params,
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
								dataList = new ArrayList<Map<String, String>>();

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
									mAdapter = new ShowTypeAdapter(activity, dataList);
									searchList.setAdapter(mAdapter);
								} else {
									ToastUtil.showMessage(activity, "暂无数据！");
									mAdapter = new ShowTypeAdapter(activity, dataList);
									searchList.setAdapter(mAdapter);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						@Override
						public void onFailure(HttpException error, String msg) {
							ToastUtil.showMessage(activity, "网络异常！");
							
							mAdapter = new ShowTypeAdapter(activity, dataList);
							searchList.setAdapter(mAdapter);
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
			case 1:
				getSearchDate(searchEt.getText().toString().trim());
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