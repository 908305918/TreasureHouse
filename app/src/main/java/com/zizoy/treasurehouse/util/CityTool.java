package com.zizoy.treasurehouse.util;

import android.content.res.Resources;

import com.example.treasurehouse.R;
import com.zizoy.treasurehouse.api.MApplication;
import com.zizoy.treasurehouse.model.CityModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author falcon
 * @Description: 城市数据处理
 */
public class CityTool {

    /**
     * 获取城市列表数据
     */
    public static List<CityModel> getCitys() {
        List<CityModel> citys = new ArrayList<>();
        String cityJson = readRawFile(R.raw.city);

        try {
            JSONObject cityObject = new JSONObject(cityJson);
            JSONArray cityArray = cityObject.getJSONArray("list");

            for (int i = 0; i < cityArray.length(); i++) {
                CityModel city = new CityModel();

                city.setId(cityArray.getJSONObject(i).getInt("regionID"));
                city.setName(cityArray.getJSONObject(i).getString("name"));

                citys.add(city);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return citys;
    }

    /**
     * 根据城市ID获取城市名称
     */
    public static String getCityNameByCode(int cityCode) {
        String cityName = "";
        String cityJson = readRawFile(R.raw.city);

        try {
            JSONObject cityObject = new JSONObject(cityJson);
            JSONArray cityArray = cityObject.getJSONArray("list");

            for (int i = 0; i < cityArray.length(); i++) {
                int cityID = cityArray.getJSONObject(i).getInt("regionID");

                if (cityID == cityCode) {
                    cityName = cityArray.getJSONObject(i).getString("name");

                    return cityName;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return cityName;
    }

    /**
     * 根据城市名称获取城市ID
     */
    public static int getCityCodeByName(String cityName) {
        int cityCode = -1;
        String cityJson = readRawFile(R.raw.city);

        try {
            JSONObject cityObject = new JSONObject(cityJson);
            JSONArray cityArray = cityObject.getJSONArray("list");

            for (int i = 0; i < cityArray.length(); i++) {
                String areaName = cityArray.getJSONObject(i).getString("name");

                if (areaName.equals(cityName)) {
                    cityCode = cityArray.getJSONObject(i).getInt("regionID");

                    return cityCode;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return cityCode;
    }

    /**
     * 获取热门城市数据
     */
    public static List<CityModel> getHotCitys() {
        List<CityModel> citys = new ArrayList<>();

        CityModel city1 = new CityModel(); city1.setId(499); city1.setName("北京");
        CityModel city2 = new CityModel(); city2.setId(121); city2.setName("南京");
        CityModel city3 = new CityModel(); city3.setId(145); city3.setName("合肥");
        CityModel city4 = new CityModel();  city4.setId(206);  city4.setName("濮阳");
        CityModel city5 = new CityModel(); city5.setId(185); city5.setName("东营");
        CityModel city6 = new CityModel(); city6.setId(198);  city6.setName("郑州");

        citys.add(city1); citys.add(city2); citys.add(city3);
        citys.add(city4); citys.add(city5); citys.add(city6);

        return citys;
    }

    /**
     * 读取资源文件数据
     *
     * @param id
     * @return
     */
    private static String readRawFile(int id) {
        String content = null;
        Resources resources = MApplication.getInstance().getResources();
        InputStream is = null;
        try {
            is = resources.openRawResource(id);
            byte buffer[] = new byte[is.available()];
            is.read(buffer);
            content = new String(buffer);
        } catch (IOException e) {
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
        return content;
    }
}