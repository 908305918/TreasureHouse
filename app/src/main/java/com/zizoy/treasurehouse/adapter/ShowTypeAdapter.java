package com.zizoy.treasurehouse.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.treasurehouse.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zizoy.treasurehouse.activity.PersonalActivity;
import com.zizoy.treasurehouse.api.MApplication;
import com.zizoy.treasurehouse.base.SuperBaseAdapter;
import com.zizoy.treasurehouse.model.ReportModel;
import com.zizoy.treasurehouse.util.DialogUtil;
import com.zizoy.treasurehouse.util.PreferencesUtils;
import com.zizoy.treasurehouse.util.ToastUtil;
import com.zizoy.xutils.exception.HttpException;
import com.zizoy.xutils.http.RequestParams;
import com.zizoy.xutils.http.ResponseInfo;
import com.zizoy.xutils.http.callback.RequestCallBack;
import com.zizoy.xutils.http.client.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * @author falcon
 * @Description: 信息列表adapter
 */
public class ShowTypeAdapter extends SuperBaseAdapter<Map<String, String>> {
    private DisplayImageOptions options;

    public ShowTypeAdapter(Context context, List<Map<String, String>> listData) {
        super(context, listData);

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.empty_photo)
                .showImageForEmptyUri(R.mipmap.empty_photo)
                .showImageOnFail(R.mipmap.empty_photo)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_item_show_type;
    }

    @Override
    protected void bindViewDatas(ViewHolder viewHolder, final Map<String, String> data, int position) {
        ImageView ico = (ImageView) viewHolder.getView(R.id.iv_showIco);
        TextView title = (TextView) viewHolder.getView(R.id.tv_showTitle);
        TextView address = (TextView) viewHolder.getView(R.id.tv_showAddress);
        TextView price = (TextView) viewHolder.getView(R.id.tv_showPrice);
        TextView read = (TextView) viewHolder.getView(R.id.tv_showRead);

        title.setText(data.get("title"));
        address.setText("地址：" + data.get("address"));
        price.setText("￥" + data.get("price"));
        if ("".equals(data.get("readernum")) || "".equals(data.get("readernum"))) {
            read.setText("0人浏览");
        } else {
            read.setText(data.get("readernum") + "人浏览");
        }
        ImageLoader.getInstance().displayImage(data.get("url"), ico, options);

        TextView jubao = viewHolder.getView(R.id.tv_jubao);
        jubao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReportModel.report(context, data.get("pid"));
            }
        });
    }


}