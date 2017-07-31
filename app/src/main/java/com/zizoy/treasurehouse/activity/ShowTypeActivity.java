package com.zizoy.treasurehouse.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.example.treasurehouse.R;
import com.zizoy.treasurehouse.adapter.MyspinnerAdapter;
import com.zizoy.treasurehouse.adapter.ShowTypeAdapter;
import com.zizoy.treasurehouse.api.MApplication;
import com.zizoy.treasurehouse.base.SuperActivity;
import com.zizoy.treasurehouse.bean.AjaxParamsBean;
import com.zizoy.treasurehouse.util.CityTool;
import com.zizoy.treasurehouse.util.ClearEditText;
import com.zizoy.treasurehouse.util.JsonUtil;
import com.zizoy.treasurehouse.util.PreferencesUtils;
import com.zizoy.treasurehouse.util.RefreshListView;
import com.zizoy.treasurehouse.util.RefreshListViewListener;
import com.zizoy.treasurehouse.util.ToastUtil;
import com.zizoy.treasurehouse.widget.expandtab.ExpandTabView;
import com.zizoy.treasurehouse.widget.expandtab.ViewLeft;
import com.zizoy.treasurehouse.widget.expandtab.ViewMiddle;
import com.zizoy.treasurehouse.widget.expandtab.ViewRight;
import com.zizoy.xutils.exception.HttpException;
import com.zizoy.xutils.http.RequestParams;
import com.zizoy.xutils.http.ResponseInfo;
import com.zizoy.xutils.http.callback.RequestCallBack;
import com.zizoy.xutils.http.client.HttpRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author falcon
 * @Description: 程序警报界面
 */
public class ShowTypeActivity extends SuperActivity {
    private TextView title;
    private LinearLayout backBtn;
    private LinearLayout gotoBtn;
    private TextView gotoTv;

    private ClearEditText searchEt;
    private Button searchBtn;
    private MyspinnerAdapter regionadapter;

    private ExpandTabView mTabView;
    private ArrayList<View> mTabs = new ArrayList<>();
    private ViewMiddle mViewMiddle;


    private RefreshListView showList;
    private ShowTypeAdapter mAdapter;

    private ListView listView;
    private PopupWindow popupWindow;
    private LinearLayout layout;

    private String userId = null;
    private String cityStr = null;
    private String typeStr = null;
    private String districtStr = "";
    private String streetStr = "";
    private boolean isChooseAddr = false;

    private int curPage = 1; // 当前页数
    private int totalPage = 0; // 总数量
    private int pageSize = 50; // 每页显示条数
    private int listType; // 刷新/加载更多标识

    private List<Map<String, String>> dataList = new ArrayList<>();
    private ArrayList<String> listOne, listTwo, listThree, listFour, listFive, listSix, listTemp;

