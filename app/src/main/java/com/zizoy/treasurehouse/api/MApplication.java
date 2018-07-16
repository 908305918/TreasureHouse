package com.zizoy.treasurehouse.api;

import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.mapapi.SDKInitializer;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.tencent.bugly.crashreport.CrashReport;
import com.zizoy.treasurehouse.service.LocationService;
import com.zizoy.treasurehouse.util.CityTool;
import com.zizoy.treasurehouse.util.PreferencesUtils;
import com.zizoy.xutils.BitmapUtils;
import com.zizoy.xutils.HttpUtils;
import com.zizoy.xutils.exception.HttpException;
import com.zizoy.xutils.http.RequestParams;
import com.zizoy.xutils.http.ResponseInfo;
import com.zizoy.xutils.http.callback.RequestCallBack;
import com.zizoy.xutils.http.client.HttpRequest;

/**
 * Application公共类
 *
 * @author falcon
 */
public class MApplication extends Application {
    private static BitmapUtils bitmapUtils;
    private static HttpUtils httpUtils;
    private static MApplication instance;

    public static String filePath = "/sdcard/zizoy/"; // 文件保存路径

    public LocationService locateService;
    private LocationClientOption locateOption;
    public Vibrator mVibrator;

    public static double lat = 0;
    public static double lon = 0;
    public static String cityStr = null;
    public static String street;
    public static String addressStr = null;

    //	public static String serverURL = "http://192.168.23.1:8080/cbg/"; // 测试服务器地址
//	public static String serverURL = "http://118.89.222.210:8080/cbg/"; // 正式服务器地址
//	public static String serverURL = "http://211.159.169.193:8080/cbg/"; // 正式服务器地址
    public static String serverURL = "http://140.143.38.179/cbg/"; // 正式服务器地址

    /**
     * 单例获得网络请求工具类
     */
    public static HttpUtils getHttpUtils() {
        if (httpUtils == null) {
            httpUtils = new HttpUtils();
            httpUtils.configTimeout(150 * 1000);
            return httpUtils;
        } else {
            return httpUtils;
        }
    }

    /**
     * BitmapUtils不是单例的 根据需要重载多个获取实例的方法
     *
     * @param appContext application context
     * @return
     */
    public static BitmapUtils getBitmapUtils(Context appContext) {
        if (bitmapUtils == null) {
            bitmapUtils = new BitmapUtils(appContext);
        }
        return bitmapUtils;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
        initImageLoader(getApplicationContext());

        /***
         * 初始化定位sdk，建议在Application中创建
         */
        locateOption = new LocationClientOption();
        locateService = new LocationService(getApplicationContext());
        mVibrator = (Vibrator) getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
        SDKInitializer.initialize(getApplicationContext());

        CrashReport.initCrashReport(getApplicationContext(), "a10f7cfae7", true); //初始化Bugly
    }

    public static MApplication getInstance() {
        return instance;
    }

