package com.zizoy.treasurehouse.util;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.treasurehouse.R;
import com.zizoy.treasurehouse.adapter.MyspinnerAdapter;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by WD on 2017/8/3.
 */
public class PopViewHelper {
    public static final int TYPE_D = 1;
    public static final int TYPE_S = 2;

    private Context mContext;
    private TextView mTextView;
    private JSONArray mDataArray;
    private ArrayList<String> mDataList;
    private MyspinnerAdapter mAdapter;
    private PopupWindow mPopupWindow;
    private int type = TYPE_D;
    private OnPopItemClickListener listener;

    public PopViewHelper(Context context, TextView textView, JSONArray array, int type) {
        mContext = context;
        mTextView = textView;
        mDataArray = array;
        this.type = type;
    }

    private void init() {
        mDataList = obtainList();
        mAdapter = new MyspinnerAdapter(mContext, mDataList);
        View view = LayoutInflater.from(mContext).inflate(R.layout.mypinner_dropdown, null);
        ListView listView = (ListView) view.findViewById(R.id.listView);
        listView.setAdapter(mAdapter);
        mPopupWindow = new PopupWindow(mTextView);
        mPopupWindow.setWidth(mTextView.getWidth());
        mPopupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(0000000000));
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setContentView(view);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                mPopupWindow.dismiss();
                String text = mDataList.get(arg2).trim();
                mTextView.setText(text);
                if (listener != null) {
                    if (type == TYPE_D) {
                        JSONArray array = mDataArray.optJSONObject(arg2).optJSONArray("streets");
                        listener.onPopItemClick(type, text, array);
                    } else {
                        listener.onPopItemClick(type, text, null);
                    }
                }
            }
        });
    }

    public void setOnPopItemClickListener(OnPopItemClickListener l) {
        listener = l;
    }

    private ArrayList<String> obtainList() {
        ArrayList<String> list = new ArrayList<>();
        if (type == TYPE_D) {
            for (int i = 0; i < mDataArray.length(); i++) {
                list.add(mDataArray.optJSONObject(i).optString("county"));
            }
        } else {
            for (int i = 0; i < mDataArray.length(); i++) {
                list.add(mDataArray.optString(i));
            }
        }
        return list;
    }

    public void show() {
        if (mPopupWindow == null) {
            init();
        }
        mPopupWindow.showAsDropDown(mTextView, 0, 0);
    }

    public interface OnPopItemClickListener {
        void onPopItemClick(int type, String text, JSONArray array);
    }
}
