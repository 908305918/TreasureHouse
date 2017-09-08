package com.zizoy.treasurehouse.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.treasurehouse.R;
import com.zizoy.treasurehouse.adapter.BannerAdapter;
import com.zizoy.treasurehouse.api.MApplication;
import com.zizoy.treasurehouse.base.SuperActivity;
import com.zizoy.treasurehouse.bean.AjaxParamsBean;
import com.zizoy.treasurehouse.model.ReportModel;
import com.zizoy.treasurehouse.util.CircleFlowIndicator;
import com.zizoy.treasurehouse.util.JsonUtil;
import com.zizoy.treasurehouse.util.ToastUtil;
import com.zizoy.treasurehouse.util.ViewFlow;
import com.zizoy.xutils.exception.HttpException;
import com.zizoy.xutils.http.RequestParams;
import com.zizoy.xutils.http.ResponseInfo;
import com.zizoy.xutils.http.callback.RequestCallBack;
import com.zizoy.xutils.http.client.HttpRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author falcon
 * @Description: 程序关于界面
 */
public class ShowDetailActivity extends SuperActivity {
    private TextView title;
    private LinearLayout backBtn;
    private LinearLayout gotoBtn;
    private TextView gotoTv;

    private ViewFlow mGallery;
    private FrameLayout photoLayout;
    private CircleFlowIndicator mIndicator;

    private TextView name; // 标题
    private TextView time; // 时间
    private TextView read; // 阅读数
    private TextView price; // 价格
    private TextView note; // 内容
    private TextView address; // 地址
    private TextView user; // 联系人
    private TextView phone; // 联系电话
    private LinearLayout contact;

    private String idStr = null;

    private List<Map<String, String>> photosData;

    // 详情接口地址
    private String detailPath = MApplication.serverURL + "post/getPostDetail";
    // 阅读数接口地址
    private String readPath = MApplication.serverURL + "post/viewPost";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_show_detail;
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
        name = (TextView) findViewById(R.id.tv_name);
        time = (TextView) findViewById(R.id.tv_time);
        read = (TextView) findViewById(R.id.tv_read);
        price = (TextView) findViewById(R.id.tv_price);
        note = (TextView) findViewById(R.id.tv_note);
        address = (TextView) findViewById(R.id.tv_address);
        user = (TextView) findViewById(R.id.tv_user);
        phone = (TextView) findViewById(R.id.tv_phone);
        contact = (LinearLayout) findViewById(R.id.ll_contact);
    }

    @Override
    protected void setListener() {
        super.setListener();

        backBtn.setOnClickListener(mBtnClick);
        contact.setOnClickListener(mBtnClick);
        gotoBtn.setOnClickListener(mBtnClick);
    }

    @Override
    protected void initCodeLogic() {
        super.initCodeLogic();

        title.setText("信息详情");
        backBtn.setVisibility(View.VISIBLE);
        gotoTv.setText("举报中介");
        gotoBtn.setVisibility(View.VISIBLE);

        getDetailDate(); // 获取详情数据
        putReadDate();
    }

    /**
     * 按钮点击触发事件
     */
    private OnClickListener mBtnClick = new OnClickListener() {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_back: // 界面返回被点击
                    activityFinishForResult(null);
                    break;

                case R.id.ll_contact: // 联系方式被点击
                    callPhone(phone.getText().toString().trim());
                    break;
                case R.id.btn_goto:
                    ReportModel.report(ShowDetailActivity.this,idStr);
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 打电话
     *
     * @param phoneStr
     */
    private void callPhone(final String phoneStr) {
        intent = new Intent();
        intent.setAction("android.intent.action.DIAL");
        intent.setData(Uri.parse("tel:" + phoneStr));
        activity.startActivity(intent);
    }

    /**
     * 提交删除数据
     */
    public void photosClick(final int position) {
        String[] urls = new String[photosData.size()];

        for (int i = 0; i < photosData.size(); i++) {
            urls[i] = photosData.get(i).get("url");
        }

        Intent intent = new Intent(activity, ImagePagerActivity.class);
        // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
        intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls);
        intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
        startActivity(intent);
    }

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
                                time.setText(jsonObject.getString("pcreatetime"));

                                if ("".equals(jsonObject.getString("readernum")) || "null".equals(jsonObject.getString("readernum"))) {
                                    read.setText("0人浏览");
                                } else {
                                    read.setText(jsonObject.getString("readernum") + "人浏览");
                                }
                                price.setText("￥" + jsonObject.getString("price"));
                                note.setText(jsonObject.getString("content"));
                                address.setText(jsonObject.getString("address"));
                                user.setText(jsonObject.getString("contact") + "：");
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
     * 提交阅读数据
     */
    private void putReadDate() {
        if (checkNet.checkNet()) {
            RequestParams params = new RequestParams();
            params.addBodyParameter("jsonpCallback", "jsonpCallback");
            params.addBodyParameter("id", idStr);

            AjaxParamsBean bean = new AjaxParamsBean();
            bean.setPid(idStr);

            String json = JsonUtil.toJson(bean);
            params.addBodyParameter("jsoninput", json);

            httpUtils.send(HttpRequest.HttpMethod.POST, readPath, params,
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
     * 按系统键返回
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            activityFinishForResult(null);
        }
        return true;
    }
}