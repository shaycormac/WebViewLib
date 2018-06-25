package com.assassin.webviewlibrary.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;

import com.assassin.webviewlibrary.BuildConfig;
import com.assassin.webviewlibrary.R;

import java.util.List;

/**
 * @Author: Shay-Patrick-Cormac
 * @Date: 2017/9/2 11:17
 * @Description: 弹出的dialog的封装
 */

public class DialogUtils {
 

    /**
     * 该方法主要用在splashActivity，即点击设置，也要关闭当前的activity
     */
    public static void alertRefuseDialog(List<String> data, final Context context, final boolean finishActivity, final boolean isSplashActivity) {
        if (data == null || data.isEmpty() || context == null)
            return;
        StringBuilder msgCN = new StringBuilder();
        int size = data.size();
        for (int i = 0; i < size; i++) {

            if (i != data.size() - 1) {
                msgCN.append(data.get(i) + ",");

            } else {
                msgCN.append(data.get(i));
            }
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyAlertDialogStyle);
        builder.setTitle("警告")
                .setMessage(String.format(context.getResources().getString(R.string.permission_explain), msgCN.toString()))
                .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //去设置 2017-10-27 包名用这个方法得到的最靠谱。BuildConfig.APPLICATION_ID
                        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                        context.startActivity(intent);
                        if (isSplashActivity)
                            ((Activity) context).finish();
                    }
                }).setNegativeButton("不用了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
             
                if (finishActivity)
                {
                    ((Activity) context).finish();
                }
            }
        }).setCancelable(false);
        AlertDialog alertDialog = builder.create();
        //设置回退键不可清除
        alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK)
                    return true;
                else
                    return false;
            }
        });
        alertDialog.show();

    }
    
    
    public static AlertDialog showAlertDialog(Context context, String title, CharSequence message, boolean cancelable, final AlertDialogListener listener)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyAlertDialogStyle);
        builder.setCancelable(cancelable)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) 
                    {
                        if (listener!=null)
                            listener.onPositiveSelect();

                    }
                }).setNegativeButton("取消", null);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        return alertDialog;
        
    }
    
    public interface AlertDialogListener
    {
        void onPositiveSelect();
    }
    
}
