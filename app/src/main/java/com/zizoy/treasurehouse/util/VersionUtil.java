package com.zizoy.treasurehouse.util;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

/**
 * @Description: 获取当前版本号
 *
 * @author falcon
 */
public class VersionUtil {
    private static int oldVerCode = -1; // 程序当前版本号
    private static String oldVerName = ""; // 程序当前程序版本名称
    private int newVerCode = -1; // 最新版本号
    private String newVerName = "";

    /**
     * 获得程序当前版本号
     *
     * @param context
     * @return
     */
    public static int getVerCode(Context context) {
        try {
            oldVerCode = context.getPackageManager().getPackageInfo("com.example.treasurehouse", 0).versionCode;

            Log.e("zizoy", "得到的本程序的版本号为：" + oldVerCode);
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return oldVerCode;
    }

    /**
     * 获得程序当前程序版本名称
     *
     * @param context
     * @return
     */
    public static String getVerName(Context context) {
        try {
            oldVerName = context.getPackageManager().getPackageInfo("com.example.treasurehouse", 0).versionName;

            Log.e("zizoy", "得到的本程序当前程序版本名称为：" + oldVerName);
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return oldVerName;
    }

    public int getNewVerCode() {
        return newVerCode;
    }

    public void setNewVerCode(int newVerCode) {
        this.newVerCode = newVerCode;
    }

    public String getNewVerName() {
        return newVerName;
    }

    public void setNewVerName(String newVerName) {
        this.newVerName = newVerName;
    }
}