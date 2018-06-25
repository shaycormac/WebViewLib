package com.assassin.webviewlibrary.receiver;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import com.assassin.webviewlibrary.util.DownloadUtil;

import java.io.File;

/**
 * Author: Shay-Patrick-Cormac
 * Date: 2018/6/20 0020 09:43
 * Version: 1.0
 * Description: 系统文件下载完毕，接受这个请求
 */

public class DownloadReceiver extends BroadcastReceiver 
{
    @Override
    public void onReceive(Context context, Intent intent) 
    {
        String action = intent.getAction();
        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) 
        {

            Long dwnId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
            DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            if (null != manager) 
            {
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(dwnId);
                Cursor cur = manager.query(query);
                if (null != cur && cur.moveToFirst()) {
                    int columnIndex = cur.getColumnIndex(DownloadManager.COLUMN_STATUS);
                    int status = cur.getInt(columnIndex);
                    if (DownloadManager.STATUS_SUCCESSFUL == status) 
                    {
                        String localUriString = cur.getString(cur.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                        File mFile = new File(Uri.parse(localUriString).getPath());
                        if (!mFile.exists()) 
                        {
                            cur.close();
                            return;
                        }
                        DownloadUtil.openPageAccordType(mFile,context);
                    }
                    cur.close();
                }

            }

        }
    }
}
