package com.assassin.webviewlibrary.provider;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.SharedPreferencesCompat;
import android.support.v4.util.ArrayMap;

import java.lang.ref.SoftReference;
import java.util.Map;


class SPHelperImpl {
    private static final String TAG = "SPHelperImpl";
    /**
     * SharedPreferences 储存的键值対文件的名称。
     */
    public static final String SP_NAME = "WEB_VIEW_SP";
    
    /**
     * 获取内部私有的sp
     */
    private static SharedPreferences getSP(Context context) {
        if (context == null) {
            return null;
        }
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
    }

    private static SoftReference<Map<String, Object>> sCacheMap;

    private static Object getCachedValue(String name) {
        if (sCacheMap != null) {
            Map<String, Object> map = sCacheMap.get();
            if (map != null) {
                return map.get(name);
            }
        }
        return null;
    }

    private static void setValueToCached(String name, Object value) {
        Map<String, Object> map;
        if (sCacheMap == null) {
            map = new ArrayMap<>();
            sCacheMap = new SoftReference<Map<String, Object>>(map);
        } else {
            map = sCacheMap.get();
            if (map == null) {
                map = new ArrayMap<>();
                sCacheMap = new SoftReference<Map<String, Object>>(map);
            }
        }
        map.put(name, value);
    }

    private static void cleanCachedValue(){
        Map<String, Object> map;
        if (sCacheMap != null) {
            map = sCacheMap.get();
            if (map != null) {
                map.clear();
            }
        }
    }


    synchronized static <T> void save(Context context, String name, T t) {
        SharedPreferences sp = getSP(context);
        if (sp == null) return;

        //相同的值，就不做覆盖
        if (t.equals(getCachedValue(name))) {
            return;
        }
        SharedPreferences.Editor editor = sp.edit();
        if (t instanceof Boolean) {
            editor.putBoolean(name, (Boolean) t);
        }
        if (t instanceof String) {
            editor.putString(name, (String) t);
        }
        if (t instanceof Integer) {
            editor.putInt(name, (Integer) t);
        }
        if (t instanceof Long) {
            editor.putLong(name, (Long) t);
        }
        if (t instanceof Float) {
            editor.putFloat(name, (Float) t);
        }
        apply(editor);
        setValueToCached(name, t);
    }

    static String get(Context context, String name, String type) {
       /* Object value = getCachedValue(name);
        if (value != null) {
            return value + "";
        } else {
            value = get_impl(context, name, type);
            setValueToCached(name, value);
            return value + "";
        }
*/
        Object value = get_impl(context, name, type);
        setValueToCached(name, value);
        return  value+"";
    }

    /**
     * 从本地获取。
     */
    private static Object get_impl(Context context, String name, String type) {
        if (!contains(context, name)) {
            return null;
        } else {
            if (type.equalsIgnoreCase(SPConstant.TYPE_STRING)) {
                return getString(context, name, null);
            } else if (type.equalsIgnoreCase(SPConstant.TYPE_BOOLEAN)) {
                return getBoolean(context, name, false);
            } else if (type.equalsIgnoreCase(SPConstant.TYPE_INT)) {
                return getInt(context, name, 0);
            } else if (type.equalsIgnoreCase(SPConstant.TYPE_LONG)) {
                return getLong(context, name, 0L);
            } else if (type.equalsIgnoreCase(SPConstant.TYPE_FLOAT)) {
                return getFloat(context, name, 0f);
            } else if (type.equalsIgnoreCase(SPConstant.TYPE_STRING_SET)) {
                return getString(context, name, null);
            }
            return null;
        }
    }
    
    static String getString(Context context, String name, String defaultValue) {
        SharedPreferences sp = getSP(context);
        if (sp == null) return defaultValue;
        return sp.getString(name, defaultValue);
    }

    static int getInt(Context context, String name, int defaultValue) {
        SharedPreferences sp = getSP(context);
        if (sp == null) return defaultValue;
        return sp.getInt(name, defaultValue);
    }

    static float getFloat(Context context, String name, float defaultValue) {
        SharedPreferences sp = getSP(context);
        if (sp == null) return defaultValue;
        return sp.getFloat(name, defaultValue);
    }

    static boolean getBoolean(Context context, String name, boolean defaultValue) {
        SharedPreferences sp = getSP(context);
        if (sp == null) return defaultValue;
        return sp.getBoolean(name, defaultValue);
    }

    static long getLong(Context context, String name, long defaultValue) {
        SharedPreferences sp = getSP(context);
        if (sp == null) return defaultValue;
        return sp.getLong(name, defaultValue);
    }

    static boolean contains(Context context, String name) {
        SharedPreferences sp = getSP(context);
        if (sp == null) return false;
        return sp.contains(name);
    }

    static void remove(Context context, String name) {
        SharedPreferences sp = getSP(context);
        if (sp == null) return;
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(name);
        apply(editor);
    }

    static void clear(Context context) {
        SharedPreferences sp = getSP(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        apply(editor);
        cleanCachedValue();
    }

    static Map<String,?> getAll(Context context){
        SharedPreferences sp = getSP(context);
        return sp.getAll();
    }


    private static void apply(SharedPreferences.Editor editor) {
        SharedPreferencesCompat.EditorCompat.getInstance().apply(editor);
    }
}