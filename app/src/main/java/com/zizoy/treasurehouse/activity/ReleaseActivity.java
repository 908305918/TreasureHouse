package com.zizoy.treasurehouse.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.treasurehouse.R;
import com.zizoy.treasurehouse.adapter.MCameraAdapter;
import com.zizoy.treasurehouse.adapter.MyspinnerAdapter;
import com.zizoy.treasurehouse.api.MApplication;
import com.zizoy.treasurehouse.base.SuperActivity;
import com.zizoy.treasurehouse.bean.AjaxParamsBean;
import com.zizoy.treasurehouse.takephoto.MultiImageSelector;
import com.zizoy.treasurehouse.util.CityTool;
import com.zizoy.treasurehouse.util.ClearEditText;
import com.zizoy.treasurehouse.util.GridViewUtil;
import com.zizoy.treasurehouse.util.JsonUtil;
import com.zizoy.treasurehouse.util.PopViewHelper;
import com.zizoy.treasurehouse.util.PreferencesUtils;
import com.zizoy.treasurehouse.util.ToastUtil;
import com.zizoy.xutils.exception.HttpException;
import com.zizoy.xutils.http.RequestParams;
import com.zizoy.xutils.http.ResponseInfo;
import com.zizoy.xutils.http.callback.RequestCallBack;
import com.zizoy.xutils.http.client.HttpRequest;

import org.json.JSONArray;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import static com.zizoy.treasurehouse.api.MApplication.filePath;

/**
 * @author falcon
 * @Description: 程序关于界面
 */
public class ReleaseActivity extends SuperActivity {
    private TextView title;
    private LinearLayout backBtn;
    private LinearLayout gotoBtn;

    private TextView typeOne; // 一级类别
    private TextView typeTwo; // 二级类别
    private ClearEditText name; // 标题
    private ClearEditText price; // 价格
    private ClearEditText user; // 姓名
    private ClearEditText phone; // 电话
    private ClearEditText address; // 地址
    private LinearLayout city; // 城市
    private TextView tv_district; // 区域
    private TextView tv_street; // 街道
    private TextView cityTv;
    private EditText note; // 内容
    private GridViewUtil photos; // 图片
    private MCameraAdapter mAdapter;
    private ImageButton photoBtn;
    private Button addBtn;

    private PopupWindow popupWindow;

    private String userId = null;
    private String nameStr = null;
    private String priceStr = null;
    private String userStr = null;
    private String phoneStr = null;
    private String cityStr = null;
    private String districtStr = null;
    private String streetStr = null;
    private String noteStr = null;
    private String addressStr = null;

    private JSONArray mStreetArray;

    private ArrayList<File> photosPathList = new ArrayList<>(); // 照片地址
    private ArrayList<Bitmap> photosImages = new ArrayList<>(); // 照片地址

    // 请求照相/图片浏览的常量
    private static final int CAMERA_PHOTOS_DATA = 3023;

    protected static final int REQUEST_STORAGE_READ_ACCESS_PERMISSION = 101;
    private ArrayList<String> mSelectPath = new ArrayList<>();

    private LinearLayout oneLayout, twoLayout;
    private ListView oneListView, twoListView;
    private MyspinnerAdapter oneAdapter, twoAdapter;
    private ArrayList<String> oneList, twoList; // 发布类别

