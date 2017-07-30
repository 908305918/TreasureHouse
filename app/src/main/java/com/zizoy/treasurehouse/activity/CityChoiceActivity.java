package com.zizoy.treasurehouse.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import com.example.treasurehouse.R;
import com.zizoy.treasurehouse.adapter.CityAdapter;
import com.zizoy.treasurehouse.adapter.HotCityAdapter;
import com.zizoy.treasurehouse.api.MApplication;
import com.zizoy.treasurehouse.base.SuperActivity;
import com.zizoy.treasurehouse.model.CityModel;
import com.zizoy.treasurehouse.util.CharacterParser;
import com.zizoy.treasurehouse.util.CityTool;
import com.zizoy.treasurehouse.util.ClearEditText;
import com.zizoy.treasurehouse.util.GridViewUtil;
import com.zizoy.treasurehouse.util.PinyinComparator;
import com.zizoy.treasurehouse.util.ToastUtil;
import com.zizoy.treasurehouse.widget.SideBar;
import com.zizoy.treasurehouse.widget.SideBar.OnTouchingLetterChangedListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author falcon
 * @Description: 程序城市选择界面
 */
public class CityChoiceActivity extends SuperActivity {
    private TextView title;
    private LinearLayout backBtn;

    private ClearEditText citySearch; // 城市搜索
    private ListView cityList; // 城市

    private TextView cityName;
    private GridViewUtil hotCityList; // 热门城市
    private CityAdapter cAdapter;
    private HotCityAdapter hAdapter;
    private TextView cityDialog;
    private SideBar mSideBar;

    // 汉字转换成拼音的类
    private CharacterParser characterParser;
    // 根据拼音来排列ListView里面的数据类
    private PinyinComparator pinyinComparator;

