package com.zizoy.treasurehouse.util;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Environment;

/**
 * @Description: 数据判断工具类
 *
 * @author falcon
 */
public class CheckUtil {
    /**
     * @param cardStr 密码
     * @return 返回状态
     * @Description: 判断密码规则
     */
    public static boolean isPasswd(String cardStr) {
        String regx = "[a-zA-Z0-9]{6,20}";
        return cardStr.matches(regx);
    }

    /**
     * 判断手机号码正则表达式
     *
     * @param mobileStr 手机号码
     * @return 返回状态
     */
    public static boolean isMobile(String mobileStr) {
        Pattern pattern = Pattern
                .compile("^((13[0-9])|(14[0-9])|(15[0-9])|(17[0-9])|(18[0-9]))\\d{8}$");
        Matcher matcher = pattern.matcher(mobileStr);

        return matcher.matches();
    }

    /**
     * 判断账号正则表达式
     *
     * @param userStr 账号（身份证号）
     * @return 返回状态
     */
    public static boolean isUser(String userStr) {
        Pattern pattern = Pattern.compile("^[A-Za-z0-9]+$");
        Matcher matcher = pattern.matcher(userStr);

        return matcher.matches();
    }

    /**
     * 检测邮箱合法性
     *
     * @param email
     * @return
     */
    public static boolean checkEmailValid(String email) {
        if ((email == null) || (email.trim().length() == 0)) {
            return false;
        }
        String regEx = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}$";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(email.trim().toLowerCase());

        return m.find();
    }

    /**
     * 是否为新版本, true  为有新版本， 否则无
     *
     * @param oldversion
     * @param newversion
     * @return
     */
    public static boolean versionCompare(String oldversion, String newversion) {
        if (oldversion == null || newversion == null) {
            return false;
        }
        String[] oldstr = oldversion.split("\\.");
        String[] newstr = newversion.split("\\.");

        int[] oldint = new int[oldstr.length];
        int[] newint = new int[newstr.length];

        try {
            for (int i = 0; i < oldstr.length; i++) {
                oldint[i] = Integer.valueOf(oldstr[i]);
            }
            for (int i = 0; i < newstr.length; i++) {
                newint[i] = Integer.valueOf(newstr[i]);
            }
        } catch (Exception e) {
        }

        // 可以简化的逻辑
        int count = oldint.length > newint.length ? newint.length
                : oldint.length;
        for (int temp = 0; temp < count; temp++) {
            if (newint[temp] == oldint[temp]) {
                continue;
            } else if (newint[temp] > oldint[temp]) {
                return true;
            } else {
                return false;
            }
        }
        if (newint.length > oldint.length) {
            return true;
        }
        return false;
    }

    /**
     * 是否有外存卡
     *
     * @return
     */
    public static boolean isExistExternalStore() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断某个服务是否正在运行的方法
     *
     * @param mContext
     * @param serviceName 是包名+服务的类名（例如：net.loonggg.testbackstage.TestService）
     * @return true代表正在运行，false代表服务没有正在运行
     */
    public static boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(40);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }
}