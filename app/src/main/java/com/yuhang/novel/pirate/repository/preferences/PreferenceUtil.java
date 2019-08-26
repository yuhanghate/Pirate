package com.yuhang.novel.pirate.repository.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * Description：SharedPreferences的管理类
 *
 */
public class PreferenceUtil {

    private static SharedPreferences mSharedPreferences = null;
    private static Editor mEditor = null;
    
    public static void init(Context context){
    	if (null == mSharedPreferences) {
    		mSharedPreferences = android.preference.PreferenceManager.getDefaultSharedPreferences(context) ;
    	}
    }
    
    public static void removeKey(String key){
        mEditor = mSharedPreferences.edit();
        mEditor.remove(key);
        mEditor.apply();
    }
    
    public static void removeAll(){
        mEditor = mSharedPreferences.edit();
        mEditor.clear();
        mEditor.apply();
    }

    public static void commitString(String key, String value){
        mEditor = mSharedPreferences.edit();
        mEditor.putString(key, value);
        mEditor.apply();
    }
    
    public static String getString(String key, String faillValue){
        return mSharedPreferences.getString(key, faillValue);
    }
    
    public static void commitInt(String key, int value){
        mEditor = mSharedPreferences.edit();
        mEditor.putInt(key, value);
        mEditor.apply();
    }
    
    public static int getInt(String key, int failValue){
        return mSharedPreferences.getInt(key, failValue);
    }
    
    public static void commitLong(String key, long value){
        mEditor = mSharedPreferences.edit();
        mEditor.putLong(key, value);
        mEditor.apply();
    }

    public static void commitFloat(String key, float value) {
        mEditor = mSharedPreferences.edit();
        mEditor.putFloat(key, value);
        mEditor.apply();
    }

    public static float getFloat(String key, float failValue) {
        return mSharedPreferences.getFloat(key, failValue);
    }
    
    public static long getLong(String key, long failValue) {
        return mSharedPreferences.getLong(key, failValue);
    }
    
    public static void commitBoolean(String key, boolean value){
        mEditor = mSharedPreferences.edit();
        mEditor.putBoolean(key, value);
        mEditor.apply();
    }
    
    public static Boolean getBoolean(String key, boolean failValue){
        return mSharedPreferences.getBoolean(key, failValue);
    }
}