    private List<CityModel> cDataList, hDataList;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_city_choice;
    }

    @Override
    protected void initData() {
        super.initData();

        // 实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparator();
    }

    @Override
    protected void initView() {
        super.initView();

        title = (TextView) findViewById(R.id.tv_title);
        backBtn = (LinearLayout) findViewById(R.id.btn_back);
        citySearch = (ClearEditText) findViewById(R.id.et_citySearch);
        cityList = (ListView) findViewById(R.id.lv_city);
        cityDialog = (TextView) findViewById(R.id.city_dialog);
        mSideBar = (SideBar) findViewById(R.id.city_sideBar);
    }

    @Override
    protected void initCodeLogic() {
        super.initCodeLogic();

        title.setText("城市选择");
        backBtn.setVisibility(View.VISIBLE);
        mSideBar.setTextView(cityDialog);
        cityList.addHeaderView(headView());

        getCityData(); // 获取城市数据
        getHotCityData(); // 获取热门城市数据
    }

    @Override
    protected void setListener() {
        super.setListener();

        backBtn.setOnClickListener(mBtnClick);
        cityList.setOnItemClickListener(citysClick);
        citySearch.addTextChangedListener(mCityWatcher);
        citySearch.setOnEditorActionListener(searchListener);
        mSideBar.setOnTouchingLetterChangedListener(cityChanged);
    }

    /**
     * 列表头部View
     *
     * @return
     */
    private View headView() {
        View view = (View) LayoutInflater.from(activity).inflate(R.layout.view_city_head, null);

        LinearLayout locate = (LinearLayout) view.findViewById(R.id.ll_locate);
        cityName = (TextView) view.findViewById(R.id.tv_cityName);
        hotCityList = (GridViewUtil) view.findViewById(R.id.gv_hotCity);

        hotCityList.setOnItemClickListener(citysClick);
        locate.setOnClickListener(mBtnClick);
        getCityLocate();

        return view;
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

                case R.id.ll_locate: // 定位城市被点击
                    finishActForResult(cityName.getText().toString().trim());
                    break;

                default: break;
            }
        }
    };

    /**
     * 城市列表点击触发事件
     */
    private OnItemClickListener citysClick = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            finishActForResult(cAdapter.getListData().get((int) id).getName());
        }
    };

    /**
     * 右侧分类触摸监听事件
     */
    private OnTouchingLetterChangedListener cityChanged = new OnTouchingLetterChangedListener() {

        @Override
        public void onTouchingLetterChanged(String s) {
            if (cDataList != null && cDataList.size() > 0) {
                // 该字母首次出现的位置
                int position = cAdapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    cityList.setSelection(position);
                }
            }
        }
    };

    /**
     * 城市搜索
     */
    private TextWatcher mCityWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

		@Override
		public void afterTextChanged(Editable s) {
			filterData(s.toString());

			if (s.toString().length() > 0) {
				changeScroTop(s.toString().length());
			} else {
				changeScroZero();
			}
		}
	};

    /**
     * 键盘搜索按钮监听
     */
    private OnEditorActionListener searchListener = new OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if ((actionId == 0 || actionId == 3) && event != null) {
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
            return false;
        }
    };
    
    /**
     * 改变键盘遮挡(向上)
     */
	private void changeScroTop(final int selection) {
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				cityList.setSelection(selection);
			}
		}, 300);
	}
	
	 /**
     * 改变键盘遮挡(恢复)
     */
	private void changeScroZero() {
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				cityList.setSelection(0);
			}
		}, 300);
	}

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     *
     * @param filterStr
     */
    private void filterData(String filterStr) {
        List<CityModel> filterDateList = new ArrayList<CityModel>();

        if (cDataList != null && cDataList.size() > 0) {
            if (TextUtils.isEmpty(filterStr)) {
                filterDateList = cDataList;
            } else {
                filterDateList.clear();
                for (CityModel sortModel : cDataList) {
                    String name = sortModel.getName();
                    if (name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr.toString())) {
                        filterDateList.add(sortModel);
                    }
                }
            }
            // 根据a-z进行排序
            Collections.sort(cDataList, pinyinComparator);
            cAdapter.updateListView(filterDateList);
        }
    }

    /**
     * 获取当前定位城市
     */
    private void getCityLocate() {
        if ("".equals(MApplication.cityStr) || MApplication.cityStr == null) {
            MApplication.cityStr = "北京";
        }
        cityName.setText(MApplication.cityStr.replace("市", ""));
    }

    /**
     * 选择城市数据
     */
    public void choiceCityData(final String cityName) {
        finishActForResult(cityName);
    }

    /**
     * 关闭当前界面
     */
    private void finishActForResult(final String cityName) {
        int cityCode = CityTool.getCityCodeByName(cityName.replace("市", ""));

        Bundle bundle = new Bundle();
        bundle.putInt("cityId", cityCode);
        bundle.putString("cityName", cityName);
        activityFinishForResult(bundle);
    }

    /**
     * 获取热门城市数据
     */
    private void getHotCityData() {
        hDataList = CityTool.getHotCitys();

        if (hDataList != null && hDataList.size() > 0) {
            for (int i = 0; i < hDataList.size(); i++) {
                hAdapter = new HotCityAdapter(activity, hDataList);
                hotCityList.setAdapter(hAdapter);
            }
        }
    }

    /**
     * 获取城市数据
     */
    private void getCityData() {
        cDataList = CityTool.getCitys();

        if (cDataList != null && cDataList.size() > 0) {
            for (int i = 0; i < cDataList.size(); i++) {
                String name = cDataList.get(i).getName();

                // 汉字转换成拼音
                String pinyin = characterParser.getSelling(name);
                String sortStr = pinyin.substring(0, 1).toUpperCase();
                // 正则表达式，判断首字母是否是英文字母
                if (sortStr.matches("[A-Z]")) {
                    cDataList.get(i).setSortLetters(sortStr.toUpperCase());
                } else {
                    cDataList.get(i).setSortLetters("#");
                }
            }
            // 根据a-z进行排序源数据
            Collections.sort(cDataList, pinyinComparator);

            if (cDataList != null && cDataList.size() > 0) {
                cAdapter = new CityAdapter(activity, cDataList);
                cityList.setAdapter(cAdapter);
            }
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