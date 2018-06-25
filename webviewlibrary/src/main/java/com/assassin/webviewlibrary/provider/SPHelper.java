package com.assassin.webviewlibrary.provider;

import android.app.Application;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SPHelper {
    public static final String COMMA_REPLACEMENT="__COMMA__";

    public static Context context;

    public static void checkContext() {
        if (context == null) {
            throw new IllegalStateException("context has not been initialed");
        }
    }

    public static void init(Application application) {
        context = application.getApplicationContext();
    }

    public synchronized static void save(String name, Boolean t) {
        checkContext();
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.parse(SPConstant.CONTENT_URI + SPConstant.SEPARATOR + SPConstant.TYPE_BOOLEAN + SPConstant.SEPARATOR + name);
        ContentValues cv = new ContentValues();
        cv.put(SPConstant.VALUE, t);
        cr.update(uri, cv, null, null);
    }

    public synchronized static void save(String name, String t) {
        checkContext();
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.parse(SPConstant.CONTENT_URI + SPConstant.SEPARATOR + SPConstant.TYPE_STRING + SPConstant.SEPARATOR + name);
        ContentValues cv = new ContentValues();
        cv.put(SPConstant.VALUE, t);
        cr.update(uri, cv, null, null);
    }

    public synchronized static void save(String name, Integer t) {
        checkContext();
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.parse(SPConstant.CONTENT_URI + SPConstant.SEPARATOR + SPConstant.TYPE_INT + SPConstant.SEPARATOR + name);
        ContentValues cv = new ContentValues();
        cv.put(SPConstant.VALUE, t);
        cr.update(uri, cv, null, null);
    }

    public synchronized static void save(String name, Long t) {
        checkContext();
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.parse(SPConstant.CONTENT_URI + SPConstant.SEPARATOR + SPConstant.TYPE_LONG + SPConstant.SEPARATOR + name);
        ContentValues cv = new ContentValues();
        cv.put(SPConstant.VALUE, t);
        cr.update(uri, cv, null, null);
    }

    public synchronized static void save(String name, Float t) {
        checkContext();
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.parse(SPConstant.CONTENT_URI + SPConstant.SEPARATOR + SPConstant.TYPE_FLOAT + SPConstant.SEPARATOR + name);
        ContentValues cv = new ContentValues();
        cv.put(SPConstant.VALUE, t);
        cr.update(uri, cv, null, null);
    }


    public synchronized static void save(String name, Set<String> t) {
        checkContext();
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.parse(SPConstant.CONTENT_URI + SPConstant.SEPARATOR + SPConstant.TYPE_STRING_SET + SPConstant.SEPARATOR + name);
        ContentValues cv = new ContentValues();
        Set<String> convert=new HashSet<>();
        for (String string:t){
            convert.add(string.replace(",",COMMA_REPLACEMENT));
        }
        cv.put(SPConstant.VALUE, convert.toString());
        cr.update(uri, cv, null, null);
    }

    public static String getString(String name, String defaultValue) {
        checkContext();
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.parse(SPConstant.CONTENT_URI + SPConstant.SEPARATOR + SPConstant.TYPE_STRING + SPConstant.SEPARATOR + name);
        String rtn = cr.getType(uri);
        if (TextUtils.isEmpty(rtn)) {
            return defaultValue;
        }
        return rtn;
    }

    public static int getInt(String name, int defaultValue) {
        checkContext();
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.parse(SPConstant.CONTENT_URI + SPConstant.SEPARATOR + SPConstant.TYPE_INT + SPConstant.SEPARATOR + name);
        String rtn = cr.getType(uri);
        if (TextUtils.isEmpty(rtn)) {
            return defaultValue;
        }
        return Integer.parseInt(rtn);
    }

    public static float getFloat(String name, float defaultValue) {
        checkContext();
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.parse(SPConstant.CONTENT_URI + SPConstant.SEPARATOR + SPConstant.TYPE_FLOAT + SPConstant.SEPARATOR + name);
        String rtn = cr.getType(uri);
        if (TextUtils.isEmpty(rtn)) {
            return defaultValue;
        }
        return Float.parseFloat(rtn);
    }

    public static boolean getBoolean(String name, boolean defaultValue) {
        checkContext();
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.parse(SPConstant.CONTENT_URI + SPConstant.SEPARATOR + SPConstant.TYPE_BOOLEAN + SPConstant.SEPARATOR + name);
        String rtn = cr.getType(uri);
        if (TextUtils.isEmpty(rtn)) {
            return defaultValue;
        }
        return Boolean.parseBoolean(rtn);
    }

    public static long getLong(String name, long defaultValue) {
        checkContext();
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.parse(SPConstant.CONTENT_URI + SPConstant.SEPARATOR + SPConstant.TYPE_LONG + SPConstant.SEPARATOR + name);
        //实际上SPContentProvider去实现这个方法
        String rtn = cr.getType(uri);
        if (TextUtils.isEmpty(rtn)) {
            return defaultValue;
        }
        return Long.parseLong(rtn);
    }

    public static Set<String> getStringSet(String name, Set<String> defaultValue) {
        checkContext();
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.parse(SPConstant.CONTENT_URI + SPConstant.SEPARATOR + SPConstant.TYPE_STRING_SET + SPConstant.SEPARATOR + name);
        String rtn = cr.getType(uri);
        if (TextUtils.isEmpty(rtn)) {
            return defaultValue;
        }
        if (!rtn.matches("\\[.*\\]")){
            return defaultValue;
        }
        String sub=rtn.substring(1,rtn.length()-1);
        String[] spl=sub.split(", ");
        Set<String> returns=new HashSet<>();
        for (String t:spl){
            returns.add(t.replace(COMMA_REPLACEMENT,", "));
        }
        return returns;
    }

    public static boolean contains(String name) {
        checkContext();
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.parse(SPConstant.CONTENT_URI + SPConstant.SEPARATOR + SPConstant.TYPE_CONTAIN + SPConstant.SEPARATOR + name);
        String rtn = cr.getType(uri);
        if (TextUtils.isEmpty(rtn)) {
            return false;
        } else {
            return Boolean.parseBoolean(rtn);
        }
    }

    public static void remove(String name) {
        checkContext();
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.parse(SPConstant.CONTENT_URI + SPConstant.SEPARATOR + SPConstant.TYPE_LONG + SPConstant.SEPARATOR + name);
        cr.delete(uri, null, null);
    }

    public static void clear(){
        checkContext();
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.parse(SPConstant.CONTENT_URI + SPConstant.SEPARATOR + SPConstant.TYPE_CLEAN);
        cr.delete(uri,null,null);
    }

    public static Map<String,?> getAll(){
        checkContext();
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.parse(SPConstant.CONTENT_URI + SPConstant.SEPARATOR + SPConstant.TYPE_GET_ALL);
        Cursor cursor=cr.query(uri,null,null,null,null);
        ArrayMap resultMap=new ArrayMap();
        if (cursor!=null && cursor.moveToFirst()){
            int nameIndex=cursor.getColumnIndex(SPConstant.CURSOR_COLUMN_NAME);
            int typeIndex=cursor.getColumnIndex(SPConstant.CURSOR_COLUMN_TYPE);
            int valueIndex=cursor.getColumnIndex(SPConstant.CURSOR_COLUMN_VALUE);
            do {
                String key=cursor.getString(nameIndex);
                String type=cursor.getString(typeIndex);
                Object value = null;
                if (type.equalsIgnoreCase(SPConstant.TYPE_STRING)) {
                    value= cursor.getString(valueIndex);
                    if (((String)value).contains(COMMA_REPLACEMENT)){
                        String str= (String) value;
                        if (str.matches("\\[.*\\]")){
                            String sub=str.substring(1,str.length()-1);
                            String[] spl=sub.split(", ");
                            Set<String> returns=new HashSet<>();
                            for (String t:spl){
                                returns.add(t.replace(COMMA_REPLACEMENT,", "));
                            }
                            value=returns;
                        }
                    }
                } else if (type.equalsIgnoreCase(SPConstant.TYPE_BOOLEAN)) {
                    value= cursor.getString(valueIndex);
                } else if (type.equalsIgnoreCase(SPConstant.TYPE_INT)) {
                    value= cursor.getInt(valueIndex);
                } else if (type.equalsIgnoreCase(SPConstant.TYPE_LONG)) {
                    value= cursor.getLong(valueIndex);
                } else if (type.equalsIgnoreCase(SPConstant.TYPE_FLOAT)) {
                    value= cursor.getFloat(valueIndex);
                } else if (type.equalsIgnoreCase(SPConstant.TYPE_STRING_SET)) {
                    value= cursor.getString(valueIndex);
                }
                resultMap.put(key,value);
            }
            while (cursor.moveToNext());
            cursor.close();
        }
        return resultMap;
    }

}