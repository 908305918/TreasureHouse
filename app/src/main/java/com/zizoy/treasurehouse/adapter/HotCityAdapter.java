package com.zizoy.treasurehouse.adapter;

import android.content.Context;
import android.view.View;
import android.widget.Button;

import com.example.treasurehouse.R;
import com.zizoy.treasurehouse.activity.CityChoiceActivity;
import com.zizoy.treasurehouse.base.SuperBaseAdapter;
import com.zizoy.treasurehouse.model.CityModel;

import java.util.List;


/**
 * @Description: 热门城市adapter
 *
 * @author falcon
 */
public class HotCityAdapter extends SuperBaseAdapter<CityModel> {

    public HotCityAdapter(Context context, List<CityModel> models) {
        super(context, models);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_item_hot_city;
    }

    @Override
    protected void bindViewDatas(ViewHolder viewHolder, final CityModel model, int position) {
        // 热门城市
        Button city = (Button) viewHolder.getView(R.id.btn_city);

        city.setText(model.getName());

        city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((CityChoiceActivity)context).choiceCityData(model.getName());
            }
        });
    }
}