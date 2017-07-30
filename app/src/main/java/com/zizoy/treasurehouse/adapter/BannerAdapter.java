package com.zizoy.treasurehouse.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.treasurehouse.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zizoy.treasurehouse.activity.ShowDetailActivity;

import java.util.List;
import java.util.Map;

/**
 * @author falcon
 * @Description: 轮播adapter
 */
public class BannerAdapter extends BaseAdapter {
    private List<Map<String, String>> dataList;
    private DisplayImageOptions options;
    private Context context;

    public BannerAdapter(Context context, List<Map<String, String>> dataList) {
        super();
        this.context = context;
        this.dataList = dataList;

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.banner_no_data)
                .showImageForEmptyUri(R.mipmap.banner_no_data)
                .showImageOnFail(R.mipmap.banner_no_data).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.view_item_banner, null);
            viewHolder.banner = (ImageView) convertView.findViewById(R.id.bannerImage);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.banner.setAdjustViewBounds(true);
        viewHolder.banner.setScaleType(ImageView.ScaleType.CENTER_CROP);

        ImageLoader.getInstance().displayImage(dataList.get(position % dataList.size()).get("url"), viewHolder.banner, options);

        viewHolder.banner.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
            	((ShowDetailActivity)context).photosClick(position % dataList.size());
            }
        });

        return convertView;
    }

    public final class ViewHolder {
        private ImageView banner; // 广告
    }
}