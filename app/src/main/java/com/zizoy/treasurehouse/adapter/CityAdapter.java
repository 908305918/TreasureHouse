package com.zizoy.treasurehouse.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.example.treasurehouse.R;
import com.zizoy.treasurehouse.model.CityModel;

import java.util.List;


/**
 * @Description: 城市adapter
 *
 * @author falcon
 */
public class CityAdapter extends BaseAdapter implements SectionIndexer {
    private Context context;
    private List<CityModel> citys = null;

    public CityAdapter(Context context, List<CityModel> citys) {
        super();
        this.context = context;
        this.citys = citys;
    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     *
     * @param citys
     */
    public void updateListView(List<CityModel> citys) {
        this.citys = citys;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return citys.size();
    }

    @Override
    public Object getItem(int position) {
        return citys.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 获取集合数据
     **/
    public List<CityModel> getListData() {
        return citys;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.view_item_city, null);
            viewHolder.section = (TextView) convertView.findViewById(R.id.tv_section);
            viewHolder.line = (View) convertView.findViewById(R.id.iv_sectionLine);
            viewHolder.name = (TextView) convertView.findViewById(R.id.tv_cityName);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final CityModel City = citys.get(position);

        // 根据position获取分类的首字母的Char ascii值
        int section = getSectionForPosition(position);

        // 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if (position == getPositionForSection(section)) {

            viewHolder.section.setVisibility(View.VISIBLE);
            viewHolder.line.setVisibility(View.VISIBLE);
            viewHolder.section.setText(City.getSortLetters());
        } else {
            viewHolder.section.setVisibility(View.GONE);
            viewHolder.line.setVisibility(View.GONE);
        }
        viewHolder.name.setText(City.getName());

        return convertView;
    }

    public final class ViewHolder {
        private TextView section; // 分类名
        private View line; // 分割线
        private TextView name; // 城市名
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的Charascii值
     */
    public int getSectionForPosition(int position) {
        return citys.get(position).getSortLetters().charAt(0);
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = citys.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public Object[] getSections() {
        return null;
    }
}