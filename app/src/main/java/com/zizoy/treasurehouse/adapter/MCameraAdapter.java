package com.zizoy.treasurehouse.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;

import com.example.treasurehouse.R;
import com.zizoy.treasurehouse.activity.ReleaseActivity;
import com.zizoy.treasurehouse.util.ToastUtil;

import java.util.List;

/**
 * @Description: 照片adapter
 *
 * @author falcon
 */
public class MCameraAdapter extends BaseAdapter {
    protected Context context;
    protected List<Bitmap> listData;
    private LayoutInflater inflater;

    public MCameraAdapter(Context context, List<Bitmap> listData) {
        this.context = context;
        this.listData = listData;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return listData == null ? 0 : listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData == null ? null : listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        ViewHolder holder = null;

        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.view_item_camera, null);

            holder.photo = (ImageView) view.findViewById(R.id.iv_photo);
            holder.detele = (Button) view.findViewById(R.id.btn_detele);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        if (listData.size() > 0) {
            holder.photo.setImageBitmap(listData.get(position));
        }
        holder.detele.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                ((ReleaseActivity)context).deletePhotos(position);
            }
        });
        return view;
    }

    class ViewHolder {
        private ImageView photo;
        private Button detele;

    }
}