    // 列表接口地址
    private String showPath = MApplication.serverURL + "post/listPostByKey";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_show_type;
    }

    @Override
    protected void initData() {
        super.initData();

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            cityStr = bundle.getString("city");
            typeStr = bundle.getString("type");
        }

        userId = PreferencesUtils.getStringPreference(activity, "ZHKJ", "userId", "");
    }

    @Override
    protected void initView() {
        super.initView();

        title = (TextView) findViewById(R.id.tv_title);
        backBtn = (LinearLayout) findViewById(R.id.btn_back);
        gotoBtn = (LinearLayout) findViewById(R.id.btn_goto);
        gotoTv = (TextView) findViewById(R.id.tv_goto);
        searchEt = (ClearEditText) findViewById(R.id.et_search);
        searchBtn = (Button) findViewById(R.id.btn_Search);
        mTabView = (ExpandTabView) findViewById(R.id.expandtab_view);
        showList = (RefreshListView) findViewById(R.id.showList);


        mViewMiddle = new ViewMiddle(this) {
            @Override
            public void initData(ArrayList<String> groups, SparseArray<LinkedList<String>> children) {
                JSONArray array = CityTool.findAreaAndStreet(cityStr);
                groups.add("全" + cityStr);
                children.append(0, new LinkedList<String>());
                for (int i = 0; i < array.length(); i++) {
                    JSONObject area = array.optJSONObject(i);
                    groups.add(area.optString("county"));
                    LinkedList<String> streets = new LinkedList<String>();
                    streets.add("全" + area.optString("county"));
                    JSONArray streetArray = area.optJSONArray("streets");
                    for (int j = 0; j < streetArray.length(); j++) {
                        streets.add(streetArray.optString(j));
                    }
                    children.append(i + 1, streets);
                }
            }
        };


        mTabs.add(mViewMiddle);
        ArrayList<String> titles = new ArrayList<>();
        titles.add("选择区域");
        mTabView.setValue(titles, mTabs);
    }

    @Override
    protected void setListener() {
        super.setListener();

        backBtn.setOnClickListener(mBtnClick);
        gotoBtn.setOnClickListener(mBtnClick);
        searchBtn.setOnClickListener(mBtnClick);
        showList.setonRefreshListener(mRefreshLoad);
        showList.setOnItemClickListener(showsClick);
        searchEt.setOnEditorActionListener(searchListener);

        mViewMiddle.setOnSelectListener(new ViewMiddle.OnSelectListener() {
            @Override
            public void getValue(String showText) {
                onRefresh(mViewMiddle, showText);
            }
        });

    }

    private void onRefresh(View view, String showText) {
        mTabView.onPressBack();
        int position = getPosition(view);
        if (position >= 0 && !mTabView.getTitle(position).equals(showText)) {
            mTabView.setTitle(showText, position);
        }
        String[] address = showText.split("-");
        if (address.length == 1) {
            districtStr = address[0].contains("全") ? "" : address[0];
        }
        if (address.length == 2) {
            districtStr = address[0].contains("全") ? "" : address[0];
            streetStr = address[1].contains("全") ? "" : address[1];
        }
        //Toast.makeText(ShowTypeActivity.this, districtStr + "==" + streetStr, Toast.LENGTH_SHORT).show();
        curPage = 1;
        listType = 1;
        getShowDate("", searchBtn.getText().toString().trim(), true, true);
    }

    private int getPosition(View tView) {
        for (int i = 0; i < mTabs.size(); i++) {
            if (mTabs.get(i) == tView) {
                return i;
            }
        }
        return -1;
    }

    @Override
    protected void initCodeLogic() {
        super.initCodeLogic();

        title.setText("信息列表");
        gotoTv.setText("我要发布");
        backBtn.setVisibility(View.VISIBLE);
        gotoBtn.setVisibility(View.VISIBLE);

        showList.setPullRefreshEnable(true);
        showList.setPullLoadEnable(true);

        mAdapter = new ShowTypeAdapter(activity, dataList);
        showList.setAdapter(mAdapter);

        getListData(); // 获取列表数据
    }

    /**
     * 获取列表数据
     */
    private void getListData() {
        listOne = new ArrayList<>();
        listTwo = new ArrayList<>();
        listThree = new ArrayList<>();
        listFour = new ArrayList<>();
        listFive = new ArrayList<>();
        listSix = new ArrayList<>();
        listTemp = new ArrayList<>();

        listOne.add("全部");
        listOne.add("出租");
        listOne.add("出售");
        listTwo.add("全部");
        listTwo.add("汽车");
        listTwo.add("其他");
        listThree.add("全部");
        listThree.add("苹果");
        listThree.add("其他");
        listFour.add("全部");
        listFour.add("冰箱");
        listFour.add("洗衣机");
        listFour.add("电视");
        listFour.add("其他");
        listFive.add("全部");
        listFive.add("全职");
        listFive.add("兼职");
        listSix.add("全部");
        listSix.add("二手交易");

        if ("房产".equals(typeStr)) { // 房产
            listTemp = listOne;
        } else if ("交通工具".equals(typeStr)) { // 交通工具
            listTemp = listTwo;
        } else if ("手机".equals(typeStr)) { // 手机
            listTemp = listThree;
        } else if ("家电".equals(typeStr)) { // 家电
            listTemp = listFour;
        } else if ("招聘".equals(typeStr)) { // 招聘
            listTemp = listFive;
        } else if ("二手交易".equals(typeStr)) { // 二手交易
            listTemp = listSix;
        }
        regionadapter = new MyspinnerAdapter(this, listTemp);
        searchBtn.setText((CharSequence) regionadapter.getItem(0));
        getShowDate(searchEt.getText().toString().trim(), searchBtn.getText().toString().trim(), true, isChooseAddr); // 获取展示数据
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

                case R.id.btn_goto: // 我要发布被点击
                    if ("".equals(userId)) {
                        ToastUtil.showMessage(activity, "请先登录！");
                        startActivity(PersonalActivity.class, null);
                    } else {
                        startActivity(ReleaseActivity.class, null);
                    }
                    overridePendingTransition(R.anim.left_in, R.anim.left_out);
                    break;

                case R.id.btn_Search: // 信息搜索被点击
                    showWindow(searchBtn, searchBtn);
                    break;

                default:
                    break;
            }
        }
    };

    /**
     * 房间列表点击触发事件
     */
    private OnItemClickListener showsClick = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Bundle bundle = new Bundle();
            Map<String, String> map = mAdapter.getListData().get((int) id);
            bundle.putString("id", map.get("pid"));

            startActivity(ShowDetailActivity.class, bundle);
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
                curPage = 1;
                listType = 1;
                isChooseAddr = false;
                getShowDate(searchEt.getText().toString().trim(), searchBtn.getText().toString().trim(), false, isChooseAddr);

                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
            return false;
        }
    };

    /**
     * 刷新/加载数据监听事件
     */
    private RefreshListViewListener mRefreshLoad = new RefreshListViewListener() {

        @Override
        public void onRefresh() {
            curPage = 1;
            listType = 1;
            getShowDate(searchEt.getText().toString().trim(), searchBtn.getText().toString().trim(), false, isChooseAddr);
        }

        @Override
        public void onLoadMore() {
            curPage++;
            listType = 2;
            getShowDate(searchEt.getText().toString().trim(), searchBtn.getText().toString().trim(), false, isChooseAddr);
        }
    };

    /**
     * 显示类型
     */
    private void showWindow(View position, final TextView txt) {
        layout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.mypinner_dropdown, null);
        listView = (ListView) layout.findViewById(R.id.listView);
        listView.setAdapter(regionadapter);
        popupWindow = new PopupWindow(position);
        popupWindow.setWidth(txt.getWidth());
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0000000000));
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setContentView(layout);
        popupWindow.showAsDropDown(position, 0, 0);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                txt.setText(listTemp.get(arg2).trim());
                popupWindow.dismiss();
                popupWindow = null;
                curPage = 1;
                listType = 1;
                isChooseAddr = false;
                getShowDate(searchEt.getText().toString().trim(), searchBtn.getText().toString().trim(), false, isChooseAddr);
            }
        });
    }

    /**
     * 获取展示数据
     */
    private void getShowDate(final String keyStr, final String typeTwoStr, final boolean isTrue, boolean isChooseAddr) {
        if (checkNet.checkNet()) {
            RequestParams params = new RequestParams();
            params.addBodyParameter("jsonpCallback", "jsonpCallback");

            AjaxParamsBean bean = new AjaxParamsBean();
            bean.setKey(keyStr);
            bean.setType(typeStr);
            bean.setType2(typeTwoStr);
            bean.setPageno(String.valueOf(curPage));
            bean.setPagesize(String.valueOf(pageSize));
            bean.setCity(cityStr);
            if (isChooseAddr) {
                bean.setDistrict(districtStr);
                bean.setStreet(streetStr);
                bean.setKey("");
            }
            String json = JsonUtil.toJson(bean);
            params.addBodyParameter("jsoninput", json);

            httpUtils.send(HttpRequest.HttpMethod.POST, showPath, params, new RequestCallBack<String>() {
                @Override
                public void onStart() {
                    if (isTrue) {
                        dialogUtil.showLoadDialog();
                    }
                }

                @Override
                public void onSuccess(ResponseInfo<String> responseInfo) {
                    if (isTrue) {
                        dialogUtil.closeDialog();// 加载成功关闭Dialog
                    }
                    showList.stopRefresh();
                    showList.stopLoadMore();

                    try {
                        String tempResult = responseInfo.result.split("totalPage=")[0];
                        String jsonObject = tempResult.substring(14, tempResult.length() - 1);
                        totalPage = Integer.valueOf(responseInfo.result.split("totalPage=")[1]);
                        JSONArray jsonArray = new JSONArray(jsonObject);
                        List<Map<String, String>> datas = new ArrayList<>();

                        if (jsonArray != null && jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                Map<String, String> map = new HashMap<>();
                                map.put("pid", jsonArray.getJSONObject(i).getString("pid"));
                                map.put("title", jsonArray.getJSONObject(i).getString("title"));
                                map.put("url", jsonArray.getJSONObject(i).getString("url"));
                                map.put("price", jsonArray.getJSONObject(i).getString("price"));
                                map.put("address", jsonArray.getJSONObject(i).getString("address"));
                                map.put("readernum", jsonArray.getJSONObject(i).getString("readernum"));

                                datas.add(map);
                            }
                            if (datas.size() > 0) {
                                if (1 == listType) {
                                    dataList.clear();
                                    dataList.addAll(datas);
                                    mAdapter.notifyDataSetChanged();
                                } else if (2 == listType) {
                                    datas.remove(0);
                                    dataList.addAll(datas);
                                    mAdapter.notifyDataSetChanged();
                                } else {
                                    dataList.addAll(datas);
                                    mAdapter.notifyDataSetChanged();
                                }
                                listType = 0;
                            }
                            if (datas.size() < totalPage) {
                                showList.setPullLoadEnable(true);
                            } else {
                                showList.setPullLoadEnable(false);
                            }
                        } else {
                            ToastUtil.showMessage(activity, "已经到底啦");

                            showList.setPullLoadEnable(false);
                            if (1 == listType) {
                                dataList.clear();
                                dataList.addAll(datas);
                                mAdapter.notifyDataSetChanged();
                            } else {
                                dataList.addAll(datas);
                                mAdapter.notifyDataSetChanged();
                            }
                            listType = 0;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(HttpException error, String msg) {
                    if (isTrue) {
                        dialogUtil.closeDialog(); // 加载失败关闭Dialog并提示
                    }
                    ToastUtil.showMessage(activity, "网络异常！");
                    showList.stopRefresh();
                    showList.stopLoadMore();
                    listType = 0;
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

    @Override
    protected void activityFinish() {
        if (!mTabView.onPressBack()) {
            super.activityFinish();
        }
    }
}