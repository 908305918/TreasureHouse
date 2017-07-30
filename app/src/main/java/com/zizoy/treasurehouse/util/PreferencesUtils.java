package com.zizoy.treasurehouse.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * @Description: 参数保存工具类
 *
 * @author falcon
 */
public class PreferencesUtils {
    /**
     * To deposit value in application
     */
    // Store the String value
    public static void setStringPreferences(Context context, String preference, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(preference, Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    // Store the int value
    public static void setIntPreferences(Context context, String preference, String key, int value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(preference, Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    // Store the Boolean value
    public static void setBooleanPreferences(Context context, String preference, String key, Boolean value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(preference, Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    // Store the long value
    public static void setLongPreference(Context context, String preference, String key, long value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(preference, Context.MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    /**
     * The value read from the program
     */
    // Read a String value
    public static String getStringPreference(Context context, String preference, String key, String defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(preference, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, defaultValue);
    }

    // Read a int value
    public static int getIntPreference(Context context, String preference, String key, int defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(preference, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, defaultValue);
    }

    // Read a Boolean value
    public static Boolean getBooleanPreference(Context context, String preference, String key, Boolean defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(preference, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    // Read a long value
    public static long getLongPreference(Context context, String preference, String key, long defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(preference, Context.MODE_PRIVATE);
        return sharedPreferences.getLong(key, defaultValue);
    }
}