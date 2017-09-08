package com.zizoy.treasurehouse.model;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.zizoy.treasurehouse.activity.PersonalActivity;
import com.zizoy.treasurehouse.api.MApplication;
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

/**
 * Created by YJB on 2017/9/8.
 */

public class ReportModel {

    public static void report(final Context context, String pid) {
        String uid = PreferencesUtils.getStringPreference(context, "ZHKJ", "userId", "");
        if (TextUtils.isEmpty(uid)) {
            Intent intent = new Intent(context, PersonalActivity.class);
            intent.putExtra("from","message");
            context.startActivity(intent);
            return;
        }
        String url = MApplication.serverURL + "/post/report";
        RequestParams params = new RequestParams();
        params.addBodyParameter("pid", pid);
        params.addBodyParameter("userId", uid);
        final DialogUtil dialog = new DialogUtil(context);
        MApplication.getHttpUtils().send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
            @Override
            public void onStart() {
                super.onStart();
                dialog.showLoadDialog();
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                dialog.closeDialog();
                try {
                    JSONObject jsonObj = new JSONObject(responseInfo.result);
                    String code = jsonObj.optString("resultCode");
                    if ("1".equals(code)) {
                        ToastUtil.showMessage(context, "举报成功！");
                    }else if("0".equals(code)){
                        ToastUtil.showMessage(context, "不能重复举报！");
                    } else {
                        ToastUtil.showMessage(context, "举报失败！");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                dialog.closeDialog();
                ToastUtil.showMessage(context, "网络异常！");
            }

        });
    }
}
