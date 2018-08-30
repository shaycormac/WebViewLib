package com.assassin.webviewdemo.app;

import android.app.Application;
import android.app.DownloadManager;
import android.content.IntentFilter;

import com.assassin.webviewlibrary.provider.SPHelper;
import com.assassin.webviewlibrary.receiver.DownloadReceiver;

/**
 * Author: Shay-Patrick-Cormac
 * Date: 2018/6/20 0020 09:50
 * Version: 1.0
 * Description: 类说明
 */

public class WebViewApp extends Application
{
    private DownloadReceiver mReceiver;
    @Override
    public void onCreate() 
    {
        super.onCreate();
        mReceiver = new DownloadReceiver();
        registerReceiver(mReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        //跨进数据共享
        SPHelper.init(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (mReceiver!=null)
        {
            unregisterReceiver(mReceiver);
        }
    }
}