    /**
     * 图片加载
     */
    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
    }

    /**
     * 开启百度定位
     */
    public void startLocate() {
        locateOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy); // 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        locateOption.setCoorType("bd09ll"); // 可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
        locateOption.setScanSpan(1000); // 可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        locateOption.setIsNeedAddress(true); // 可选，设置是否需要地址信息，默认不需要
        locateOption.setIsNeedLocationDescribe(true); // 可选，设置是否需要地址描述
        locateOption.setNeedDeviceDirect(true); // 可选，设置是否需要设备方向结果
        locateOption.setLocationNotify(false); // 可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        locateOption.setIgnoreKillProcess(true); // 可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        locateOption.setIsNeedLocationPoiList(true); // 可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        locateOption.SetIgnoreCacheException(false); // 可选，默认false，设置是否收集CRASH信息，默认收集
        locateOption.setIsNeedAltitude(false); // 可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用

        locateService.setLocationOption(locateOption);
        locateService.registerListener(locateListener); // 开启定位监听
        locateService.start(); // 启动定位SDK
    }

    /**
     * 关闭百度定位
     */
    public void closeLocate() {
        locateService.unregisterListener(locateListener); // 注销定位监听
        locateService.stop(); // 停止定位服务
    }

    /**
     * 停止百度定位
     */
    public void stopLocate() {
        locateService.stop();
    }

    /**
     * 定位结果回调，重写onReceiveLocation方法
     */
    private BDLocationListener locateListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                StringBuffer buffer = new StringBuffer();
                buffer.append("time : ");
                /**
                 * 时间也可以使用systemClock.elapsedRealtime()方法 获取的是自从开机以来，每次回调的时间；
                 * location.getTime() 是指服务端出本次结果的时间，如果位置不发生变化，则时间不变
                 */
                buffer.append(location.getTime());
                buffer.append("\nlocType : "); // 定位类型
                buffer.append(location.getLocType());
                buffer.append("\nlocType description : "); // 对应的定位类型说明
                buffer.append(location.getLocTypeDescription());
                buffer.append("\nlatitude : "); // 纬度
                buffer.append(location.getLatitude());
                buffer.append("\nlontitude : "); // 经度
                buffer.append(location.getLongitude());
                buffer.append("\nradius : "); // 半径
                buffer.append(location.getRadius());
                buffer.append("\nCountryCode : "); // 国家码
                buffer.append(location.getCountryCode());
                buffer.append("\nCountry : "); // 国家名称
                buffer.append(location.getCountry());
                buffer.append("\nprovince : "); // 省份名称
                buffer.append(location.getProvince());
                buffer.append("\ncitycode : "); // 城市编码
                buffer.append(location.getCityCode());
                buffer.append("\ncity : "); // 城市名称
                buffer.append(location.getCity());
                buffer.append("\nDistrict : "); // 区
                buffer.append(location.getDistrict());
                buffer.append("\nStreet : "); // 街道
                buffer.append(location.getStreet());
                buffer.append("\naddr : "); // 地址信息
                buffer.append(location.getAddrStr());
                buffer.append("\nUserIndoorState: "); // 返回用户室内外判断结果
                buffer.append(location.getUserIndoorState());
                buffer.append("\nDirection(not all devices have value): ");
                buffer.append(location.getDirection()); // 方向
                buffer.append("\nlocationdescribe: ");
                buffer.append(location.getLocationDescribe()); // 位置语义化信息
                buffer.append("\nPoi: "); // POI信息

                if (location.getPoiList() != null && !location.getPoiList().isEmpty()) {
                    for (int i = 0; i < location.getPoiList().size(); i++) {
                        Poi poi = (Poi) location.getPoiList().get(i);
                        buffer.append(poi.getName() + ";");
                    }
                }
                if (location.getLocType() == BDLocation.TypeGpsLocation) { // GPS定位结果
                    buffer.append("\nspeed : ");
                    buffer.append(location.getSpeed()); // 速度 单位：km/h
                    buffer.append("\nsatellite : ");
                    buffer.append(location.getSatelliteNumber()); // 卫星数目
                    buffer.append("\nheight : ");
                    buffer.append(location.getAltitude()); // 海拔高度 单位：米
                    buffer.append("\ngps status : ");
                    buffer.append(location.getGpsAccuracyStatus()); // gps质量判断
                    buffer.append("\ndescribe : ");
                    buffer.append("gps定位成功");
                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) { // 网络定位结果
                    // 运营商信息
                    if (location.hasAltitude()) { // 如果有海拔高度
                        buffer.append("\nheight : ");
                        buffer.append(location.getAltitude()); // 单位：米
                    }
                    buffer.append("\noperationers : ");// 运营商信息
                    buffer.append(location.getOperators());
                    buffer.append("\ndescribe : ");
                    buffer.append("网络定位成功");
                } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                    buffer.append("\ndescribe : ");
                    buffer.append("离线定位成功，离线定位结果也是有效的");
                } else if (location.getLocType() == BDLocation.TypeServerError) {
                    buffer.append("\ndescribe : ");
                    buffer.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
                } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                    buffer.append("\ndescribe : ");
                    buffer.append("网络不同导致定位失败，请检查网络是否通畅");
                } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                    buffer.append("\ndescribe : ");
                    buffer.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
                }
                lat = location.getLatitude();
                lon = location.getLongitude();
                cityStr = location.getCity();
                street = location.getStreet();
                addressStr = location.getAddrStr();
                addressStr = addressStr.replace(location.getCountry(), "").replace(location.getProvince(), "");

                int cityId = CityTool.getCityCodeByName(cityStr.replace("市", ""));
                PreferencesUtils.setIntPreferences(getApplicationContext(), "XXCP", "cityId", cityId);
                Log.e("zizoy", "-lat-" + lat + "-lon-" + lon + "-cityStr-" + cityStr);
                Log.e("zizoy", buffer.toString());

                statistics();
            }
        }
    };


    private boolean isFirst = true;

    /**
     * 统计
     */
    private void statistics() {
        if (!isFirst) {
            return;
        }
        isFirst = false;
        String url = MApplication.serverURL + "/count/activationCount";
        TelephonyManager tm = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE));
        RequestParams params = new RequestParams();
        params.addBodyParameter("type", "0");
        params.addBodyParameter("machineFlag", tm.getDeviceId());
        params.addBodyParameter("city", cityStr);
        params.addBodyParameter("street", street);
        MApplication.getHttpUtils().send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                Log.e("TAG",responseInfo.result);
            }

            @Override
            public void onFailure(HttpException e, String s) {

            }
        });
    }
}