    // 添加发布接口地址
    private String addPath = MApplication.serverURL + "post/insertPost";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_release;
    }

    @Override
    protected void initData() {
        super.initData();

        userId = PreferencesUtils.getStringPreference(activity, "ZHKJ", "userId", "");
    }

    @Override
    protected void initView() {
        super.initView();

        title = (TextView) findViewById(R.id.tv_title);
        backBtn = (LinearLayout) findViewById(R.id.btn_back);
        gotoBtn = (LinearLayout) findViewById(R.id.btn_home);
        typeOne = (TextView) findViewById(R.id.tv_typeOne);
        typeTwo = (TextView) findViewById(R.id.tv_typeTwo);
        name = (ClearEditText) findViewById(R.id.et_name);
        price = (ClearEditText) findViewById(R.id.et_price);
        user = (ClearEditText) findViewById(R.id.et_user);
        phone = (ClearEditText) findViewById(R.id.et_phone);
        address = (ClearEditText) findViewById(R.id.et_address);
        city = (LinearLayout) findViewById(R.id.ll_city);
        cityTv = (TextView) findViewById(R.id.tv_city);
        note = (EditText) findViewById(R.id.et_note);
        photoBtn = (ImageButton) findViewById(R.id.btn_photoAdd);
        photos = (GridViewUtil) findViewById(R.id.gv_photos);
        addBtn = (Button) findViewById(R.id.btn_releaseAdd);
        tv_district = (TextView) findViewById(R.id.tv_district);
        tv_street = (TextView) findViewById(R.id.tv_street);

    }

    @Override
    protected void initCodeLogic() {
        super.initCodeLogic();

        title.setText("添加发布");
        backBtn.setVisibility(View.VISIBLE);
        gotoBtn.setVisibility(View.VISIBLE);

        mAdapter = new MCameraAdapter(activity, photosImages);
        photos.setAdapter(mAdapter);
        if (photosImages.size() > 0) {
            photos.setVisibility(View.VISIBLE);
        } else {
            photos.setVisibility(View.GONE);
        }

        getTypeOneList();
        getTypeTwoList();
        getCityLocate(); // 获取当前定位城市
    }

    /**
     * 获取一级类别数据
     */
    private void getTypeOneList() {
        oneList = new ArrayList<>();

        oneList.add("房产");
        oneList.add("手机");
        oneList.add("招聘");
        oneList.add("家电");
        oneList.add("交通工具");
        oneList.add("二手交易");
        oneAdapter = new MyspinnerAdapter(this, oneList);
        typeOne.setText((CharSequence) oneAdapter.getItem(0).toString());
    }

    /**
     * 获取二级类别数据
     */
    private void getTypeTwoList() {
        twoList = new ArrayList<>();

        twoList.add("出租");
        twoList.add("出售");
        twoAdapter = new MyspinnerAdapter(ReleaseActivity.this, twoList);
        typeTwo.setText((CharSequence) twoAdapter.getItem(0).toString());
    }

    @Override
    protected void setListener() {
        super.setListener();

        backBtn.setOnClickListener(mBtnClick);
        gotoBtn.setOnClickListener(mBtnClick);
        typeOne.setOnClickListener(mBtnClick);
        typeTwo.setOnClickListener(mBtnClick);
        city.setOnClickListener(mBtnClick);
        price.setOnClickListener(mBtnClick);
        photoBtn.setOnClickListener(mBtnClick);
        addBtn.setOnClickListener(mBtnClick);
        tv_district.setOnClickListener(mBtnClick);
        tv_street.setOnClickListener(mBtnClick);
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

                case R.id.btn_home: // 回到首页被点击
                    activityFinish();
                    break;

                case R.id.tv_typeOne: // 一级类别被点击
                    showWindowOne(typeOne, typeOne);
                    break;

                case R.id.tv_typeTwo: // 二级类别被点击
                    showWindowTwo(typeTwo, typeTwo);
                    break;

                case R.id.ll_city: // 发布城市被点击
                    startActivityForResult(CityChoiceActivity.class, null, 1);
                    activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
                    break;

                case R.id.tv_district: // 区域
                    cityStr = cityTv.getText().toString().trim();
                    if (TextUtils.isEmpty(cityStr)) {
                        ToastUtil.showMessage(activity, "请选择对应的城市！");
                    } else {
                        JSONArray array = CityTool.findAreaAndStreet(cityStr);
                        PopViewHelper dPop = new PopViewHelper(ReleaseActivity.this, tv_district, array, PopViewHelper.TYPE_D);
                        dPop.setOnPopItemClickListener(new PopViewHelper.OnPopItemClickListener() {
                            @Override
                            public void onPopItemClick(int type, String text, JSONArray array) {
                                districtStr = text;
                                mStreetArray = array;
                                streetStr = "";
                                tv_street.setText("街道");
                            }
                        });
                        dPop.show();
                    }
                    break;

                case R.id.tv_street: // 街道
                    if (TextUtils.isEmpty(districtStr)) {
                        ToastUtil.showMessage(activity, "请选择对应的区域！");
                    } else {
                        PopViewHelper sPop = new PopViewHelper(ReleaseActivity.this, tv_street, mStreetArray, PopViewHelper.TYPE_S);
                        sPop.setOnPopItemClickListener(new PopViewHelper.OnPopItemClickListener() {
                            @Override
                            public void onPopItemClick(int type, String text, JSONArray array) {
                                streetStr = text;
                            }
                        });
                        sPop.show();
                    }
                    break;

                case R.id.btn_photoAdd: // 图片添加被点击
                    String mCityStr = cityTv.getText().toString().trim();
                    String mAddressStr = address.getText().toString().trim();

                    PreferencesUtils.setStringPreferences(activity, "ZHKJ", "cityName", mCityStr);
                    PreferencesUtils.setStringPreferences(activity, "ZHKJ", "address", mAddressStr);

                    pickImage();
                    break;

                case R.id.btn_releaseAdd: // 信息发布被点击
                    nameStr = name.getText().toString().trim();
                    priceStr = price.getText().toString().trim();
                    userStr = user.getText().toString().trim();
                    phoneStr = phone.getText().toString().trim();
                    cityStr = cityTv.getText().toString().trim();
                    noteStr = note.getText().toString().trim();
                    addressStr = address.getText().toString().trim();

                    if ("".equals(nameStr) || nameStr == null) {
                        ToastUtil.showMessage(activity, "标题不能为空！");
                        name.requestFocus();
                    } else if ("".equals(priceStr) || priceStr == null) {
                        ToastUtil.showMessage(activity, "价格不能为空！");
                        price.requestFocus();
                    } else if ("".equals(userStr) || userStr == null) {
                        ToastUtil.showMessage(activity, "姓名不能为空！");
                        user.requestFocus();
                    } else if ("".equals(phoneStr) || phoneStr == null) {
                        ToastUtil.showMessage(activity, "手机号不能为空！");
                        phone.requestFocus();
                    } else if ("".equals(cityStr) || cityStr == null) {
                        ToastUtil.showMessage(activity, "请选择对应的城市！");
                    } else if ("".equals(noteStr) || noteStr == null) {
                        ToastUtil.showMessage(activity, "内容不能为空！");
                        note.requestFocus();
                    } else if (noteStr.length() < 10) {
                        ToastUtil.showMessage(activity, "内容至少输入10字符！");
                    } else if (photosPathList.size() == 0) {
                        ToastUtil.showMessage(activity, "至少要上传一张图片！");
                    } else {
                        putAddData();
                    }
                    break;

                default:
                    break;
            }
        }
    };

    /**
     * 获取当前定位城市
     */
    private void getCityLocate() {
        if ("".equals(MApplication.cityStr) || MApplication.cityStr == null) {
            MApplication.cityStr = "北京";
            MApplication.addressStr = "暂无定位地址信息";
        }
        address.setText(MApplication.addressStr);
        cityTv.setText(MApplication.cityStr.replace("市", ""));
        address.setSelection(address.getText().toString().trim().length());
    }

    /**
     * 帖子编辑
     */
    public void deletePhotos(final int mPosition) {
        photosImages.remove(mPosition);
        photosPathList.remove(mPosition);
        mSelectPath.remove(mPosition);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 大类型
     *
     * @param position
     * @param txt
     */
    public void showWindowOne(View position, final TextView txt) {
        oneLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.mypinner_dropdown, null);
        oneListView = (ListView) oneLayout.findViewById(R.id.listView);
        oneListView.setAdapter(oneAdapter);
        popupWindow = new PopupWindow(position);
        popupWindow.setWidth(txt.getWidth());
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0000000000));
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setContentView(oneLayout);
        popupWindow.showAsDropDown(position, 0, 0);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
            }
        });
        oneListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                txt.setText(oneList.get(arg2).trim());
                /**
                 * 小类型
                 */
                twoList = new ArrayList<String>();

                if (arg2 == 0) {
                    twoList.add("出租");
                    twoList.add("出售");
                } else if (arg2 == 1) {
                    twoList.add("苹果");
                    twoList.add("其他");
                } else if (arg2 == 2) {
                    twoList.add("全职");
                    twoList.add("兼职");
                } else if (arg2 == 3) {
                    twoList.add("冰箱");
                    twoList.add("洗衣机");
                    twoList.add("电视");
                    twoList.add("其他");
                } else if (arg2 == 4) {
                    twoList.add("汽车");
                    twoList.add("其他");
                } else if (arg2 == 5) {
                    twoList.add("二手交易");
                }
                twoAdapter = new MyspinnerAdapter(ReleaseActivity.this, twoList);
                typeTwo.setText((CharSequence) twoAdapter.getItem(0).toString());
                popupWindow.dismiss();
            }
        });
    }

    /**
     * 小类型
     *
     * @param position
     * @param txt
     */
    public void showWindowTwo(View position, final TextView txt) {
        twoLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.mypinner_dropdown, null);
        twoListView = (ListView) twoLayout.findViewById(R.id.listView);
        twoListView.setAdapter(twoAdapter);
        popupWindow = new PopupWindow(position);
        popupWindow.setWidth(txt.getWidth());
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0000000000));
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setContentView(twoLayout);
        popupWindow.showAsDropDown(position, 0, 0);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
            }
        });
        twoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                txt.setText(twoList.get(arg2).trim());
                popupWindow.dismiss();
            }
        });
    }

    private void pickImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN // Permission was added in API Level 16
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE, getString(R.string.mis_permission_rationale),
                    REQUEST_STORAGE_READ_ACCESS_PERMISSION);
        } else {
            int maxNum = 12;
            MultiImageSelector selector = MultiImageSelector.create(activity);
            selector.showCamera(true);
            selector.count(maxNum);
            selector.multi();

            selector.origin(mSelectPath);
            selector.start(activity, CAMERA_PHOTOS_DATA);
        }
    }

    private void requestPermission(final String permission, String rationale, final int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.mis_permission_dialog_title)
                    .setMessage(rationale)
                    .setPositiveButton(R.string.mis_permission_dialog_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
                        }
                    })
                    .setNegativeButton(R.string.mis_permission_dialog_cancel, null)
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_STORAGE_READ_ACCESS_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImage();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * 拍照上传图片处理
     *
     * @param photoFile
     */
    private void disposeCameraPhotos(final File photoFile, final int index) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getPath(), newOpts);

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        float hh = 800f;// 这里设置高度为800f
        float ww = 480f;// 这里设置宽度为480f
        int be = 1;// be=1表示不缩放
        if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 设置缩放比例
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(photoFile.getPath(), newOpts);
        try {
            File file = new File(filePath + "photos/");
            if (!file.exists()) {
                file.mkdirs(); // 创建照片存储目录
            }

            String suffix = index + ".jpg";
            String filePath = DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + suffix;
            File tempPhotoFile = new File(file.getPath() + "/" + filePath);

            FileOutputStream fos = new FileOutputStream(tempPhotoFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            photosImages.add(bitmap);
            photosPathList.add(tempPhotoFile);
            mAdapter.notifyDataSetChanged();
            if (photosImages.size() > 0) {
                photos.setVisibility(View.VISIBLE);
            } else {
                photos.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 提交发布数据
     */
    private void putAddData() {
        if (checkNet.checkNet()) {
            ToastUtil.showMessage(activity, "网络延迟，请等待一分钟");

            RequestParams params = new RequestParams();
            params.addBodyParameter("jsonpCallback", "jsonpCallback");

            AjaxParamsBean bean = new AjaxParamsBean();
            bean.setUid(userId);
            bean.setTitle(nameStr);
            bean.setType(typeOne.getText().toString().trim());
            bean.setType2(typeTwo.getText().toString().trim());
            bean.setPrice(priceStr);
            bean.setContact(userStr);
            bean.setPhone(phoneStr);
            bean.setAddress(addressStr);
            bean.setContent(noteStr);
            bean.setLongitude(MApplication.lon + "");
            bean.setLatitude(MApplication.lat + "");
            bean.setCity(cityStr);
            bean.setDistrict(districtStr);
            bean.setStreet(streetStr);

            String json = JsonUtil.toJson(bean);
            params.addBodyParameter("jsoninput", json);
            params.addBodyParameter("uploadify", photosPathList);

            httpUtils.send(HttpRequest.HttpMethod.POST, addPath, params,
                    new RequestCallBack<String>() {
                        @Override
                        public void onStart() {
                            dialogUtil.showSubmitDialog(); // 显示加载Dialog
                        }

                        @Override
                        public void onLoading(long total, long current, boolean isUploading) {
                        }

                        @Override
                        public void onSuccess(ResponseInfo<String> responseInfo) {
                            dialogUtil.closeDialog();// 加载成功关闭Dialog
                            try {
                                String jsonObject = responseInfo.result.substring(15, responseInfo.result.length() - 2);

                                if ("1".equals(jsonObject)) {
                                    ToastUtil.showMessage(activity, "该信息发布成功");
                                    startActivity(MyAddActivity.class, null);
                                    overridePendingTransition(R.anim.left_in, R.anim.left_out);
                                    finishActivity();
                                } else {
                                    ToastUtil.showMessage(activity, "该信息发布失败！");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(HttpException error, String msg) {
                            dialogUtil.closeDialog(); // 加载失败关闭Dialog并提示
                            ToastUtil.showMessage(activity, "网络异常！");
                        }
                    });
        } else {
            dialogUtil.showNetworkDialog(); // 显示提示界面
        }
    }

    /**
     * 获取返回参数
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        String mCityStr = PreferencesUtils.getStringPreference(activity, "ZHKJ", "cityName", "");
        String mAddressStr = PreferencesUtils.getStringPreference(activity, "ZHKJ", "address", "");

        cityTv.setText(mCityStr);
        address.setText(mAddressStr);
        address.setSelection(address.getText().toString().trim().length());

        if (resultCode != RESULT_OK) {
            return;
        }

        if (1 == requestCode) { // 城市选择
            cityTv.setText(data.getExtras().getString("cityName"));
            districtStr=null;
            streetStr =null;
            tv_district.setText("区域");
            tv_street.setText("街道");
        } else if (CAMERA_PHOTOS_DATA == requestCode) { // 拍照/本地浏览
            if (data != null) {
                mSelectPath = data.getStringArrayListExtra(MultiImageSelector.EXTRA_RESULT);

                if (mSelectPath != null && mSelectPath.size() > 0) {
                    photosImages.clear();
                    photosPathList.clear();

                    for (int index = 0; index < mSelectPath.size(); index++) {
                        disposeCameraPhotos(new File(mSelectPath.get(index)), index);
                    }
                }
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