package com.assassin.webviewlibrary.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.SharedPreferencesCompat;
import android.text.TextUtils;

import java.util.Map;


public class SharedPreferManager 
{

    private static SharedPreferManager sManager;
    private static SharedPreferences sSharedPreferences;
    private static final String SP_NAME = "web_view_share_prefer";
    
    private SharedPreferManager(Context pContext)
    {
        sSharedPreferences = pContext.getApplicationContext().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
    }
    
    public static SharedPreferManager getInstance(Context pContext)
    {
        if (sManager==null)
        {
            synchronized (SharedPreferManager.class)
            {
                if (sManager==null)
                {
                    sManager = new SharedPreferManager(pContext);
                }
                
            }
        }
        return sManager;
        
    }

    

    private  SharedPreferences.Editor getEditor() {
        return sSharedPreferences.edit();
    }


    /**
     * 向sp存入值
     *
     * @param key
     * @param value
     */
    public  void put(String key, Object value)
    {
        //健壮性的校验 2018-03-07
        if (TextUtils.isEmpty(key) || value==null)
        {
            return;
        }
        SharedPreferences.Editor editor = getEditor();
        if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        } else {
            editor.putString(key, value.toString());
        }

        apply(editor);
    }

    /**
     * 从sp获取值
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public  Object get(String key, Object defaultValue) {
        if (defaultValue instanceof String) {
            return sSharedPreferences.getString(key, (String) defaultValue);
        } else if (defaultValue instanceof Integer) {
            return sSharedPreferences.getInt(key, (Integer) defaultValue);
        } else if (defaultValue instanceof Float) {
            return sSharedPreferences.getFloat(key, (Float) defaultValue);
        } else if (defaultValue instanceof Long) {
            return sSharedPreferences.getLong(key, (Long) defaultValue);
        } else if (defaultValue instanceof Boolean) {
            return sSharedPreferences.getBoolean(key, (Boolean) defaultValue);
        }

        return null;
    }

    /**
     * 从sp中移除某个键所对应的值
     *
     * @param key
     */
    public  void remove(String key) {
        SharedPreferences.Editor editor = getEditor().remove(key);
        apply(editor);
    }

    /**
     * 清空Sp
     */
    public void clear() {
        SharedPreferences.Editor editor = getEditor().clear();
        apply(editor);
    }

    /**
     * 获取存储在sp的所有数据
     *
     * @return
     */
    public  Map<String, ?> getAll() {
        return sSharedPreferences.getAll();
    }

    /**
     * 判断sp是否包含指定的键
     *
     * @param key
     * @return
     */
    public  boolean contains(String key) {
        return sSharedPreferences.contains(key);
    }

    private  void apply(SharedPreferences.Editor editor) {
        SharedPreferencesCompat.EditorCompat.getInstance().apply(editor);
    }


}
